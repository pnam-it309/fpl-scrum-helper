package udpm.hn.server.core.admin.department.department.model.request;

import lombok.*;
import udpm.hn.server.core.common.base.PageableRequest;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ADMajorRequest extends PageableRequest {
    private String majorName;

}
