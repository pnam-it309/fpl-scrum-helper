package udpm.hn.server.core.member.projectdetails.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MBMeUpdateCommentRequest {

    private String id;

    private String content;

    private String idTodo;

    private String idTodoList;

    private String idStaffProject;

}
