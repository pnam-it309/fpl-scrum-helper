package udpm.hn.server.core.member.chart.burndown.model.reponse;

import lombok.*;
import udpm.hn.server.core.member.chart.burndown.model.request.BurndownPoint;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class BurndownChartResponse {

    private List<BurndownPoint> data;

    private List<Integer> totalActualUntilNow;

}
