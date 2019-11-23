package tadeas_musil.ticketing_system.repository;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import tadeas_musil.ticketing_system.entity.User;

@DataJpaTest
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    public void existsByUsername_shouldReturnTrue_givenExistingUser() {
        User user = new User();
        user.setUsername("name");
        userRepository.save(user);

        boolean userExists = userRepository.existsByUsername("name");

        assertTrue(userExists);
    }

    @Test
    public void existsByUsername_shouldReturnFalse_givenNonExistingUser() {

        boolean userExists = userRepository.existsByUsername("name");

        assertFalse(userExists);
    }

}
