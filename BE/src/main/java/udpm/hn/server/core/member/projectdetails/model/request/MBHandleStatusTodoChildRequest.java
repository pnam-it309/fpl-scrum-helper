package udpm.hn.server.core.member.projectdetails.model.request;


import lombok.Getter;
import lombok.Setter;
import udpm.hn.server.infrastructure.constant.StatusTodoChild;

@Getter
@Setter
public class MBHandleStatusTodoChildRequest {

    private String idTodoChild;

    private StatusTodoChild statusTodoChild;

    private String idTodo ;

}
