package udpm.hn.server.core.member.projectdetails.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MBMeDeleteTodoListRequest {
    @NotBlank
    @NotNull
    @NotEmpty
    private String id;

    @NotBlank
    @NotNull
    @NotEmpty
    private String projectId;

    private String idPhase ;

}
