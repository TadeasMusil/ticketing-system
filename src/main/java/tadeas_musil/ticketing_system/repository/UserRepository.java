package tadeas_musil.ticketing_system.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import tadeas_musil.ticketing_system.entity.User;

@Repository
public interface UserRepository
    extends JpaRepository<User, Long>, QuerydslPredicateExecutor<User> {

  Optional<User> findByUsername(String username);

  boolean existsByUsername(String username);

  @Query("SELECT u from User u JOIN u.roles r WHERE r.name IN :roles ORDER BY u.username")
  List<User> findAllByRoleInAndOrderByUsername(String... roles);

  @Query("SELECT u from User u WHERE u.username LIKE CONCAT('%', :query, '%')"
      + "OR u.firstName LIKE CONCAT('%', :query, '%')" + "OR u.lastName LIKE CONCAT('%', :query, '%')")
  List<User> findByQuery(String query);

  @Transactional
	@Modifying(clearAutomatically = true, flushAutomatically = true)
	@Query("UPDATE User u SET u.isDisabled = ?2 WHERE u.id = ?1")
  void setIsDisabled(Long ticketId, boolean isDisabled);

  @Query("SELECT u FROM User u JOIN FETCH u.roles LEFT JOIN FETCH u.departments d WHERE u.id = :id")
  Optional<User> findByIdAndFetchRolesAndDepartments(Long id);
}