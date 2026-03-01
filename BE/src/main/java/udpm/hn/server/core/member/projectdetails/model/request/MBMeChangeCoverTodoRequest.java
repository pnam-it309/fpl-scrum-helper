package udpm.hn.server.core.member.projectdetails.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
public class MBMeChangeCoverTodoRequest {

    @NotNull
    @NotBlank
    @NotEmpty
    private String idTodo;

    @NotNull
    @NotBlank
    @NotEmpty
    private String idImage;

    @NotNull
    @NotBlank
    @NotEmpty
    @Length(max = 100)
    private String urlImage;

    private String idTodoList;

    @NotNull
    @NotBlank
    @NotEmpty
    private String status;
}
