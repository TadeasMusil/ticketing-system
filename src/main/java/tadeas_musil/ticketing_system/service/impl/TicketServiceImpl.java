package tadeas_musil.ticketing_system.service.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;

import javax.mail.MessagingException;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import lombok.RequiredArgsConstructor;
import tadeas_musil.ticketing_system.entity.Department;
import tadeas_musil.ticketing_system.entity.QTicket;
import tadeas_musil.ticketing_system.entity.Ticket;
import tadeas_musil.ticketing_system.entity.TicketEvent;
import tadeas_musil.ticketing_system.entity.TicketToken;
import tadeas_musil.ticketing_system.entity.User;
import tadeas_musil.ticketing_system.entity.enums.Priority;
import tadeas_musil.ticketing_system.entity.enums.TicketEventType;
import tadeas_musil.ticketing_system.repository.DepartmentRepository;
import tadeas_musil.ticketing_system.repository.TicketRepository;
import tadeas_musil.ticketing_system.repository.UserRepository;
import tadeas_musil.ticketing_system.service.EmailService;
import tadeas_musil.ticketing_system.service.TicketEventService;
import tadeas_musil.ticketing_system.service.TicketService;
import tadeas_musil.ticketing_system.service.TokenService;

@Service
@RequiredArgsConstructor
public class TicketServiceImpl implements TicketService {

  private final TicketRepository ticketRepository;

  private final DepartmentRepository departmentRepository;

  private final UserRepository userRepository;

  private final EmailService emailService;

  private final TokenService<TicketToken> ticketTokenService;

  private final TicketEventService ticketEventService;

  private static final int TICKET_PAGE_SIZE = 10;

  private static final String TICKET_ACCESS_EMAIL_SUBJECT = "Temporary ticket access";

  /**
   * Creates a new token for the given ticket and sends a temporary link to
   * author's email
   */
  public void sendTicketAccessEmail(Long ticketId, String emailAddress) throws MessagingException {
    TicketToken token = ticketTokenService.createToken(ticketId);
    Map<String, Object> templateVariables = new HashMap<>();
    templateVariables.put("ticketId", ticketId);
    templateVariables.put("ticketToken", token.getToken()
                                              .toString());
    templateVariables.put("baseUrl", ServletUriComponentsBuilder.fromCurrentContextPath()
                                                                .build()
                                                                .toUriString());
    String emailText = emailService.createStringFromTemplate("email/temporary-ticket-access", templateVariables);
    emailService.sendHtmlEmail(TICKET_ACCESS_EMAIL_SUBJECT, emailAddress, emailText);
  }

  @Override
  public Ticket createTicket(Ticket ticket) {
    TicketEvent creationEvent = ticket.getEvents()
                                      .get(0);
    creationEvent.setType(TicketEventType.CREATE);
    creationEvent.setAuthor(ticket.getAuthor());
    creationEvent.setTicket(ticket);

    ticket.setPriority(Priority.MEDIUM);
    return ticketRepository.save(ticket);
  }

  @Override
  public Ticket getById(Long id) {
    return ticketRepository.findByIdAndFetchEvents(id)
                           .orElseThrow(() -> new NoSuchElementException("Ticket with ID: " + id + " doesn't exist"));
  }

  @Override
  public void updatePriority(Long ticketId, Priority priority) {
    Assert.isTrue(ticketRepository.existsById(ticketId), "Ticket " + ticketId + " does not exist.");
    Assert.isTrue(isValid(priority), "Invalid priority: " + priority.name());

    ticketRepository.setPriority(ticketId, priority);
    ticketEventService.createEvent(ticketId, TicketEventType.PRIORITY_CHANGE, priority.name());
  }

  private boolean isValid(Priority priority) {
    for (Priority p : Priority.values()) {
      if (p.equals(priority)) {
        return true;
      }
    }
    return false;
  }

  @Override
  public void updateDepartment(Long ticketId, Department department) {
    Assert.isTrue(ticketRepository.existsById(ticketId), "Ticket " + ticketId + " does not exist.");
    Assert.isTrue(departmentRepository.existsById(department.getName()),
        "Department " + department.getName() + " does not exist.");

    ticketRepository.setDepartment(ticketId, department);
    ticketEventService.createEvent(ticketId, TicketEventType.DEPARTMENT_CHANGE, department.getName());
  }

  @Override
  public void updateOwner(Long ticketId, String newOwnerUsername) {
    Assert.isTrue(ticketRepository.existsById(ticketId), "Ticket " + ticketId + " does not exist.");

    if (Objects.equals("", newOwnerUsername)) {
      ticketRepository.setOwner(ticketId, null);
      ticketEventService.createEvent(ticketId, TicketEventType.OWNER_CHANGE, null);
    } else {
      User newOwner = userRepository.findByUsername(newOwnerUsername)
                                    .orElseThrow(() -> new UsernameNotFoundException(newOwnerUsername));

      Assert.isTrue(isStaffMember(newOwner), "Can not assign ticket to " + newOwnerUsername);

      ticketRepository.setOwner(ticketId, newOwnerUsername);
      ticketEventService.createEvent(ticketId, TicketEventType.OWNER_CHANGE, newOwnerUsername);
    }
  }

  private boolean isStaffMember(User user) {
    return user.getRoles()
               .stream()
               .map(r -> r.getName())
               .anyMatch(role -> role.equals("ADMIN")
                                 || role.equals("STAFF"));
  }

  @Override
  public void updateStatus(Long ticketId, boolean isClosed) {
    Assert.isTrue(ticketRepository.existsById(ticketId), "Ticket " + ticketId + " does not exist.");

    ticketRepository.setIsClosed(ticketId, isClosed);
    if (isClosed) {
      ticketEventService.createEvent(ticketId, TicketEventType.CLOSE, "CLOSED");
    } else {
      ticketEventService.createEvent(ticketId, TicketEventType.REOPEN, "OPEN");
    }
  }

  @Override
  public void createResponse(Long ticketId, String content) {
    Assert.isTrue(ticketRepository.existsById(ticketId), "Ticket " + ticketId + " does not exist.");

    ticketEventService.createEvent(ticketId, TicketEventType.RESPONSE, content);
  }

  @Override
  public Page<Ticket> getAssignedTickets(String username, Predicate predicate, int page) {
    Pageable pageable = PageRequest.of(page, TICKET_PAGE_SIZE, Sort.by("created")
                                                                   .descending());
    BooleanBuilder builder = new BooleanBuilder(predicate);
    builder.and(QTicket.ticket.owner.eq(username));
    return ticketRepository.findAll(predicate, pageable);
  }

  @Override
  public Page<Ticket> getAllByAuthor(String username, Predicate predicate, int page) {
    Pageable pageable = PageRequest.of(page, TICKET_PAGE_SIZE, Sort.by("created")
                                                                   .descending());
    BooleanBuilder builder = new BooleanBuilder(predicate);
    builder.and(QTicket.ticket.author.eq(username));
    return ticketRepository.findAll(predicate, pageable);
  }

  @Override
  public Page<Ticket> getAll(Predicate predicate, int page) {
    Pageable pageable = PageRequest.of(page, TICKET_PAGE_SIZE, Sort.by("created")
                                                                   .descending());
    return ticketRepository.findAll(predicate, pageable);
  }
}
