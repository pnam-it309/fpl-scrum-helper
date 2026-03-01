package udpm.hn.server.core.admin.role.model.request;

import lombok.Getter;
import lombok.Setter;
import udpm.hn.server.core.common.base.PageableRequest;

@Getter
@Setter
public class ADRoleRequest extends PageableRequest {

    private String q; // Đổi từ roleName → q để khớp với request từ frontend
    private String department; // Đổi từ facilityName → department để đồng bộ
}
