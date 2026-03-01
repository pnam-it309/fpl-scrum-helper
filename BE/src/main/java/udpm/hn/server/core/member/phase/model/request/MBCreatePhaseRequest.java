package udpm.hn.server.core.member.phase.model.request;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MBCreatePhaseRequest {

    private String name;

    private String code;

    private Long startTime;

    private Long endTime;

    private String idProject;

}
