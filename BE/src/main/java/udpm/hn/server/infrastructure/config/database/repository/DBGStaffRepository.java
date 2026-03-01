package udpm.hn.server.infrastructure.config.database.repository;

import udpm.hn.server.entity.Staff;
import udpm.hn.server.infrastructure.constant.EntityStatus;
import udpm.hn.server.repository.StaffRepository;

import java.util.Optional;

public interface DBGStaffRepository extends StaffRepository {
    Optional<Staff> findByEmailFptAndStatus(String emailFpt, EntityStatus status);
}
