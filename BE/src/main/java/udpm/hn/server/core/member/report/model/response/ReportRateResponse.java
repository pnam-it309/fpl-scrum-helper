package udpm.hn.server.core.member.report.model.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ReportRateResponse {

    private Long totalPhaseDays;
    private Long holidayCount;
    private Long reportCount;
    private Double reportRate;

}
