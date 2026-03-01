package udpm.hn.server.infrastructure.schedule.report;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import udpm.hn.server.core.manage.report.model.response.MAReportResponse;
import udpm.hn.server.core.manage.report.repository.MAReportRepository;
import udpm.hn.server.entity.Project;
import udpm.hn.server.infrastructure.constant.EntityStatus;
import udpm.hn.server.infrastructure.constant.StatusProject;
import udpm.hn.server.repository.ProjectRepository;
import udpm.hn.server.repository.StaffProjectRepository;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReportSchedule {

    private final ProjectRepository projectRepository;

    private final MAReportRepository reportRepository;

    private final StaffProjectRepository staffProjectRepository;

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String sender;

    @Scheduled(cron = "0 0 20 * * *")
    private void sendEmailExport() {
        List<Project> projectList = listProject();
        long todayEpoch = LocalDate.now()
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli();


        for (Project project : projectList) {
            String projectId = project.getId();
            if (!(project.getStartTime() < todayEpoch && project.getEndTime() > todayEpoch)) {
                continue;
            }


            // Lấy danh sách tên staff chưa báo cáo trong ngày hôm nay
            List<String> staffNames = mailUser(projectId);

            if (staffNames != null && !staffNames.isEmpty()) {
                String emailTo = emailSend(projectId);
                String subject = "Báo cáo công việc ngày " + LocalDate.now();
                String content = buildEmailContent(project.getName(), staffNames);

                try {
                    MimeMessage message = mailSender.createMimeMessage();
                    MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

                    helper.setFrom(sender);          // 👈 Gửi từ email cấu hình
                    helper.setTo(emailTo);           // 👈 Gửi tới email quản lý
                    helper.setSubject(subject);
                    helper.setText(content, false);  // false = plain text

                    mailSender.send(message);

                    log.info("✅ Đã gửi email từ: {} tới: {} cho project: {}", sender, emailTo, project.getName());
                } catch (Exception e) {
                    log.error("❌ Gửi email thất bại từ {} tới {}: {}", sender, emailTo, e.getMessage());
                }
            }
        }
    }






    private List<Project> listProject(){

        List<Project> filteredProjects = projectRepository.findAll().stream()
                .filter(a -> a.getStatus() == EntityStatus.ACTIVE)
                .filter(a-> a.getStatusProject() == StatusProject.DANG_DIEN_RA)
                .toList();

        log.info("Danh sách các project ====> {}",filteredProjects.stream().toList());
        return filteredProjects;

    }

    private List<String> mailUser(String idProject){
        Optional<Project> project = projectRepository.findById(idProject);
        Long timestamp = LocalDateTime.now()
                .atZone(ZoneId.systemDefault()) // chuyển sang ZoneId
                .toInstant()                    // chuyển sang Instant
                .toEpochMilli();               // lấy mili giây

        LocalDate date = Instant.ofEpochMilli(timestamp)
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
        Long dateStart = date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
        Long dateEnd = date.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
        if(project.isPresent()){
            List<MAReportResponse> listUserReport = reportRepository.getAllReport(dateStart,dateEnd,idProject);
            return listUserReport.stream()
                    .filter(a -> a.getReportTime() == null)
                    .map(a -> a.getNameStaff() == null ? a.getNameStudent() : a.getNameStaff())
                    .toList();

        }else {
            return null;
        }
    }


    private String emailSend(String idProject){
        return staffProjectRepository.findAll().stream()
                .filter(a -> a.getProject() != null && a.getProject().getId().equals(idProject))
                .filter(a -> a.getStatus() == EntityStatus.ACTIVE)
                .filter(a -> a.getStaff() != null)
                .filter(a-> a.getStaff().getStatus() == EntityStatus.ACTIVE)
                .map(a -> a.getStaff().getEmailFe())
                .findFirst() // Lấy 1 người đại diện để gửi
                .orElse("vantruong22082005@gmail.com"); // Email mặc định nếu không tìm thấy
    }

    private String buildEmailContent(String projectName, List<String> staffNames) {
        StringBuilder content = new StringBuilder();
        content.append("Dự án: ").append(projectName).append("\n");
        content.append("Danh sách nhân sự chưa gửi báo cáo hoặc báo cáo muộn trong ngày:\n");

        for (int i = 0; i < staffNames.size(); i++) {
            content.append((i + 1)).append(". ").append(staffNames.get(i)).append("\n");
        }

        content.append("\nVui lòng nhắc nhở các thành viên hoàn thành báo cáo đúng hạn.");
        return content.toString();
    }

    //nhắc nhở báo cáo hàng ngày
    private String buildReminderContent(String projectName) {
        StringBuilder content = new StringBuilder();
        content.append("Bạn đang tham gia dự án: ").append(projectName).append("\n");
        content.append("Đây là lời nhắc bạn vui lòng hoàn thành báo cáo công việc trong hôm nay.\n");
        content.append("Cảm ơn bạn đã hợp tác!");
        return content.toString();
    }

    private List<String> getAllUserEmailsByProject(String idProject) {
        return staffProjectRepository.findAll().stream()
                .filter(a -> a.getProject() != null && a.getProject().getId().equals(idProject))
                .filter(a -> a.getStatus() == EntityStatus.ACTIVE)
                .filter(a -> a.getStaff() != null && a.getStaff().getStatus() == EntityStatus.ACTIVE)
                .map(a -> a.getStaff().getEmailFe())
                .distinct()
                .toList();
    }

    @Scheduled(cron = "0 0 16 * * *")
    private void sendReminderToAllUsers() {
        List<Project> projects = listProject();
        for (Project project : projects) {
            String projectId = project.getId();
            List<String> emails = getAllUserEmailsByProject(projectId);

            for (String email : emails) {
                try {
                    String subject = "Nhắc nhở báo cáo công việc ngày " + LocalDate.now();
                    String content = buildReminderContent(project.getName());

                    MimeMessage message = mailSender.createMimeMessage();
                    MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

                    helper.setFrom(sender);
                    helper.setTo(email);
                    helper.setSubject(subject);
                    helper.setText(content, false);

                    mailSender.send(message);
                    log.info("📧 Đã gửi nhắc nhở báo cáo tới: {}", email);

                } catch (Exception e) {
                    log.error("❌ Lỗi khi gửi email tới {}: {}", email, e.getMessage());
                }
            }
        }
    }


}


