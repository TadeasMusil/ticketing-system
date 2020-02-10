package tadeas_musil.ticketing_system.service;

import org.springframework.stereotype.Service;

@Service()
public interface UserStatisticsService {

    long getNumberOfAssignedTickets(String username);

    long getNumberOfClosedTickets(String username);

    long getNumberOfCreatedTickets(String username);
}