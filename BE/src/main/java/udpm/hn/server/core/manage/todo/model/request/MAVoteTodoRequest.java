package udpm.hn.server.core.manage.todo.model.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;


@Getter
@Setter
@ToString
public class MAVoteTodoRequest {

    private String idProject;

    private String idUser;

    private String idTodo;

}
