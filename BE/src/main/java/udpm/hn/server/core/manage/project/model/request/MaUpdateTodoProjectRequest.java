package udpm.hn.server.core.manage.project.model.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import udpm.hn.server.infrastructure.constant.PriorityLevel;
import udpm.hn.server.infrastructure.constant.StatusTodo;

@Getter
@Setter
@ToString
public class MaUpdateTodoProjectRequest {

    private String projectId;

    private String nameTodo;

    private StatusTodo statusTodo;

    private String idStaffProject;

    private PriorityLevel priorityLevel;

}
