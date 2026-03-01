package udpm.hn.server.core.admin.staff.model.request;


import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ADCreateStaffRequest {

    @NotBlank(message = "Tên không được để trống")
    private String name;

    @NotBlank(message = "Mã không được để trống")
    private String code;

    @NotBlank(message = "Email Fe khong được để trống")
    private String emailFe;

    @NotBlank(message = "Email fpt khong được để trống")
    private String emailFpt;

    private String idRole;

    private String emailLogin;

}
