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
@Table(name = "activity")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Activity  extends PrimaryEntity implements Serializable {

    @Column(name = "member_created_id", length = EntityProperties.LENGTH_ID)
    private String memberCreatedId;

    @Column(name = "member_id", length = EntityProperties.LENGTH_ID)
    private String memberId;

    @Column(name = "todo_id", length = EntityProperties.LENGTH_ID)
    private String todoId;

    @Column(name = "todo_list_id", length = EntityProperties.LENGTH_ID)
    private String todoListId;

    @Column(name = "project_id", length = EntityProperties.LENGTH_ID)
    private String projectId;

    @Column(name = "content_action", length = EntityProperties.LENGTH_DESCRIPTION)
    private String contentAction;

    @Column(name = "content_action_project", length = EntityProperties.LENGTH_DESCRIPTION)
    private String contentActionProject;

    @Column(name = "url_image", length = EntityProperties.LENGTH_NAME)
    private String urlImage;

    @Column(name = "image_id", length = EntityProperties.LENGTH_ID)
    private String imageId;

    @Column(name = "url", length = EntityProperties.LENGTH_URL)
    private String url;

}
