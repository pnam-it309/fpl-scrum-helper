package udpm.hn.server.core.manage.todo.model.request;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class MATodoCURequest {

    private String idType;

    private String idProject;

    private String name;

    private String code;

    private String status;

    private Integer priorityLevel;

    private Short point;

    private String idUser;
    private String urlPath;
    private String nameUser;

}
