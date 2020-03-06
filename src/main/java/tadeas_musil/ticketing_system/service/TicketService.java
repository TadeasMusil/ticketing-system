package tadeas_musil.ticketing_system.service;

import javax.mail.MessagingException;

import com.querydsl.core.types.Predicate;

import org.springframework.data.domain.Page;

import tadeas_musil.ticketing_system.entity.Department;
import tadeas_musil.ticketing_system.entity.Ticket;
import tadeas_musil.ticketing_system.entity.enums.Priority;

public interface TicketService {

    Ticket createTicket(Ticket ticket);

    void sendTicketAccessEmail(Long ticketId, String email) throws MessagingException;

    Ticket getById(Long id);

    void updatePriority(Long ticketId, Priority priority);

    void updateDepartment(Long ticketId, Department department);

    void updateOwner(Long ticketId, String owner);

    void updateStatus(Long ticketId, boolean isClosed);

    void createResponse(Long ticketId, String content);

    Page<Ticket> getAssignedTickets(String username, Predicate predicate, int page);

    Page<Ticket> getAll(Predicate predicate, int page);

    Page<Ticket> getAllByAuthor(String username, Predicate predicate, int page);
}