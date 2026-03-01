package udpm.hn.server.core.admin.department.department.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import udpm.hn.server.core.admin.department.department.model.request.ADMajorRequest;
import udpm.hn.server.core.admin.department.department.model.response.ADDetailMajorResponse;
import udpm.hn.server.core.admin.department.department.model.response.ADMajorResponse;
import udpm.hn.server.entity.Major;
import udpm.hn.server.repository.MajorRepository;

import java.util.Optional;

@Repository
public interface ADMajorExtendRepository extends MajorRepository {
    @Query(value = """
            SELECT
                ROW_NUMBER() OVER (ORDER BY mj.createdDate DESC ) AS orderNumber,
                mj.id AS majorId,
                mj.name AS majorName,
                mj.code AS majorCode,
                mj.status AS majorStatus,
                mj.createdDate AS createdDate
            FROM
                Major mj
            WHERE
                mj.department.id = :id
                AND ( :#{#req.majorName} IS NULL
                OR mj.name LIKE CONCAT('%', :#{#req.majorName}, '%')
                OR mj.code LIKE CONCAT('%', :#{#req.majorName}, '%'))
                order by mj.createdDate DESC
        """, countQuery = """
                SELECT
                    COUNT(mj.id)
                FROM
                    Major mj
                WHERE
                    mj.department.id = :id
                    AND ( :#{#req.majorName} IS NULL
                    OR mj.name LIKE CONCAT('%', :#{#req.majorName}, '%')
                    OR mj.code LIKE CONCAT('%', :#{#req.majorName}, '%')) 
                     
            """
    )
    Page<ADMajorResponse> getAllMajorByDepartmentIdFilter(String id, Pageable pageable, @Param("req")ADMajorRequest req);

    Optional<Major> findMajorByNameAndDepartmentId(String name, String departmentId);

    Optional<Major> findMajorByCodeAndDepartmentId(String name, String departmentId);

    @Query(value = """
            SELECT
            	mj.id AS id,
            	mj.code AS majorCode,
            	mj.name AS majorName
            FROM
            	Major mj
            WHERE
                mj.id = :id
            """,countQuery = """
            SELECT
                COUNT(mj.id)
            FROM
                Major mj
            WHERE
                mj.id = :id
            """
    )
    ADDetailMajorResponse getDetailMajor(String id);

}
