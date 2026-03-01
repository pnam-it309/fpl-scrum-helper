package udpm.hn.server.core.manage.report.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;
import udpm.hn.server.core.manage.report.model.request.HolidayCreateRequest;
import udpm.hn.server.core.manage.report.model.request.MAReportRequest;
import udpm.hn.server.core.manage.report.model.request.MAReportUserRequest;
import udpm.hn.server.core.manage.report.service.MAReportService;
import udpm.hn.server.infrastructure.constant.MappingConstants;
import udpm.hn.server.utils.Helper;

import java.io.FileNotFoundException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping(MappingConstants.API_MANAGE_REPORT)
public class MAReportRestController {

    private final MAReportService maReportService;

    @GetMapping
    public ResponseEntity<?> getAllReport( MAReportRequest request){
        return Helper.createResponseEntity(maReportService.getAllReport(request));
    }

    @GetMapping("/allReport")
    public ResponseEntity<?> findAllReport( MAReportRequest request){
        return Helper.createResponseEntity(maReportService.findAllReport(request));
    }



    @PostMapping("/docx/{idProject}")
    public ResponseEntity<byte[]> downloadWordFile(@PathVariable String idProject,@RequestBody MAReportUserRequest request) {
        log.info("id project truyèn về trong doc:{}",idProject);
        log.info("danh sách id user trong docx:{}",request.toString());
        byte[] zipContent = maReportService.readFileDoc(idProject, request);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.add("Content-Disposition", "attachment; filename=\"bao-cao.zip\"");
        headers.setContentLength(zipContent.length);

        return ResponseEntity.ok()
                .headers(headers)
                .body(zipContent);
    }

    @PostMapping("/export/{idProject}")
    public ResponseEntity<?> exportReport(HttpServletResponse response, @PathVariable String idProject,@RequestBody MAReportUserRequest request){
        log.info("Export report:{}",response);
        return Helper.createResponseEntity(maReportService.exportReport(response,idProject,request));
    }

    @GetMapping("/history")
    public ResponseEntity<?> getChangeHistory(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "50") int size
    ) throws FileNotFoundException {
        return Helper.createResponseEntity(maReportService.getLogsImportReport(page, size));
    }

    @GetMapping("/report-compensation")
    public ResponseEntity<?> getAllReportCompensation(){
        return Helper.createResponseEntity(maReportService.getAllReports());
    }

    @PostMapping("/create-holiday/{idProject}")
    public ResponseEntity<?> createMultipleHolidays(@RequestBody HolidayCreateRequest request, @PathVariable String idProject) {
        return Helper.createResponseEntity(maReportService.createMultipleHolidays(request, idProject));

    }

    @GetMapping("/detail-holiday/{idProject}")
    public ResponseEntity<?> detail(@PathVariable String idProject){
        return Helper.createResponseEntity(maReportService.detailHoliday(idProject));
    }

    @PutMapping("/update-holiday/{idProject}")
    public ResponseEntity<?> updateStatus(@RequestBody List<Long> dates, @PathVariable String idProject) {
        return Helper.createResponseEntity(maReportService.updateHolidaysStatus(dates, idProject));

    }
}
