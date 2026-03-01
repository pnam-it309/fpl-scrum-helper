package udpm.hn.server.infrastructure.job.staff.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import udpm.hn.server.entity.StaffMajorFacility;
import udpm.hn.server.repository.StaffMajorFacilityRepository;

import java.util.List;

@Repository
public interface ConfigStaffMajorFacilityRepository extends StaffMajorFacilityRepository {

@Query(value = """
    SELECT smf
    FROM StaffMajorFacility smf
    WHERE smf.staff.id = :staffId
    AND smf.majorFacility.departmentFacility.facility.code = :facilityCode
    """)
    List<StaffMajorFacility> findAllByStaffAndFacility(String staffId, String facilityCode);

}
