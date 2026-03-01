package udpm.hn.server.infrastructure.doc.ADReportProject.model.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class GenerateReportRequest {

    private List<String> projectIds;

}
