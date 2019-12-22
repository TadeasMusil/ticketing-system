package tadeas_musil.ticketing_system.repository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import tadeas_musil.ticketing_system.entity.Department;

@DataJpaTest
public class DepartmentRepositoryTest {

    @Autowired
    private DepartmentRepository departmentRepository;

    @Test
    public void findAllByOrderByNameAsc_shouldReturnDepartmentsInCorrectOrder() {
        Department firstDepartment = new Department();
        firstDepartment.setName("first");
        Department secondDepartment = new Department();
        secondDepartment.setName("second");
        departmentRepository.save(firstDepartment);
        departmentRepository.save(secondDepartment);

        List<Department> departments = departmentRepository.findAllByOrderByNameAsc();

        assertThat(departments).hasSize(2)
                                .first().hasFieldOrPropertyWithValue("name", "first");
    }

}
