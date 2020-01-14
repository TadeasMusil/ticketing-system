package tadeas_musil.ticketing_system.repository;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import tadeas_musil.ticketing_system.entity.Ticket;
import tadeas_musil.ticketing_system.entity.TicketEvent;
import tadeas_musil.ticketing_system.entity.enums.Priority;

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

    @Test
    public void setPriority_shouldSetPriorityToHIGH() {
        Ticket ticket = new Ticket();
        ticket.setPriority(Priority.LOW);
        ticketRepository.save(ticket);

        ticketRepository.setPriority(Long.valueOf(1), Priority.HIGH);
        Ticket updatedTicket = ticketRepository.getOne(Long.valueOf(1));

      assertThat(updatedTicket.getPriority()).isEqualTo(Priority.HIGH);
      

    }

}
