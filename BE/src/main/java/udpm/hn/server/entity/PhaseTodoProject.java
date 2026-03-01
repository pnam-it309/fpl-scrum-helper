package udpm.hn.server.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import udpm.hn.server.entity.base.PrimaryEntity;

import java.io.Serializable;

@Entity
@Table(name = "phase_todo_project")
@Getter
@Setter
@AllArgsConstructor
@ToString
@NoArgsConstructor
public class PhaseTodoProject  extends PrimaryEntity implements Serializable {

    @ManyToOne
    @JoinColumn(name = "id_todo")
    private Todo todo;

    @ManyToOne
    @JoinColumn(name = "id_phase_project")
    private PhaseProject phaseProject;

}
