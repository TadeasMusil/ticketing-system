package tadeas_musil.ticketing_system.service.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;

import javax.mail.MessagingException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import tadeas_musil.ticketing_system.entity.Department;
import tadeas_musil.ticketing_system.entity.Role;
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
import tadeas_musil.ticketing_system.service.TicketTokenService;

@Service
@RequiredArgsConstructor
public class TicketServiceImpl implements TicketService {

    private final TicketRepository ticketRepository;

    private final DepartmentRepository departmentRepository;

    private final UserRepository userRepository;

    private final EmailService emailService;

    private final TicketTokenService ticketTokenService;

    private final TicketEventService ticketEventService;

    public static final int TICKET_PAGE_SIZE = 10;

    @Value("${ticket.access_email.subject}")
    private String accessEmailSubject;

    /**
     * Creates a new token for the given ticket and sends a temporary link to
     * author's email
     */
    public void sendTicketAccessEmail(Long ticketId, String email) throws MessagingException {
        TicketToken token = ticketTokenService.createToken(ticketId);
        Map<String, Object> templateVariables = new HashMap<>();
        templateVariables.put("ticketId", ticketId);
        templateVariables.put("ticketToken", token.getToken());
        String emailText = emailService.createStringFromTemplate("templateName", templateVariables);
        emailService.sendHtmlEmail(accessEmailSubject, email, emailText);
    }

    @Override
    public Ticket createTicket(Ticket ticket) {
        TicketEvent creationEvent = ticket.getEvents().get(0);
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
        if (ticketRepository.existsById(ticketId)) {
            if (isValid(priority)) {
                ticketRepository.setPriority(ticketId, priority);
                ticketEventService.createEvent(ticketId, TicketEventType.PRIORITY_CHANGE, priority.name());
            } else {
                throw new IllegalArgumentException("Invalid priority: " + priority.name());
            }
        } else {
            throw new IllegalArgumentException("Ticket " + ticketId + " does not exist.");
        }
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
        if (ticketRepository.existsById(ticketId)) {
            if (departmentRepository.existsById(department.getName())) {
                ticketRepository.setDepartment(ticketId, department);
                ticketEventService.createEvent(ticketId, TicketEventType.DEPARTMENT_CHANGE, department.getName());
            } else {
                throw new IllegalArgumentException("Invalid department: " + department.getName());
            }

        } else {
            throw new IllegalArgumentException("Ticket " + ticketId + " does not exist.");
        }

    }

    @Override
    public void updateOwner(Long ticketId, String newOwnerUsername) {
        if (ticketRepository.existsById(ticketId)) {
            if (Objects.equals("", newOwnerUsername)) {
                ticketRepository.setOwner(ticketId, null);
                ticketEventService.createEvent(ticketId, TicketEventType.OWNER_CHANGE, null);
            } else {
                User newOwner = userRepository.findByUsername(newOwnerUsername)
                        .orElseThrow(() -> new UsernameNotFoundException(newOwnerUsername));

                if (anyRoleMatch(newOwner.getRoles(), "ADMIN", "STAFF")) {
                    ticketRepository.setOwner(ticketId, newOwnerUsername);
                    ticketEventService.createEvent(ticketId, TicketEventType.OWNER_CHANGE, newOwnerUsername);
                } else {
                    throw new IllegalArgumentException("Can not assign ticket to " + newOwnerUsername);
                }
            }

        } else {
            throw new IllegalArgumentException("Ticket " + ticketId + " does not exist.");
        }
    }

    private boolean anyRoleMatch(Set<Role> roles, String... stringRoles) {
        return roles.stream().map(r -> r.getName()).anyMatch(Set.of(stringRoles)::contains);
    }

    @Override
    public void updateStatus(Long ticketId, boolean isClosed) {
        if (ticketRepository.existsById(ticketId)) {
            ticketRepository.setIsClosed(ticketId, isClosed);
            if (isClosed) {
                ticketEventService.createEvent(ticketId, TicketEventType.CLOSE, "CLOSED");
            } else {
                ticketEventService.createEvent(ticketId, TicketEventType.REOPEN, "OPEN");
            }
        } else {
            throw new IllegalArgumentException("Ticket " + ticketId + " does not exist.");
        }
    }

    @Override
    public void createResponse(Long ticketId, String content) {
        if (ticketRepository.existsById(ticketId)) {
            ticketEventService.createEvent(ticketId, TicketEventType.RESPONSE, content);
        } else {
            throw new IllegalArgumentException("Ticket " + ticketId + " does not exist.");
        }

    }

    @Override
    public Page<Ticket> getAssignedTickets(String username, int page) {
        Pageable pageable = PageRequest.of(page, TICKET_PAGE_SIZE, Sort.by("created").descending());
        return ticketRepository.findByOwner(username, pageable);
    }

    @Override
    public Page<Ticket> getAllTickets(int page) {
        Pageable pageable = PageRequest.of(page, TICKET_PAGE_SIZE, Sort.by("created").descending());
        return ticketRepository.findAll(pageable);
    }
}
