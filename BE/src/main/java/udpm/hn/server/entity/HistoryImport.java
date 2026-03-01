package udpm.hn.server.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;
import udpm.hn.server.entity.base.PrimaryEntity;
import udpm.hn.server.infrastructure.constant.LogFileType;
import udpm.hn.server.infrastructure.constant.LogType;

import java.io.Serializable;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@DynamicUpdate
@Table(name = "history_import")
public class HistoryImport extends PrimaryEntity implements Serializable {

    @ManyToOne
    @JoinColumn(name = "id_staff")
    private Staff staff;

    @Column(name = "message")
    private String message;

    @ManyToOne
    @JoinColumn(name = "id_facility")
    private Facility facility;

    @ManyToOne
    @JoinColumn(name = "id_role")
    private Role role;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "email")
    private String email;

}
