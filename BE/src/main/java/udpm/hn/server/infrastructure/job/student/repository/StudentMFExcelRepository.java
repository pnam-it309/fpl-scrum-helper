package udpm.hn.server.infrastructure.job.student.repository;

import udpm.hn.server.entity.MajorFacility;
import udpm.hn.server.repository.MajorFacilityRepository;

import java.util.Optional;

public interface StudentMFExcelRepository extends MajorFacilityRepository {
    Optional<MajorFacility> findByMajor_IdAndDepartmentFacility_Id(String idMajor, String idDepartmentFacility);

}
