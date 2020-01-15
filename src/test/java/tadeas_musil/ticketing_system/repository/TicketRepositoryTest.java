package tadeas_musil.ticketing_system.repository;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import tadeas_musil.ticketing_system.entity.Department;
import tadeas_musil.ticketing_system.entity.Ticket;
import tadeas_musil.ticketing_system.entity.TicketCategory;
import tadeas_musil.ticketing_system.entity.TicketEvent;
import tadeas_musil.ticketing_system.entity.enums.Priority;

@DataJpaTest
public class TicketRepositoryTest {

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private TicketCategoryRepository ticketCategoryRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

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

    @Test
    public void setCategory_shouldSetCategoryToNewCategory() {
        TicketCategory category = new TicketCategory();
        category.setName("categoryName");
        ticketCategoryRepository.save(category);
        
        Ticket ticket = new Ticket();
        ticket.setCategory(category);
        ticketRepository.save(ticket);

        TicketCategory newCategory = new TicketCategory();
        newCategory.setName("newCategoryName");
        ticketCategoryRepository.save(newCategory);

        ticketRepository.setCategory(Long.valueOf(1), newCategory);
        Ticket updatedTicket = ticketRepository.getOne(Long.valueOf(1));

      assertThat(updatedTicket.getCategory().getName()).isEqualTo(newCategory.getName());
    }

    @Test
    public void setDepartment_shouldSetDepartmentToNewDepartment() {
        Department department = new Department();
        department.setName("departmentName");
        departmentRepository.save(department);
        
        Ticket ticket = new Ticket();
        ticket.setDepartment(department);
        ticketRepository.save(ticket);

        Department newDeparment = new Department();
        newDeparment.setName("newDepartmentName");
        departmentRepository.save(newDeparment);

        ticketRepository.setDepartment(Long.valueOf(1), newDeparment);
        Ticket updatedTicket = ticketRepository.getOne(Long.valueOf(1));

        assertThat(updatedTicket.getDepartment().getName()).isEqualTo(newDeparment.getName());
    }

}
