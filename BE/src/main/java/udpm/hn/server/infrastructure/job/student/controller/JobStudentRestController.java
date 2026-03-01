package udpm.hn.server.infrastructure.job.student.controller;


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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import udpm.hn.server.core.common.base.ResponseObject;
import udpm.hn.server.infrastructure.constant.MappingConstants;
import udpm.hn.server.infrastructure.job.filecheck.ExcelFormatValidator;
import udpm.hn.server.infrastructure.job.student.jobconfig.StudentJobLauncher;
import udpm.hn.server.infrastructure.job.student.service.JobStudentService;
import udpm.hn.server.infrastructure.job.student.service.impl.DowLoadFileLog;
import udpm.hn.server.infrastructure.job.student.service.impl.DownloadStudentTemplate;
import udpm.hn.server.infrastructure.job.student.service.impl.HistoryImportStudentService;
import udpm.hn.server.utils.Helper;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping(MappingConstants.API_ADMIN_STUDENT)
@Slf4j
public class JobStudentRestController {


    private final DownloadStudentTemplate downloadStudentTemplate;

    @Setter(onMethod_ = @Autowired, onParam_ = @Qualifier("importStudent"))
    private final JobStudentService storageService;


    @Setter(onMethod_ = @Autowired)
    private StudentJobLauncher excelFileStudentToDatabaseJobLauncher;

    @Setter(onMethod_ = @Autowired)
    private JobLauncher jobLauncher;

    @Setter(onMethod_ = @Autowired)
    private Job excelFileToDatabaseJobStudent;

    @Setter(onMethod_ = @Autowired)
    private HistoryImportStudentService historyImportStudentService;


    private static final String TEMPLATE_FILE_NAME = "template_import_sinh_vien.xlsx";

    private final DowLoadFileLog dowLoadFileLog;

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

    @GetMapping("/template")
    public ResponseEntity<?> downloadTemplate() throws IOException {

        HttpHeaders headers = new HttpHeaders();

        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", TEMPLATE_FILE_NAME);

        ResponseObject<?> response = downloadStudentTemplate.downloadTemplate();

        if (response.getData() instanceof ByteArrayInputStream) {
            return new ResponseEntity<>(
                    ((ByteArrayInputStream) response.getData()).readAllBytes(),
                    headers,
                    HttpStatus.OK
            );
        }

        return Helper.createResponseEntity(response);
    }

//    @PostMapping("/file/upload")
//    public ResponseEntity<?> uploadFileStudent(@RequestParam("filePath")MultipartFile filePath){
//        String message = "";
//        try {
//            storageService.init();
//            String fileName = storageService.save(filePath);
//            message = "Uploaded the file successfully: " + filePath.getOriginalFilename();
//            excelFileStudentToDatabaseJobLauncher.setFullPathFileName(fileName);
//            excelFileStudentToDatabaseJobLauncher.enable();
//            JobParameters jobParameters = new JobParametersBuilder()
//                    .addString("filePath", fileName).toJobParameters();
//            jobLauncher.run(excelFileToDatabaseJobStudent, jobParameters);
//            return ResponseEntity.status(HttpStatus.OK).body("import thành công");
//        }catch (Exception e){
//            message = "import thất bại";
//            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(message);
//        }
//    }

    @PostMapping("/file/upload")
    public ResponseEntity<?> uploadFileStudent(@RequestParam("filePath") MultipartFile filePath) {
        try {
            if (!ExcelFormatValidator.isValidFormat(filePath)) {
                String logMessage = "File không đúng định dạng - " + filePath.getOriginalFilename();
                historyImportStudentService.saveHistory(logMessage);

                return ResponseEntity
                        .badRequest()
                        .body("File Excel không đúng định dạng. Vui lòng sử dụng file mẫu.");
            }

            storageService.init();
            String fileName = storageService.save(filePath);

            excelFileStudentToDatabaseJobLauncher.setFullPathFileName(fileName);
            excelFileStudentToDatabaseJobLauncher.enable();

            JobParameters jobParameters = new JobParametersBuilder()
                    .addString("filePath", fileName)
                    .toJobParameters();

            jobLauncher.run(excelFileToDatabaseJobStudent, jobParameters);

            return ResponseEntity.status(HttpStatus.OK).body("Import thành công");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity
                    .status(HttpStatus.EXPECTATION_FAILED)
                    .body("Import thất bại: " + e.getMessage());
        }
    }



}



