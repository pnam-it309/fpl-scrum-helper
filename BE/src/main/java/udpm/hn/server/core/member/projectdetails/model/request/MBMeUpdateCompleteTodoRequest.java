package udpm.hn.server.core.member.projectdetails.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MBMeUpdateCompleteTodoRequest {

    @NotNull
    @NotBlank
    @NotEmpty
    private String id;

    @NotNull
    private Short status;

    @NotNull
    @NotBlank
    @NotEmpty
    private String idTodo;

    private String idTodoList;

    private String idUser;

    private String projectId;

    private String urlPath;

}
