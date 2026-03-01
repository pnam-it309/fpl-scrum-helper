package udpm.hn.server.infrastructure.doc.ADReportProject.model.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReportContent {
    private String projectName;
    private String username;
    private String reportName;
    private String reportBody;
}
