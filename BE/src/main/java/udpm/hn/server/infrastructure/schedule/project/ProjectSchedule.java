package udpm.hn.server.infrastructure.schedule.project;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import udpm.hn.server.infrastructure.schedule.project.repository.ScheduleProjectRepository;

import java.util.Calendar;

@Component
@Slf4j
@RequiredArgsConstructor
public class ProjectSchedule {

    private final ScheduleProjectRepository projectRepository;

    @Value("")
    private String cronTime;

//    @Scheduled(cron = "${schedule.project.cron}", zone = "UTC")
//    public void updateProjectStatus() {
//
//        log.info(getCurrentTime()+"");
//
//        List<Project> projects = projectRepository.findAll();
//
//        projects.forEach(project -> {
//            if(getCurrentTime() > project.getEndTime()) {
//                project.setStatusProject(StatusProject.DA_DIEN_RA);
//            }
//
//            projectRepository.save(project);
//            log.info("Cập nhật dự án {} sang trạng thái: Đã diễn ra", project.getId());
//        });
//    }

    private long getCurrentTime() {
        return Calendar.getInstance().getTimeInMillis();
    }

}
