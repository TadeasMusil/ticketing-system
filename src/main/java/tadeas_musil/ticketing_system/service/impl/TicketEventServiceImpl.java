package tadeas_musil.ticketing_system.service.impl;

import java.security.AccessControlException;
import java.util.ArrayList;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import tadeas_musil.ticketing_system.entity.Ticket;
import tadeas_musil.ticketing_system.entity.TicketEvent;
import tadeas_musil.ticketing_system.entity.User;
import tadeas_musil.ticketing_system.entity.enums.TicketEventType;
import tadeas_musil.ticketing_system.repository.TicketEventRepository;
import tadeas_musil.ticketing_system.repository.UserRepository;
import tadeas_musil.ticketing_system.service.TicketEventService;

@Service
@RequiredArgsConstructor
public class TicketEventServiceImpl implements TicketEventService {

    private final TicketEventRepository ticketEventRepository;

    private final UserRepository userRepository;

    public static final int EVENTS_PAGE_SIZE = 8;

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

    @Override
    public Page<TicketEvent> getEventsByAuthor(String username, int page) {
        Pageable pageable = PageRequest.of(page, EVENTS_PAGE_SIZE);
        return ticketEventRepository.findByAuthorOrderByCreatedDesc(username, pageable);
    }

    @Override
    @Transactional
    public Page<TicketEvent> getEventsByUsersDepartments(String username, int page) {
        User user = userRepository.findByUsername(username)
        .orElseThrow(() -> new UsernameNotFoundException(username));
        if(user.getDepartments().isEmpty()){
            return new PageImpl<>(new ArrayList<>());
        }
        Pageable pageable = PageRequest.of(page, EVENTS_PAGE_SIZE);
        return ticketEventRepository.findByDepartmentsOrderByCreatedDesc(user.getDepartments(), pageable);
    }

    @Override
    public Page<TicketEvent> getAllEvents(int page) {
        return ticketEventRepository.findAll(PageRequest.of(page, EVENTS_PAGE_SIZE));
    }
}
