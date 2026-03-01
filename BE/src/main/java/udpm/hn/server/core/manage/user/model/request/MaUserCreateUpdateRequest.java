package udpm.hn.server.core.manage.user.model.request;

import lombok.*;
import udpm.hn.server.core.admin.project.model.request.ADProjectSTRequest;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@ToString
@NoArgsConstructor
public class MaUserCreateUpdateRequest {

    private List<ADProjectSTRequest> listMembers;

}
