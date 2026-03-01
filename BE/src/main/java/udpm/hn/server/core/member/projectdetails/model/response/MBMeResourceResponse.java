package udpm.hn.server.core.member.projectdetails.model.response;

import org.springframework.beans.factory.annotation.Value;

public interface MBMeResourceResponse {

    String getId();

    String getName();

    String getUrl();

    String getTodoId();

    Long getCreatedDate();

}
