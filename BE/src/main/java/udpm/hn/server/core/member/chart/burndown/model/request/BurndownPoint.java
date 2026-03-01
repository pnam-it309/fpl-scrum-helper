package udpm.hn.server.core.member.chart.burndown.model.request;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class BurndownPoint {

    private String date;

    private Integer expected;

    private Integer actual;

}
