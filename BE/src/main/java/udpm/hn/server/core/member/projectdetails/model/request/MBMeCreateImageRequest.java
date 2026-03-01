package udpm.hn.server.core.member.projectdetails.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MBMeCreateImageRequest {

    @NotBlank
    @NotNull
    @NotEmpty
    private String urlImage;

    @NotBlank
    @NotNull
    @NotEmpty
    private String nameFileOld;

    @NotBlank
    @NotNull
    @NotEmpty
    private String idTodo;

    private String idTodoList;

    private String projectId;

    private String idUser;

    private String urlPath;

}
