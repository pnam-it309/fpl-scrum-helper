package udpm.hn.server.infrastructure.job.staff.commonio;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import udpm.hn.server.entity.Staff;
import udpm.hn.server.entity.StaffMajorFacility;
import udpm.hn.server.infrastructure.constant.EntityStatus;
import udpm.hn.server.infrastructure.job.staff.model.dto.TransferStaffRole;
import udpm.hn.server.infrastructure.job.staff.repository.ConfigStaffCustomRepository;
import udpm.hn.server.infrastructure.job.staff.repository.ConfigStaffMajorFacilityRepository;

@Component
@Slf4j
@RequiredArgsConstructor
@Transactional
public class StaffWriter implements ItemWriter<Staff> {

    @Setter(onMethod_ = {@Autowired})
    private ConfigStaffCustomRepository staffCustomRepository;

    @Setter(onMethod_ = {@Autowired})
    private ConfigStaffMajorFacilityRepository staffMajorFacilityRepository;

    @Transactional
    @Override
    public void write(Chunk<? extends Staff> chunk) throws Exception {
//        int recordNumber = 0;
//
//        for (TransferStaffRole transferStaffRole : chunk) {
//            recordNumber++;
//            try {
//                Staff savedStaff = saveOrUpdateStaff(transferStaffRole.getStaff());
//                StaffMajorFacility savedStaffMajorFacility = saveStaffMajorFacility(savedStaff, transferStaffRole.getStaffMajorFacility());
//                log.info(
//                        "Successfully processed record number {}: Saved Staff Major Facility - {}",
//                        recordNumber, savedStaffMajorFacility
//                );
//            } catch (Exception e) {
//                log.error("Error processing record number {}: {}", recordNumber, transferStaffRole, e);
//            }
//        }
//    }

//    private Staff saveOrUpdateStaff(Staff staff) {
//        return staffCustomRepository
//                .findByCodeAndStatus(staff.getCode(), EntityStatus.ACTIVE)
//                .orElseGet(() -> staffCustomRepository.save(staff));
//    }
//
//
//    private StaffMajorFacility saveStaffMajorFacility(Staff staff, StaffMajorFacility staffMajorFacility) {
//        staffMajorFacility.setStaff(staff);
//        return staffMajorFacilityRepository.save(staffMajorFacility);
//    }
    }
}
