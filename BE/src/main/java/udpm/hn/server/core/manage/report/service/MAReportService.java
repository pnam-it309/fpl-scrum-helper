package udpm.hn.server.core.manage.report.service;

import jakarta.servlet.http.HttpServletResponse;
import udpm.hn.server.core.common.base.ResponseObject;
import udpm.hn.server.core.manage.report.model.request.HolidayCreateRequest;
import udpm.hn.server.core.manage.report.model.request.MAReportRequest;
import udpm.hn.server.core.manage.report.model.request.MAReportUserRequest;

import java.io.FileNotFoundException;
import java.util.List;

public interface MAReportService {

    ResponseObject<?> getAllReport(MAReportRequest request);

    ResponseObject<?> findAllReport(MAReportRequest request);

    byte[] readFileDoc(String idProject,MAReportUserRequest request);

    ResponseObject<?> exportReport(HttpServletResponse response, String idProject, MAReportUserRequest request);

    ResponseObject<?> getLogsImportReport(int page, int size) throws FileNotFoundException;

    ResponseObject<?> getAllReports();

    ResponseObject<?> createMultipleHolidays(HolidayCreateRequest request, String idProject);

    ResponseObject<?> detailHoliday(String idProject);

    ResponseObject<?> updateHolidaysStatus(List<Long> dates, String idProject);

}
