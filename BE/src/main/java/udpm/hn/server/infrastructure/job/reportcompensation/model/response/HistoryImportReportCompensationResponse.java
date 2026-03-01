package udpm.hn.server.infrastructure.job.reportcompensation.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import udpm.hn.server.entity.Role;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class HistoryImportReportCompensationResponse {

    private String id;
    private String email;
    private String message;
    private List<Role> roles;
    private Long createdDate;

}
