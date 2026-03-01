package udpm.hn.server.core.admin.staff.model.request;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@ToString
@NoArgsConstructor
public class ADCreateStaffFDM {

    private String idStaffDetail;

    private String idFacility;

    private String idDepartment;

    private String idMajor;

    private String emailLogin ;

}


