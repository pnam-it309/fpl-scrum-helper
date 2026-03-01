package udpm.hn.server.entity;

import jakarta.persistence.*;
import lombok.*;
import udpm.hn.server.entity.base.PrimaryEntity;
import udpm.hn.server.infrastructure.constant.EntityProperties;
import udpm.hn.server.infrastructure.constant.StatusReport;
import udpm.hn.server.infrastructure.constant.StatusWorking;

import java.io.Serializable;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Table(name = "holiday")

public class Holiday extends PrimaryEntity implements Serializable {

    @Column(name = "code")
    private String code;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "report_status")
    private StatusReport statusReport;

    @Column(name = "date")
    private Long date;

    @Column(name = "describe")
    private String describe;

    @ManyToOne
    @JoinColumn(name = "id_project")
    private Project project;

}
