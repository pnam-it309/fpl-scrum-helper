package udpm.hn.server.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import udpm.hn.server.entity.base.PrimaryEntity;
import udpm.hn.server.infrastructure.constant.EntityProperties;

import java.io.Serializable;


@Entity
@Table(name = "facility")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Facility extends PrimaryEntity implements Serializable {

    @Column(name = "name",length = EntityProperties.LENGTH_NAME)
    private String name;

    @Column(name = "code",length = EntityProperties.LENGTH_NAME)
    private String code;

}
