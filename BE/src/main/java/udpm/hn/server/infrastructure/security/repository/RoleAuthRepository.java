package udpm.hn.server.infrastructure.security.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import udpm.hn.server.infrastructure.constant.EntityStatus;
import udpm.hn.server.repository.RoleRepository;

import java.util.List;

public interface RoleAuthRepository extends RoleRepository {
    @Query(
            value = """

                    SELECT DISTINCT
                          r.code
                      FROM
                          Role r
                      LEFT JOIN
                       StaffRole sr ON r.id = sr.role.id
                      WHERE
                          sr.staff.id = :id
                    """
    )
    List<String> findRoleByStaffId(@Param("id") String id);

    @Query("""
    SELECT DISTINCT r.code
    FROM StaffRole sr
    JOIN sr.staff s
    JOIN sr.role r
    JOIN StaffMajorFacility smf ON smf.staff.id = s.id
    JOIN MajorFacility mf ON smf.majorFacility.id = mf.id
    JOIN DepartmentFacility df ON mf.departmentFacility.id = df.id
    WHERE df.facility.id = :facilityId
    AND s.id = :staffId
    AND s.status = :status
""")
    List<String> findAllRoleCodesByFacilityIdAndStaffIdAndStatus(
            @Param("staffId") String staffId,
            @Param("facilityId") String facilityId,
        @Param("status") EntityStatus status
    );

    @Query("""
    SELECT DISTINCT r.code
    FROM StaffRole sr
    JOIN sr.staff s
    JOIN sr.role r
    JOIN StaffMajorFacility smf ON smf.staff.id = s.id
    JOIN MajorFacility mf ON smf.majorFacility.id = mf.id
    JOIN DepartmentFacility df ON mf.departmentFacility.id = df.id
    WHERE df.facility.id = :facilityId
    AND s.id = :staffId
""")
    List<String> findAllRoleCodesByFacilityIdAndStaffId(
            @Param("staffId") String staffId,
            @Param("facilityId") String facilityId
    );

}
