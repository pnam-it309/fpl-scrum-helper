package udpm.hn.server.core.manage.project.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import udpm.hn.server.entity.Staff;
import udpm.hn.server.entity.Student;
import udpm.hn.server.infrastructure.constant.PriorityLevel;
import udpm.hn.server.infrastructure.constant.StatusTodo;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TodoProjectResponseDTO {

    private String orderNumber;
    private String idTodo;
    private String nameTodo;
    private String deadline;
    private String descriptionsTodo;
    private StatusTodo statusTodo;
    private String namePhaseProject;
    private String picture;
    private String email;
    private PriorityLevel priorityLevel;
    private String createdDate;
    private String lastModifiedDate;
    private String phaseId;
    private String projectId;
    private List<Student> students;
    private List<Staff> staff;
}
