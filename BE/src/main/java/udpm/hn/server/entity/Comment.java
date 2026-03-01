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
@Table(name = "comment")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Comment extends PrimaryEntity implements Serializable {

    @ManyToOne
    @JoinColumn(name = "id_staff_project")
    private StaffProject staffProject;

    @ManyToOne
    @JoinColumn(name = "id_todo")
    private Todo todo;

    @Column(name = "content",length = EntityProperties.LENGTH_CONTENT)
    private String content;

    @Column(name = "status_edit")
    private Integer statusEdit;

}
