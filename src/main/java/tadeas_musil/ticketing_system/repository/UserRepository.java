package tadeas_musil.ticketing_system.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import tadeas_musil.ticketing_system.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
  
  User findByUsername(String username);

  boolean existsByUsername(String username);
  
}