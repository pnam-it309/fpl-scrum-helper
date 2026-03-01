package udpm.hn.server.infrastructure.job.staff.jobconfig;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import udpm.hn.server.infrastructure.job.staff.service.UploadStaffService;
import udpm.hn.server.infrastructure.job.student.service.JobStudentService;
import udpm.hn.server.utils.UserContextHelper;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@Slf4j
@NoArgsConstructor
public class StaffJobLauncher {

    private final AtomicBoolean enabled = new AtomicBoolean(false);

    @Autowired
    @Qualifier("saveStaffDataToDatabaseJob")
    private Job job;

    @Autowired
    private JobLauncher jobLauncher;

    @Setter(onMethod_ = @Autowired)
    private UploadStaffService uploadStaffService;

    @Setter
    private String fullPathFileName;

    @Scheduled(cron = "${excel.file.to.database.job.cron}")
    void launchExcelToDatabaseJobStaff() {
        if (enabled.get() && fullPathFileName != null) {
            try {
                JobExecution jobExecution = jobLauncher.run(job, newExecutionStudent());
                ExitStatus exitStatus = jobExecution.getExitStatus();
                if (exitStatus.getExitCode().equals(ExitStatus.COMPLETED.getExitCode())) {
                    uploadStaffService.deleteAll();
                }
                if (exitStatus.getExitCode().equals(ExitStatus.FAILED.getExitCode())) {
                    uploadStaffService.deleteAll();
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
        parameters.put("staffExcelFileName", new JobParameter<>(fullPathFileName, String.class));
        return new JobParameters(parameters);
    }
    public void enable() {
        enabled.set(true);
    }

    public void disable() {
        enabled.set(false);
    }

}
