package udpm.hn.server.infrastructure.job.MBReport.Export.service;

import udpm.hn.server.utils.UserContextHelper;

import java.io.OutputStream;

public interface ExportReportService {
    void exportReportDocx(UserContextHelper userContextHelper, String projectId, OutputStream out) throws Exception;

    void exportReportExcel(UserContextHelper userContextHelper, String projectId, OutputStream out) throws Exception;

}
