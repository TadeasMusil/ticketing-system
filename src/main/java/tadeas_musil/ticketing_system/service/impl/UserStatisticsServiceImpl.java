package tadeas_musil.ticketing_system.service.impl;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import tadeas_musil.ticketing_system.entity.enums.TicketEventType;
import tadeas_musil.ticketing_system.repository.TicketEventRepository;
import tadeas_musil.ticketing_system.repository.TicketRepository;
import tadeas_musil.ticketing_system.service.UserStatisticsService;

@Service
@RequiredArgsConstructor
public class UserStatisticsServiceImpl implements UserStatisticsService {

    private final TicketEventRepository ticketEventRepository;

    private final TicketRepository ticketRepository;

    @Override
    public long getNumberOfAssignedTickets(String username) {
        return ticketRepository.countByOwnerAndIsClosed(username, false);
    }

    @Override
    public long getNumberOfClosedTickets(String username) {
        return ticketEventRepository.countByAuthorAndType(username, TicketEventType.CLOSE);
    }

    @Override
    public long getNumberOfCreatedTickets(String username) {
        return ticketEventRepository.countByAuthorAndType(username, TicketEventType.CREATE);
    }

    
}
