package tadeas_musil.ticketing_system.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import tadeas_musil.ticketing_system.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
  
  Optional<User> findByUsername(String username);

  boolean existsByUsername(String username);

  @Query("SELECT u from User u JOIN u.roles r WHERE r.name IN :roles ORDER BY u.username")
  List<User> findAllByRoleInAndOrderByUsername(String... roles);
}