package udpm.hn.server.core.member.projectdetails.model.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class MBMeCreateTodoResponse {

    private String idTodo;

    private String name;

    private String idTodoList;

    private String nameTodoList;

    private int indexTodo;

    private String phaseId;

    private String projectId;

    private String createBy;

    private String deadline;

    private List<MBMeConvertLabelResponse> labels;

}
