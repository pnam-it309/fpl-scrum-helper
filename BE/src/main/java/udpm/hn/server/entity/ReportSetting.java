package udpm.hn.server.entity;

import jakarta.persistence.*;
import lombok.*;
import udpm.hn.server.entity.base.PrimaryEntity;

import java.io.Serializable;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Table(name = "report_setting")
public class ReportSetting extends PrimaryEntity implements Serializable {

    @Column(name = "stop_report_hour")
    private Long stopReportHour;

    @ManyToOne
    @JoinColumn(name = "id_project")
    private Project project;

}
