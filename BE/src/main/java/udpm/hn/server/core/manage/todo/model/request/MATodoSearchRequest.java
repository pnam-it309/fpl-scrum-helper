package udpm.hn.server.core.manage.todo.model.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import udpm.hn.server.core.common.base.PageableRequest;


@Getter
@Setter
@ToString
public class MATodoSearchRequest extends PageableRequest {

    private String search;

    private String idProject;

    private Integer level;

}
