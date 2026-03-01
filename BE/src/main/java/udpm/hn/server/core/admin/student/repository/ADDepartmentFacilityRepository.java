package udpm.hn.server.core.admin.student.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import udpm.hn.server.entity.Department;
import udpm.hn.server.entity.DepartmentFacility;
import udpm.hn.server.entity.Facility;
import udpm.hn.server.repository.DepartmentFacilityRepository;

import java.util.Optional;

public interface ADDepartmentFacilityRepository extends DepartmentFacilityRepository {

    Optional<DepartmentFacility> findByDepartmentAndFacility(Department department, Facility facility);

    @Query("""
        SELECT df 
        FROM DepartmentFacility df
        WHERE df.department.id = :idDepartment 
        AND df.facility.id = :idFacility
       """)
    Optional<DepartmentFacility> findByDepartmentAndFacilityAndStudent(@Param("idDepartment") String idDepartment,
                                                             @Param("idFacility") String idFacility);

}
