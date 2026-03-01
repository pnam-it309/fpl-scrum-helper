package udpm.hn.server.core.manage.report.model.request;

import lombok.Getter;
import lombok.Setter;
import udpm.hn.server.core.common.base.PageableRequest;


@Getter
@Setter
public class MAReportRequest extends PageableRequest {

    private Long time;

    private String idProject;
}
