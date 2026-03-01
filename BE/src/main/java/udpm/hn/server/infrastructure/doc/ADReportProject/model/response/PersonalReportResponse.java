package udpm.hn.server.infrastructure.doc.ADReportProject.model.response;

import udpm.hn.server.infrastructure.constant.StatusReport;

public interface PersonalReportResponse {
    String getWorkDoneToday();
    String getObstacles();
    String getWorkPlanTomorrow();
    StatusReport getStatusReport();
    String getReportTime();
}
