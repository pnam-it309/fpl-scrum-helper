package udpm.hn.server.core.member.projectdetails.model.response;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MBUpdateBackgroundProject {

    @NotNull
    @NotEmpty
    private String idProject;

    private String backgroundColor ;

    private String backgroundImage;

}
