package udpm.hn.server.core.manage.user.model.request;

import lombok.*;
import udpm.hn.server.core.common.base.PageableRequest;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class MAUserRequest extends PageableRequest {

    String search;

}
