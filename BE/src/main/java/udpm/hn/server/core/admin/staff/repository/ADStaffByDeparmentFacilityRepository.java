package udpm.hn.server.core.admin.staff.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import udpm.hn.server.core.admin.staff.model.response.ADStaffByDepartmentFacility;
import udpm.hn.server.repository.FacilityRepository;

import java.util.Optional;

public interface ADStaffByDeparmentFacilityRepository extends FacilityRepository {

    @Query(value =
            """
            select
                smf.id as id,
                f.id as idFacility,
                f.name as nameFacility,
                d.id as idDepartment,
                d.name as nameDepartment,
                m.id as idMajor,
                m.name as nameMajor
            from
                Facility f
            left join DepartmentFacility df on
                f.id = df.facility.id
            left  join Department d on
                df.department.id = d.id
            left join MajorFacility mf on
                df.id = mf.departmentFacility.id
            left join Major m on
                mf.major.id = m.id
            left join StaffMajorFacility smf on
                mf.id = smf.majorFacility.id
            where smf.staff.id = :staffId
            """)
    Optional<ADStaffByDepartmentFacility> staffByDepartmentFacility(@Param("staffId") String staffId);

}