package udpm.hn.server.core.member.report.model.request;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString

public class UpdateReportSettingRequest {
    private Long stopReportHour;
}
