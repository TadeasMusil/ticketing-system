package tadeas_musil.ticketing_system.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import tadeas_musil.ticketing_system.entity.Department;


@Repository
public interface DepartmentRepository extends JpaRepository<Department, String> {
  
    List<Department> findAllByOrderByNameAsc();
} 