package udpm.hn.server.infrastructure.security.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import udpm.hn.server.entity.Student;
import udpm.hn.server.infrastructure.constant.EntityStatus;
import udpm.hn.server.infrastructure.security.user.StudentFacilityResponse;

import java.util.Optional;

public interface StudentAuthRepository extends JpaRepository<Student ,String> {

    @Query("SELECT s FROM Student s " +
            "JOIN s.majorFacility mf " +
            "JOIN mf.departmentFacility df " +
            "JOIN df.facility f " +
            "WHERE s.email = :email " +
            "AND f.id = :facilityId " +
            "AND s.status = :status")
    Optional<Student> findStudentsByEmailAndFacility(
            @Param("email") String email,
            @Param("facilityId") String facilityId,
            @Param("status") EntityStatus status);


    Optional<Student> findByEmail(String email);

    @Query("""
            SELECT df.facility.id AS facilityId FROM Student s 
            JOIN s.majorFacility mf 
            JOIN mf.departmentFacility df 
            WHERE s.id = :idStudent
            """)
    Optional<StudentFacilityResponse> findFacilityIdByStudentId(@Param("idStudent") String idStudent);

    Optional<Student> findByEmailAndStatus(String email, EntityStatus status);
}
