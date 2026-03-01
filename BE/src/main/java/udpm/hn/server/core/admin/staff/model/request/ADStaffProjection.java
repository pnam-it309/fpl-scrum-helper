package udpm.hn.server.core.admin.staff.model.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import udpm.hn.server.core.admin.staff.model.response.StaffResponse;

import java.util.HashSet;
import java.util.Set;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ADStaffProjection {

    private String id;

    private String orderNumber;

    private String codeStaff;

    private String nameStaff;

    private String emailFe;

    private String emailFpt;

    private String status;

    private String idRole;

    private String nameRole;

    private String nameFacility;

    private Set<String> nameRoles = new HashSet<>();

    public ADStaffProjection(StaffResponse projection) {
        this.id = projection.getId();
        this.orderNumber = projection.getOrderNumber();
        this.codeStaff = projection.getCodeStaff();
        this.nameStaff = projection.getNameStaff();
        this.emailFe = projection.getEmailFe();
        this.emailFpt = projection.getEmailFpt();
        this.status = projection.getStatus();
        this.nameFacility = projection.getNameFacility();
        this.idRole = projection.getIdRole();
    }

    public void addRole(String role) {
        if (role != null) {
            this.nameRoles.add(role);
        }
    }

    public String getNameRoles() {
        return String.join(", ", nameRoles);
    }

}
