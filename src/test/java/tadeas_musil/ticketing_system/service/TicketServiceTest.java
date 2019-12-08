package tadeas_musil.ticketing_system.service;

import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import tadeas_musil.ticketing_system.service.impl.TicketServiceImpl;

@ExtendWith(MockitoExtension.class)
public class TicketServiceTest {
  @InjectMocks
  private TicketService ticketService = new TicketServiceImpl();

  @Mock
  private EmailService emailService;

  @Mock
  private TicketTokenService ticketTokenService;

  @Test
  public void sendTicketAccessEmail_shouldSendEmail_givenCorrectData() throws Exception {
    ReflectionTestUtils.setField(ticketService, "accessEmailSubject", "subject");
    when(emailService.createStringFromTemplate(anyString(), anyMap())).thenReturn("message");

    ticketService.sendTicketAccessEmail(Long.valueOf(5), "name@email.com");

    verify(emailService).sendHtmlEmail("subject", "name@email.com", "message");
  }

}