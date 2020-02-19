package tadeas_musil.ticketing_system.service.impl;

import java.util.List;
import java.util.NoSuchElementException;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import tadeas_musil.ticketing_system.entity.QUser;
import tadeas_musil.ticketing_system.entity.User;
import tadeas_musil.ticketing_system.repository.RoleRepository;
import tadeas_musil.ticketing_system.repository.UserRepository;
import tadeas_musil.ticketing_system.service.UserService;

@Service("userServiceImpl")
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Override
    public User createUser(User user) {
        user.getRoles().add(roleRepository.findByName("USER"));
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    @Override
    public List<User> getAllStaffMembers() {
        return userRepository.findAllByRoleInAndOrderByUsername("ADMIN", "STAFF");
    }

    @Override
    public Page<User> getAll(String searchQuery, Predicate predicate, int page) {
        Pageable pageable = PageRequest.of(page, 8, Sort.by("username").ascending());
        BooleanBuilder builder = new BooleanBuilder(predicate);
        if (searchQuery != null) {
            builder.and(QUser.user.username.likeIgnoreCase("%" + searchQuery + "%")
                    .or(QUser.user.firstName.likeIgnoreCase("%" + searchQuery + "%"))
                    .or(QUser.user.lastName.likeIgnoreCase("%" + searchQuery + "%")));
        }
        return userRepository.findAll(builder, pageable);
    }

    @Override
    public void updateAccountStatus(Long id, boolean isDisabled) {
        if (userRepository.existsById(id)) {
            userRepository.setIsDisabled(id, isDisabled);
        } else {
            throw new IllegalArgumentException("User with ID: " + id + " does not exist.");
        }
    }
   
    @Override
    public User getById(Long id) {
        return userRepository.findByIdAndFetchRoles(id)
                .orElseThrow(() -> new NoSuchElementException("User with ID: " + id + " doesn't exist"));
    }
}
