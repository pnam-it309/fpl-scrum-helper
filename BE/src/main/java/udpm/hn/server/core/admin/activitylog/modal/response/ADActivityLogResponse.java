package udpm.hn.server.core.admin.activitylog.modal.response;

public interface ADActivityLogResponse {

    String getId();

    Long getCreateDate();

    String getExecutorEmail();

    String getContent();

    String getRole();

}
