package udpm.hn.server.core.member.phase.model.request;

import lombok.Getter;
import lombok.Setter;
import udpm.hn.server.core.common.base.PageableRequest;


@Getter
@Setter
public class MBPhaseRequest extends PageableRequest {

    private String idProject;

    private String search;

}
