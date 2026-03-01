package udpm.hn.server.infrastructure.job.MBReport.Export.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import udpm.hn.server.entity.Project;
import udpm.hn.server.entity.Report;
import udpm.hn.server.infrastructure.job.MBReport.Export.model.request.ReportData;

import java.util.List;

@Repository
public interface ExportReportDocxRepository extends JpaRepository<Report, String> {

    @Query("""
        select new udpm.hn.server.infrastructure.job.MBReport.Export.model.request.ReportData(
            r.workDoneToday,
        	r.obstacles,
        	r.workPlanTomorrow,
        	r.reportTime)
        from Report r
        JOIN StaffProject sp on r.staffProject.id = sp.id
        JOIN Project p ON p.id = sp.project.id
        where (sp.staff.id = :userId OR sp.student.id = :userId)
        and p.id = :idProject
       """
    )
    List<ReportData> exportDocx(@Param("userId") String userId, @Param("idProject") String idProject);

    @Query("SELECT p FROM Project p WHERE p.id = :id")
    Project getProject(@Param("id") String id);

}
