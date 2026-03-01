package udpm.hn.server.core.member.projectdetails.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import udpm.hn.server.core.common.base.ResponseObject;
import udpm.hn.server.core.member.projectdetails.model.request.MBMeCreate1NotificationCommentRequest;
import udpm.hn.server.core.member.projectdetails.model.request.MBMeCreateNotificationCommentRequest;
import udpm.hn.server.core.member.projectdetails.service.MBMeNotificationService;
import udpm.hn.server.infrastructure.constant.MappingConstants;

@RestController
@RequestMapping(MappingConstants.API_PROJECT_DETAILS_NOTIFICATION_TODO)
@RequiredArgsConstructor
public class MBMeNotificationController {

    @Autowired
    private MBMeNotificationService mbMeNotificationService;

    @PostMapping
    public ResponseObject<?> createNotificationTag(@RequestBody MBMeCreateNotificationCommentRequest request) {
        return mbMeNotificationService.createNotification(request);
    }

    @PostMapping("/v1")
    public ResponseObject<?> createV1NotificationTag(@RequestBody MBMeCreate1NotificationCommentRequest request) {
        return mbMeNotificationService.create1Notification(request);
    }

    @PostMapping("/v2")
    public ResponseObject<?> createV2NotificationTag(@RequestBody MBMeCreate1NotificationCommentRequest request) {
        return mbMeNotificationService.createv2Notification(request);
    }

    @MessageMapping("/create-notification")
        @SendTo("/topic/create-notification")
        public ResponseObject<?> sendNotification(@DestinationVariable String memberId) {
            return new ResponseObject<>(true, HttpStatus.OK,"gửi thành công");
    }

}
