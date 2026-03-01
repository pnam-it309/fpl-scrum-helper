package udpm.hn.server.core.member.projectdetails.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.*;
import udpm.hn.server.core.common.base.ResponseObject;
import udpm.hn.server.core.member.projectdetails.model.request.MBMeFindNotificationMemberRequest;
import udpm.hn.server.core.member.projectdetails.model.request.MBMessageNotificationMemberRequest;
import udpm.hn.server.core.member.projectdetails.service.MBMeMemberNotificationService;
import udpm.hn.server.infrastructure.constant.MappingConstants;

@RestController
@RequestMapping(MappingConstants.API_PROJECT_DETAILS_NOTIFICATION_MEMBER_TODO)
@RequiredArgsConstructor
public class MBMeNotificationMemberController {

    private final MBMeMemberNotificationService mbMeMemberNotificationService;

    @GetMapping()
    public ResponseObject<?> getNotifAllicationMember(final MBMeFindNotificationMemberRequest request) {
        return mbMeMemberNotificationService.getAllNotificationMember(request);
    }

    @GetMapping("/count")
    public ResponseObject<?> countNotificationMember(@RequestParam("memberId") String memberId) {
        return mbMeMemberNotificationService.countNotificationMember(memberId);
    }

    @PutMapping("/update-status")
    public ResponseObject<?> updateStatus(@RequestParam("idNotificationMember") String idNotificationMember) {
        return mbMeMemberNotificationService.updateStatus(idNotificationMember);
    }

    @PutMapping("/update-all-status")
    public ResponseObject<?> updateAllStatus(@RequestParam("memberId") String memberId) {
        return mbMeMemberNotificationService.updateAllStatus(memberId);
    }

    @MessageMapping("/message-notification-member")
    @SendTo("/topic/subscribe-notification-member")
    public ResponseObject<?> sendNotificationMember(MBMessageNotificationMemberRequest request) {
        return new ResponseObject<>(true, HttpStatus.OK,"gửi thành công");
    }

}
