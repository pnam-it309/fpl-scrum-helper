package udpm.hn.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import udpm.hn.server.entity.Resource;

@Repository
public interface ResourceRepository extends JpaRepository<Resource,String> {
}
