package udpm.hn.server.core.member.projectdetails.model.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class MBTodoAndTodoListObject {
    private Object data;

    private Object dataActivity;

    private String idTodoListOld;

    private Integer indexBefore;

    private Integer indexAfter;

    private String sessionId;
}
