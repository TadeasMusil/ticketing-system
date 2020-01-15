package tadeas_musil.ticketing_system.service;

import javax.mail.MessagingException;

import org.springframework.stereotype.Service;

import tadeas_musil.ticketing_system.entity.Ticket;
import tadeas_musil.ticketing_system.entity.TicketCategory;
import tadeas_musil.ticketing_system.entity.User;
import tadeas_musil.ticketing_system.entity.enums.Priority;

@Service()
public interface TicketService {

    Ticket createTicket(Ticket ticket);

    void sendTicketAccessEmail(Long ticketId, String email) throws MessagingException;

    Ticket getById(Long id);

    void updatePriority(Long ticketId, Priority priority);

    void updateCategory(Long ticketId, TicketCategory category);

}