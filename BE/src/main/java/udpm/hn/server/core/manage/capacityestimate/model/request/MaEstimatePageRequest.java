package udpm.hn.server.core.manage.capacityestimate.model.request;

import lombok.Getter;
import lombok.Setter;
import udpm.hn.server.core.common.base.PageableRequest;

@Getter
@Setter
public class MaEstimatePageRequest extends PageableRequest {

    private String idProject;

}
