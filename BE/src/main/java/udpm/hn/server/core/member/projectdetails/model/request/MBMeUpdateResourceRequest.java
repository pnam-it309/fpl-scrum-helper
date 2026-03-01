package udpm.hn.server.core.member.projectdetails.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
public class MBMeUpdateResourceRequest {


    @NotEmpty
    @NotBlank
    @NotNull
    private String id;

    @Length(max = 100)
    private String name;

    @NotEmpty
    @NotBlank
    @NotNull
    private String url;

    @NotEmpty
    @NotBlank
    @NotNull
    private String idTodo;

    private String idTodoList;

}
