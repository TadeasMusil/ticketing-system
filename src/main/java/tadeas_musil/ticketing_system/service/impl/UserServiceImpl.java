package tadeas_musil.ticketing_system.service.impl;

import java.util.List;
import java.util.NoSuchElementException;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import tadeas_musil.ticketing_system.entity.QUser;
import tadeas_musil.ticketing_system.entity.User;
import tadeas_musil.ticketing_system.repository.RoleRepository;
import tadeas_musil.ticketing_system.repository.UserRepository;
import tadeas_musil.ticketing_system.service.UserService;

@Service
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

    @Transactional
    @Override
    public Page<User> getAllByRole(String role, Predicate predicate, Pageable pageable) {
        BooleanBuilder builder = new BooleanBuilder(predicate);
        builder.and(QUser.user.roles.any().name.equalsIgnoreCase(role));

        Page<User> page = userRepository.findAll(builder, pageable);
        if (!page.getContent().isEmpty()) {
            Hibernate.initialize(page.getContent().get(0).getDepartments());
        }
        return page;
    }

    @Override
    public void updateAccountStatus(Long id, boolean isDisabled) {
        Assert.isTrue(userRepository.existsById(id), "User with ID: " + id + " does not exist.");
        userRepository.setIsDisabled(id, isDisabled);
    }

    @Override
    public User getById(Long id) {
        return userRepository.findByIdAndFetchRolesAndDepartments(id)
                .orElseThrow(() -> new NoSuchElementException("User with ID: " + id + " doesn't exist"));
    }
}
