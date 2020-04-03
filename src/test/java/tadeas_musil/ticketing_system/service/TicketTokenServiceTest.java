package tadeas_musil.ticketing_system.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import tadeas_musil.ticketing_system.entity.PasswordResetToken;
import tadeas_musil.ticketing_system.entity.Ticket;
import tadeas_musil.ticketing_system.entity.TicketToken;
import tadeas_musil.ticketing_system.helper.LocalDateTimeHelper;
import tadeas_musil.ticketing_system.helper.UUIDHelper;
import tadeas_musil.ticketing_system.repository.TicketRepository;
import tadeas_musil.ticketing_system.repository.TicketTokenRepository;
import tadeas_musil.ticketing_system.service.impl.TicketTokenServiceImpl;

@ExtendWith(MockitoExtension.class)
public class TicketTokenServiceTest {

  private TicketTokenServiceImpl ticketTokenService;

  @Mock
  private TicketTokenRepository ticketTokenRepository;

  @Mock
  private UUIDHelper uuidHelper;

  @Mock
  private LocalDateTimeHelper localDateTimeHelper;

  @Mock
  private TicketRepository ticketRepository;

  @BeforeEach
  public void setUp() {
    ticketTokenService = new TicketTokenServiceImpl(localDateTimeHelper, ticketRepository, ticketTokenRepository,
        uuidHelper);
  }

  @Test
  public void createTicketToken_shouldCreateTicketToken() throws Exception {
    Ticket ticket = new Ticket();
    ticket.setId(5L);

    UUID uuid = UUID.randomUUID();
    when(uuidHelper.randomUUID()).thenReturn(uuid);

    ReflectionTestUtils.setField(localDateTimeHelper, "timezone", "UTC");
    when(localDateTimeHelper.getCurrentDateTime()).thenReturn(LocalDateTime.of(2000, 1, 1, 10, 30, 15));

    when(ticketRepository.findById(anyLong())).thenReturn(Optional.of(ticket));
    when(ticketTokenRepository.save(any())).then(returnsFirstArg());

    TicketToken createdToken = ticketTokenService.createToken(ticket.getId());

    assertThat(createdToken).hasFieldOrPropertyWithValue("token", uuid)
                            .hasFieldOrPropertyWithValue("ticket", ticket)
                            .hasFieldOrPropertyWithValue("expiryDate", LocalDateTime.of(2000, 1, 2, 10, 30, 15));
  }

  @Test
  public void validateToken_shouldReturnFalse_givenNonExistingToken() {
    when(ticketTokenRepository.findByToken(any())).thenReturn(Optional.empty());

    boolean isValid = ticketTokenService.validateToken(new TicketToken());

    assertThat(isValid).isFalse();
  }

  @Test
  public void validateToken_shouldReturnFalse_givenExpiredToken() {
    TicketToken token = new TicketToken();
    token.setExpiryDate(LocalDateTime.of(1950, 1, 1, 1, 1));
    when(localDateTimeHelper.getCurrentDateTime()).thenReturn(LocalDateTime.of(2000, 1, 1, 1, 1));

    when(ticketTokenRepository.findByToken(any())).thenReturn(Optional.of(token));

    boolean isValid = ticketTokenService.validateToken(new TicketToken());

    assertThat(isValid).isFalse();
  }

  @Test
  public void validateToken_shouldReturnFalse_givenNonMatchingTicketId() {
    Ticket ticket = new Ticket();
    ticket.setId(555555L);
    TicketToken token = new TicketToken();
    token.setTicket(ticket);
    token.setExpiryDate(LocalDateTime.of(2100, 1, 1, 1, 1));
    
    when(localDateTimeHelper.getCurrentDateTime()).thenReturn(LocalDateTime.of(2000, 1, 1, 1, 1));

    when(ticketTokenRepository.findByToken(any())).thenReturn(Optional.of(token));

    Ticket differentTicket = new Ticket();
    differentTicket.setId(1L);
    
    boolean isValid = ticketTokenService.validateToken(new TicketToken(differentTicket));

    assertThat(isValid).isFalse();
  }

  @Test
  public void validateToken_shouldReturnTrue_givenValidToken() {
    Ticket ticket = new Ticket();
    ticket.setId(5L);
    TicketToken token = new TicketToken();
    token.setTicket(ticket);

    token.setExpiryDate(LocalDateTime.of(2100, 1, 1, 1, 1));
    when(localDateTimeHelper.getCurrentDateTime()).thenReturn(LocalDateTime.of(2000, 1, 1, 1, 1));

    when(ticketTokenRepository.findByToken(any())).thenReturn(Optional.of(token));

    boolean isValid = ticketTokenService.validateToken(token);

    assertThat(isValid).isTrue();
  }
}
