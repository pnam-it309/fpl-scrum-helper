package udpm.hn.server.infrastructure.job.student.repository;

import udpm.hn.server.entity.DepartmentFacility;
import udpm.hn.server.entity.Major;
import udpm.hn.server.entity.MajorFacility;
import udpm.hn.server.repository.MajorFacilityRepository;

import java.util.Optional;

public interface StudentMajorFacilityExcelRepository extends MajorFacilityRepository {

    Optional<MajorFacility> findByMajorAndDepartmentFacility(Major major, DepartmentFacility departmentFacility);



}
