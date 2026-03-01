package udpm.hn.server.core.manage.todo.model.request;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@ToString
@Setter
public class MATodoStatisticsRequest {

    private String idProject;

    private String idPhase;
}
