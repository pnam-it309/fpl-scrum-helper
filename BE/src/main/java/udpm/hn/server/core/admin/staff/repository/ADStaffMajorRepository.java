package udpm.hn.server.core.admin.staff.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import udpm.hn.server.core.admin.staff.model.response.ADStaffByDepartmentFacility;
import udpm.hn.server.repository.MajorRepository;

import java.util.List;

public interface ADStaffMajorRepository extends MajorRepository {

    @Query(value = """
        select distinct
            m.id as idMajor,
            m.name as nameMajor,
            m.status as status
        from
            Major m
        join MajorFacility mf on m.id= mf.major.id
        join DepartmentFacility df on mf.departmentFacility.id = df.id
        join Department d on df.department.id = d.id
        where d.id = :idDepartment
  
        """)
    List<ADStaffByDepartmentFacility> getMajorByDepartment(@Param("idDepartment") String idDepartment);

}