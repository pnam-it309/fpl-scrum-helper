package udpm.hn.server.core.member.projectdetails.model.request;

import lombok.Getter;
import lombok.Setter;
import udpm.hn.server.core.common.base.PageableRequest;

@Getter
@Setter
public class MBMeFindCommentRequest extends PageableRequest {

    private String idTodo;

}
