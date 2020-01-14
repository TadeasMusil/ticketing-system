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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import tadeas_musil.ticketing_system.entity.Ticket;
import tadeas_musil.ticketing_system.entity.TicketToken;
import tadeas_musil.ticketing_system.entity.enums.Priority;
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
  private UserRepository userRepository;

  @Mock
  private TicketRepository ticketRepository;

  private TicketServiceImpl ticketService;

  @BeforeEach
  private void setUp() {
    ticketService = new TicketServiceImpl(userRepository, ticketRepository, emailService, ticketTokenService);
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
    ticketService.updatePriority(Long.valueOf(5), priority);

    verify(ticketRepository).setPriority(Long.valueOf(5), priority);
  }
}