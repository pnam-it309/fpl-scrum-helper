package udpm.hn.server.core.admin.student.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import udpm.hn.server.core.admin.department.department.model.response.DetailDepartmentResponse;
import udpm.hn.server.core.admin.student.model.request.StudentRequest;
import udpm.hn.server.core.admin.student.model.response.StudentResponse;
import udpm.hn.server.repository.StudentRepository;

@Repository
public interface ADStudentExtendRepository extends StudentRepository {

    @Query(value = """
        SELECT 
            ROW_NUMBER() OVER (ORDER BY s.createdDate desc, s.id DESC ) AS orderNumber,
            s.id AS id,
            s.name AS name,
            s.code AS code,
            s.email AS email,
            s.image AS image,
            s.status AS status,
            CONCAT(m.name, ' - ', d.name, ' - ', f.name) AS majorFacility,
            s.createdDate AS createdDate
        FROM 
            Student s
        LEFT JOIN s.majorFacility mf
        LEFT JOIN mf.departmentFacility df
        LEFT JOIN df.facility f
        LEFT JOIN df.department d
        LEFT JOIN mf.major m
        WHERE 
            (:#{#req.search} IS NULL OR 
            LOWER(s.name) LIKE LOWER(CONCAT('%', :#{#req.search}, '%')) OR 
            LOWER(s.code) LIKE LOWER(CONCAT('%', :#{#req.search}, '%')))
            AND
            (:#{#req.searchDF} IS NULL OR 
            LOWER(f.name) LIKE LOWER(CONCAT('%', :#{#req.searchDF}, '%')) OR 
            LOWER(m.name) LIKE LOWER(CONCAT('%', :#{#req.searchDF}, '%')) OR 
            LOWER(d.name) LIKE LOWER(CONCAT('%', :#{#req.searchDF}, '%')))
            AND s.status = 0
            order by s.createdDate desc 
        """,
            countQuery = """
        SELECT COUNT(s.id)
        FROM Student s
        LEFT JOIN s.majorFacility mf
        LEFT JOIN mf.departmentFacility df
        LEFT JOIN df.facility f
        LEFT JOIN df.department d
        WHERE
            (:#{#req.search} IS NULL OR
            LOWER(s.name) LIKE LOWER(CONCAT('%', :#{#req.search}, '%')) OR
            LOWER(s.code) LIKE LOWER(CONCAT('%', :#{#req.search}, '%')))
            AND
            (:#{#req.searchDF} IS NULL OR
            LOWER(f.name) LIKE LOWER(CONCAT('%', :#{#req.searchDF}, '%')) OR
            LOWER(d.name) LIKE LOWER(CONCAT('%', :#{#req.searchDF}, '%')))
            AND s.status = 0
        """
    )
    Page<StudentResponse> getAllStudent(Pageable pageable, @Param("req") StudentRequest req);

//    Boolean existsByCode(String code);
//
//    Boolean existsByEmail(String email);

    @Query("SELECT COUNT(s) > 0 FROM Student s WHERE s.code = :code AND s.status = 0")
    Boolean existsByCode(@Param("code") String code);

    @Query("SELECT COUNT(s) > 0 FROM Student s WHERE s.email = :email AND s.status = 0")
    Boolean existsByEmail(@Param("email") String email);


    @Query(value = """
            SELECT
            	s.id AS id,
            	s.code AS code,
            	s.name AS name,
            	s.email AS email,
            	s.status AS status,
            	s.createdDate AS createdDate
            FROM
            	Student s
            WHERE
                s.id = :id
            """, countQuery = """
            SELECT
                COUNT(s.id)
            FROM
                Student s
            WHERE
                 s.id = :id
            """
    )
    StudentResponse getDetailStudent(String id);
}
