package udpm.hn.server.core.admin.role.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import udpm.hn.server.core.admin.role.model.request.ADRoleRequest;
import udpm.hn.server.core.admin.role.model.response.ADRoleResponse;
import udpm.hn.server.repository.RoleRepository;

@Repository
public interface ADRoleRepository extends RoleRepository {
    @Query(value = """
            SELECT r.name AS roleName,
                   r.id AS idRole,
                   r.code AS roleCode,
                   f.name AS facilityName
            FROM role r
            LEFT JOIN facility f ON r.id_facility = f.id
            WHERE r.status = 0 
            AND f.status = 0 
            AND (:q = '' OR r.name LIKE CONCAT('%', :q, '%') OR r.code LIKE CONCAT('%', :q, '%'))
            AND (:department = '' OR f.name LIKE CONCAT('%', :department, '%'))
            order by r.created_date desc
            """,
            countQuery = """
            SELECT COUNT(*)
            FROM role r
            LEFT JOIN facility f ON r.id_facility = f.id
            WHERE r.status = 0 
            AND f.status = 0
            AND (:q = '' OR r.name LIKE CONCAT('%', :q, '%') OR r.code LIKE CONCAT('%', :q, '%'))
            AND (:department = '' OR f.name LIKE CONCAT('%', :department, '%'))
    """, nativeQuery = true)
    Page<ADRoleResponse> getAllRole(Pageable pageable,
                                    @Param("q") String searchQuery,
                                    @Param("department") String department);
}
