package tadeas_musil.ticketing_system.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.BeanWrapperImpl;

public class PasswordMatchValidator implements ConstraintValidator<PasswordMatch, Object> {

    private String message;

    private String firstFieldName;

    private String secondFieldName;

    @Override
    public void initialize(PasswordMatch constraintAnnotation) {
        message = constraintAnnotation.message();
        firstFieldName = constraintAnnotation.first();
        secondFieldName = constraintAnnotation.second();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        final Object firstFieldValue = new BeanWrapperImpl(value).getPropertyValue(firstFieldName);
        final Object secondFieldValue = new BeanWrapperImpl(value).getPropertyValue(secondFieldName);
        
        boolean isValid = firstFieldValue == null && secondFieldValue == null
                || firstFieldValue != null && firstFieldValue.equals(secondFieldValue);
        
        if (!isValid) {
            context.buildConstraintViolationWithTemplate(message)
                    .addPropertyNode(firstFieldName)
                    .addConstraintViolation()
                    .disableDefaultConstraintViolation();
        }
        return isValid;

    }
}