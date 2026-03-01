package udpm.hn.server.core.admin.department.departmentfacility.model.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class CreateOrUpdateDepartmentFacilityRequest {

    private String departmentId;

    @NotBlank(message = "Cơ sở không được để trống")
    private String facilityId;

}
