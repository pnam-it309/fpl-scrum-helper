package udpm.hn.server.core.member.projectdetails.model.request;

import lombok.Getter;
import lombok.Setter;
import udpm.hn.server.core.common.base.PageableRequest;

@Getter
@Setter
public class MBMeFindNotificationMemberRequest extends PageableRequest {

    private String idMember;

}
