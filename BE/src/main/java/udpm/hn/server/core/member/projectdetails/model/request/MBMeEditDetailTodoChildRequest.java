package udpm.hn.server.core.member.projectdetails.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MBMeEditDetailTodoChildRequest {

    @NotNull
    @NotBlank
    @NotEmpty
    private String idTodoChild;

    private String name;

}
