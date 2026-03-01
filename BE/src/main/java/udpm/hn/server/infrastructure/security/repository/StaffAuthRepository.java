package udpm.hn.server.infrastructure.security.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import udpm.hn.server.entity.Staff;
import udpm.hn.server.infrastructure.constant.EntityStatus;
import udpm.hn.server.infrastructure.security.user.StaffFacilityResponse;
import udpm.hn.server.repository.StaffRepository;

import java.util.Optional;

public interface StaffAuthRepository extends StaffRepository {

    Optional<Staff> findByEmailFptAndStatus(String emailFpt, EntityStatus status);

    @Query("""
    SELECT s 
    FROM Staff s
    JOIN StaffMajorFacility smf ON s.id = smf.staff.id
    JOIN MajorFacility mf ON smf.majorFacility.id = mf.id
    JOIN DepartmentFacility df ON mf.departmentFacility.id = df.id
    JOIN Facility f ON df.facility.id = f.id
    WHERE s.emailFpt = :emailFpt
    AND f.id = :facilityId
    AND s.status = :status
    """)
    Optional<Staff> findStaffByEmailFptAndFacility(
            @Param("emailFpt") String emailFpt,
            @Param("facilityId") String facilityId,
            @Param("status") EntityStatus status
    );

    @Query("""
        SELECT 
            f.id AS facilityId
        FROM Staff s
        LEFT JOIN StaffMajorFacility smf ON s.id = smf.staff.id
        LEFT JOIN MajorFacility mf ON smf.majorFacility.id = mf.id
        LEFT JOIN DepartmentFacility df ON mf.departmentFacility.id = df.id
        LEFT JOIN Facility f ON df.facility.id = f.id
        WHERE s.id = :idStaff
    """)
    Optional<StaffFacilityResponse> findStaffWithFacilityById(@Param("idStaff") String idStaff);

    Optional<Staff> findByIdOrEmailFpt(String id, String emailFpt);

}
