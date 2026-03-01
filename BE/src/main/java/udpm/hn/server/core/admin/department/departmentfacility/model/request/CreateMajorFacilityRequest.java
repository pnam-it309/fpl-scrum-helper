package udpm.hn.server.core.admin.department.departmentfacility.model.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateMajorFacilityRequest {

    @NotNull(message = "Vui lòng chọn chuyên ngành")
    private String majorId;

    @NotNull(message = "ID bộ môn theo cơ sở không được để trống")
    private String departmentFacilityId;

}
