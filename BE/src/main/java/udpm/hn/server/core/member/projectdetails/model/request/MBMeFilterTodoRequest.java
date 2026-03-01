package udpm.hn.server.core.member.projectdetails.model.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class MBMeFilterTodoRequest {

    private String projectId;

    private String idPhase;

    private String idTodoList;

    private String name;

//    private List<String> member;

//    private List<String> label;

//    private List<String> dueDate;
}
