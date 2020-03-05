package tadeas_musil.ticketing_system.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import lombok.RequiredArgsConstructor;
import tadeas_musil.ticketing_system.entity.Department;
import tadeas_musil.ticketing_system.entity.User;
import tadeas_musil.ticketing_system.repository.DepartmentRepository;
import tadeas_musil.ticketing_system.repository.UserRepository;
import tadeas_musil.ticketing_system.service.StaffService;
import tadeas_musil.ticketing_system.service.UserService;

@Service
@RequiredArgsConstructor
public class StaffServiceImpl implements StaffService {

    private final UserRepository userRepository;

    private final UserService userService;

    private final DepartmentRepository departmentRepository;

    @Override
    public List<User> getAllStaffMembers() {
        return userRepository.findAllByRoleInAndOrderByUsername("ADMIN", "STAFF");
    }
   
    @Transactional
    @Override
    public void addToDepartment(Long userId, Department department) {
        Assert.isTrue(departmentRepository.existsById(department.getName()), "Department does not exist: " + department.getName());
        User staffMember = userService.getById(userId);
        staffMember.getDepartments().add(department);
        userRepository.save(staffMember);
    }
    
    @Transactional
    @Override
    public void removeFromDepartment(Long userId, Department department) {
        User staffMember = userService.getById(userId);
        staffMember.getDepartments().remove(department);
        userRepository.save(staffMember);
    }

    @Override
    @Transactional
    public User updateStaffMember(User staffMember) {
        User user = userRepository.getOne(staffMember.getId());
        user.setFirstName(staffMember.getFirstName());
        user.setLastName(staffMember.getLastName());
        return userRepository.save(user);
    }
}
