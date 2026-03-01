package udpm.hn.server.infrastructure.job.staff.controller;

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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import udpm.hn.server.core.common.base.ResponseObject;
import udpm.hn.server.infrastructure.constant.MappingConstants;
import udpm.hn.server.infrastructure.job.filecheck.ExcelFormatStaffValidator;
import udpm.hn.server.infrastructure.job.filecheck.ExcelFormatValidator;
import udpm.hn.server.infrastructure.job.staff.jobconfig.StaffJobLauncher;
import udpm.hn.server.infrastructure.job.staff.service.UploadStaffService;
import udpm.hn.server.infrastructure.job.staff.service.impl.DowLoadFileLogStaff;
import udpm.hn.server.infrastructure.job.staff.service.impl.DownloadStaffTemplate;
import udpm.hn.server.infrastructure.job.staff.service.impl.HistoryImportStaffService;
import udpm.hn.server.infrastructure.job.student.jobconfig.StudentJobLauncher;
import udpm.hn.server.infrastructure.job.student.service.JobStudentService;
import udpm.hn.server.infrastructure.job.student.service.impl.DowLoadFileLog;
import udpm.hn.server.infrastructure.job.student.service.impl.DownloadStudentTemplate;
import udpm.hn.server.infrastructure.job.student.service.impl.HistoryImportStudentService;
import udpm.hn.server.repository.HistoryImportRepository;
import udpm.hn.server.utils.Helper;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping(MappingConstants.API_ADMIN_STAFF)
@Slf4j
public class ImportStaffController {
    private final HistoryImportRepository historyImportRepository;

    private final DownloadStaffTemplate downloadStaffTemplate;

    @Setter(onMethod_ = @Autowired, onParam_ = @Qualifier("importStaff"))
    private final UploadStaffService storageService;

    @Setter(onMethod_ = @Autowired)
    private HistoryImportStaffService historyImportStaffService;

    @Setter(onMethod_ = @Autowired)
    private StaffJobLauncher excelFileStaffToDatabaseJobLauncher;

    @Setter(onMethod_ = @Autowired)
    private JobLauncher jobLauncher;

    @Setter(onMethod_ = @Autowired)
    private Job saveStaffDataToDatabaseJob;

    private static final String TEMPLATE_FILE_NAME = "template_import_staff.xlsx";

    private final DowLoadFileLogStaff dowLoadFileLog;

    @GetMapping("/download-csv")
    public ResponseEntity<byte[]> downloadCsv() {
        try {
            byte[] csvContent = dowLoadFileLog.getCsvFile();

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=log.csv")
                    .contentType(MediaType.parseMediaType("text/csv"))
                    .body(csvContent);

        } catch (IOException e) {
            return ResponseEntity.badRequest().build(); // Trả về lỗi 404 nếu file không tồn tại
        }
    }

    @GetMapping("/template")
    public ResponseEntity<?> downloadTemplate() throws IOException {

        HttpHeaders headers = new HttpHeaders();

        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", TEMPLATE_FILE_NAME);

        ResponseObject<?> response = downloadStaffTemplate.downloadTemplate();

        if (response.getData() instanceof ByteArrayInputStream) {
            return new ResponseEntity<>(
                    ((ByteArrayInputStream) response.getData()).readAllBytes(),
                    headers,
                    HttpStatus.OK
            );
        }

        return Helper.createResponseEntity(response);
    }

    @PostMapping("/file/upload")
    public ResponseEntity<?> uploadFileStudent(@RequestParam("file")MultipartFile filePath){
        String message = "";
        try {

            if (!ExcelFormatStaffValidator.isValidFormat(filePath)) {
                String logMessage = "File không đúng định dạng - " + filePath.getOriginalFilename();
                historyImportStaffService.saveHistory(logMessage);

                return ResponseEntity
                        .badRequest()
                        .body("File Excel không đúng định dạng. Vui lòng sử dụng file mẫu.");
            }

            storageService.init();
            String fileName = storageService.save(filePath);
            excelFileStaffToDatabaseJobLauncher.setFullPathFileName(fileName);
            excelFileStaffToDatabaseJobLauncher.enable();
            JobParameters jobParameters = new JobParametersBuilder()
                    .addString("staffExcelFileName", fileName).toJobParameters();
            jobLauncher.run(saveStaffDataToDatabaseJob, jobParameters);
            return ResponseEntity.status(HttpStatus.OK).body("import thành công");
        }catch (Exception e){
            message = "import thất bại";
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(message);
        }
    }

}
