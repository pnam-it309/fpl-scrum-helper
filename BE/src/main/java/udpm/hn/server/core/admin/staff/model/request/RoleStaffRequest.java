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
public class RoleStaffRequest {

    private String idRole;

    private String idStaff;

    private String codeRole;

    private String emailLogin ;

}
