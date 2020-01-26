package tadeas_musil.ticketing_system.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import tadeas_musil.ticketing_system.entity.Role;
import tadeas_musil.ticketing_system.entity.User;

@DataJpaTest
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Test
    public void existsByUsername_shouldReturnTrue_givenExistingUser() {
        saveUser("username", null);

        boolean userExists = userRepository.existsByUsername("username");

        assertTrue(userExists);
    }

    private User saveUser(String username, Role role) {
        User user = new User();
        user.setUsername(username);
        user.getRoles().add(role);
        return userRepository.save(user);
    }

    @Test
    public void existsByUsername_shouldReturnFalse_givenNonExistingUser() {

        boolean userExists = userRepository.existsByUsername("username");

        assertFalse(userExists);
    }

    @Test
    public void findAllByRoleOrderByLastName_shouldReturnAllStaffMembersInCorrectOrder() {
        Role userRole = roleRepository.save(new Role("USER"));
        saveUser("user@email.com", userRole);

        Role staffRole = roleRepository.save(new Role("STAFF"));
        User firstStaffUser = saveUser("firstStaff@email.com", staffRole);
        User secondStaffUser = saveUser("secondStaff@email.com", staffRole);

        Role adminRole = roleRepository.save(new Role("ADMIN"));
        User firstAdminUser = saveUser("firstAdmin@email.com", adminRole);
        User secondAdminUser = saveUser("secondAdmin@email.com", adminRole);

        List<User> staffMembers = userRepository.findAllByRoleInAndOrderByUsername("STAFF", "ADMIN");

        assertThat(staffMembers).hasSize(4).startsWith(firstAdminUser, firstStaffUser, secondAdminUser,
                secondStaffUser);

    }
}
