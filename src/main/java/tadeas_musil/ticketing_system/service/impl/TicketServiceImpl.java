package tadeas_musil.ticketing_system.service.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

import javax.mail.MessagingException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import tadeas_musil.ticketing_system.entity.Department;
import tadeas_musil.ticketing_system.entity.Ticket;
import tadeas_musil.ticketing_system.entity.TicketCategory;
import tadeas_musil.ticketing_system.entity.enums.Priority;
import tadeas_musil.ticketing_system.entity.TicketToken;
import tadeas_musil.ticketing_system.repository.DepartmentRepository;
import tadeas_musil.ticketing_system.repository.TicketCategoryRepository;
import tadeas_musil.ticketing_system.repository.TicketRepository;
import tadeas_musil.ticketing_system.repository.UserRepository;
import tadeas_musil.ticketing_system.service.EmailService;
import tadeas_musil.ticketing_system.service.TicketService;
import tadeas_musil.ticketing_system.service.TicketTokenService;

@Service
@RequiredArgsConstructor
public class TicketServiceImpl implements TicketService {

    private final TicketRepository ticketRepository;

    private final TicketCategoryRepository ticketCategoryRepository;

    private final DepartmentRepository departmentRepository;

    private final EmailService emailService;

    private final TicketTokenService ticketTokenService;

    @Value("${ticket.access_email.subject}")
    private String accessEmailSubject;

    /**
     * Creates a new token for the given ticket and sends a temporary link to
     * author's email
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

    @Override
    public Ticket getById(Long id) {
        return ticketRepository.findByIdAndFetchEvents(id)
                .orElseThrow(() -> new NoSuchElementException("Ticket with ID: " + id + " doesn't exist"));
    }

    @Override
    public void updatePriority(Long ticketId, Priority priority) {
        if (ticketRepository.existsById(ticketId)) {
            if (isValid(priority)) {
                ticketRepository.setPriority(ticketId, priority);
            } else {
                throw new IllegalArgumentException("Invalid priority: " + priority.name());
            }
        } else {
            throw new IllegalArgumentException("Ticket " + ticketId + " does not exist.");
        }
    }

    private boolean isValid(Priority priority) {
        for (Priority p : Priority.values()) {
            if (p.equals(priority)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void updateCategory(Long ticketId, TicketCategory category) {
        if (ticketRepository.existsById(ticketId)) {
            if (ticketCategoryRepository.existsById(category.getName())) {
                ticketRepository.setCategory(ticketId, category);
            } else {
                throw new IllegalArgumentException("Invalid category: " + category.getName());
            }

        } else {
            throw new IllegalArgumentException("Ticket " + ticketId + " does not exist.");
        }
    }

    @Override
    public void updateDepartment(Long ticketId, Department department) {
        if (ticketRepository.existsById(ticketId)) {
            if (departmentRepository.existsById(department.getName())) {
                ticketRepository.setDepartment(ticketId, department);
            } else {
                throw new IllegalArgumentException("Invalid department: " + department.getName());
            }

        } else {
            throw new IllegalArgumentException("Ticket " + ticketId + " does not exist.");
        }

    }

}
