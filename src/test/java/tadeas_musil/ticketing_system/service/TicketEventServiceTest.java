package tadeas_musil.ticketing_system.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.security.AccessControlException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import tadeas_musil.ticketing_system.entity.TicketEvent;
import tadeas_musil.ticketing_system.entity.enums.TicketEventType;
import tadeas_musil.ticketing_system.repository.TicketEventRepository;
import tadeas_musil.ticketing_system.service.impl.TicketEventServiceImpl;

@ExtendWith(value = {MockitoExtension.class, SpringExtension.class})
public class TicketEventServiceTest {
  @Mock
  private TicketEventRepository ticketEventRepository;

  private TicketEventService ticketEventService;

  @BeforeEach
  private void setUp() {
    ticketEventService = new TicketEventServiceImpl(ticketEventRepository);
  }

  @Test
  @WithMockUser(username = "username")
  public void createEvent_shouldCreateEvent_givenAuthenticatedUser() {
    when(ticketEventRepository.save(any())).then(returnsFirstArg());

    TicketEvent ticketEvent = ticketEventService.createEvent(Long.valueOf(1), TicketEventType.MESSAGE,
        "Message content");

    assertThat(ticketEvent.getAuthor()).isEqualTo("username");
    assertThat(ticketEvent.getContent()).isEqualTo("Message content");
    assertThat(ticketEvent.getTicket().getId()).isEqualTo(Long.valueOf(1));
    assertThat(ticketEvent.getType()).isEqualTo(TicketEventType.MESSAGE);
  }

  @Test
  public void createEvent_shouldThrowException_givenUserNotAuthenticated() {

    assertThrows(AccessControlException.class,
        () -> ticketEventService.createEvent(Long.valueOf(1), TicketEventType.MESSAGE, "Message content"));
  }
}