package udpm.hn.server.infrastructure.job.MBReport.Export.model.request;

import lombok.*;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
@Setter
public class ReportData {

    private String workDone;

    private String obstacles;

    private String workPlan;

    private Long reportTime;

}
