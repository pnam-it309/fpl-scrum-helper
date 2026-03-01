package udpm.hn.server.core.member.projectdetails.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MBMeDeleteLabelProjectRequest {

    @NotEmpty
    @NotBlank
    @NotNull
    private String idLabelProject;

    @NotEmpty
    @NotBlank
    @NotNull
    private String projectId;

}
