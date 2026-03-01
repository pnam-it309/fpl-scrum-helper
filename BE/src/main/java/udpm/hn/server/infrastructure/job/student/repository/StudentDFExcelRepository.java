package udpm.hn.server.infrastructure.job.student.repository;

import udpm.hn.server.entity.DepartmentFacility;
import udpm.hn.server.repository.DepartmentFacilityRepository;

import java.util.Optional;

public interface StudentDFExcelRepository extends DepartmentFacilityRepository {

        Optional<DepartmentFacility> findDepartmentFacilityByFacility_IdAndDepartment_Id(String idFacility, String idDepartment);


}
