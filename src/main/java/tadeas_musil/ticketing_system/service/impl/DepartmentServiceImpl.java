package tadeas_musil.ticketing_system.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import tadeas_musil.ticketing_system.entity.Department;
import tadeas_musil.ticketing_system.entity.User;
import tadeas_musil.ticketing_system.repository.DepartmentRepository;
import tadeas_musil.ticketing_system.repository.UserRepository;
import tadeas_musil.ticketing_system.service.DepartmentService;
import tadeas_musil.ticketing_system.service.UserService;

@Service
@RequiredArgsConstructor
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository departmentRepository;

    private final UserRepository userRepository;

    @Override
    public List<Department> getAllDepartments() {
        return departmentRepository.findAllByOrderByNameAsc();
    }

    @Override
    public List<Department> getDepartmentsByUsername(String username) {
       User user = userRepository.findByUsername(username)
                                .orElseThrow(() -> new UsernameNotFoundException(username));
        return user.getDepartments();
    }

}
