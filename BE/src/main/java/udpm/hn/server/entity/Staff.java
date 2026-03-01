package udpm.hn.server.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Nationalized;
import udpm.hn.server.entity.base.PrimaryEntity;
import udpm.hn.server.infrastructure.constant.EntityProperties;

import java.io.Serializable;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Table(name = "staff")
@DynamicUpdate
    public class Staff extends PrimaryEntity implements Serializable {

    @Column(name = "code",length = EntityProperties.LENGTH_NAME)
    private String code;

    @Column(name = "name",length = EntityProperties.LENGTH_NAME)
    private String name;

    @Column(name = "email_fe", length = EntityProperties.LENGTH_NAME)
    @Nationalized
    private String emailFe;

    @Column(name = "email_fpt", length = EntityProperties.LENGTH_NAME)
    @Nationalized
    private String emailFpt;

    @Column(name = "picture", length = EntityProperties.LENGTH_PICTURE)
    private String picture;

    public Staff(String code, String name, String emailFe, String picture) {
        this.code = code;
        this.name = name;
        this.emailFe = emailFe;
        this.picture = picture;
    }

//    @OneToMany(mappedBy = "staff")
//    private List<StaffRole> staffRoles;


}
