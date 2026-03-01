package udpm.hn.server.infrastructure.doc.ADReportProject.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import udpm.hn.server.infrastructure.constant.MappingConstants;
import udpm.hn.server.infrastructure.doc.ADReportProject.model.request.GenerateReportRequest;
import udpm.hn.server.infrastructure.doc.ADReportProject.service.ReportExportService;

@RestController
@RequestMapping(MappingConstants.API_ADMIN_REPORT)
@RequiredArgsConstructor
public class ReportExportController {

    private final ReportExportService reportExportService;

    @PostMapping("/download-zip")
    public ResponseEntity<byte[]> downloadZip(@RequestBody GenerateReportRequest request) {
        try {
            byte[] zipContent = reportExportService.downloadZip(request);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=bao_cao_du_an.zip")
                    .contentLength(zipContent.length)
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(zipContent);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(null);
        }
    }

    @PostMapping("/download-excel-one-file-one-sheet")
    public ResponseEntity<byte[]> downloadExcelOneFileOneSheet(@RequestBody GenerateReportRequest request) {
        try {
            byte[] excelContent = reportExportService.downloadExcelOneFileOneSheet(request);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=BaoCao_TongHop.xlsx")
                    .contentLength(excelContent.length)
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .body(excelContent);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(null);
        }
    }

    @PostMapping("/download-excel-one-file-multi-sheet")
    public ResponseEntity<byte[]> downloadReportExcelOneFileMultiSheet(@RequestBody GenerateReportRequest request) {
        try {
            byte[] excelContent = reportExportService.downloadReportExcelOneFileMultiSheet(request);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=BaoCao_TongHop.xlsx")
                    .contentLength(excelContent.length)
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .body(excelContent);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(null);
        }
    }

    @PostMapping("/download-excel-file-multi-zip")
    public ResponseEntity<byte[]> downloadReportExcelMultiFileSheetZip(@RequestBody GenerateReportRequest request) {
        try {
            // Gọi service để lấy nội dung zip file dưới dạng byte[]
            byte[] zipContent = reportExportService.downloadReportExcelMultiFileSheetZip(request);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=BaoCao_TongHop.zip")
                    .contentLength(zipContent.length)
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(zipContent);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(null);
        }
    }

}
