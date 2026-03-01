package udpm.hn.server.core.member.projectdetails.model.response;

public interface MBMeCommentResponse {

    String getId();

    String getContent();

    String getTodoId();

    String getProjectId();

    String getStaffProjectId();

    Integer getStatusEdit();

    Long getCreatedDate();

    // Dữ liệu thành viên (chỉ lấy một phía: student hoặc staff)
    String getUserId();

    String getUserCode();

    String getUserName();

    String getUserEmail();

    String getUserImage();

}
