package tadeas_musil.ticketing_system.service.impl;

import java.security.AccessControlException;
import java.util.Optional;

import javax.naming.NoPermissionException;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import tadeas_musil.ticketing_system.entity.Ticket;
import tadeas_musil.ticketing_system.entity.TicketEvent;
import tadeas_musil.ticketing_system.entity.enums.TicketEventType;
import tadeas_musil.ticketing_system.repository.TicketEventRepository;
import tadeas_musil.ticketing_system.service.TicketEventService;

@Service
@RequiredArgsConstructor
public class TicketEventServiceImpl implements TicketEventService {

    private final TicketEventRepository ticketEventRepository;

    public TicketEvent createEvent(Long ticketId, TicketEventType type, String content) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            throw new AccessControlException("Can't create ticket event. User not authenticated.");
        }
        TicketEvent event = new TicketEvent();
        event.setAuthor(authentication.getName());
        event.setTicket(new Ticket(ticketId));
        event.setContent(content);
        event.setType(type);
        return ticketEventRepository.save(event);
    }
}
