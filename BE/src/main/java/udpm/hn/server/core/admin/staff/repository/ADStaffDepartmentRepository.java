package udpm.hn.server.core.admin.staff.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import udpm.hn.server.core.admin.staff.model.response.ADStaffByDepartmentFacility;
import udpm.hn.server.repository.DepartmentRepository;

import java.util.List;

public interface ADStaffDepartmentRepository extends DepartmentRepository {

    @Query(value =
            """
                select
                    d.id as idDepartment,
                    d.name as nameDepartment,
                    d.status as status
                from
                    Department d
                join DepartmentFacility df on
                    d.id = df.department.id
                join Facility f on
                    df.facility.id = f.id
                where
                    f.id = :idFacility   
                   
            """)
    List<ADStaffByDepartmentFacility> getAllDepartmentByFacility(@Param("idFacility")String  idFacility);

}