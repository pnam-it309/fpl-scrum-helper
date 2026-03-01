package udpm.hn.server.core.manage.todotable.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import udpm.hn.server.entity.LabelProject;
import udpm.hn.server.entity.Staff;
import udpm.hn.server.entity.Student;
import udpm.hn.server.infrastructure.constant.PriorityLevel;
import udpm.hn.server.infrastructure.constant.StatusTodo;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TodoPhaseProjectDTO {
    private String orderNumber;

    private String idTodo;

    private Short indexTodo;

    private String todoName;

    private String deadlineTodo;

    private String completionTime;

    private String labelName;

//    List<TodoListPhaseResponse> getTodoList();

    private String createdDate;

    private String todoListId;

    private String todoListName;

    private StatusTodo statusTodo;

    private PriorityLevel priorityLevel;

    private List<Student> students;

    private List<Staff> staff;

    private List<LabelProject> label;

    private String typeTodo;
}
