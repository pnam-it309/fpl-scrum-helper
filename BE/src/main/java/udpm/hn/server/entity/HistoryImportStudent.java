package udpm.hn.server.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;
import udpm.hn.server.entity.base.PrimaryEntity;

import java.io.Serializable;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@DynamicUpdate
@Table(name = "history_import_student")
public class HistoryImportStudent extends PrimaryEntity implements Serializable {

    @Column(name = "email")
    private String email;

    @Column(name = "message")
    private String message;

    @ManyToOne
    @JoinColumn(name = "id_role")
    private Role role;

}
