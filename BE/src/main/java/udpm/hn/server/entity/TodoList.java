package udpm.hn.server.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import org.hibernate.annotations.Index;
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
import udpm.hn.server.infrastructure.constant.StatusListTodo;

import java.io.Serializable;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Table(name = "todo_list")
public class   TodoList extends PrimaryEntity implements Serializable {

    @Column(name = "code",length = EntityProperties.LENGTH_NAME)
    private String code;

    @Column(name = "name",length = EntityProperties.LENGTH_NAME)
    private String name;

    @Column(name = "describe",length = EntityProperties.LENGTH_CONTENT)
    private String describe;

    @Column(nullable = false)
    @Index(name = "idx_todo_list_id")
    private Byte indexTodoList;


    @Column(name = "status_list_todo")
    @Enumerated(EnumType.ORDINAL)
    private StatusListTodo StatusListTodo;

    @ManyToOne
    @JoinColumn(name = "id_project")
    private Project project;

    @ManyToOne
    @JoinColumn(name = "id_phase_project")
    private PhaseProject phaseProject;
}
