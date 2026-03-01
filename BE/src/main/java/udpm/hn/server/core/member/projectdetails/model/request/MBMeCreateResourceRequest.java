package udpm.hn.server.core.member.projectdetails.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
public class MBMeCreateResourceRequest {

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

    private String projectId;

    private String idUser;

    private String urlPath;

}
