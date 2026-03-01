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
@Table(name = "todo_vote")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TodoVote extends PrimaryEntity implements Serializable {

    @ManyToOne
    @JoinColumn(name = "id_todo")
    private Todo todo;

    @ManyToOne
    @JoinColumn(name = "id_staff_project")
    private StaffProject staffProject;


    @ManyToOne
    @JoinColumn(name = "id_stage_vote")
    private StageVote stageVote;

    @JoinColumn(name = "status_vote")
    private Integer statusVote;

}
