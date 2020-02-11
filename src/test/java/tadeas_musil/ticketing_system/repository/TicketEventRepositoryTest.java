package tadeas_musil.ticketing_system.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.DirtiesContext;

import tadeas_musil.ticketing_system.entity.Department;
import tadeas_musil.ticketing_system.entity.Ticket;
import tadeas_musil.ticketing_system.entity.TicketEvent;
import tadeas_musil.ticketing_system.entity.enums.TicketEventType;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class TicketEventRepositoryTest {

    @Autowired
    private TicketEventRepository ticketEventRepository;

    @Autowired
    private DepartmentRepository departmentRepository;
    
    @Autowired
    private TicketRepository ticketRepository;

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

    @Test
    public void findByDepartments_() {
        Department firstDepartment = departmentRepository.save(new Department("firstDepartment"));
        Department secondDepartment = departmentRepository.save(new Department("secondDepartment"));
        Department thirdDepartment = departmentRepository.save(new Department("thirdDepartment"));
        
        Ticket firstTicket = new Ticket(firstDepartment);
        firstTicket.addEvent(new TicketEvent("Jim"));
        
        Ticket secondTicket = new Ticket(secondDepartment);
        secondTicket.addEvent(new TicketEvent("Bob"));
        
        Ticket thirdTicket = new Ticket(thirdDepartment);
        thirdTicket.addEvent(new TicketEvent("Adam"));

        ticketRepository.saveAll(List.of(firstTicket, secondTicket, thirdTicket));

        Page<TicketEvent> eventsPage = ticketEventRepository
        .findByDepartmentsOrderByCreatedDesc(Set.of(firstDepartment, secondDepartment), PageRequest.of(0, 5));

        assertThat(eventsPage)
        .hasSize(2)
        .first()
        .hasFieldOrPropertyWithValue("author", "Bob");
    }

}
