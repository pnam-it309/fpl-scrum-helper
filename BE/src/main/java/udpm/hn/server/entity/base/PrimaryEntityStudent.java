package udpm.hn.server.entity.base;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import udpm.hn.server.infrastructure.constant.EntityProperties;
import udpm.hn.server.infrastructure.constant.EntityStudentStatus;
import udpm.hn.server.infrastructure.listener.CreatePrimaryEntityListener;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@MappedSuperclass
@EntityListeners(CreatePrimaryEntityListener.class)
public  abstract  class PrimaryEntityStudent  extends AuditEntity implements IsIdentified {

    @Id
    @Column(length = EntityProperties.LENGTH_ID, updatable = false)
    private String id;

    @Column(name = "status")
    @Enumerated(EnumType.ORDINAL)
    private EntityStudentStatus status;

}
