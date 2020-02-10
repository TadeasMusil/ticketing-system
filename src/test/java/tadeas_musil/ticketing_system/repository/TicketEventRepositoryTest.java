package tadeas_musil.ticketing_system.repository;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.DirtiesContext;

import tadeas_musil.ticketing_system.entity.TicketEvent;
import tadeas_musil.ticketing_system.entity.enums.TicketEventType;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class TicketEventRepositoryTest {

    @Autowired
    private TicketEventRepository ticketEventRepository;

    @Test
    public void findAllByOrderByNameAsc_shouldReturnTicketEventsInCorrectOrder() {
        TicketEvent firstTicketEvent = ticketEventRepository.save(new TicketEvent("Jim"));
        TicketEvent secondTicketEvent = ticketEventRepository.save(new TicketEvent("Jim"));
        TicketEvent thirdTicketEvent = ticketEventRepository.save(new TicketEvent("Bob"));
        TicketEvent fourthTicketEvent = ticketEventRepository.save(new TicketEvent("Jim"));

        Page<TicketEvent> events = ticketEventRepository.findByAuthorOrderByCreatedDesc("Jim", PageRequest.of(0, 5));

        assertThat(events)
        .hasSize(3)
        .startsWith(fourthTicketEvent, secondTicketEvent, firstTicketEvent);
    }

    @Test
    public void countByAuthorAndType_shouldReturnTwo_givenTwoMatchingEvents() {
        ticketEventRepository.save(new TicketEvent("Jim", TicketEventType.DEPARTMENT_CHANGE));
        ticketEventRepository.save(new TicketEvent("Bob", TicketEventType.CREATE));

        ticketEventRepository.save(new TicketEvent("Jim", TicketEventType.CREATE));
        ticketEventRepository.save(new TicketEvent("Jim", TicketEventType.CREATE));

        long count = ticketEventRepository.countByAuthorAndType("Jim", TicketEventType.CREATE);

        assertThat(count).isEqualTo(2L);
    }

    @Test
    public void findByAuthorOrderByCreatedDesc_shouldReturnThreeEventsInCorrectOrder_givenThreeMatchingEvents() {
        TicketEvent first = ticketEventRepository.save(new TicketEvent("Jim"));
        ticketEventRepository.save(new TicketEvent("Bob"));

        TicketEvent second = ticketEventRepository.save(new TicketEvent("Jim"));
        TicketEvent third =  ticketEventRepository.save(new TicketEvent("Jim"));

        Page<TicketEvent> eventsPage = ticketEventRepository.findByAuthorOrderByCreatedDesc("Jim", PageRequest.of(0, 3));

        assertThat(eventsPage)
        .hasSize(3)
        .startsWith(third, second, first);
    }

}
