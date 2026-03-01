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

import java.io.Serializable;


@Entity
@Table(name = "notification")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Notification extends PrimaryEntity implements Serializable {

    @Column(name = "content",length = EntityProperties.LENGTH_DESCRIPTION)
    private String name;

    @ManyToOne
    @JoinColumn(name = "id_todo")
    private Todo todo;

    @Column(name = "member_id_created", length = EntityProperties.LENGTH_ID)
    private String memberIdCreated;

    @Column(name = "url", length = 500)
    private String url;
    
}
