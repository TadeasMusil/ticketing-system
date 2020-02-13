package tadeas_musil.ticketing_system.service;

import java.util.List;

import javax.mail.MessagingException;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import tadeas_musil.ticketing_system.entity.Department;
import tadeas_musil.ticketing_system.entity.Ticket;
import tadeas_musil.ticketing_system.entity.enums.Priority;

@Service()
public interface TicketService {

    Ticket createTicket(Ticket ticket);

    void sendTicketAccessEmail(Long ticketId, String email) throws MessagingException;

    Ticket getById(Long id);

    void updatePriority(Long ticketId, Priority priority);

    void updateDepartment(Long ticketId, Department department);

    void updateOwner(Long ticketId, String owner);

    void updateStatus(Long ticketId, boolean isClosed);

    void createResponse(Long ticketId, String content);

    Page<Ticket> getAssignedTickets(String username, int page);

    Page<Ticket> getAllTickets(int page);
}