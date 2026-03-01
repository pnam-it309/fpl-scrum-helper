package udpm.hn.server.core.member.projectdetails.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MBMeJoinOrOutLabelTodoRequest extends MBMeTodoAndTodoListRequest{

    @NotNull
    @NotBlank
    @NotEmpty
    private String idLabel;

    @NotNull
    @NotBlank
    @NotEmpty
    private String idTodoJoinOrOut;

}
