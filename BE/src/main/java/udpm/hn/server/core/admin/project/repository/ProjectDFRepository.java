package udpm.hn.server.core.admin.project.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import udpm.hn.server.core.admin.project.model.response.ADProjectDFResponse;
import udpm.hn.server.repository.DepartmentFacilityRepository;

import java.util.List;

public interface ProjectDFRepository extends DepartmentFacilityRepository {

    @Query(value = """
        select 
            mf.id as id,
            m.name as nameMajor,
            d.name as nameDepartment
        from MajorFacility mf
        join Major m on mf.major.id = m.id
        join DepartmentFacility df on mf.departmentFacility.id = df.id 
        join Department d on df.department.id = d.id
        join Facility f on df.facility.id = f.id
        where f.id = :idFacility
""")
    List<ADProjectDFResponse> getAllDepartmentFacility(@Param("idFacility") String idFacility);

}
