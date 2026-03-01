package udpm.hn.server.core.member.projectdetails.model.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MBMeActivityConvertResponse {
    private String memberCreatedId;
    private String memberId;
    private String projectId;
    private String todoId;
    private String todoListId;
    private String contentAction;
    private String contentActionProject;
    private String urlImage;
    private Long createdDate;
    private Integer totalRecords;
    private String urlPath;
    // Thêm thông tin thành viên
    private String memberName;
    private String memberEmail;
    private String memberImage;
}
