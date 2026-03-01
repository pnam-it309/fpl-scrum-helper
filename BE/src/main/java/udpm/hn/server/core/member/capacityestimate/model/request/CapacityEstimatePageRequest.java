package udpm.hn.server.core.member.capacityestimate.model.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import udpm.hn.server.core.common.base.PageableRequest;


@Getter
@ToString(callSuper = true)
@Setter
public class CapacityEstimatePageRequest extends PageableRequest {

    private String idProject;

    private String idUser;
}
