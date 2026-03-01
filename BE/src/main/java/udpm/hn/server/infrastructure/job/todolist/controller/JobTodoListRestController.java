package udpm.hn.server.infrastructure.job.todolist.controller;


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
import udpm.hn.server.infrastructure.job.filecheck.TodoListExcelValidator;
import udpm.hn.server.infrastructure.job.student.service.impl.HistoryImportStudentService;
import udpm.hn.server.infrastructure.job.todolist.jobconfig.TodoListJobLauncher;
import udpm.hn.server.infrastructure.job.todolist.service.JobTodoListService;
import udpm.hn.server.infrastructure.job.todolist.service.impl.DowLoadFileLogTodo;
import udpm.hn.server.infrastructure.job.todolist.service.impl.DownloadTodoListTemplate;
import udpm.hn.server.infrastructure.job.todolist.service.impl.HistoryImportTodoService;
import udpm.hn.server.repository.HistoryImportTodoRepository;
import udpm.hn.server.utils.Helper;

import java.io.ByteArrayInputStream;
import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping(MappingConstants.API_MANAGE_TODO_LIST)
@Slf4j
public class JobTodoListRestController {
    private final HistoryImportTodoRepository historyImportTodoRepository;

    private final DownloadTodoListTemplate downloadTodoListTemplate;

    @Setter(onMethod_ = @Autowired, onParam_ = @Qualifier("importTodoList"))
    private final JobTodoListService storageService;

    @Setter(onMethod_ = @Autowired)
    private HistoryImportTodoService historyImportTodoService;

    @Setter(onMethod_ = @Autowired)
    private TodoListJobLauncher excelFileTodoListToDatabaseJobLauncher;

    @Setter(onMethod_ = @Autowired)
    private JobLauncher jobLauncher;

    @Setter(onMethod_ = @Autowired)
    private Job excelFileToDatabaseJobTodoList;

    private static final String TEMPLATE_FILE_NAME = "template_import_todo_list.xlsx";

    private final DowLoadFileLogTodo dowLoadFileLog;

    @GetMapping("/download-csv")
    public ResponseEntity<byte[]> downloadCsv() {
        try {
            byte[] csvContent = dowLoadFileLog.getCsvFile();

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"import-log.csv\"")
                    .contentType(MediaType.parseMediaType("text/csv; charset=UTF-8"))
                    .body(csvContent);

        } catch (IOException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/template")
    public ResponseEntity<?> downloadTemplate() throws IOException {

        HttpHeaders headers = new HttpHeaders();

        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", TEMPLATE_FILE_NAME);

        ResponseObject<?> response = downloadTodoListTemplate.downloadTemplate();

        if (response.getData() instanceof ByteArrayInputStream) {
            return new ResponseEntity<>(
                    ((ByteArrayInputStream) response.getData()).readAllBytes(),
                    headers,
                    HttpStatus.OK
            );
        }

        return Helper.createResponseEntity(response);
    }

    @PostMapping("/file/upload/{idProject}")
    public ResponseEntity<?> uploadFileTodoList(@RequestParam("filePath") MultipartFile filePath,
                                                @PathVariable("idProject") String idProject) {
        try {
            if (!TodoListExcelValidator.isValidTodoListFormat(filePath)) {
                String logMessage = "File không đúng định dạng - " + filePath.getOriginalFilename();
                historyImportTodoService.saveHistory(idProject,logMessage);
                return ResponseEntity
                        .badRequest()
                        .body("File Excel không đúng định dạng. Vui lòng sử dụng file mẫu.");
            }

            storageService.init();
            String fileName = storageService.save(filePath);

            excelFileTodoListToDatabaseJobLauncher.setFullPathFileName(fileName);
            excelFileTodoListToDatabaseJobLauncher.enable();

            JobParameters jobParameters = new JobParametersBuilder()
                    .addString("filePath", fileName)
                    .addString("idProject", idProject)
                    .toJobParameters();

            jobLauncher.run(excelFileToDatabaseJobTodoList, jobParameters);

            return ResponseEntity.status(HttpStatus.OK).body("Import thành công");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity
                    .status(HttpStatus.EXPECTATION_FAILED)
                    .body("Import thất bại: " + e.getMessage());
        }
    }

}



