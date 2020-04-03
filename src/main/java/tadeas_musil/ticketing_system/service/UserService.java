package tadeas_musil.ticketing_system.service;

import java.util.List;
import java.util.UUID;

import javax.mail.MessagingException;

import com.querydsl.core.types.Predicate;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import tadeas_musil.ticketing_system.entity.User;

@Service
public interface UserService {

  User createUser(User user);

  Page<User> getAllByRole(String role, Predicate predicate, Pageable pageable);

  void updateAccountStatus(Long id, boolean isDisabled);

  User getById(Long id);

  void updatePassword(User updatedUser);

  void sendPasswordResetLink(String email) throws MessagingException;
}
