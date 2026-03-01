package udpm.hn.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import udpm.hn.server.entity.PhaseProject;

public interface PhaseProjectRepository extends JpaRepository<PhaseProject,String> {
}
