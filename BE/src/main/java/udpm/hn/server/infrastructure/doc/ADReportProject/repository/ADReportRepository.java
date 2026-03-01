package udpm.hn.server.infrastructure.doc.ADReportProject.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import udpm.hn.server.infrastructure.doc.ADReportProject.model.response.PersonalReportResponse;
import udpm.hn.server.infrastructure.doc.ADReportProject.model.response.ReportProjection;
import udpm.hn.server.repository.ReportRepository;

import java.util.List;

public interface ADReportRepository extends ReportRepository {

    @Query("""
    SELECT p.name AS projectName,
           CASE WHEN st.name IS NOT NULL THEN st.name ELSE su.name END AS staffOrStudentName,
           r.obstacles AS obstacles,
           r.workDoneToday AS workDoneToday
    FROM Report r
    INNER JOIN r.project p
    JOIN r.staffProject sp
    LEFT JOIN sp.staff st
    LEFT JOIN sp.student su
    WHERE p.id = :projectId AND sp.id = :staffProjectId
""")
    List<ReportProjection> findReportsByProjectIdAndStaffProjectId(
            @Param("projectId") String projectId,
            @Param("staffProjectId") String staffProjectId
    );


    @Query("SELECT r.workDoneToday AS workDoneToday, r.obstacles AS obstacles, r.workPlanTomorrow AS workPlanTomorrow, " +
            "r.statusReport AS statusReport, r.reportTime AS reportTime " +
            "FROM Report r " +
            "WHERE r.staffProject.id = :staffProjectId " +
            "ORDER BY r.reportTime ASC")
    List<PersonalReportResponse> findAllReportsByStaffProjectId(@Param("staffProjectId") String staffProjectId);


    @Query("""
    SELECT DISTINCT
        p.name AS projectName,
        CASE WHEN st.name IS NOT NULL THEN st.name ELSE su.name END AS staffOrStudentName,
        CASE WHEN st.name IS NOT NULL THEN st.emailFpt ELSE su.email END AS staffOrStudentEmail
    FROM StaffProject sp
    INNER JOIN sp.project p
    LEFT JOIN sp.staff st
    LEFT JOIN sp.student su
    WHERE p.id = :projectId AND sp.id = :staffProjectId
""")
    List<ReportProjection> findMembersByProjectIdAndStaffProjectId(
            @Param("projectId") String projectId,
            @Param("staffProjectId") String staffProjectId
    );

}
