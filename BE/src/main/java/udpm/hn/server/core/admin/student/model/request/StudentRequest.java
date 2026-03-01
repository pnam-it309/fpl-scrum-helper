package udpm.hn.server.core.admin.student.model.request;

import lombok.Getter;
import lombok.Setter;
import udpm.hn.server.core.common.base.PageableRequest;
import udpm.hn.server.infrastructure.constant.EntityStatus;

@Getter
@Setter
public class StudentRequest extends PageableRequest {

    private String search;

    private String searchDF;

    private EntityStatus status;

}
