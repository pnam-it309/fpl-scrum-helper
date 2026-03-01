package udpm.hn.server.core.admin.student.model.request;

import lombok.*;
import udpm.hn.server.core.admin.staff.model.request.ADCreateStaffFDM;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ADUpdateStudentFDMRequest extends ADCreateStudentFDMRequest {

    private String idUpdateFacility;

    private String idUpdateDepartment;

    private String idUpdateMajor;
}
