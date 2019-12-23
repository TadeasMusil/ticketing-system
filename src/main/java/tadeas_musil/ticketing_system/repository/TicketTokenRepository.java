package tadeas_musil.ticketing_system.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import tadeas_musil.ticketing_system.entity.TicketToken;


@Repository
public interface TicketTokenRepository extends JpaRepository<TicketToken, Long> {
    
    Optional<TicketToken> findByToken(String token);
}