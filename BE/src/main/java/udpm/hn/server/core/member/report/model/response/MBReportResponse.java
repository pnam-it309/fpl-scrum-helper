package udpm.hn.server.core.member.report.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import udpm.hn.server.core.common.base.IsIdentify;
import udpm.hn.server.infrastructure.constant.StatusReport;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MBReportResponse {

    private String idReport;
    private String wordDoneTodayReport;
    private String obstaclesReport;
    private String wordPlanTomorrowReport;
    private Long reportTime;
    private Long createDate;
    private StatusReport statusReport;
    private String idProject;
    private String sourceType;

    public MBReportResponse(Long reportTime, StatusReport statusReport, String idProject, String sourceType) {
        this.reportTime = reportTime;
        this.statusReport = statusReport;
        this.idProject = idProject;
        this.sourceType = sourceType;
    }
}