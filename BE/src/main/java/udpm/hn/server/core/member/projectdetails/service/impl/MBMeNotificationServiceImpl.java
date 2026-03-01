package udpm.hn.server.core.member.projectdetails.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import udpm.hn.server.core.common.base.ResponseObject;
import udpm.hn.server.core.member.projectdetails.model.request.MBMeCreate1NotificationCommentRequest;
import udpm.hn.server.core.member.projectdetails.model.request.MBMeCreateNotificationCommentRequest;
import udpm.hn.server.core.member.projectdetails.repository.*;
import udpm.hn.server.core.member.projectdetails.service.MBMeNotificationService;
import udpm.hn.server.entity.MemberNotification;
import udpm.hn.server.entity.Notification;
import udpm.hn.server.entity.StaffProject;
import udpm.hn.server.entity.Todo;
import udpm.hn.server.infrastructure.constant.EntityStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class MBMeNotificationServiceImpl implements MBMeNotificationService {

    private final MBMeNotificationMemberRepository mbMeNotificationMemberRepository;

    private final MBMeNotificationRepository mbMeNotificationRepository;

    private final MBMeTodoRepository mbMeTodoRepository;

    private final MBMeStaffProjectRepository mbMeStaffProjectRepository;

    @Override
    public ResponseObject<?> createNotification(MBMeCreateNotificationCommentRequest request) {
        Notification notification = new Notification();
        notification.setMemberIdCreated(request.getIdUser());
        notification.setUrl(request.getUrl());

        Optional<Todo> todoFind = mbMeTodoRepository.findById(request.getIdTodo());

        if(todoFind.isEmpty()){
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND,"khoogn tìm thấy todo");
        }

        notification.setTodo(todoFind.get());
        notification.setName(request.getUsername() + " đã nhắc đến bạn trong một bình luận");
        Notification newNotification = mbMeNotificationRepository.save(notification);

        List<String> mentionedIds = request.getMentionedIds();
        List<MemberNotification> newList = new ArrayList<>();
        mentionedIds.forEach(idUserTag -> {
            if (!idUserTag.equals(request.getIdUser())) {
               Optional<String > idStaffProject =
                mbMeStaffProjectRepository.findIdStaffProjectByIdUserAndIdProject(idUserTag, request.getIdProject());
                StaffProject staffProjectFind =
                mbMeStaffProjectRepository.findById(idStaffProject.get()).orElse(null);
                MemberNotification notificationMember = new MemberNotification();
                notificationMember.setNotification(newNotification);
                notificationMember.setStaffProject(staffProjectFind);
                notificationMember.setMemberId(idUserTag);
                notificationMember.setStatus(EntityStatus.ACTIVE);
                newList.add(notificationMember);
            }
        });

        mbMeNotificationMemberRepository.saveAll(newList);

        return new ResponseObject<>(null,HttpStatus.OK,"Thêm thành công thng báo ");
    }

    @Override
    public ResponseObject<?> create1Notification(MBMeCreate1NotificationCommentRequest request) {
        // 1️⃣ Tạo Notification gốc
        Notification notification = new Notification();
        notification.setMemberIdCreated(request.getIdUser());
        notification.setUrl(request.getUrl());

        // Nội dung thông báo (FE gửi content tuỳ biến)
        String notificationContent = (request.getContent() != null && !request.getContent().isEmpty())
                ? request.getContent()
                : request.getUsername() + " đã cập nhật giai đoạn dự án";
        notification.setName(notificationContent);

        Notification newNotification = mbMeNotificationRepository.save(notification);

        // 2️⃣ Lấy toàn bộ StaffProject theo idProject
        List<StaffProject> staffProjects = mbMeStaffProjectRepository.findAllByProjectId(request.getIdProject());

        if (staffProjects.isEmpty()) {
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Không tìm thấy thành viên nào trong dự án");
        }

        // 3️⃣ Loop từng StaffProject → lấy idStaff hoặc idStudent
        List<MemberNotification> memberNotifications = new ArrayList<>();

        for (StaffProject sp : staffProjects) {
            String memberId = null;

            if (sp.getStaff() != null) {
                memberId = sp.getStaff().getId();   // ✅ Lấy ID Staff
            } else if (sp.getStudent() != null) {
                memberId = sp.getStudent().getId(); // ✅ Lấy ID Student
            }

            // Chỉ lưu nếu có memberId hợp lệ
            if (memberId != null && !memberId.isEmpty()) {
                MemberNotification mn = new MemberNotification();
                mn.setNotification(newNotification);
                mn.setMemberId(memberId);
                mn.setStatus(EntityStatus.ACTIVE);
                memberNotifications.add(mn);
            }
        }

        mbMeNotificationMemberRepository.saveAll(memberNotifications);

        return new ResponseObject<>(null, HttpStatus.OK,
                "Đã gửi thông báo cho toàn bộ thành viên của dự án");
    }

    @Override
    public ResponseObject<?> createv2Notification(MBMeCreate1NotificationCommentRequest request) {
        // 1️⃣ Tạo Notification gốc
        Notification notification = new Notification();
        notification.setMemberIdCreated(request.getIdUser()); // Người tạo
        notification.setUrl(request.getUrl());

        // Nội dung thông báo
        String notificationContent = (request.getContent() != null && !request.getContent().isEmpty())
                ? request.getContent()
                : request.getUsername() + " đã cập nhật dự án";
        notification.setName(notificationContent);

        Notification newNotification = mbMeNotificationRepository.save(notification);

        // 2️⃣ Tạo MemberNotification cho người nhận duy nhất
        if (request.getIdReceiver() != null && !request.getIdReceiver().isEmpty()) {
            MemberNotification mn = new MemberNotification();
            mn.setNotification(newNotification);
            mn.setMemberId(request.getIdReceiver());
            mn.setStatus(EntityStatus.ACTIVE);
            mbMeNotificationMemberRepository.save(mn);

            return new ResponseObject<>(null, HttpStatus.OK,
                    "Đã gửi thông báo cho người nhận: " + request.getIdReceiver());
        } else {
            return new ResponseObject<>(null, HttpStatus.BAD_REQUEST,
                    "idReceiver không được để trống");
        }
    }


}
