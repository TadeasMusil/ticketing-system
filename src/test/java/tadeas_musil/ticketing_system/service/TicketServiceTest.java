package tadeas_musil.ticketing_system.service;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.util.ReflectionTestUtils;

import tadeas_musil.ticketing_system.entity.Department;
import tadeas_musil.ticketing_system.entity.Role;
import tadeas_musil.ticketing_system.entity.Ticket;
import tadeas_musil.ticketing_system.entity.TicketToken;
import tadeas_musil.ticketing_system.entity.User;
import tadeas_musil.ticketing_system.entity.enums.Priority;
import tadeas_musil.ticketing_system.entity.enums.TicketEventType;
import tadeas_musil.ticketing_system.repository.DepartmentRepository;
import tadeas_musil.ticketing_system.repository.TicketRepository;
import tadeas_musil.ticketing_system.repository.UserRepository;
import tadeas_musil.ticketing_system.service.impl.TicketServiceImpl;

@ExtendWith(MockitoExtension.class)
public class TicketServiceTest {

  @Mock
  private EmailService emailService;

  @Mock
  private TicketTokenService ticketTokenService;

  @Mock
  private TicketRepository ticketRepository;

  @Mock
  private UserRepository userRepository;

  @Mock
  private DepartmentRepository departmentRepository;

  @Mock
  private TicketEventService ticketEventService;

  private TicketServiceImpl ticketService;

  @BeforeEach
  private void setUp() {
    ticketService = new TicketServiceImpl(ticketRepository, departmentRepository, userRepository, emailService,
        ticketTokenService, ticketEventService);
  }

  @Test
  public void getById_shouldThrowNoSuchElementException_givenNonExistingTicket() throws Exception {
    when(ticketRepository.findByIdAndFetchEvents(anyLong())).thenReturn(Optional.empty());

    assertThrows(NoSuchElementException.class, () -> ticketService.getById(1L));
  }

  @Test
  public void getById_shouldReturnCorrectTicket_givenExistingTicket() throws Exception {
    Ticket ticket = new Ticket();
    ticket.setAuthor("author@email.com");
    when(ticketRepository.findByIdAndFetchEvents(anyLong())).thenReturn(Optional.of(ticket));

    Ticket result = ticketService.getById(1L);

    assertEquals("author@email.com", result.getAuthor());
  }

  @ParameterizedTest
  @EnumSource(Priority.class)
  public void updatePriority_shouldUpdatePriority_givenValidPriority(Priority priority) throws Exception {
    when(ticketRepository.existsById(anyLong())).thenReturn(true);
    Long ticketId = 1L;

    ticketService.updatePriority(ticketId, priority);

    verify(ticketRepository).setPriority(ticketId, priority);
    verify(ticketEventService).createEvent(ticketId, TicketEventType.PRIORITY_CHANGE, priority.name());

  }

  @Test
  public void updatePriority_shouldThrowException_givenNonExistentTicket() throws Exception {
    when(ticketRepository.existsById(anyLong())).thenReturn(false);

    assertThrows(IllegalArgumentException.class, () -> ticketService.updatePriority(1L, Priority.HIGH));
  }

  @Test
  public void updateDepartment_shouldUpdateDepartment_givenValidParameters() throws Exception {
    Department newDepartment = new Department();
    newDepartment.setName("departmentName");
    Long ticketId = 1L;

    when(ticketRepository.existsById(anyLong())).thenReturn(true);
    when(departmentRepository.existsById(anyString())).thenReturn(true);

    ticketService.updateDepartment(ticketId, newDepartment);

    verify(ticketRepository).setDepartment(ticketId, newDepartment);
    verify(ticketEventService).createEvent(ticketId, TicketEventType.DEPARTMENT_CHANGE, newDepartment.getName());
  }

  @Test
  public void updateDepartment_shouldThrowException_givenNonExistentTicket() throws Exception {
    Department newDepartment = new Department();
    newDepartment.setName("departmentName");

    when(ticketRepository.existsById(anyLong())).thenReturn(false);

    assertThrows(IllegalArgumentException.class, () -> ticketService.updateDepartment(5L, newDepartment));
  }

  @Test
  public void updateDepartment_shouldThrowException_givenNonExistentDepartment() throws Exception {
    Department newDepartment = new Department();
    newDepartment.setName("departmentName");

    when(ticketRepository.existsById(anyLong())).thenReturn(true);
    when(departmentRepository.existsById(anyString())).thenReturn(false);

    assertThrows(IllegalArgumentException.class, () -> ticketService.updateDepartment(5L, newDepartment));
  }

  @Test
  public void updateOwner_shouldThrowException_givenNonExistentTicket() throws Exception {
    when(ticketRepository.existsById(anyLong())).thenReturn(false);

    assertThrows(IllegalArgumentException.class,
        () -> ticketService.updateOwner(5L, "newOwner@email.com"));
  }

  @Test
  public void updateOwner_shouldThrowException_givenNonExistentNewOwner() throws Exception {
    when(ticketRepository.existsById(anyLong())).thenReturn(true);
    when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());

    assertThrows(UsernameNotFoundException.class,
        () -> ticketService.updateOwner(5L, "newOwner@email.com"));
  }

  @Test
  public void updateOwner_shouldThrowException_givenNewOwnerDoesNotHaveRightRole() throws Exception {
    Role wrongRole = new Role();
    wrongRole.setName("wrongRole");

    User newOwner = new User();
    newOwner.setRoles(Set.of(wrongRole));

    when(ticketRepository.existsById(anyLong())).thenReturn(true);
    when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(newOwner));

    assertThrows(IllegalArgumentException.class,
        () -> ticketService.updateOwner(5L, "newOwner@email.com"));
  }

  @ParameterizedTest
  @ValueSource(strings = { "ADMIN", "STAFF" })
  public void updateOwner_shouldUpdateOwner_givenValidRole(String roleName) throws Exception {
    Role role = new Role();
    role.setName(roleName);

    User newOwner = new User();
    newOwner.getRoles().add(role);
    newOwner.setUsername("newOwner@email.com");

    Long ticketId = 1L;

    when(ticketRepository.existsById(anyLong())).thenReturn(true);
    when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(newOwner));

    ticketService.updateOwner(ticketId, newOwner.getUsername());

    verify(ticketRepository).setOwner(ticketId, newOwner.getUsername());
    verify(ticketEventService).createEvent(ticketId, TicketEventType.OWNER_CHANGE, newOwner.getUsername());
  }

  @Test
  public void updateStatus_shouldCloseTicket_givenExistingTicket() throws Exception {
    when(ticketRepository.existsById(anyLong())).thenReturn(true);
    Long ticketId = 1L;

    ticketService.updateStatus(ticketId, true);

    verify(ticketRepository).setIsClosed(ticketId, true);
    verify(ticketEventService).createEvent(ticketId, TicketEventType.CLOSE, "CLOSED");
  }

   @Test
  public void updateStatus_shouldReopenTicket_givenExistingTicket() throws Exception {
    when(ticketRepository.existsById(anyLong())).thenReturn(true);
    Long ticketId = 1L;

    ticketService.updateStatus(ticketId, false);

    verify(ticketRepository).setIsClosed(ticketId, false);
    verify(ticketEventService).createEvent(ticketId, TicketEventType.REOPEN, "OPEN");
  }

  @Test
  public void updateStatus_shouldThrowException_givenNonExistingTicket() throws Exception {
    when(ticketRepository.existsById(anyLong())).thenReturn(false);

    assertThrows(IllegalArgumentException.class, () -> ticketService.updateStatus(5L, true));
  }

  @Test
  public void createResponse_shouldThrowException_givenNonExistingTicket() throws Exception {
    when(ticketRepository.existsById(anyLong())).thenReturn(false);

    assertThrows(IllegalArgumentException.class, () -> ticketService.createResponse(5L, "content"));
  }

  @Test
  public void createResponse_shouldcreateResponse_givenExistingTicket() throws Exception {
    when(ticketRepository.existsById(anyLong())).thenReturn(true);
    Long ticketId = 1L;
    
    ticketService.createResponse(ticketId, "content");

    verify(ticketEventService).createEvent(ticketId, TicketEventType.RESPONSE, "content");
  }
}
