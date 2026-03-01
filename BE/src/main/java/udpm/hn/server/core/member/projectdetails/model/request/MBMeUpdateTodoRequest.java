package udpm.hn.server.core.member.projectdetails.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import udpm.hn.server.infrastructure.constant.PriorityLevel;

@Getter
@Setter
public class MBMeUpdateTodoRequest extends MBMeTodoAndTodoListRequest{
    @NotNull
    @NotBlank
    @NotEmpty
    private String idTodoChange;

    private Integer priorityLevel;
}
