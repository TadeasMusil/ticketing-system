package tadeas_musil.ticketing_system.validation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import tadeas_musil.ticketing_system.entity.Ticket;
import tadeas_musil.ticketing_system.repository.TicketRepository;

@SpringBootTest
public class EmailAndIdMatchValidatorTest {
   
    @Autowired
    private Validator validator;
    
    @MockBean
    private TicketRepository ticketRepository;

    private TicketAccessForm getFormPassingAllValidations() {
        TicketAccessForm form = new TicketAccessForm();
        form.setAuthorEmail("user@email.com");
        form.setTicketId(Long.valueOf(5));
        return form;
    }

    @Test
    public void validation_shouldReturnNoError_givenMatchingTicketAndAuthor() {
        TicketAccessForm form = getFormPassingAllValidations();
        Ticket ticket = new Ticket();
        String author = new String();
        ticket.setAuthor(form.getAuthorEmail());
       
        when(ticketRepository.existsById(anyLong())).thenReturn(true);
        when(ticketRepository.findById(anyLong())).thenReturn(Optional.of(ticket));

        Set<ConstraintViolation<TicketAccessForm>> violations = validator.validate(form);

        assertThat(violations).isEmpty();

    }

    @Test
    public void validation_shouldReturnError_givenNonExistentTicket() {
        TicketAccessForm form = getFormPassingAllValidations();
        when(ticketRepository.existsById(anyLong())).thenReturn(false);

        Set<ConstraintViolation<TicketAccessForm>> violations = validator.validate(form);

        assertThat(violations).isNotEmpty();

    }

    @Test
    public void validation_shouldReturnError_givenNullId() {
        TicketAccessForm form = new TicketAccessForm();
        form.setTicketId(null);

        Set<ConstraintViolation<TicketAccessForm>> violations = validator.validate(form);

        assertThat(violations).isNotEmpty();

    }

    @Test
    public void validation_shouldReturnError_givenWhiteSpaceEmail() {
        TicketAccessForm form = new TicketAccessForm();
        form.setAuthorEmail(" ");

        Set<ConstraintViolation<TicketAccessForm>> violations = validator.validate(form);

        assertThat(violations).isNotEmpty();

    }

    @Test
    public void validation_shouldReturnError_givenNonMatchingTicketAndAuthor() {
        TicketAccessForm form = new TicketAccessForm();
        form.setAuthorEmail("name@mail.com");
        form.setTicketId(Long.valueOf(5));
        Ticket ticket = new Ticket();
        ticket.setAuthor("noMatch@email.com");

        when(ticketRepository.existsById(anyLong())).thenReturn(true);
        when(ticketRepository.findById(anyLong())).thenReturn(Optional.of(ticket));

        Set<ConstraintViolation<TicketAccessForm>> violations = validator.validate(form);

        assertThat(violations).isNotEmpty();

    }

}