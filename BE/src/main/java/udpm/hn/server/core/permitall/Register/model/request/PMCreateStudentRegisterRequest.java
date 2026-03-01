package udpm.hn.server.core.permitall.Register.model.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class PMCreateStudentRegisterRequest {

    private String idFacility;

    private String idDepartment;

    private String idMajor;

    @NotBlank(message = "Mã sinh viên không được để trống")
    @Length(max = 50,message = "Mã của sinh viên không được dài quá 50 ký tự")
    private String code;

    @NotBlank(message = "Tên sinh viên không được để trống")
    @Length(max = 50,message = "Tên của sinh viên không được dài quá 50 ký tự")
    private String name;

    @NotBlank(message = "Email không được để trống")
    private String email;

}
