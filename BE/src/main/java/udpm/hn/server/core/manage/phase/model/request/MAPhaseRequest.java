package udpm.hn.server.core.manage.phase.model.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import udpm.hn.server.core.common.base.PageableRequest;


@Getter
@ToString(callSuper = true)
@Setter
public class MAPhaseRequest extends PageableRequest {

    private String search;

    private String idProject;

    private Integer status;

    private Long time;

}
