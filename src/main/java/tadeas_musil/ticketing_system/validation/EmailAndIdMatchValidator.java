package tadeas_musil.ticketing_system.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import tadeas_musil.ticketing_system.repository.TicketRepository;

@Component
public class EmailAndIdMatchValidator implements ConstraintValidator<EmailAndIdMatch, TicketAccessForm> {

    @Autowired
    private TicketRepository ticketRepository;

    @Override
    public void initialize(EmailAndIdMatch constraintAnnotation) {
    }

    @Override
    public boolean isValid(TicketAccessForm form, ConstraintValidatorContext context) {
        if (ticketRepository.existsById(form.getTicketId())) {
            return ticketRepository.findById(form.getTicketId())
                                    .get()
                                    .getAuthor()
                                    .equals(form.getAuthorEmail());
        }
        return false;

    }
}
