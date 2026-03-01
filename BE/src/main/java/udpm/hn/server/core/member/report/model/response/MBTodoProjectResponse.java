package udpm.hn.server.core.member.report.model.response;

import udpm.hn.server.infrastructure.constant.PriorityLevel;

public interface MBTodoProjectResponse {

    String getIdTodo();

    Long getOrderNumber();

    String getNameTodo();

    Long getCreatedDate();

    String getProjectId();

    PriorityLevel getPriorityLevel();

    Long getEndTime();

    Long getStartTime();
}
