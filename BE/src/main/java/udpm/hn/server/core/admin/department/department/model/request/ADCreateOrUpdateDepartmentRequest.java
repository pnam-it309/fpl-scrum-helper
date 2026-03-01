package udpm.hn.server.core.admin.department.department.model.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ADCreateOrUpdateDepartmentRequest {
    private String departmentId;

    @NotBlank(message = "Tên bộ môn không được để trống")
    @Length(max = 50,message = "Tên của bộ môn không được dài quá 50 ký tự")
    private String departmentName;

    @NotBlank(message = "Mã bộ môn không được để trống")
    @Length(max = 50,message = "Mã của bộ môn không được dài quá 50 ký tự")
    private String departmentCode;

    private Integer departmentStatus;

    private Long createdDate;
}
