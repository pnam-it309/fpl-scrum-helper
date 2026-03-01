package udpm.hn.server.core.member.projectdetails.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MBMeUpdateDeadlineTodoRequest extends MBMeTodoAndTodoListRequest{

    private String idTodoUpdate;

    @NotBlank
    @NotEmpty
    private String deadline;

    private Long reminder;

    private String projectId;

    private String idUser;

    private String urlPath;

}
