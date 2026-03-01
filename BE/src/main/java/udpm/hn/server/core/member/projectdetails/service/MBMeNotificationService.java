package udpm.hn.server.core.member.projectdetails.service;

import jakarta.validation.Valid;
import udpm.hn.server.core.common.base.ResponseObject;
import udpm.hn.server.core.member.projectdetails.model.request.MBMeCreate1NotificationCommentRequest;
import udpm.hn.server.core.member.projectdetails.model.request.MBMeCreateNotificationCommentRequest;

public interface MBMeNotificationService  {

    ResponseObject<?> createNotification(@Valid MBMeCreateNotificationCommentRequest request);

    ResponseObject<?> create1Notification(@Valid MBMeCreate1NotificationCommentRequest request);


    /**
     * Tạo thông báo cho 1 người nhận duy nhất.
     * @param request chứa idUser (người tạo), idReceiver (người nhận), url, content
     */
    ResponseObject<?> createv2Notification(@Valid MBMeCreate1NotificationCommentRequest request);


}
