package udpm.hn.server.core.manage.project.model.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class MaCreateOrUpdateTodoListProject {

    private String todoListId;

    private String projectId;

    private String codeTodoList;

    private String nameTodoList;

    private String describeTodoList;

    private Byte indexTodoList;

}
