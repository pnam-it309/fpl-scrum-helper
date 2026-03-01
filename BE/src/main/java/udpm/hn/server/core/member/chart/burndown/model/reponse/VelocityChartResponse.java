package udpm.hn.server.core.member.chart.burndown.model.reponse;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class VelocityChartResponse {
    private String phaseName;
    private Double estimated;
    private Double actual;
}
