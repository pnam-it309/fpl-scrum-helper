package udpm.hn.server.core.manage.project.model.response;

import udpm.hn.server.infrastructure.constant.EntityStatus;

public interface MaTodoListProjectResponse {

    Long getOrderNumber();

    String getTodoListId();

    String getCodeTodoList();

    String getNameTodoList();

    String getDescribeTodoList();

    EntityStatus getStatusTodoList();

    String getCreatedDate();

    String getNameProject();
}
