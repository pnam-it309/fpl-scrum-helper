package udpm.hn.server.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import udpm.hn.server.entity.base.PrimaryEntity;

import java.io.Serializable;


@Entity
@Table(name = "label_project_todo")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LabelProjectTodo extends PrimaryEntity implements Serializable {

    @ManyToOne
    @JoinColumn(name = "id_todo")
    private Todo todo;

    @ManyToOne
    @JoinColumn(name = "id_label_project")
    private LabelProject labelProject;
}
