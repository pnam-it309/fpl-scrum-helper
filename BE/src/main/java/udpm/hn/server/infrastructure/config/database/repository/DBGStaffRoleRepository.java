package udpm.hn.server.infrastructure.config.database.repository;

import udpm.hn.server.entity.Role;
import udpm.hn.server.entity.Staff;
import udpm.hn.server.repository.StaffRoleRepository;

public interface DBGStaffRoleRepository extends StaffRoleRepository {
    boolean existsByStaffAndRole(Staff staff, Role role);
}
