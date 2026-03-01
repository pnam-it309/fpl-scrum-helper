package udpm.hn.server.core.manage.phase.model.request;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class MACreatePhaseRequest {

    private String name;

    private String code;

    private Long startTime;

    private Long endTime;

    private String idProject;

    private String descriptions;

}
