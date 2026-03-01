package udpm.hn.server.infrastructure.job.student.jobconfig;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import udpm.hn.server.infrastructure.job.student.service.JobStudentService;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;


@Component
@Slf4j
public class StudentJobLauncher {

    private final AtomicBoolean enabled = new AtomicBoolean(false);

    @Autowired
    @Qualifier("excelFileToDatabaseJobStudent")
    private Job job;


    @Autowired
    private JobLauncher jobLauncher;


    @Setter(onMethod_ = @Autowired)
    private JobStudentService jobStudentService;

    @Setter
    private String fullPathFileName;


    @Scheduled(cron = "${excel.file.to.database.job.cron}")
    void launchExcelToDatabaseJobStudent() {
        if (enabled.get() && fullPathFileName != null) {
            try {
                log.info("Launching excel to database job");
                JobExecution jobExecution = jobLauncher.run(job, newExecutionStudent());
                ExitStatus exitStatus = jobExecution.getExitStatus();
                log.info("Exit status: {}", exitStatus);
                if (exitStatus.getExitCode().equals(ExitStatus.COMPLETED.getExitCode())) {
                    jobStudentService.deleteAll();
                }
                if (exitStatus.getExitCode().equals(ExitStatus.FAILED.getExitCode())) {
                    log.error("Error launching excel to database job");
                    jobStudentService.deleteAll();
                }
            } catch (Exception e) {
                log.error("Error launching excel to database job", e);
            } finally {
                disable();
                log.info("ĐÃ DISABLE");

            }
        }
    }
    private JobParameters newExecutionStudent() {
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
