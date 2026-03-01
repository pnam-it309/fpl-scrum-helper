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
import lombok.ToString;
import udpm.hn.server.entity.base.PrimaryEntity;
import udpm.hn.server.infrastructure.constant.EntityProperties;

import java.io.Serializable;


@Entity
@Table(name = "student")
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@Setter
public class Student  extends PrimaryEntity implements Serializable {

    @Column(name = "code", length = EntityProperties.LENGTH_CODE)
    private String code;

    @Column(name = "name", length = EntityProperties.LENGTH_NAME)
    private String name;

    @Column(name = "email")
    private String email;

    @Column(name = "image",length = EntityProperties.LENGTH_PICTURE)
    private String image;

    @ManyToOne
    @JoinColumn(name = "id_major_facility")
    private MajorFacility majorFacility;

}
