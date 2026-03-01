package udpm.hn.server.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import udpm.hn.server.entity.base.PrimaryEntity;
import udpm.hn.server.infrastructure.constant.EntityProperties;

import java.io.Serializable;

@Builder
@Entity
@Table(name = "type_todo")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TypeTodo  extends PrimaryEntity implements Serializable {

    @Column(name = "type", length = EntityProperties.LENGTH_NAME)
    private String type;

}
