package udpm.hn.server.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import udpm.hn.server.entity.base.PrimaryEntity;
import udpm.hn.server.infrastructure.constant.EntityProperties;
import udpm.hn.server.infrastructure.constant.StatusImage;

import java.io.Serializable;


@Entity
@Table(name = "image")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Image extends PrimaryEntity implements Serializable {

    @Column(name = "name",length = EntityProperties.LENGTH_NAME)
    private String name;

    @Column(name = "url_image", length = EntityProperties.LENGTH_NAME)
    private String urlImage;

    @Column(name = "code",length = EntityProperties.LENGTH_NAME)
    private String code;

    @ManyToOne
    @JoinColumn(name = "id_todo")
    private Todo todo;

    @Column(name = "status_image", nullable = false)
    private StatusImage statusImage;

}
