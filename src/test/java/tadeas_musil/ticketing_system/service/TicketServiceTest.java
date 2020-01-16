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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.util.ReflectionTestUtils;

import tadeas_musil.ticketing_system.entity.Department;
import tadeas_musil.ticketing_system.entity.Role;
import tadeas_musil.ticketing_system.entity.Ticket;
import tadeas_musil.ticketing_system.entity.TicketCategory;
import tadeas_musil.ticketing_system.entity.TicketToken;
import tadeas_musil.ticketing_system.entity.User;
import tadeas_musil.ticketing_system.entity.enums.Priority;
import tadeas_musil.ticketing_system.repository.DepartmentRepository;
import tadeas_musil.ticketing_system.repository.TicketCategoryRepository;
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
  private TicketCategoryRepository ticketCategoryRepository;

  @Mock
  private UserRepository userRepository;

  @Mock
  private DepartmentRepository departmentRepository;

  private TicketServiceImpl ticketService;

  @BeforeEach
  private void setUp() {
    ticketService = new TicketServiceImpl(ticketRepository, ticketCategoryRepository, 
        departmentRepository,userRepository, emailService, ticketTokenService);
  }

  @Test
  public void sendTicketAccessEmail_shouldSendEmail_givenCorrectData() throws Exception {
    ReflectionTestUtils.setField(ticketService, "accessEmailSubject", "subject");
    when(emailService.createStringFromTemplate(anyString(), anyMap())).thenReturn("message");

    TicketToken token = new TicketToken();
    token.setToken("token");
    when(ticketTokenService.createToken(anyLong())).thenReturn(token);

    ticketService.sendTicketAccessEmail(Long.valueOf(5), "name@email.com");

    verify(emailService).sendHtmlEmail("subject", "name@email.com", "message");
  }

  @Test
  public void getById_shouldThrowNoSuchElementException_givenNonExistingTicket() throws Exception {
    when(ticketRepository.findByIdAndFetchEvents(anyLong())).thenReturn(Optional.empty());

    assertThrows(NoSuchElementException.class, () -> ticketService.getById(Long.valueOf(1)));
  }

  @Test
  public void getById_shouldReturnCorrectTicket_givenExistingTicket() throws Exception {
    Ticket ticket = new Ticket();
    ticket.setAuthor("author@email.com");
    when(ticketRepository.findByIdAndFetchEvents(anyLong())).thenReturn(Optional.of(ticket));

    Ticket result = ticketService.getById(Long.valueOf(1));

    assertEquals("author@email.com", result.getAuthor());
  }

  @ParameterizedTest
  @EnumSource(Priority.class)
  public void updatePriority_shouldUpdatePriority_givenValidPriority(Priority priority) throws Exception {
    when(ticketRepository.existsById(anyLong())).thenReturn(true);

    ticketService.updatePriority(Long.valueOf(5), priority);

    verify(ticketRepository).setPriority(Long.valueOf(5), priority);
  }

  @Test
  public void updatePriority_shouldThrowException_givenNonExistentTicket() throws Exception {
    when(ticketRepository.existsById(anyLong())).thenReturn(false);

    assertThrows(IllegalArgumentException.class, () -> ticketService.updatePriority(Long.valueOf(1), Priority.HIGH));
  }

  @Test
  public void updateCategory_shouldUpdateCategory_givenValidParameters() throws Exception {
    TicketCategory newCategory = new TicketCategory();
    newCategory.setName("newCategory");

    when(ticketRepository.existsById(anyLong())).thenReturn(true);
    when(ticketCategoryRepository.existsById(anyString())).thenReturn(true);

    ticketService.updateCategory(Long.valueOf(5), newCategory);

    verify(ticketRepository).setCategory(Long.valueOf(5), newCategory);
  }

  @Test
  public void updateCategory_shouldThrowException_givenNonExistentTicket() throws Exception {
    TicketCategory newCategory = new TicketCategory();
    newCategory.setName("newCategory");

    when(ticketRepository.existsById(anyLong())).thenReturn(false);
    
    assertThrows(IllegalArgumentException.class, () -> ticketService.updateCategory(Long.valueOf(5), newCategory));
  }

  @Test
  public void updateCategory_shouldThrowException_givenNonExistentCategory() throws Exception {
    TicketCategory newCategory = new TicketCategory();
    newCategory.setName("newCategory");

    when(ticketRepository.existsById(anyLong())).thenReturn(true);
    when(ticketCategoryRepository.existsById(anyString())).thenReturn(false);

    assertThrows(IllegalArgumentException.class, () -> ticketService.updateCategory(Long.valueOf(5), newCategory));
  }

  @Test
  public void updateDepartment_shouldUpdateDepartment_givenValidParameters() throws Exception {
      Department newDepartment = new Department();
      newDepartment.setName("departmentName");

    when(ticketRepository.existsById(anyLong())).thenReturn(true);
    when(departmentRepository.existsById(anyString())).thenReturn(true);

    ticketService.updateDepartment(Long.valueOf(5),  newDepartment);

    verify(ticketRepository).setDepartment(Long.valueOf(5), newDepartment);
  }

  @Test
  public void updateDepartment_shouldThrowException_givenNonExistentTicket() throws Exception {
    Department newDepartment = new Department();
    newDepartment.setName("departmentName");

    when(ticketRepository.existsById(anyLong())).thenReturn(false);
  
    
    assertThrows(IllegalArgumentException.class, () -> ticketService.updateDepartment(Long.valueOf(5), newDepartment));
  }

  @Test
  public void updateDepartment_shouldThrowException_givenNonExistentDepartment() throws Exception {
    Department newDepartment = new Department();
    newDepartment.setName("departmentName");

    when(ticketRepository.existsById(anyLong())).thenReturn(true);
    when(departmentRepository.existsById(anyString())).thenReturn(false);

    assertThrows(IllegalArgumentException.class, () -> ticketService.updateDepartment(Long.valueOf(5), newDepartment));
  }

  @Test
  public void updateOwner_shouldThrowException_givenNonExistentTicket() throws Exception {
    when(ticketRepository.existsById(anyLong())).thenReturn(false);
  
    assertThrows(IllegalArgumentException.class, () -> ticketService.updateOwner(Long.valueOf(5), "newOwner@email.com"));
  }

  @Test
  public void updateOwner_shouldThrowException_givenNonExistentNewOwner() throws Exception {
    when(ticketRepository.existsById(anyLong())).thenReturn(true);
    when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());

    assertThrows(UsernameNotFoundException.class, () -> ticketService.updateOwner(Long.valueOf(5), "newOwner@email.com"));
  }

  @Test
  public void updateOwner_shouldThrowException_givenNewOwnerDoesNotHaveRightRole() throws Exception {
    Role wrongRole = new Role();
    wrongRole.setName("wrongRole");
    
    User newOwner = new User();
    newOwner.setRoles(Set.of(wrongRole));
    
    when(ticketRepository.existsById(anyLong())).thenReturn(true);
    when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(newOwner));
    
    assertThrows(IllegalArgumentException.class, () -> ticketService.updateOwner(Long.valueOf(5), "newOwner@email.com"));
  }

  @Test
  public void updateOwner_shouldUpdateOwner_givenValidParameters() throws Exception {
    Role adminRole = new Role();
    adminRole.setName("ADMIN");
    
    User newOwner = new User();
    newOwner.setRoles(Set.of(adminRole));
    
    when(ticketRepository.existsById(anyLong())).thenReturn(true);
    when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(newOwner));

    ticketService.updateOwner(Long.valueOf(5), "newOwner@email.com");
    
    verify(ticketRepository).setOwner(Long.valueOf(5), "newOwner@email.com");
  }
}