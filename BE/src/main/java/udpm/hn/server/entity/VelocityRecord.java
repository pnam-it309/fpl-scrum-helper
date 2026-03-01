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
@Table(name = "velocity_record")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VelocityRecord  extends PrimaryEntity implements Serializable {


    @Column(name = "estimated_point")

    private Double estimatedPoint;

    @Column(name = "actual_point")
    private Double actualPoint;

    @Column(name = "estimated_working_days")
    private Integer estimatedWorkingDays;//ước tính ngày làm việc

    @Column(name = "actual_working_day")
    private Integer actualWorkingDay;//Ngày làm việc thực tế

    @ManyToOne
    @JoinColumn(name = "id_phase_project")
    private PhaseProject phaseProject;

}
