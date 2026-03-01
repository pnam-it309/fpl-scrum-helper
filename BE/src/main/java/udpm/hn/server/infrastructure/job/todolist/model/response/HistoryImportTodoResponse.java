package udpm.hn.server.infrastructure.job.todolist.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import udpm.hn.server.entity.Role;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class HistoryImportTodoResponse {

    private String id;
    private String email;
    private String message;
    private List<Role> roles;
    private Long createdDate;

    private String idProject;

}
