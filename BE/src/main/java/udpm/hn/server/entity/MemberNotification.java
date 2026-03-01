package udpm.hn.server.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import udpm.hn.server.entity.base.PrimaryEntity;
import udpm.hn.server.infrastructure.constant.EntityProperties;

import java.io.Serializable;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Table(name = "member_notification")
public class MemberNotification  extends PrimaryEntity implements Serializable {

    @ManyToOne
    @JoinColumn(name = "id_notification")
    private Notification notification ;

    @ManyToOne
    @JoinColumn(name = "id_staff_project")
    private StaffProject staffProject ;

    @Column(name = "member_id", length = EntityProperties.LENGTH_ID)
    private String memberId;

}
