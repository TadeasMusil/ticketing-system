package tadeas_musil.ticketing_system.repository;

import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import tadeas_musil.ticketing_system.entity.Department;
import tadeas_musil.ticketing_system.entity.TicketEvent;
import tadeas_musil.ticketing_system.entity.enums.TicketEventType;

@Repository
public interface TicketEventRepository extends JpaRepository<TicketEvent, Long> {
    
    Page<TicketEvent> findByAuthorOrderByCreatedDesc(String author, Pageable pageable);

    @Query("SELECT e FROM TicketEvent e JOIN e.ticket t WHERE t.department in :departments ORDER BY e.created DESC")
    Page<TicketEvent> findByDepartmentsOrderByCreatedDesc(Set<Department> departments, Pageable pageable);

    @Query("SELECT COUNT(e) FROM  TicketEvent e WHERE e.author =?1 AND e.type = ?2")
    long countByAuthorAndType(String username, TicketEventType type);
}