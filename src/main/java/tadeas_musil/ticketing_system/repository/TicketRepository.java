package tadeas_musil.ticketing_system.repository;

import java.util.Optional;

import com.querydsl.core.types.dsl.StringExpression;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import tadeas_musil.ticketing_system.entity.Department;
import tadeas_musil.ticketing_system.entity.QTicket;
import tadeas_musil.ticketing_system.entity.Ticket;
import tadeas_musil.ticketing_system.entity.enums.Priority;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long>,QuerydslPredicateExecutor<Ticket>, QuerydslBinderCustomizer<QTicket> {
  
  @Query("SELECT t FROM Ticket t LEFT JOIN FETCH t.events WHERE t.id = ?1")
  Optional<Ticket> findByIdAndFetchEvents(Long id);

  @Transactional
	@Modifying(clearAutomatically = true, flushAutomatically = true)
	@Query("UPDATE Ticket t SET t.priority = ?2 WHERE t.id = ?1")
  void setPriority(Long ticketId, Priority priority);
  
  @Transactional
	@Modifying(clearAutomatically = true, flushAutomatically = true)
	@Query("UPDATE Ticket t SET t.department = ?2 WHERE t.id = ?1")
  void setDepartment(Long ticketId, Department department);
  
  @Transactional
	@Modifying(clearAutomatically = true, flushAutomatically = true)
	@Query("UPDATE Ticket t SET t.owner = ?2 WHERE t.id = ?1")
  void setOwner(Long ticketId, String owner);

  @Transactional
	@Modifying(clearAutomatically = true, flushAutomatically = true)
	@Query("UPDATE Ticket t SET t.isClosed = ?2 WHERE t.id = ?1")
  void setIsClosed(Long ticketId, boolean isClosed);

  @Query("SELECT COUNT(t) FROM  Ticket t WHERE t.owner =?1 AND isClosed = ?2")
  long countByOwnerAndIsClosed(String username, boolean isClosed);

  @Override
  default void customize(QuerydslBindings bindings, QTicket ticket) {
    bindings.including(
      ticket.author,
      ticket.created,
      ticket.department,
      ticket.owner,
      ticket.priority);
    bindings.excludeUnlistedProperties(true);
  }
}