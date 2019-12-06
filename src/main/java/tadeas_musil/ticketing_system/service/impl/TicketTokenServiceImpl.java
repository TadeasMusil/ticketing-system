package tadeas_musil.ticketing_system.service.impl;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import tadeas_musil.ticketing_system.entity.Ticket;
import tadeas_musil.ticketing_system.entity.TicketToken;
import tadeas_musil.ticketing_system.helper.LocalDateTimeHelper;
import tadeas_musil.ticketing_system.helper.UUIDHelper;
import tadeas_musil.ticketing_system.repository.TicketRepository;
import tadeas_musil.ticketing_system.repository.TicketTokenRepository;
import tadeas_musil.ticketing_system.service.TicketTokenService;

@Service
public class TicketTokenServiceImpl implements TicketTokenService {

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private TicketTokenRepository ticketTokenRepository;

    @Autowired
    private UUIDHelper uuidHelper;

    @Autowired
    private LocalDateTimeHelper localDateTimeHelper;
    
    @Value("${ticket_token.duration}")
    private int tokenDuration;

    @Override
    public TicketToken createToken(Long ticketId) {
        Ticket ticket = ticketRepository.findById(ticketId).orElseThrow();
        TicketToken token = new TicketToken();
        token.setTicket(ticket);
        token.setToken(uuidHelper.randomUUID());
        token.setExpiryDate(createExpiryDate());
        return ticketTokenRepository.save(token);
    }

    private LocalDateTime createExpiryDate(){
        return localDateTimeHelper.getCurrentDateTime().plusHours(tokenDuration);
    }
}
