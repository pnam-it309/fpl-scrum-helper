package udpm.hn.server.core.member.projectdetails.model.request;

public interface MBMeNotificationMemberResponse {

    String getId();

    String getNotificationId();

    String getContent();

    String getUrl();

    Integer getStatus();

    Long getCreatedDate();

    String getTodoId();

}
