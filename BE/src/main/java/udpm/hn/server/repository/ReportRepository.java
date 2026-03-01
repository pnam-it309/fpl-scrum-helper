package udpm.hn.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import udpm.hn.server.entity.Report;

public interface ReportRepository extends JpaRepository<Report, String> {

    @Query("""
    SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END
    FROM Report r
    WHERE r.reportTime = :reportTime
      AND (
        r.staffProject.staff.emailFe = :email
        OR r.staffProject.staff.emailFpt = :email
      ) and r.project.id = :idProject
""")
    boolean existsByReportTimeAndStaffEmail(@Param("reportTime") Long reportTime, @Param("email") String email,  @Param("idProject") String idProject);

    boolean existsByReportTimeAndStaffProject_Student_Email(Long reportTime, String email);

}
