package tadeas_musil.ticketing_system.service.impl;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import lombok.NoArgsConstructor;
import tadeas_musil.ticketing_system.entity.Ticket;
import tadeas_musil.ticketing_system.entity.TicketToken;
import tadeas_musil.ticketing_system.helper.LocalDateTimeHelper;
import tadeas_musil.ticketing_system.helper.UUIDHelper;
import tadeas_musil.ticketing_system.repository.TicketRepository;
import tadeas_musil.ticketing_system.repository.TicketTokenRepository;

@Service
public class TicketTokenServiceImpl extends BaseTokenService<TicketToken> {

  private final TicketRepository ticketRepository;

  private final TicketTokenRepository ticketTokenRepository;

  private final UUIDHelper uuidHelper;

  private static int TOKEN_DURATION_HOURS = 24;

  public TicketTokenServiceImpl(LocalDateTimeHelper localDateTimeHelper, TicketRepository ticketRepository,
      TicketTokenRepository ticketTokenRepository, UUIDHelper uuidHelper) {
    super(localDateTimeHelper);
    this.ticketRepository = ticketRepository;
    this.ticketTokenRepository = ticketTokenRepository;
    this.uuidHelper = uuidHelper;
  }

  @Override
  public TicketToken createToken(Long ticketId) {
    Ticket ticket = ticketRepository.findById(ticketId)
                                    .orElseThrow();
    TicketToken token = new TicketToken();
    token.setTicket(ticket);
    token.setToken(uuidHelper.randomUUID());
    token.setExpiryDate(createExpiryDate(TOKEN_DURATION_HOURS));
    return ticketTokenRepository.save(token);
  }

  @Override
  public boolean validateToken(TicketToken token) {
    Optional<TicketToken> existingToken = ticketTokenRepository.findByToken(token.getToken());

    if (existingToken.isPresent()
        && tokenNotExpired(existingToken.get()
                                        .getExpiryDate())
        && existingToken.get()
                        .getTicket()
                        .getId()
                        .equals(token.getTicket()
                                     .getId())) {
      return true;
    }
    return false;
  }

  @Override
  public TicketToken getByToken(UUID token) {
    return ticketTokenRepository.findByToken(token)
                                .orElseThrow();

  }

}
