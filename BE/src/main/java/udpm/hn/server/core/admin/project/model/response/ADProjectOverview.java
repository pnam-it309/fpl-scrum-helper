package udpm.hn.server.core.admin.project.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ADProjectOverview {
    private Long totalProjects;
    private Long inProgress;
    private Long done;
    private Long paused;
    private Long overdue;

}
