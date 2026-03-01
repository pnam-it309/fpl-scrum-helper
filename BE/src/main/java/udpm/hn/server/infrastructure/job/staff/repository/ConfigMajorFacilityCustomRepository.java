package udpm.hn.server.infrastructure.job.staff.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import udpm.hn.server.entity.MajorFacility;
import udpm.hn.server.repository.MajorFacilityRepository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConfigMajorFacilityCustomRepository extends MajorFacilityRepository {

    @Query("""
                    SELECT DISTINCT mf
                    FROM MajorFacility mf
                    WHERE mf.departmentFacility.department.name = :departmentName
                    AND mf.departmentFacility.facility.name = :facilityName
                    AND mf.major.name = :majorName
            """)
    List<MajorFacility> getMajorFacilities(String departmentName, String majorName, String facilityName);

    Optional<MajorFacility> findByDepartmentFacility_Facility_Code(String facilityCode);

}
