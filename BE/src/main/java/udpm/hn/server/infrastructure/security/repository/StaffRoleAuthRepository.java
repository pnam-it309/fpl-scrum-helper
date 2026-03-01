package udpm.hn.server.infrastructure.security.repository;

import org.springframework.data.jpa.repository.Query;
import udpm.hn.server.repository.StaffRoleRepository;

import java.util.List;

public interface StaffRoleAuthRepository extends StaffRoleRepository {

    @Query(value = """
              SELECT DISTINCT r.name FROM Role r
                        JOIN StaffRole sr ON r.id = sr.role.id
                        WHERE sr.staff.id = :staffId
      
            """)
    List<String> getRoleNamesByStaffId(String staffId);

    @Query(value = """
            SELECT DISTINCT r.code  FROM Role r
                JOIN StaffRole sr ON r.id = sr.role.id
                WHERE sr.staff.id = :staffId AND sr.status = 0
        """)
    List<String> getRoleCodesByStaffId(String staffId);
}
