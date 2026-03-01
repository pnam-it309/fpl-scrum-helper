package udpm.hn.server.core.member.projectdetails.model.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MBMeCreate1NotificationCommentRequest {

    private String idProject;   // ✅ Project để lấy toàn bộ thành viên
    private String url;         // ✅ URL để điều hướng khi click thông báo
    private String idUser;      // ✅ Người tạo thông báo
    private String username;    // ✅ Tên người tạo thông báo
    private String content;     // ✅ Nội dung tuỳ biến (ví dụ "Giai đoạn Sprint 1 đã hoàn thành")
    private String idTodo;      // ✅ Optional, giữ lại cho backward compatibility

    private String idReceiver;  // ✅ Người nhận thông báo duy nhất

}
