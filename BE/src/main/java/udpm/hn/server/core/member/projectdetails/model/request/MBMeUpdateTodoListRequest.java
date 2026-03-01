package udpm.hn.server.core.member.projectdetails.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MBMeUpdateTodoListRequest {
    @NotNull
    @NotEmpty
    @NotBlank
    private String idTodoList;

    @NotBlank
    @NotEmpty
    @NotNull
    private String idProject;

    @NotNull
    @NotEmpty
    @NotBlank
    private String indexBefore;

    @NotNull
    @NotEmpty
    @NotBlank
    private String indexAfter;

    private String idPhase ;

    @NotNull
    @NotEmpty
    @NotBlank
    private String sessionId;
}
