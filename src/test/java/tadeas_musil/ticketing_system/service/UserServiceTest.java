package tadeas_musil.ticketing_system.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import tadeas_musil.ticketing_system.entity.Role;
import tadeas_musil.ticketing_system.entity.User;
import tadeas_musil.ticketing_system.repository.RoleRepository;
import tadeas_musil.ticketing_system.repository.UserRepository;
import tadeas_musil.ticketing_system.service.impl.UserServiceImpl;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
  @InjectMocks
  private UserService userService = new UserServiceImpl();

  @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

  @Test
  public void createUser_shouldReturnCorrectUser() throws Exception {
    User user = new User();
    user.setFirstName("firstName");
    user.setLastName("lastName");
    user.setPassword("password");
    user.setUsername("name@email.com");
    
    when(passwordEncoder.encode(anyString())).thenReturn("encryptedPassword");
    when(roleRepository.findByName(anyString())).thenReturn(new Role());
    when(userRepository.save(any(User.class))).then(returnsFirstArg());
    
    User createdUser = userService.createUser(user);

    assertThat(createdUser).hasFieldOrPropertyWithValue("username", "name@email.com")
                    .hasFieldOrPropertyWithValue("firstName", "firstName")
                    .hasFieldOrPropertyWithValue("lastName", "lastName")
                    .hasFieldOrPropertyWithValue("password", "encryptedPassword");
    assertThat(createdUser.getRoles()).hasSize(1);
  }

}