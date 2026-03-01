package udpm.hn.server.core.member.projectdetails.model.response;

import org.springframework.beans.factory.annotation.Value;

public interface MBMeImageResponse {

    String getId();

    String getUrlImage();

    String getNameImage();

    Integer getStatusImage();

    String getTodoId();

    Long getCreatedDate();

}
