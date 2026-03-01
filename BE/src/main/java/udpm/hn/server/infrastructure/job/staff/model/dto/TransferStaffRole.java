package udpm.hn.server.infrastructure.job.staff.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import udpm.hn.server.entity.Staff;
import udpm.hn.server.entity.StaffMajorFacility;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TransferStaffRole {

    private Staff staff;

//    private Role role;

    private StaffMajorFacility staffMajorFacility;

}
