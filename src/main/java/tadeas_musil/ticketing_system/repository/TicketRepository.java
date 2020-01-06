package tadeas_musil.ticketing_system.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import tadeas_musil.ticketing_system.entity.Ticket;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
  
  @Query("SELECT t FROM Ticket t LEFT JOIN FETCH t.events WHERE t.id = ?1")
  Optional<Ticket> findByIdAndFetchEvents(Long id);
}