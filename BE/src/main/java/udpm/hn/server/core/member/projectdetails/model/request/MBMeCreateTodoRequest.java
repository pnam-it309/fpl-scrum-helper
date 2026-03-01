package udpm.hn.server.core.member.projectdetails.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MBMeCreateTodoRequest {

    @NotEmpty
    @NotBlank
    @NotNull
    private String idTodo;

    @NotEmpty
    @NotBlank
    @NotNull
    private String name;

    @NotEmpty
    @NotBlank
    @NotNull
    private String idTodoList;

    private String phaseId;

    @NotEmpty
    @NotBlank
    @NotNull
    private String projectId;

    private Short indexTodo;

    private String idUser;

    private String sessionId;
}
