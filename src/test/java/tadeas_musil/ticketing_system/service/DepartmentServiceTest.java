package tadeas_musil.ticketing_system.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import tadeas_musil.ticketing_system.entity.Department;
import tadeas_musil.ticketing_system.entity.User;
import tadeas_musil.ticketing_system.repository.DepartmentRepository;
import tadeas_musil.ticketing_system.repository.UserRepository;
import tadeas_musil.ticketing_system.service.impl.DepartmentServiceImpl;

@ExtendWith(MockitoExtension.class)
public class DepartmentServiceTest {

  @Mock
  private DepartmentRepository departmentRepository;

  @Mock
  private UserRepository userRepository;

  private DepartmentService departmentService;

  @BeforeEach
  private void setUp() {
    departmentService = new DepartmentServiceImpl(departmentRepository, userRepository);
  }

  @Test
  public void getDepartmentsByUsername_shouldThrowException_givenNonExistingUser() {
    when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());

    assertThrows(UsernameNotFoundException.class,
        () -> departmentService.getDepartmentsByUsername("nonExistentUsername"));
  }

  @Test 
  public void getDepartmentsByUsername_shouldShouldReturnDepartment_givenExistingUser() {
    User user = new User();
    Department department = new Department();
    department.setName("departmentName");
    user.setDepartments(Set.of(department));
    
    when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));

    Set<Department> departments = departmentService.getDepartmentsByUsername("user");
    
    assertThat(departments).hasSize(1)
                          .first().hasFieldOrPropertyWithValue("name", "departmentName");
  }
}