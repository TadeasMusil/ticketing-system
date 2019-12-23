package tadeas_musil.ticketing_system.service;

import java.util.List;

import org.springframework.stereotype.Service;

import tadeas_musil.ticketing_system.entity.Department;
import tadeas_musil.ticketing_system.entity.TicketCategory;

@Service
public interface DepartmentService {

    List<Department> getAllDepartments();

    List<Department> getDepartmentsByUsername(String username);

}