package udpm.hn.server.core.member.myproject.model.response;

import java.util.Date;

public interface MBMyProjectResponse {

    String getId();

    String getOrderNumber();

    String getNameProject();

    String getCodeProject();

    String getNameDepartment();

    String getStatus();

    String getIdFacility();

    Long getCreatedDateProject();

    Long getEndTimeProject();

    Long getStartTimeProject();

    Long getProgressProject();

}
