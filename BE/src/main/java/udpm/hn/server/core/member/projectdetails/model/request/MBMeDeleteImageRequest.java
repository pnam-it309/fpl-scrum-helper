package udpm.hn.server.core.member.projectdetails.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MBMeDeleteImageRequest {

    @NotNull
    @NotBlank
    @NotEmpty
    private String id;

    @NotNull
    @NotBlank
    @NotEmpty
    private String urlImage;

    @NotNull
    @NotBlank
    @NotEmpty
    private String nameImage;

    @NotNull
    @NotBlank
    @NotEmpty
    private String statusImage;

    @NotNull
    @NotBlank
    @NotEmpty
    private String idTodo;

    private String idTodoList;

    private String projectId;

    private String idUser;

    private String urlPath;

}
