package tadeas_musil.ticketing_system.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import tadeas_musil.ticketing_system.entity.Role;

@DataJpaTest
public class RoleRepositoryTest {

    @Autowired
    private RoleRepository roleRepository;

    @Test
    public void findByName_shouldFindRole_givenExistingRole() {
        Role userRole = new Role();
        userRole.setName("USER");
        roleRepository.save(userRole);

        Role role = roleRepository.findByName("USER");

        assertEquals(userRole, role);
    }

}
