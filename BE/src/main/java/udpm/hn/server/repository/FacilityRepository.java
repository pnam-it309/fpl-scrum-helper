package udpm.hn.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import udpm.hn.server.entity.Facility;

import java.util.Optional;

@Repository
public interface FacilityRepository extends JpaRepository<Facility,String> {

    Optional<Facility> findByName(String nameFacility);

}
