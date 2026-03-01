package udpm.hn.server.core.manage.project.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import udpm.hn.server.core.manage.project.model.response.MaProjectDFResponse;
import udpm.hn.server.repository.DepartmentFacilityRepository;

import java.util.List;

@Repository
public interface MaProjectDFRepository extends DepartmentFacilityRepository {
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
    List<MaProjectDFResponse> getAllDepartmentFacility(@Param("idFacility") String idFacility);

}
