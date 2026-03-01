package udpm.hn.server.core.member.phase.model.request;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
@Setter
public class MBTodoByPhase {

    private List<String> idTodo;

    private String idPhase;

    private String idProject;

}
