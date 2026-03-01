package udpm.hn.server.infrastructure.doc.ADReportProject.service;

import udpm.hn.server.infrastructure.doc.ADReportProject.model.request.GenerateReportRequest;

public interface ReportExportService {

    byte[] downloadExcelOneFileOneSheet(GenerateReportRequest request);

    byte[] downloadReportExcelOneFileMultiSheet(GenerateReportRequest request);

    byte[] downloadReportExcelMultiFileSheetZip(GenerateReportRequest request);

    byte[] downloadZip(GenerateReportRequest request);

}
