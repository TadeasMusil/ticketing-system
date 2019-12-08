package tadeas_musil.ticketing_system.validation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import tadeas_musil.ticketing_system.entity.User;
import tadeas_musil.ticketing_system.entity.User.Registration;
import tadeas_musil.ticketing_system.repository.UserRepository;

@SpringBootTest
public class PasswordMatchValidatorTest {

    @Autowired
    private Validator validator;

    @MockBean
    private UserRepository userRepository;

    private User getUserPassingAllValidations(){
        User user = new User();
        user.setUsername("name@email.com");
        user.setFirstName("firstName");
        user.setLastName("lastName");
        user.setPassword("password");
        user.setPasswordConfirmation("password");
        return user;
    }

    @Test
    public void registrationValidation_shouldReturnNoViolation_givenNewUser() {
        User user = getUserPassingAllValidations();
        when(userRepository.existsByUsername(anyString())).thenReturn(false);

        Set<ConstraintViolation<User>> violations = validator.validate(user, Registration.class);

        assertThat(violations).isEmpty();
    }

    @Test
    public void registrationValidation_shouldReturnViolation_givenExistingUser() {
        User user = getUserPassingAllValidations();
        when(userRepository.existsByUsername(anyString())).thenReturn(true);

        Set<ConstraintViolation<User>> violations = validator.validate(user, Registration.class);

        assertThat(violations).hasSize(1)
                                .first().extracting("propertyPath")
                                        .toString()
                                        .equals("username"); 
    }

    @Test
    public void registrationValidation_shouldReturnViolation_givenWrongUsernameFormat() {
        User user = getUserPassingAllValidations();
        user.setUsername("notAnEmail");

        Set<ConstraintViolation<User>> violations = validator.validate(user, Registration.class);

        assertThat(violations).hasSize(1)
                                .first().extracting("propertyPath")
                                        .toString()
                                        .equals("username"); 
    }

    @Test
    public void registrationValidation_shouldReturnViolation_givenEmptyFirstName() {
        User user = getUserPassingAllValidations();
        user.setFirstName("  ");

        Set<ConstraintViolation<User>> violations = validator.validate(user, Registration.class);

        assertThat(violations).hasSize(1)
                                .first().extracting("propertyPath")
                                        .toString()
                                        .equals("firstName"); 
    }

    @Test
    public void registrationValidation_shouldReturnViolation_givenEmptyLastName() {
        User user = getUserPassingAllValidations();
        user.setLastName("  ");

        Set<ConstraintViolation<User>> violations = validator.validate(user, Registration.class);

        assertThat(violations).hasSize(1)
                                .first().extracting("propertyPath")
                                        .toString()
                                        .equals("lastName"); 
    }

    @Test
    public void registrationValidation_shouldReturnViolation_givenNoPassword() {
        User user = getUserPassingAllValidations();
        user.setPassword(null);
        user.setPasswordConfirmation(null);
        Set<ConstraintViolation<User>> violations = validator.validate(user, Registration.class);

        assertThat(violations).hasSize(1)
                                .first().extracting("propertyPath")
                                        .toString()
                                        .equals("password"); 
    }

    @Test
    public void registrationValidation_shouldReturnViolation_givenShortPassword() {
        User user = getUserPassingAllValidations();
        user.setPassword("short");
        user.setPasswordConfirmation("short");
        
        Set<ConstraintViolation<User>> violations = validator.validate(user, Registration.class);

        assertThat(violations).hasSize(1)
                                .first().extracting("propertyPath")
                                        .toString()
                                        .equals("password"); 
    }

    @Test
    public void registrationValidation_shouldReturnViolation_givenDifferentPasswords() {
        User user = getUserPassingAllValidations();
        user.setPasswordConfirmation("differentPassword");
        
        Set<ConstraintViolation<User>> violations = validator.validate(user, Registration.class);

        assertThat(violations).hasSize(1)
                                .first().extracting("propertyPath")
                                        .toString()
                                        .equals("password"); 
    }
}