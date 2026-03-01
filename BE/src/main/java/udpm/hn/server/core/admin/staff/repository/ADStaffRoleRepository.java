package udpm.hn.server.core.admin.staff.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import udpm.hn.server.core.admin.staff.model.response.StaffRoleResponse;
import udpm.hn.server.entity.Role;
import udpm.hn.server.entity.StaffRole;
import udpm.hn.server.repository.RoleRepository;

import java.util.List;
import java.util.Optional;

public interface ADStaffRoleRepository extends RoleRepository {

    @Query(value = """
               select
                row_number() over(
                       order by r.id desc) as orderNumber,
               sr.id as id,
               r.name as name ,
               r.id as idRole,
               r.code as code,
               f.name as nameFacility
               from
               	Role r
               join StaffRole sr on
               	r.id = sr.role.id
               join Staff s on
               	sr.staff.id = s.id
               left join Facility f on r.facility = f
               	where s.id = :idStaff
               	ORDER BY sr.createdDate DESC
               """)
    List<StaffRoleResponse> getRoleByStaff(@Param("idStaff") String idStaff);

    @Query(value = """
               select
                    r
               from
               	Role r
               left join Facility f on r.facility = f
               where f.id= :idFacility
               """)
    List<Role> getRoleByFacility(@Param("idFacility") String idFacility);

    @Query(value = """
               select
                    r
               from
               	Role r
               left join Facility f on r.facility = f
               where f is null
               """)
    List<Role> getRoleDefault();

    @Query(value = """
              SELECT DISTINCT 
                    r.name as name,
                    r.code as code,
                    r.id as id,
                    f.name as nameFacility
              FROM Role r
              LEFT JOIN Facility f ON r.facility = f
            """)
    List<StaffRoleResponse> getAllRole();

    @Query("""
               select
                sr.id as id,
                r.name as name,
                r.id as idRole,
                r.code as code
                from
                	StaffRole sr
                LEFT join Role r on
                	r.id = sr.role.id
                LEFT join Staff s on
                	sr.staff.id = s.id
                where s.id = :idStaff
            """)
    List<StaffRoleResponse> getStaffRoleByStaff(@Param("idStaff") String idStaff);

    @Query("""
               select
                    sr
                from
                	StaffRole sr
                where sr.staff.id = :idStaff and sr.createdDate NOT IN (
                    SELECT 
                        MIN(sr1.createdDate) 
                    FROM 
                        StaffRole sr1
                    LEFT JOIN Role r on r.id = sr1.role.id
                    where sr1.staff.id = :idStaff and sr1.role.code = "ADMIN" 
                )
            """)
    List<StaffRole> getStaffRoleByIdStaff(@Param("idStaff") String idStaff);

    @Query("""
            select
                sr
               from
               	StaffRole sr
               join Role r on
               	r.id = sr.role.id
               WHERE r.code = "ADMIN" and r.facility is null
               ORDER BY sr.createdDate ASC
        """)
    List<StaffRole> getLastStaffRoleAdmin();

    @Query("""
            select r 
            from Role r 
            where r.code = :code
            and r.facility is null
                """)
    Optional<Role> findRoleDefaultByCode(@Param("code") String code);

    @Query("""
        SELECT sr
        FROM StaffRole sr
        where sr.role.id = :idRole and sr.staff.id = :idStaff
        ORDER BY sr.createdDate DESC
        """)
    List<StaffRole> findTheLastByRoleIdAndStaffId(@Param("idRole") String idRole,@Param("idStaff") String idStaff);

    @Query("""
        SELECT sr
        FROM StaffRole sr
        where sr.role.id = :idRole and sr.staff.id = :idStaff
        ORDER BY sr.createdDate DESC
        """)
    List<StaffRole> findTheNewestByRoleIdAndStaffId(@Param("idRole") String idRole,@Param("idStaff") String idStaff);
}