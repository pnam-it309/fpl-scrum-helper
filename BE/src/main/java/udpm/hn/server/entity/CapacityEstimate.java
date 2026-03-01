package udpm.hn.server.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import udpm.hn.server.entity.base.PrimaryEntity;

import java.io.Serializable;
@Entity
@Table(name = "capacity_estimate")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CapacityEstimate extends PrimaryEntity implements Serializable {

    @Column(name = "workday")
    private Short workday;// số ngày làm việc thực tế

    @Column(name = "adjusted_story_points")
    private Integer adjustedStoryPoints; //ước lượng năng lực

    @Column(name = "actual_capacity_achieved")
    private Integer actualCapacityAchieved;//Năng lực thực tế đạt được

    @Column(name = "describe")
    private String describe;

    @ManyToOne
    @JoinColumn(name = "id_staff_project")
    private StaffProject staffProject ;

    @ManyToOne
    @JoinColumn(name = "id_phase_project")
    private PhaseProject phaseProject;

}
