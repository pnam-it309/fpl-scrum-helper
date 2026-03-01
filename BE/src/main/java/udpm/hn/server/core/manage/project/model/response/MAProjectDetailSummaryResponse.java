package udpm.hn.server.core.manage.project.model.response;

import udpm.hn.server.infrastructure.constant.StatusProject;

public interface MAProjectDetailSummaryResponse {

    String getId();

    String getName();

    String getCode();

    String getNameDepartment();

    Long getStartTime();

    Long getEndTime();

    StatusProject getStatus();

    Long getCreatedDateProject();

    String getCategory();

    Integer getAmountOfMembers();

    Long getActualEndTime();

}
