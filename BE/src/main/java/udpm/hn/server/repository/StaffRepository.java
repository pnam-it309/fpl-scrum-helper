package udpm.hn.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import udpm.hn.server.entity.Staff;

import java.util.Optional;

public interface StaffRepository extends JpaRepository<Staff, String> {

    Optional<Staff> findByEmailFe(String email);

    Optional<Staff> findByEmailFpt(String emailFpt);

    Optional<Staff> findByEmailFeOrEmailFpt(String emailFe, String emailFpt);

    @Query(
            value = """
                            select s.emailFpt from Staff s 
                            where s.id = :idStaff
                    """
    )
    String getEmailFpt(@Param("idStaff") String idStaff);

    @Query("SELECT s FROM Staff s WHERE s.emailFe = :email OR s.emailFpt = :email")
    Optional<Staff> findByAnyEmail(@Param("email") String email);

}
