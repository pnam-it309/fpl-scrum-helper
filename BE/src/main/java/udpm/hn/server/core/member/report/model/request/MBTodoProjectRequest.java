package udpm.hn.server.core.member.report.model.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import udpm.hn.server.core.common.base.PageableRequest;

@Getter
@Setter
@ToString
public class MBTodoProjectRequest extends PageableRequest {

    private String name;
}
