package udpm.hn.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import udpm.hn.server.entity.MajorFacility;

public interface MajorFacilityRepository extends JpaRepository<MajorFacility,String> {
}
