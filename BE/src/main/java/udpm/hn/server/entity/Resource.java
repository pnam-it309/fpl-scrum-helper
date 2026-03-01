package udpm.hn.server.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import udpm.hn.server.entity.base.PrimaryEntity;
import udpm.hn.server.infrastructure.constant.EntityProperties;

import java.io.Serializable;

@Entity
@Table(name = "resource")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Resource extends PrimaryEntity implements Serializable {

    @Column(name = "name",length = EntityProperties.LENGTH_NAME)
    private String name;

    @Column(name = "url",length = EntityProperties.LENGTH_URL)
    private String url;

    @ManyToOne
    @JoinColumn(name = "id_todo")
    private Todo todo;

}
