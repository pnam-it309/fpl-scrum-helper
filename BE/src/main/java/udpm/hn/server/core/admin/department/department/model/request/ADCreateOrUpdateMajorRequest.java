package udpm.hn.server.core.admin.department.department.model.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ADCreateOrUpdateMajorRequest {

    @NotBlank(message = "Bộ môn chưa được chọn")
    private String departmentId;

    @NotBlank(message = "Tên chuyên ngành không được để trống")
    private String majorName;

    private String majorCode;

    private String majorId;

    private Integer majorStatus;

    private Long createdDate;
}
