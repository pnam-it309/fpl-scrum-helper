package udpm.hn.server.core.member.projectdetails.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MBMeDeleteResourceRequest {

    @NotEmpty
    @NotBlank
    @NotNull
    private String id;

    private String url;

    private String name;

    private String idTodo;

    private String idTodoList;

    private String projectId;

    private String idUser;

    private String urlPath;

}
