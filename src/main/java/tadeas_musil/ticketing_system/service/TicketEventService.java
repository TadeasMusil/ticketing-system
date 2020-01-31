package tadeas_musil.ticketing_system.service;

import org.springframework.stereotype.Service;

import tadeas_musil.ticketing_system.entity.TicketEvent;
import tadeas_musil.ticketing_system.entity.enums.TicketEventType;

@Service
public interface TicketEventService {

    TicketEvent createEvent(Long ticketId, TicketEventType type, String content);

}