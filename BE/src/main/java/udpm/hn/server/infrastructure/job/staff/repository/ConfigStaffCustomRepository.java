package udpm.hn.server.infrastructure.job.staff.repository;

import org.springframework.stereotype.Repository;
import udpm.hn.server.entity.Staff;
import udpm.hn.server.infrastructure.constant.EntityStatus;
import udpm.hn.server.repository.StaffRepository;

import java.util.Optional;

@Repository
public interface ConfigStaffCustomRepository extends StaffRepository {

    Optional<Staff> findByCodeAndStatus(String code, EntityStatus status);

    Optional<Staff> findByEmailFeOrEmailFpt(String emailFe, String emailFpt);

    //    Optional<Staff> findByAccountFe(String accountFe);
//
//    Optional<Staff> findByAccountFpt(String accountFpt);

}
