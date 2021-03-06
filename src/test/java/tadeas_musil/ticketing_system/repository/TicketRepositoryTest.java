package tadeas_musil.ticketing_system.repository;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;

import tadeas_musil.ticketing_system.entity.Department;
import tadeas_musil.ticketing_system.entity.Ticket;
import tadeas_musil.ticketing_system.entity.TicketEvent;
import tadeas_musil.ticketing_system.entity.enums.Priority;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class TicketRepositoryTest {

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Test
    public void findByIdAndFetchEvents_shouldFindRole_givenExistingRole() {
        Ticket ticket = new Ticket();
        ticket.setAuthor("author");

        TicketEvent event = new TicketEvent();
        event.setContent("eventContent");

        ticket.addEvent(event);
        ticket = ticketRepository.save(ticket);
       
        Ticket result = ticketRepository.findByIdAndFetchEvents(ticket.getId()).get();

        assertThat(result).hasFieldOrPropertyWithValue("author", "author");
        assertThat(result.getEvents()).hasSize(1)
                                        .first()
                                        .hasFieldOrPropertyWithValue("content", "eventContent");

    }

    @Test
    public void setPriority_shouldSetPriorityToHIGH() {
        Ticket ticket = new Ticket();
        ticket.setPriority(Priority.LOW);
        ticket = ticketRepository.save(ticket);

        ticketRepository.setPriority(1L, Priority.HIGH);
        Ticket updatedTicket = ticketRepository.getOne(1L);

      assertThat(updatedTicket.getPriority()).isEqualTo(Priority.HIGH);
    }

    
    @Test
    public void setDepartment_shouldSetDepartmentToDifferentDepartment() {
        Department department = new Department();
        department.setName("departmentName");
        department =  departmentRepository.save(department);
        
        Ticket ticket = new Ticket();
        ticket.setDepartment(department);
        ticket = ticketRepository.save(ticket);
 
        Department differentDepartment = new Department();
        differentDepartment.setName("differentDepartmentName");
        differentDepartment = departmentRepository.save(differentDepartment);

        ticketRepository.setDepartment(1L, differentDepartment);
        Ticket updatedTicket = ticketRepository.getOne(1L);

        assertThat(updatedTicket.getDepartment().getName()).isEqualTo(differentDepartment.getName());
    }

    @Test
    public void setOwner_shouldSetOwnerToNewOwner() {
        Ticket ticket = new Ticket();
        ticket.setOwner("owner@email.com");
        ticketRepository.save(ticket);

        ticketRepository.setOwner(1L, "newOwner@email.com");
        Ticket updatedTicket = ticketRepository.getOne(1L);

        assertThat(updatedTicket.getOwner()).isEqualTo("newOwner@email.com");
    }

    @Test
    public void setIsClosed_shouldSetIsClosedToTrue() {
        Ticket ticket = new Ticket();
        ticket.setClosed(false);
        ticketRepository.save(ticket);

        ticketRepository.setIsClosed(1L, true);
        Ticket updatedTicket = ticketRepository.getOne(1L);

        assertThat(updatedTicket.isClosed()).isEqualTo(true);
    }

    @Test
    public void countByOwnerAndStatus_shouldReturnTwo() {
        ticketRepository.save(new Ticket("Jim", false));
        ticketRepository.save(new Ticket("Bob", true));

        ticketRepository.save(new Ticket("Jim", true));
        ticketRepository.save(new Ticket("Jim", true));

        long count = ticketRepository.countByOwnerAndIsClosed("Jim", true);

        assertThat(count).isEqualTo(2L);
    }

}
