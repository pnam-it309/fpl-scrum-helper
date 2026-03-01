package udpm.hn.server.infrastructure.job.reportcompensation.controller;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import udpm.hn.server.core.common.base.ResponseObject;
import udpm.hn.server.infrastructure.constant.MappingConstants;
import udpm.hn.server.infrastructure.job.reportcompensation.jobconfig.ReportCompensationJobLauncher;
import udpm.hn.server.infrastructure.job.reportcompensation.service.JobReportCompensationService;
import udpm.hn.server.infrastructure.job.reportcompensation.service.impl.DowLoadFileLogReport;
import udpm.hn.server.infrastructure.job.reportcompensation.service.impl.DownloadReportCompensationTemplate;

import udpm.hn.server.infrastructure.job.reportcompensation.service.impl.HistoryImportReportCompensationService;
import udpm.hn.server.utils.Helper;

import java.io.ByteArrayInputStream;
import java.io.IOException;

@Controller
@RequiredArgsConstructor
@RequestMapping(MappingConstants.API_MANAGE_PROJECT)
@Slf4j
public class ReportCompensationController {

    private static final String TEMPLATE_FILE_NAME = "template_Bao_cao_bu.xlsx";

    private final DownloadReportCompensationTemplate downloadReportCompensationTemplate;

    @Setter(onMethod_ = @Autowired, onParam_ = @Qualifier("importReportCompensation"))
    private final JobReportCompensationService storageService;


    @Setter(onMethod_ = @Autowired)
    private ReportCompensationJobLauncher reportCompensationJobLauncher;

    @Setter(onMethod_ = @Autowired)
    private JobLauncher jobLauncher;

    @Setter(onMethod_ = @Autowired)
    private HistoryImportReportCompensationService historyImportReportCompensationService;

    @Setter(onMethod_ = @Autowired)
    private Job excelFileToDatabaseJobReportCompensation;

    private final DowLoadFileLogReport dowLoadFileLog;

    @GetMapping("/template")
    public ResponseEntity<?> downloadTemplate() throws IOException {

        HttpHeaders headers = new HttpHeaders();

        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", TEMPLATE_FILE_NAME);

        ResponseObject<?> response = downloadReportCompensationTemplate.downloadTemplate();

        if (response.getData() instanceof ByteArrayInputStream) {
            return new ResponseEntity<>(
                    ((ByteArrayInputStream) response.getData()).readAllBytes(),
                    headers,
                    HttpStatus.OK
            );
        }

        return Helper.createResponseEntity(response);
    }

    @GetMapping("/download-csv")
    public ResponseEntity<byte[]> downloadCsv() {
        try {
            byte[] csvContent = dowLoadFileLog.getCsvFile();

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=import-log.csv")
                    .contentType(MediaType.parseMediaType("text/csv"))
                    .body(csvContent);

        } catch (IOException e) {
            return ResponseEntity.badRequest().build(); // Trả về lỗi 404 nếu file không tồn tại
        }
    }

    @PostMapping("/file/upload/{idProject}")
    public ResponseEntity<?> uploadFileReportCompensation(
                                        @RequestParam("filePath") MultipartFile filePath,
                                        @PathVariable("idProject") String idProject
    ) {
        try {
            storageService.init();
            String fileName = storageService.save(filePath);

            reportCompensationJobLauncher.setFullPathFileName(fileName);
            reportCompensationJobLauncher.enable();

            JobParameters jobParameters = new JobParametersBuilder()
                    .addString("filePath", fileName)
                    .addString("idProject", idProject)
                    .toJobParameters();



            jobLauncher.run(excelFileToDatabaseJobReportCompensation, jobParameters);

            return ResponseEntity.status(HttpStatus.OK).body("Import thành công");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity
                    .status(HttpStatus.EXPECTATION_FAILED)
                    .body("Import thất bại: " + e.getMessage());
        }
    }


}
