package udpm.hn.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import udpm.hn.server.entity.StaffRole;

import java.util.List;
import java.util.Optional;

public interface StaffRoleRepository extends JpaRepository<StaffRole, String> {

    List<StaffRole> findByStaffIdAndRoleId(String idStaff, String  idRole);

    Optional<StaffRole> findByRoleIdAndStaffId(String idRole , String idStaff);

}
