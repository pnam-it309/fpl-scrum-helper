package udpm.hn.server.core.member.report.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import udpm.hn.server.core.member.report.model.response.MBReportSettingReponse;
import udpm.hn.server.entity.ReportSetting;
import udpm.hn.server.repository.ReportSettingRepository;

import java.util.Optional;

public interface MBReportSetting extends ReportSettingRepository {

    @Query("SELECT r.stopReportHour AS stopReportHour FROM ReportSetting r WHERE r.project.id = :projectId")
    Optional<MBReportSettingReponse> getSettingByProjectId(@Param("projectId") String projectId);

    Optional<ReportSetting> findByProject_Id(String projectId);

    @Query("SELECT r.stopReportHour AS stopReportHour FROM ReportSetting r WHERE r.project.id = :projectId")
    Long getStopReportHour(@Param("projectId") String projectId);}
