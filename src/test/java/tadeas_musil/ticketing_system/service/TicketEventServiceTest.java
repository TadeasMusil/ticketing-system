package tadeas_musil.ticketing_system.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.security.AccessControlException;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import tadeas_musil.ticketing_system.entity.TicketEvent;
import tadeas_musil.ticketing_system.entity.User;
import tadeas_musil.ticketing_system.entity.enums.TicketEventType;
import tadeas_musil.ticketing_system.repository.TicketEventRepository;
import tadeas_musil.ticketing_system.repository.UserRepository;
import tadeas_musil.ticketing_system.service.impl.TicketEventServiceImpl;

@ExtendWith(value = {MockitoExtension.class, SpringExtension.class})
public class TicketEventServiceTest {
  
  @Mock
  private TicketEventRepository ticketEventRepository;

  @Mock
  private UserRepository userRepository;

  private TicketEventService ticketEventService;



  @BeforeEach
  private void setUp() {
    ticketEventService = new TicketEventServiceImpl(ticketEventRepository, userRepository);
  }

  @Test
  @WithMockUser(username = "username")
  public void createEvent_shouldCreateEvent_givenAuthenticatedUser() {
    when(ticketEventRepository.save(any())).then(returnsFirstArg());

    TicketEvent ticketEvent = ticketEventService.createEvent(1L, TicketEventType.RESPONSE,
        "Message content");

    assertThat(ticketEvent.getAuthor()).isEqualTo("username");
    assertThat(ticketEvent.getContent()).isEqualTo("Message content");
    assertThat(ticketEvent.getTicket().getId()).isEqualTo(1L);
    assertThat(ticketEvent.getType()).isEqualTo(TicketEventType.RESPONSE);
  }

  @Test
  public void createEvent_shouldThrowException_givenUserNotAuthenticated() {

    assertThrows(AccessControlException.class,
        () -> ticketEventService.createEvent(1L, TicketEventType.RESPONSE, "Message content"));
  }

  @Test
  public void getEventsByUsersDepartments_shouldThrowException_givenNonExistingUser() {
    when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());

    assertThrows(UsernameNotFoundException.class,
        () -> ticketEventService.getEventsByUsersDepartments("username", 1));
  }

  @Test
  public void getEventsByUsersDepartments_shouldReturnEmptyPage_givenUserWithNoDepartment() {
    when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(new User()));

    Page<TicketEvent> page =  ticketEventService.getEventsByUsersDepartments("username", 1);

    assertThat(page).hasSize(0);
  }
}