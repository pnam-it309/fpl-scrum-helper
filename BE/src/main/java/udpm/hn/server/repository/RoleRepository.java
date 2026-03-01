package udpm.hn.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import udpm.hn.server.entity.Role;

import java.util.List;

public interface RoleRepository extends JpaRepository<Role, String> {

    Role findRolesByCode(String code);

    @Query(
            value = """
                            select r from StaffRole st
                            join Role r on r.id = st.role.id
                            join Staff s on s.id = st.staff.id
                            where s.id = :idStaff
                    """
    )
    List<Role> findRoleByStaff(@Param("idStaff") String idStaff);
}
