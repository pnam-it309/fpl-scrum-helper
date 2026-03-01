package udpm.hn.server.core.admin.student.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import udpm.hn.server.core.admin.student.model.response.ADStudentByDepartmentFacilityResponse;
import udpm.hn.server.entity.Facility;
import udpm.hn.server.repository.FacilityRepository;

import java.util.Optional;

@Repository
public interface ADStudentByDeparmentFacilityRepository extends FacilityRepository {

    @Query(value = """
            select
            df.id as id,
            f.id as idFacility,
            f.name as nameFacility,
            d.id as idDepartment,
            d.name as nameDepartment,
            m.id as idMajor,
            m.name as nameMajor
            from Student s
            join MajorFacility mf on s.majorFacility.id = mf.id
            join DepartmentFacility df on mf.departmentFacility.id = df.id
            join Facility f on df.facility.id = f.id
            join Department d on df.department.id = d.id
            join Major m on mf.major.id = m.id
            where s.id = :studentId
            """)
    Optional<ADStudentByDepartmentFacilityResponse> studentByDepartmentFacility(@Param("studentId") String studentId);

    Optional<Facility> findById(String id);
}
