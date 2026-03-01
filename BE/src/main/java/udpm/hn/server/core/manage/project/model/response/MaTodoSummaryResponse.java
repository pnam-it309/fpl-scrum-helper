package udpm.hn.server.core.manage.project.model.response;

import udpm.hn.server.infrastructure.constant.StatusTodo;

public interface MaTodoSummaryResponse {

    Integer getAmount();

    StatusTodo getTodoStatus();

}
