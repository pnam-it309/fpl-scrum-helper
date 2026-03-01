package udpm.hn.server.core.manage.stage.model.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import udpm.hn.server.core.common.base.PageableRequest;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class StageVoteRequest extends PageableRequest {

    private String idProject;

}
