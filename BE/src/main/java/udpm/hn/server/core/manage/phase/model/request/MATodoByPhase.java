package udpm.hn.server.core.manage.phase.model.request;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import udpm.hn.server.core.common.base.PageableRequest;

import java.util.List;

@Getter
@ToString
@Setter

public class MATodoByPhase extends PageableRequest {

    private List<String> idTodo;

    private String idPhase;

    private String idProject;

    private String index;



    private String idUser;
    private String urlPath;
    private String nameUser;

}
