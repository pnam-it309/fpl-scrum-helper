package udpm.hn.server.core.admin.project.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;


@Getter
@Setter
@AllArgsConstructor
@ToString
@NoArgsConstructor
@Builder
public class ADProjectCreateRequest {

    @NotBlank(message = "Tên không được để trống")
    private String nameProject;

    @NotBlank(message = "Mã không được để trống")
    private String codeProject;

    private String descriptions;

    @NotNull
    private Long startTime;

    @NotNull
    private Long endTime;

    private String idCategory;

    private String idMajorFacility;

    private List<ADProjectSTRequest> listMembers;

    private String emailLogin ;

}
