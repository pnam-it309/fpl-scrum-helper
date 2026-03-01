package udpm.hn.server.infrastructure.configemail;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class AsyncEmailService {

    private final EmailSender emailService;

    public AsyncEmailService(EmailSender emailService) {
        this.emailService = emailService;
    }

    @Async // Thêm annotation này
    public void sendEmailToStaffs(String[] emails, String subject, String title, String content) {
        emailService.sendEmail(emails, subject, title, content);
    }

    @Async // Thêm annotation này
    public void sendEmailToStudents(String[] emails, String subject, String title, String content) {
        emailService.sendEmail(emails, subject, title, content);
    }
}