package udpm.hn.server.core.manage.project.model.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import udpm.hn.server.core.common.base.PageableRequest;
import udpm.hn.server.infrastructure.constant.PriorityLevel;
import udpm.hn.server.infrastructure.constant.StatusTodo;
import udpm.hn.server.utils.StatusPhase;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MaTodoListProjectRequest extends PageableRequest {

    private String search;

    private StatusTodo status;

    private PriorityLevel level;
}
