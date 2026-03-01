package udpm.hn.server.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.*;
import udpm.hn.server.entity.base.PrimaryEntity;
import udpm.hn.server.infrastructure.constant.*;

import java.io.Serializable;

@Builder
@Entity
@Table(name = "todo")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Todo  extends PrimaryEntity implements Serializable {

    @Column(name = "code", length = EntityProperties.LENGTH_NAME)
    private String code;

    @Column(name = "name", length = EntityProperties.LENGTH_NAME)
    private String name;

    @Column(name = "completion_time")
    private Long completionTime;

    @Enumerated(EnumType.ORDINAL)
    @Column(name="priority_level")
    private PriorityLevel priorityLevel;

    private Long deadline;

    @Column(name = "delivery_date")
    private Long deliveryDate;

    @Column(name = "status_reminder")
    private StatusReminder statusReminder;

    @Column(name = "progress")
    private Float progress;

    @Column(name = "story_point")
    private Short storyPoint;

    @Column
    private Short indexTodo;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "todo_status")
    private StatusTodo statusTodo;

    @ManyToOne
    @JoinColumn(name = "id_staff_project")
    private StaffProject staffProject;

    @ManyToOne
    @JoinColumn(name = "id_todo_list")
    private TodoList todoList;

    @Column(name = "descriptions",length = EntityProperties.LENGTH_CONTENT)
    private String descriptions;

    @Column(name = "image_id", length = EntityProperties.LENGTH_ID)
    private String imageId;

    @Column(name = "url_image", length = EntityProperties.LENGTH_NAME)
    private String urlImage;

    @ManyToOne
    @JoinColumn(name = "id_type_todo")
    private TypeTodo typeTodo;

}
