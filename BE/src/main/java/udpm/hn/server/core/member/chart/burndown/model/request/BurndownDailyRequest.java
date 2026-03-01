package udpm.hn.server.core.member.chart.burndown.model.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BurndownDailyRequest {

    private String date;
    private Double estimatedNumberOfTasks;
    private Long reality;
    private Long totalActual;
    private Long remaining;

}
