package udpm.hn.server.core.admin.department.department.model.request;

import lombok.*;
import udpm.hn.server.core.common.base.PageableRequest;
import udpm.hn.server.infrastructure.constant.EntityStatus;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class DepartmentSearchRequest extends PageableRequest {

    private String departmentSearch;

    private EntityStatus departmentStatus;

}
