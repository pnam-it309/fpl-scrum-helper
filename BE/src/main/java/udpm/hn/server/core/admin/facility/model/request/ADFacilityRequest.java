package udpm.hn.server.core.admin.facility.model.request;

import lombok.Getter;
import lombok.Setter;
import udpm.hn.server.core.common.base.PageableRequest;
import udpm.hn.server.infrastructure.constant.EntityStatus;

@Getter
@Setter
public class ADFacilityRequest extends PageableRequest {

    private String facilityName;

    private EntityStatus facilityStatus;

}
