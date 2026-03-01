package udpm.hn.server.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import udpm.hn.server.entity.base.PrimaryEntity;

import java.io.Serializable;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "department_facility")
@ToString
@Builder
public class DepartmentFacility extends PrimaryEntity implements Serializable {

    @ManyToOne
    @JoinColumn(name = "id_facility")
    private Facility facility;

    @ManyToOne
    @JoinColumn(name = "id_department")
    private Department department;

}
