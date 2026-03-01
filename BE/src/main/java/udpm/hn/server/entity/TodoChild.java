package udpm.hn.server.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import udpm.hn.server.entity.base.PrimaryEntity;
import udpm.hn.server.infrastructure.constant.EntityProperties;
import udpm.hn.server.infrastructure.constant.StatusTodoChild;

import java.io.Serializable;

@Entity
@Table(name = "todo_child")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TodoChild extends PrimaryEntity implements Serializable {

    @Column(name = "name",length = EntityProperties.LENGTH_NAME)
    private String name;

    @Column(name = "code",length = EntityProperties.LENGTH_NAME)
    private String code;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "todochild_status")
    private StatusTodoChild statusTodoChild;

    @ManyToOne
    @JoinColumn(name = "id_todo")
    private Todo todo;

}
