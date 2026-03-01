package udpm.hn.server.core.admin.staff.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import udpm.hn.server.entity.Department;
import udpm.hn.server.entity.DepartmentFacility;
import udpm.hn.server.entity.Facility;
import udpm.hn.server.repository.DepartmentFacilityRepository;

import java.util.Optional;

public interface ADStaffDepartmentFacilityRepository extends DepartmentFacilityRepository {

    Optional<DepartmentFacility> findByDepartmentAndFacility(Department department, Facility facility);


    @Query(value = """
            select
                df
            from
                DepartmentFacility df
            where
                df.department.id = :idDepartment
                and df.facility.id = :idFacility
            """)
    Optional<DepartmentFacility> findByDepartmentAndFacilityAndStaff(@Param("idDepartment") String idDepartment, @Param("idFacility") String idFacility);

}