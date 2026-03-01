package udpm.hn.server.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import udpm.hn.server.entity.base.PrimaryEntity;

import java.io.Serializable;

@Entity
@Table(name = "activity_log")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ActivityLog extends PrimaryEntity implements Serializable{

    @Column(name = "executor_email")
    private String executorEmail;

    @Column(name = "content")
    private String content ;

    @Column(name = "role")
    private String role ;

}
