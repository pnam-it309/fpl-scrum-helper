package udpm.hn.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import udpm.hn.server.entity.Activity;

@Repository
public interface ActivityRepository extends JpaRepository<Activity, String> {
}
