package udpm.hn.server.core.manage.project.model.request;

import lombok.Getter;
import lombok.Setter;
import udpm.hn.server.core.common.base.PageableRequest;

@Getter
@Setter
public class MaUserProjectRequest extends PageableRequest {

    private String idProject;
}
