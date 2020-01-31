package tadeas_musil.ticketing_system.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import tadeas_musil.ticketing_system.entity.CannedResponse;

@Repository
public interface CannedResponseRepository extends JpaRepository<CannedResponse, Long> {
  
    List<CannedResponse> findAllByOrderByNameAsc();

	Optional<CannedResponse> findByName(String name);

	boolean existsByName(String name);
} 