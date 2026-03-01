package udpm.hn.server.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.*;
import udpm.hn.server.entity.base.PrimaryEntity;
import udpm.hn.server.infrastructure.constant.EntityProperties;
import udpm.hn.server.infrastructure.constant.StatusProject;

import java.io.Serializable;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
@Table(name = "project")
public class Project extends PrimaryEntity implements Serializable {

    @Column(name = "code", length = EntityProperties.LENGTH_NAME)
    private String code;

    @Column(name = "name", length = EntityProperties.LENGTH_NAME)
    private String name;

    @Column(name = "start_time")
    private Long startTime;

    @Column(name = "actual_end_date")
    private Long actualEndDate;

    @Column(name = "end_time")
    private Long endTime;

    @Column(name = "descriptions")
    private String descriptions;

    @Column(name = "progress")
    private Float progress;

    @Column(name = "background_color")
    private String backgroundColor;

    @Column(name = "background_image")
    private String backgroundImage;

    @ManyToOne
    @JoinColumn(name = "id_category")
    private Category category;

    @ManyToOne
    @JoinColumn(name = "id_major_facility")
    private MajorFacility majorFacility;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "project_status")
    private StatusProject statusProject;

}
