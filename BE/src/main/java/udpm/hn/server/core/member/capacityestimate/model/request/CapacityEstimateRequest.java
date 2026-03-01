package udpm.hn.server.core.member.capacityestimate.model.request;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@ToString
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CapacityEstimateRequest {

    private String idSprint;

    private String idProject;

    private Short workday;

    private String idUser;

    private String descriptions;

    private Integer availableHours;

//    private Integer adjustedStoryPoints;

}
