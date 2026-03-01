package udpm.hn.server.core.member.projectdetails.model.request;

import lombok.Getter;
import lombok.Setter;
import udpm.hn.server.core.common.base.PageableRequest;
import udpm.hn.server.infrastructure.constant.EntityStatus;

@Getter
@Setter
public class MBTypeTodoRequest {

    private String typeName;

    private EntityStatus typeStatus;

}
