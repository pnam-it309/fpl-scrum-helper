package udpm.hn.server.core.manage.phase.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MANotificationRequest {

    @NotEmpty
    @NotBlank
    @NotNull
    private String idPhase;

    private String projectId;

    private String idUser;

    private String urlPath;
}
