package udpm.hn.server.core.member.projectdetails.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MBMeUpdateIndexTodoRequest {

    @NotNull
    @NotBlank
    @NotEmpty
    private String idTodo;

    @NotNull
    @NotBlank
    @NotEmpty
    private String idTodoListOld;

    @NotNull
    @NotBlank
    @NotEmpty
    private String nameTodoListOld;

    @NotNull
    @NotBlank
    @NotEmpty
    private String idTodoListNew;

    @NotNull
    @NotBlank
    @NotEmpty
    private String nameTodoListNew;

    private Short indexBefore;

    private Short indexAfter;

    // giai đoạn
    @NotNull
    @NotBlank
    @NotEmpty
    private String phaseId;

    @NotNull
    @NotBlank
    @NotEmpty
    private String projectId;

    private String idUser;

    private String sessionId;

    private String urlPath;

}
