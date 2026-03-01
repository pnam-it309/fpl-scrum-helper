package udpm.hn.server.core.admin.department.department.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import udpm.hn.server.core.admin.department.department.model.request.DepartmentSearchRequest;
import udpm.hn.server.core.admin.department.department.model.response.DepartmentReponse;
import udpm.hn.server.core.admin.department.department.model.response.DetailDepartmentResponse;
import udpm.hn.server.repository.DepartmentRepository;

@Repository
public interface ADDepartmentExtendRepository extends DepartmentRepository {
    @Query(value = """
                    SELECT 
                        ROW_NUMBER() OVER (ORDER BY d.createdDate DESC) AS orderNumber,
                        d.id AS departmentId, 
                        d.code AS departmentCode, 
                        d.name AS departmentName, 
                        d.status AS departmentStatus,
                        d.createdDate AS createdDate  
                    FROM 
                        Department d
                    WHERE 
                        (:#{#rep.departmentSearch} IS NULL OR
                        d.name LIKE CONCAT('%', :#{#rep.departmentSearch}, '%') OR
                        d.code LIKE CONCAT('%', :#{#rep.departmentSearch}, '%'))
                        AND (:#{#rep.departmentStatus} IS NULL OR d.status = :#{#rep.departmentStatus})
                        order by d.createdDate DESC 
                    """, countQuery = """
        SELECT 
            COUNT(d.id)
        FROM 
            Department d
        WHERE 
            (:#{#rep.departmentSearch} IS NULL OR
            d.name LIKE CONCAT('%', :#{#rep.departmentSearch}, '%') OR
            d.code LIKE CONCAT('%', :#{#rep.departmentSearch}, '%'))
            AND (:#{#rep.departmentStatus} IS NULL OR d.status = :#{#rep.departmentStatus})

            """
    )
    Page<DepartmentReponse> getAllDepartmentByFilter(Pageable pageable, @Param("rep") DepartmentSearchRequest req);

    Boolean existsByName(String name);

    Boolean existsByCode(String code);

    @Query(value = """
            SELECT
            	d.id AS id,
            	d.code AS departmentCode,
            	d.name AS departmentName
            FROM
            	Department d
            WHERE
                d.id = :id
            """, countQuery = """
            SELECT
                COUNT(d.id)
            FROM
                Department d
            WHERE
                 d.id = :id
            """
            )
    DetailDepartmentResponse getDetailDepartment(String id);

}
