package udpm.hn.server.infrastructure.job.MBReport.Export.controller;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import udpm.hn.server.infrastructure.constant.MappingConstants;
import udpm.hn.server.infrastructure.job.MBReport.Export.service.ExportReportService;
import udpm.hn.server.utils.UserContextHelper;

@Controller
@RequiredArgsConstructor
@RequestMapping(MappingConstants.API_MEMBER_REPORT)
public class MBExportReportDocx {

    private final ExportReportService exportReportService;
    private final UserContextHelper userContextHelper;

    @GetMapping("/docx/{projectId}")
    public void exportDocx(@PathVariable String projectId, HttpServletResponse response) throws Exception {
        response.setContentType("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
        response.setHeader("Content-Disposition", "attachment; filename=report.docx");

        exportReportService.exportReportDocx(userContextHelper, projectId, response.getOutputStream());
    }

    @GetMapping("/export-excel/{projectId}")
    public void exportExcel(@PathVariable String projectId, HttpServletResponse response) throws Exception {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=\"bao_cao.xlsx\"");
        exportReportService.exportReportExcel(userContextHelper, projectId, response.getOutputStream());
    }

}
