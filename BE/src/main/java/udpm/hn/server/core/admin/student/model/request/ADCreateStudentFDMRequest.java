package udpm.hn.server.core.admin.student.model.request;


import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@ToString
@NoArgsConstructor
public class ADCreateStudentFDMRequest {

    private String idStudentDetail;

    private String idFacility;

    private String idDepartment;

    private String idMajor;

    private String emailLogin;

}
