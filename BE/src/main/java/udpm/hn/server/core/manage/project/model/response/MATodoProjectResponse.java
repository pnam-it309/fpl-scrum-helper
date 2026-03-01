package udpm.hn.server.core.manage.project.model.response;

import com.fasterxml.jackson.annotation.JsonRawValue;
import udpm.hn.server.infrastructure.constant.PriorityLevel;
import udpm.hn.server.infrastructure.constant.StatusTodo;

import java.util.List;

public interface MATodoProjectResponse {

    String getOrderNumber();

    String getIdTodo();

    String getNameTodo();

    String getDeadline();

    String getDescriptionsTodo();

    StatusTodo getStatusTodo();

    String getNamePhaseProject();

    List<MaPhaseProjectResponse> getPhaseProject();

    String getPicture();

    String getEmail();

    PriorityLevel getPriorityLevel();

    String getCreatedDate();

    String getLastModifiedDate();

    String getPhaseId();

    String getProjectId();

    @JsonRawValue
    String getStudents();

    @JsonRawValue
    String getStaff();
}
