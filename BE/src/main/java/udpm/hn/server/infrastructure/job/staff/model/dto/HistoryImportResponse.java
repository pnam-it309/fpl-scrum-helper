package udpm.hn.server.infrastructure.job.staff.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import udpm.hn.server.entity.Role;
import udpm.hn.server.entity.Staff;
import udpm.hn.server.infrastructure.constant.LogFileType;
import udpm.hn.server.infrastructure.constant.LogType;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class HistoryImportResponse {

    private String id;
    private String fileName;
    private String message;
    private LogType type;
    private LogFileType logFileType;
    private List<Role> roles;
    private Staff staff;
    private Long createdDate;

}
