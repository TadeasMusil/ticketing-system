package tadeas_musil.ticketing_system.repository;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import tadeas_musil.ticketing_system.entity.Ticket;
import tadeas_musil.ticketing_system.entity.TicketEvent;

@DataJpaTest
public class TicketRepositoryTest {

    @Autowired
    private TicketRepository ticketRepository;

    @Test
    public void findByName_shouldFindRole_givenExistingRole() {
        Ticket ticket = new Ticket();
        ticket.setContent("content");

        TicketEvent event = new TicketEvent();
        event.setContent("eventContent");

        ticket.addEvent(event);
        ticketRepository.save(ticket);
       
      Ticket result = ticketRepository.findByIdAndFetchEvents(Long.valueOf(1)).get();

      assertThat(result).hasFieldOrPropertyWithValue("content", "content");
      assertThat(result.getEvents()).hasSize(1)
                                    .first()
                                    .hasFieldOrPropertyWithValue("content", "eventContent");

    }

}
