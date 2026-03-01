package udpm.hn.server.core.member.myproject.model.request;

import lombok.Getter;
import lombok.Setter;
import udpm.hn.server.core.common.base.PageableRequest;
import udpm.hn.server.infrastructure.constant.EntityStatus;
import udpm.hn.server.infrastructure.constant.StatusProject;

@Getter
@Setter
public class MBMyProjectSearchRequest extends PageableRequest {

    private String projectName;

    private String idUser;

    private StatusProject ProjectStatus;


}
