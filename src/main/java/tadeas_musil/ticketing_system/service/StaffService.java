package tadeas_musil.ticketing_system.service;

import java.util.List;

import org.springframework.stereotype.Service;

import tadeas_musil.ticketing_system.entity.Department;
import tadeas_musil.ticketing_system.entity.User;

@Service
public interface StaffService {

    List<User> getAllStaffMembers();

    void addToDepartment(Long id, Department department);

    void removeFromDepartment(Long id, Department department);

    User updateStaffMember(User staffMember);
}