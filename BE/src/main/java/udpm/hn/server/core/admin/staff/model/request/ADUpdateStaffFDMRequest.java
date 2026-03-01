package udpm.hn.server.core.admin.staff.model.request;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ADUpdateStaffFDMRequest extends ADCreateStaffFDM {

    private String idUpdateFacility;

    private String idUpdateDepartment;

    private String idUpdateMajor;

}
