package udpm.hn.server.core.manage.todotable.model.response;

import com.fasterxml.jackson.annotation.JsonRawValue;
import udpm.hn.server.infrastructure.constant.PriorityLevel;
import udpm.hn.server.infrastructure.constant.StatusTodo;

import java.util.List;

public interface TodoByPhaseProjectResponse {
    String getOrderNumber();

    String getIdTodo();

    Short getIndexTodo();

    String getTodoName();

    String getDeadlineTodo();

    String getCompletionTime();

    List<TodoListPhaseResponse> getTodoList();

    String getCreatedDate();

    String getTodoListId();

    String getTodoListName();

    String getLabelName();

    StatusTodo getStatusTodo();
    PriorityLevel getPriorityLevel();

    String getTypeTodo();

    @JsonRawValue
    String getStudents();

    @JsonRawValue
    String getStaff();

    @JsonRawValue
    String getLabel();
}
