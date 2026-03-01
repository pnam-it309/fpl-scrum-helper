package udpm.hn.server.core.admin.staff.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import udpm.hn.server.core.admin.staff.model.request.ADStaffRequest;
import udpm.hn.server.core.admin.staff.model.response.StaffResponse;
import udpm.hn.server.entity.Staff;
import udpm.hn.server.infrastructure.constant.EntityStatus;
import udpm.hn.server.repository.StaffRepository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ADStaffRepository extends StaffRepository {
        @Query("""
        SELECT s.id 
        FROM Staff s
        LEFT JOIN StaffMajorFacility smf ON s.id = smf.staff.id
        LEFT JOIN MajorFacility mf ON smf.majorFacility.id = mf.id
        LEFT JOIN DepartmentFacility df ON mf.departmentFacility.id = df.id
        LEFT JOIN Facility f ON f.id = df.facility.id
        WHERE 
            (:#{#req.search} IS NULL OR :#{#req.search} = '' OR 
                s.name LIKE CONCAT('%', :#{#req.search}, '%') OR 
                s.code LIKE CONCAT('%', :#{#req.search}, '%') OR 
                s.emailFe LIKE CONCAT('%', :#{#req.search}, '%')
            )
            AND (:#{#req.searchFacility} IS NULL OR :#{#req.searchFacility} = '' OR f.id = :#{#req.searchFacility})
            AND (:status IS NULL OR s.status = :status)
        ORDER BY s.createdDate DESC
    """)
        Page<String> findStaffIds(Pageable pageable, @Param("req") ADStaffRequest req, @Param("status") EntityStatus status);

    @Query(
            value = """

                       SELECT 
                         ROW_NUMBER() OVER (ORDER BY s.createdDate DESC) AS orderNumber,
                            s.id AS id,
                            s.code AS codeStaff,
                            s.name AS nameStaff,
                            s.emailFe AS emailFe,
                            s.emailFpt AS emailFpt,
                            s.status AS status,
                            r.name AS nameRole,
                            r.id AS idRole,
                            f.name AS nameFacility
                       FROM Staff s
                       LEFT JOIN StaffRole sr ON s.id = sr.staff.id
                       LEFT JOIN StaffMajorFacility smf ON s.id = smf.staff.id
                       LEFT JOIN MajorFacility mf ON smf.majorFacility.id = mf.id
                       LEFT JOIN DepartmentFacility df ON mf.departmentFacility.id = df.id
                       LEFT JOIN Facility f on f.id = df.facility.id
                       LEFT JOIN Role r on sr.role.id = r.id
                       WHERE 
                             (:#{#req.search} IS NULL
                             OR :#{#req.search} = ''
                             OR s.name LIKE CONCAT('%', :#{#req.search}, '%')
                             OR s.code LIKE CONCAT('%', :#{#req.search}, '%')
                             OR s.emailFe LIKE CONCAT('%', :#{#req.search}, '%'))
                             AND (:#{#req.searchFacility} IS NULL 
                                  OR :#{#req.searchFacility} = '' 
                                  OR f.id = :#{#req.searchFacility})
                             AND (:status IS NULL OR s.status = :status)
                       ORDER BY s.createdDate DESC
                    """,
            countQuery = """
       SELECT COUNT(DISTINCT s.id)
       FROM Staff s
       LEFT JOIN StaffRole sr ON s.id = sr.staff.id
       LEFT JOIN StaffMajorFacility smf ON s.id = smf.staff.id
       LEFT JOIN MajorFacility mf ON smf.majorFacility.id = mf.id
       LEFT JOIN DepartmentFacility df ON mf.departmentFacility.id = df.id
       LEFT JOIN Facility f on f.id = df.facility.id
       LEFT JOIN Role r on sr.role.id = r.id
       WHERE 
             (:#{#req.search} IS NULL
             OR :#{#req.search} = ''
             OR s.name LIKE CONCAT('%', :#{#req.search}, '%')
             OR s.code LIKE CONCAT('%', :#{#req.search}, '%')
             OR s.emailFe LIKE CONCAT('%', :#{#req.search}, '%'))
             AND (:#{#req.searchFacility} IS NULL 
                  OR :#{#req.searchFacility} = '' 
                  OR f.id = :#{#req.searchFacility})
             AND (:status IS NULL OR s.status = :status)
       ORDER BY s.createdDate DESC
    """
    )
    Page<StaffResponse> getAllStaff(Pageable pageable, @Param("req") ADStaffRequest req, @Param("status") EntityStatus status);
        @Query("""
        SELECT 
            s.id AS id,
            s.code AS codeStaff,
            s.name AS nameStaff,
            s.emailFe AS emailFe,
            s.emailFpt AS emailFpt,
            s.status AS status,
            r.name AS nameRole,
            r.id AS idRole,
            f.name AS nameFacility
        FROM Staff s
        LEFT JOIN StaffRole sr ON s.id = sr.staff.id
        LEFT JOIN Role r ON sr.role.id = r.id
        LEFT JOIN StaffMajorFacility smf ON s.id = smf.staff.id
        LEFT JOIN MajorFacility mf ON smf.majorFacility.id = mf.id
        LEFT JOIN DepartmentFacility df ON mf.departmentFacility.id = df.id
        LEFT JOIN Facility f ON f.id = df.facility.id
        WHERE s.id IN :ids
        ORDER BY s.createdDate DESC
    """)
        List<StaffResponse> findStaffDetailsByIds(@Param("ids") List<String> ids);

    @Query(value = """
        
        select
            s.id as id,
            s.code as codeStaff,
            s.name as nameStaff,
            s.emailFe as emailFe,
            s.emailFpt as emailFpt,
            s.status as status
        from
            Staff s
        where
            s.id = :id               
        """ )
    Optional<StaffResponse> findByIdStaff(@Param("id") String id);


    @Query("""
        SELECT     
        S.id as id,
            S.picture as image,
            S.code as codeStaff,
            S.name as nameStaff,
            S.emailFe as emailFe,
            S.emailFpt as emailFpt,
            S.status as status
        FROM Staff S                    
        LEFT JOIN StaffProject sp ON S = sp.staff
        WHERE sp.id IS NULL
    """)
    List<StaffResponse> getAllStaffNoProject();

    Optional<Staff> findByCode(String code);

    Optional<Staff> findByEmailFe(String emailFe);

    Optional<Staff> findByEmailFpt(String emailFPT);

    Boolean existsByEmailFe(String emailFe);

    Boolean existsByEmailFpt(String emailFpt);

    Boolean existsByCode(String code);

    @Query("SELECT COUNT(st) FROM Staff st")
    Long countAllStaff();

}