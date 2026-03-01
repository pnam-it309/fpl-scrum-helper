package udpm.hn.server.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import udpm.hn.server.entity.base.PrimaryEntity;
import udpm.hn.server.infrastructure.constant.EntityProperties;
import udpm.hn.server.utils.StatusPhase;

import java.io.Serializable;

@Entity
@Table(name = "phase_project")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PhaseProject  extends PrimaryEntity implements Serializable {

    @Column(name = "code", length = EntityProperties.LENGTH_NAME)
    private String code;

    @Column(name = "name", length = EntityProperties.LENGTH_NAME)
    private String name;

    @Column(name = "start_time")

    private Long startTime;

    @Column(name = "end_time")
    private Long endTime;

    @Column(name = "descriptions")
    private String descriptions;

//    @Column(name = "progress")
//    private Float progress;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "phase_status")
    private StatusPhase statusPhase;

    @ManyToOne
    @JoinColumn(name = "id_project")
    private Project project;

}
