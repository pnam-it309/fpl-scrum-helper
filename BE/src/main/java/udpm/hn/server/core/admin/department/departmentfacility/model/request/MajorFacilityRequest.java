package udpm.hn.server.core.admin.department.departmentfacility.model.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import udpm.hn.server.core.common.base.PageableRequest;

@Getter
@Setter
public class MajorFacilityRequest extends PageableRequest {

    private String majorName;

    @NotNull
    private String departmentFacilityId ;

}
