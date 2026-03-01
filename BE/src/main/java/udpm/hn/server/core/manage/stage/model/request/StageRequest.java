package udpm.hn.server.core.manage.stage.model.request;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StageRequest {

    String idProject;

    String idStage;

    Long startTime;

    Long endTime;

    String idPhase;

}
