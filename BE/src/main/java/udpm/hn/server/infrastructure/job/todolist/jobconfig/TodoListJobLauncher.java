package udpm.hn.server.infrastructure.job.todolist.jobconfig;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import udpm.hn.server.infrastructure.job.todolist.service.JobTodoListService;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;


@Component
@Slf4j
public class TodoListJobLauncher {

    private final AtomicBoolean enabled = new AtomicBoolean(false);

    @Autowired
    @Qualifier("excelFileToDatabaseJobTodoList")
    private Job job;


    @Autowired
    private JobLauncher jobLauncher;


    @Setter(onMethod_ = @Autowired)
    private JobTodoListService jobTodoListService;

    @Setter
    private String fullPathFileName;


    @Scheduled(cron = "${excel.file.to.database.job.cron}")
    void launchExcelToDatabaseJobTodoList() {
        if (enabled.get() && fullPathFileName != null) {
            try {
                log.info("Launching excel to database job");
                JobExecution jobExecution = jobLauncher.run(job, newExecutionTodoList());
                ExitStatus exitStatus = jobExecution.getExitStatus();
                log.info("Exit status: {}", exitStatus);
                if (exitStatus.getExitCode().equals(ExitStatus.COMPLETED.getExitCode())) {
                    jobTodoListService.deleteAll();
                }
                if (exitStatus.getExitCode().equals(ExitStatus.FAILED.getExitCode())) {
                    log.error("Error launching excel to database job");
                    jobTodoListService.deleteAll();
                }
            } catch (Exception e) {
                log.error("Error launching excel to database job", e);
            } finally {
                disable();
                log.info("ĐÃ DISABLE");

            }
        }
    }
    private JobParameters newExecutionTodoList() {
        log.info("fullPathFileName: {}", fullPathFileName);
        Map<String, JobParameter<?>> parameters = new HashMap<>();
        parameters.put("time", new JobParameter<>(new Date(), Date.class));
        parameters.put("filePath", new JobParameter<>(fullPathFileName, String.class));
        return new JobParameters(parameters);
    }
    public void enable() {
        enabled.set(true);
    }

    public void disable() {
        enabled.set(false);
    }

}
