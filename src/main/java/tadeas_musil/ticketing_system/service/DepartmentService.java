package tadeas_musil.ticketing_system.service;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import tadeas_musil.ticketing_system.entity.Department;

@Service
public interface DepartmentService {

    List<Department> getAllDepartments();

    Set<Department> getDepartmentsByUsername(String username);

}