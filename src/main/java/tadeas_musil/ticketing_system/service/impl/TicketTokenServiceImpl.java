package tadeas_musil.ticketing_system.service.impl;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import tadeas_musil.ticketing_system.entity.Ticket;
import tadeas_musil.ticketing_system.entity.TicketToken;
import tadeas_musil.ticketing_system.helper.LocalDateTimeHelper;
import tadeas_musil.ticketing_system.helper.UUIDHelper;
import tadeas_musil.ticketing_system.repository.TicketRepository;
import tadeas_musil.ticketing_system.repository.TicketTokenRepository;
import tadeas_musil.ticketing_system.service.TicketTokenService;

@Service
@RequiredArgsConstructor
public class TicketTokenServiceImpl implements TicketTokenService {

    private final TicketRepository ticketRepository;

    private final TicketTokenRepository ticketTokenRepository;

    private final UUIDHelper uuidHelper;

    private final LocalDateTimeHelper localDateTimeHelper;

    private static int TOKEN_DURATION_HOURS = 24;

    @Override
    public TicketToken createToken(Long ticketId) {
        Ticket ticket = ticketRepository.findById(ticketId).orElseThrow();
        TicketToken token = new TicketToken();
        token.setTicket(ticket);
        token.setToken(uuidHelper.randomUUID());
        token.setExpiryDate(createExpiryDate());
        return ticketTokenRepository.save(token);
    }

    private LocalDateTime createExpiryDate() {
        return localDateTimeHelper.getCurrentDateTime().plusHours(TOKEN_DURATION_HOURS);
    }

    @Override
    public boolean validateToken(Long ticketId, String ticketToken) {
        Optional<TicketToken> token = ticketTokenRepository.findByToken(ticketToken);

        if (token.isPresent() && tokenNotExpired(token.get().getExpiryDate())
                                && token.get().getTicket().getId().equals(ticketId)) {
            return true;
        }
        return false;
    }

    private boolean tokenNotExpired(LocalDateTime expiryDate) {
        if (localDateTimeHelper.getCurrentDateTime().compareTo(expiryDate) > 0) {
            return false;
        }
        return true;
    }
}
