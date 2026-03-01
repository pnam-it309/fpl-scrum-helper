package udpm.hn.server.core.member.projectdetails.model.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ADCreateUpdateTypeTodoRequest {

    @NotBlank(message = "loại không được để trống")
    private String type;

    private String typeId;

}
