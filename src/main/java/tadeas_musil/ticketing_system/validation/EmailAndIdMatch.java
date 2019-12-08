package tadeas_musil.ticketing_system.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = EmailAndIdMatchValidator.class)
@Target({ ElementType.METHOD,ElementType.ANNOTATION_TYPE ,ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface EmailAndIdMatch {
    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String email();

    String id();

    String message() default "Email and ticket number do not match";

    @Target({ ElementType.PARAMETER,ElementType.ANNOTATION_TYPE })
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @interface List {
        PasswordMatch[] value();
    }
}
