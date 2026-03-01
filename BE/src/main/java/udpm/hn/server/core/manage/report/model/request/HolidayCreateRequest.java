package udpm.hn.server.core.manage.report.model.request;

import lombok.*;
import udpm.hn.server.infrastructure.constant.StatusReport;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class HolidayCreateRequest {

    private List<Long> dates;

    private String describe;

}
