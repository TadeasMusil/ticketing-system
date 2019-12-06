package tadeas_musil.ticketing_system.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import tadeas_musil.ticketing_system.entity.Ticket;
import tadeas_musil.ticketing_system.entity.TicketToken;
import tadeas_musil.ticketing_system.helper.LocalDateTimeHelper;
import tadeas_musil.ticketing_system.helper.UUIDHelper;
import tadeas_musil.ticketing_system.repository.TicketRepository;
import tadeas_musil.ticketing_system.repository.TicketTokenRepository;
import tadeas_musil.ticketing_system.service.impl.TicketTokenServiceImpl;

@ExtendWith(MockitoExtension.class)
public class TicketTokenServiceTest {

  @InjectMocks
  private TicketTokenService ticketTokenService = new TicketTokenServiceImpl();

  @Mock
  private TicketTokenRepository ticketTokenRepository;

  @Mock
  private UUIDHelper uuidHelper;

  @Mock
  private LocalDateTimeHelper localDateTimeHelper;

  @Mock
  private TicketRepository ticketRepository;

  @Test
  public void createTicketToken_shouldCreateTicketToken() throws Exception {
    Ticket ticket = new Ticket();
    ticket.setId(Long.valueOf(5));
    
    when(uuidHelper.randomUUID()).thenReturn("randomUUID");
    ReflectionTestUtils.setField(ticketTokenService, "tokenDuration", 24);

    ReflectionTestUtils.setField(localDateTimeHelper, "timezone", "UTC");
    when(localDateTimeHelper.getCurrentDateTime()).thenReturn(LocalDateTime.of(2000, 1, 1, 10, 30, 15));

    when(ticketRepository.findById(anyLong())).thenReturn(Optional.of(ticket));
    when(ticketTokenRepository.save(any())).then(returnsFirstArg());

    TicketToken createdToken = ticketTokenService.createToken(ticket.getId());

    assertThat(createdToken).hasFieldOrPropertyWithValue("token", "randomUUID")
                            .hasFieldOrPropertyWithValue("ticket", ticket)
                            .hasFieldOrPropertyWithValue("expiryDate", LocalDateTime.of(2000, 1, 2, 10, 30, 15));

  }

}