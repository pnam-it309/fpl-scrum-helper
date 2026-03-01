package udpm.hn.server.utils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import udpm.hn.server.entity.Assign;
import udpm.hn.server.entity.MemberNotification;
import udpm.hn.server.entity.Notification;
import udpm.hn.server.entity.Todo;
import udpm.hn.server.infrastructure.configemail.EmailSender;
import udpm.hn.server.infrastructure.constant.EntityStatus;
import udpm.hn.server.infrastructure.constant.StatusReminder;
import udpm.hn.server.repository.AssignRepository;
import udpm.hn.server.repository.MemberNotificationRepository;
import udpm.hn.server.repository.NotificationRepository;
import udpm.hn.server.repository.PhaseRepository;
import udpm.hn.server.repository.ProjectRepository;
import udpm.hn.server.repository.ToDoRepository;
import udpm.hn.server.repository.TodoVoteRepository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class ScheduleDaily {

    private final ProjectRepository projectRepository;

    private final PhaseRepository phaseRepository;

    private final ToDoRepository toDoRepository;

    private final TodoVoteRepository todoVoteRepository;

    private final AssignRepository assignRepository;

    private final SimpMessagingTemplate messagingTemplate;

    private final NotificationRepository notificationRepository;

    private final MemberNotificationRepository memberNotificationRepository;
    private final EmailSender emailSender;

    @Scheduled(cron = "0 * * * * ?")
    public void dailyChecking() {
        List<Todo> todosFind = toDoRepository.getAllTodoReminder();
        List<String> listStrMemberId = new ArrayList<>();
        boolean check = false;
        for (Todo item : todosFind) {
            Long deliveryDateMinutes = item.getDeliveryDate();

            long deadline = item.getDeadline();

            long deliveryDateMillis = 0;
            if (deliveryDateMinutes != 0) {
                deliveryDateMillis = deadline - (deliveryDateMinutes * 60L * 1000);
                System.out.println("Ngày giao hàng được tính toán: " + new Date(deliveryDateMillis));
            } else {
                System.out.println("Ngày giao hàng không thay đổi : " + new Date(deadline));
            }

            if (deliveryDateMillis < System.currentTimeMillis() && item.getStatusReminder() == StatusReminder.CHUA_GUI) {

                List<Assign> listAssign = assignRepository.getAllAssignByIdTodo(item.getId());

                if (listAssign == null) {
                    log.warn("Không tìm thấy TodoVote cho Todo ID: " + item.getId());
                    continue;
                }

                String phaseId = toDoRepository.getPhaseByIdTodo(item.getId());
                String projectId = toDoRepository.getProjectByIdTodo(item.getId());

                Optional<Todo> todoOptional = toDoRepository.findById(item.getId());
                if (!todoOptional.isPresent()) {
                    log.warn("Không tìm thấy Todo với ID: " + item.getId());
                    continue;
                }

                Notification notification = new Notification();
                notification.setTodo(todoOptional.get());
                notification.setMemberIdCreated(null);
                notification.setName("Đầu việc " + item.getName() + " mà bạn đang tham gia sẽ hết hạn vào lúc " + DateConverter.convertDateToStringMail(item.getDeadline()));
                notification.setUrl("/project-detail/" + projectId + "/" + phaseId + "?idTodo=" + item.getId());

                Notification newNotification = notificationRepository.save(notification);
                if (newNotification == null) {
                    log.warn("Không thể tạo Notification cho Todo ID: " + item.getId());
                    continue;
                }

                List<MemberNotification> notificationMemberList = new ArrayList<>();

                for (Assign assign : listAssign) {
                    String idStaffProject = assign.getStaffProject().getId();
                    Optional<String> memberId = todoVoteRepository.getUserIdByStaffProjectId(idStaffProject);
                    if (memberId.isEmpty()) {
                        log.warn("Không tìm thấy memberId cho StaffProject ID: " + idStaffProject);
                        continue;
                    }

                    MemberNotification notificationMember = new MemberNotification();
                    notificationMember.setMemberId(memberId.get());

                    Optional<Notification> notificationOpt = notificationRepository.findById(newNotification.getId());
                    if (!notificationOpt.isPresent()) {
                        log.warn("Không tìm thấy Notification với ID: " + newNotification.getId());
                        continue;
                    }

                    notificationMember.setNotification(notificationOpt.get());
                    notificationMember.setStatus(EntityStatus.ACTIVE);
                    notificationMemberList.add(notificationMember);

                    if (!listStrMemberId.contains(memberId.get())) {
                        listStrMemberId.add(memberId.get());
                    }
                }

                if (!notificationMemberList.isEmpty()) {
                    memberNotificationRepository.saveAll(notificationMemberList);
                }

                List<String> emailList = new ArrayList<>();

                for (Assign todoVote : listAssign) {
                    String staffProjectId = todoVote.getStaffProject().getId();
                    Optional<String> email = todoVoteRepository.getEmailUserByStaffProjectId(staffProjectId);
                    if (email.isEmpty()) {
                        break;
                    }
                    emailList.add(email.get());
                }

                String[] emailArray = emailList.toArray(new String[0]);
                String emailContent = """
                        <html><body>
                        <p><span style=\"font-size: 20px;\">Xin chào!</span> Đây là một email
                        nhắc nhở về đầu việc <span style=\"color: red;\"><b>
                        """ + item.getName() + """
                        </b></span> mà bạn đang tham gia.</p>
                        <p>Chúng tôi xin nhắc nhở rằng ngày hạn của đầu việc
                        này sẽ sắp đến trong thời gian tới.
                        Vì vậy, chúng tôi đề nghị bạn hoàn thành và
                        gửi báo cáo/trạng thái của đầu việc trước ngày hạn.</p>
                        <p>Thông tin chi tiết về đầu việc:</p>
                        <ul>
                        <li>Tên đầu việc: <span style=\"color: red;\"><b>
                        """ + item.getName() + "</b></span></li>" + "<li>Ngày hạn: <span style=\"color: red;\"><b>" + DateConverter.convertDateToStringMail(item.getDeadline()) + "</b></span></li>" + """
                        </ul>
                        <p>Vui lòng đảm bảo rằng bạn đã hoàn thành công việc và gửi báo cáo
                        đúng hạn để đảm bảo tiến độ và chất lượng của dự án.</p>
                        <p>Nếu bạn có bất kỳ câu hỏi hoặc cần hỗ trợ bổ sung, xin vui lòng
                        liên hệ với chúng tôi.</p>
                        <p>Xin cảm ơn sự cống hiến và đóng góp của bạn cho dự án!</p>
                        <p>Trân trọng,</p>
                        <p>- Quản lý dự án xưởng -</p>
                        </body></html>
                        """;
                Runnable emailTask = () -> {
                    emailSender.sendEmail(emailArray, "Quản lý dự án xưởng xin thông báo", "Thông báo đầu việc sắp hết hạn", emailContent);
                };
                new Thread(emailTask).start();
                toDoRepository.updateStatusReminder(item.getId());
                check = true;
            }
        }

        for (String str : listStrMemberId) {
            log.info("Gửi thông báo cho thành viên ID: " + str);
            messagingTemplate.convertAndSend("/topic/create-notification-member/" + str, check);
        }
    }

}
