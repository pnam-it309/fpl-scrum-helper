package udpm.hn.server.core.manage.project.model.response;

public interface MaProjectResponse {

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

    Float getProgressProject();

}
