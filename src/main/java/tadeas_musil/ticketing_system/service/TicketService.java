package tadeas_musil.ticketing_system.service;

import javax.mail.MessagingException;

import org.springframework.stereotype.Service;

import tadeas_musil.ticketing_system.entity.Ticket;
import tadeas_musil.ticketing_system.entity.User;

@Service()
public interface TicketService {

    Ticket createTicket(Ticket ticket);

    void sendTicketAccessEmail(Long ticketId, String email) throws MessagingException;

    Ticket getById(Long id);

}