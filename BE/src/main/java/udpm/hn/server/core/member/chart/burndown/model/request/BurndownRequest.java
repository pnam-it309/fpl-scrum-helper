package udpm.hn.server.core.member.chart.burndown.model.request;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class BurndownRequest {

    private Double estimatedNumberOfTasks;

    private List<Long> reality;

    private Long totalActual;

    private Long remaining;

    private List<BurndownDailyRequest> byDate;
}
