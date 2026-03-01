package udpm.hn.server.infrastructure.config.database.repository;

import udpm.hn.server.entity.Facility;
import udpm.hn.server.entity.Role;
import udpm.hn.server.repository.RoleRepository;

import java.util.List;
import java.util.Optional;

public interface DBGRoleRepository extends RoleRepository {
    Optional<Role> findByCode(String code);

    List<Role> findAllByFacility(Facility facility);

    Optional<Role> findByCodeAndNameAndFacility(String s, String s1, Facility facility);

}
