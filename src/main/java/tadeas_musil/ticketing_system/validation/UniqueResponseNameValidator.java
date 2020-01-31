package tadeas_musil.ticketing_system.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import tadeas_musil.ticketing_system.repository.CannedResponseRepository;

@Component
public class UniqueResponseNameValidator implements ConstraintValidator<UniqueResponseName, String> {

    @Autowired
    private CannedResponseRepository cannedResponseRepository;
    
    private String message;

    @Override
    public void initialize(UniqueResponseName constraintAnnotation) {
        message = constraintAnnotation.message();
    }

    @Override
    public boolean isValid(String name, ConstraintValidatorContext context) {
         return !cannedResponseRepository.existsByName(name);
    }
}
