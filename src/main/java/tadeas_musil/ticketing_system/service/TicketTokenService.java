package tadeas_musil.ticketing_system.service;

import org.springframework.stereotype.Service;

import tadeas_musil.ticketing_system.entity.TicketToken;

@Service
public interface TicketTokenService {

    TicketToken createToken(Long ticketId);

    boolean validateToken(Long ticketId, String token);

}