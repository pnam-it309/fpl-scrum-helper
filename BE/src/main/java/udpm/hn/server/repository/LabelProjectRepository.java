package udpm.hn.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import udpm.hn.server.entity.LabelProject;

public interface LabelProjectRepository  extends JpaRepository<LabelProject, String> {
}
