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
@Table(name = "staff_major_facility")
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@Setter
public class StaffMajorFacility extends PrimaryEntity implements Serializable {

    @ManyToOne
    @JoinColumn(name = "id_major_facility")
    private MajorFacility majorFacility;

    @ManyToOne
    @JoinColumn(name = "id_staff")
    private Staff staff;

}
