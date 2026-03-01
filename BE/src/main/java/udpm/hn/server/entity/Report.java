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
import lombok.ToString;
import udpm.hn.server.entity.base.PrimaryEntity;
import udpm.hn.server.infrastructure.constant.EntityProperties;
import udpm.hn.server.infrastructure.constant.Help;
import udpm.hn.server.infrastructure.constant.StatusReport;
import udpm.hn.server.infrastructure.constant.StatusWorking;

import java.io.Serializable;


@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Table(name = "report")

public class Report extends PrimaryEntity implements Serializable {

    @Column(name = "work_done_today", length = EntityProperties.LENGTH_CONTENT)
    private String workDoneToday;

    @Column(name = "obstacles", length = EntityProperties.LENGTH_CONTENT)
    private String obstacles;

    @Column(name = "work_plan_tomorrow", length = EntityProperties.LENGTH_CONTENT)
    private String workPlanTomorrow;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "report_status")
    private StatusReport statusReport;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "help")
    private Help help;

    @Column(name = "report_time")
    private Long reportTime;

    @Column(name = "status_working")
    private StatusWorking statusWorking;

    @Column(name = "describe")
    private String describe;

    @ManyToOne
    @JoinColumn(name = "id_todo")
    private Todo todo;

    @ManyToOne
    @JoinColumn(name = "id_project")
    private Project project;

    @ManyToOne
    @JoinColumn(name = "id_staff_project")
    private StaffProject staffProject;

}
