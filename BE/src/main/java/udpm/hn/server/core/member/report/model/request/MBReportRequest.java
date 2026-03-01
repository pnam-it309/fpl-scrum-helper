package udpm.hn.server.core.member.report.model.request;

import lombok.*;
import udpm.hn.server.core.common.base.PageableRequest;
import udpm.hn.server.infrastructure.constant.Help;
import udpm.hn.server.infrastructure.constant.StatusReport;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class MBReportRequest extends PageableRequest {

    private String workDoneToday;

    private String obstacles;

    private String workPlanTomorrow;

    private StatusReport statusReport;

    private Boolean help;

    private Long reportTime;

    private Long reportTimeEnd;

    private String idTodo;
}
