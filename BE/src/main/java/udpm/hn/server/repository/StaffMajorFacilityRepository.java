package udpm.hn.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import udpm.hn.server.entity.MajorFacility;
import udpm.hn.server.entity.Staff;
import udpm.hn.server.entity.StaffMajorFacility;

import java.util.Optional;

public interface StaffMajorFacilityRepository extends JpaRepository<StaffMajorFacility,String> {

    Optional<StaffMajorFacility> findByStaffAndMajorFacility(Staff staff, MajorFacility majorFacility);


}
