package tadeas_musil.ticketing_system.service.impl;

import java.util.HashMap;
import java.util.Map;

import javax.mail.MessagingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import tadeas_musil.ticketing_system.entity.Ticket;
import tadeas_musil.ticketing_system.entity.TicketToken;
import tadeas_musil.ticketing_system.entity.User;
import tadeas_musil.ticketing_system.repository.TicketRepository;
import tadeas_musil.ticketing_system.repository.UserRepository;
import tadeas_musil.ticketing_system.service.EmailService;
import tadeas_musil.ticketing_system.service.TicketService;
import tadeas_musil.ticketing_system.service.TicketTokenService;

@Service("ticketServiceImpl")
public class TicketServiceImpl implements TicketService {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private TicketTokenService ticketTokenService;

    @Value("${ticket.access_email.subject}")
    private String accessEmailSubject;

    
    
    /**
     Creates a new token for the given ticket and sends a temporary link to author's email
    */
    public void sendTicketAccessEmail(Long ticketId, String email) throws MessagingException {
        TicketToken token = ticketTokenService.createToken(ticketId);
        Map<String, Object> templateVariables = new HashMap<>();
        templateVariables.put("ticketId", ticketId);
        templateVariables.put("ticketToken", token.getToken());
        String emailText = emailService.createStringFromTemplate("templateName", templateVariables);
        emailService.sendHtmlEmail(accessEmailSubject, email, emailText);
    }

    @Override
    public Ticket createTicket(Ticket ticket) {
       return ticketRepository.save(ticket);
        
    }
}
