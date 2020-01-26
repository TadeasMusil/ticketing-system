package tadeas_musil.ticketing_system.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import tadeas_musil.ticketing_system.entity.TicketEvent;

@Repository
public interface TicketEventRepository extends JpaRepository<TicketEvent, Long> {
}