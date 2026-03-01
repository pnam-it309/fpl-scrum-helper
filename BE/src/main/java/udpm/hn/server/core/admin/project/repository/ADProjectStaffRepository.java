package udpm.hn.server.core.admin.project.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import udpm.hn.server.core.admin.project.model.response.ADProjectStaffStudentResponse;
import udpm.hn.server.repository.StaffRepository;

import java.util.List;

public interface ADProjectStaffRepository extends StaffRepository {

    @Query("""
        SELECT 
            s.id as id,
            s.name as name,
            s.code as code,
            s.emailFe as email,
            s.picture as image
        from Staff s 
        join StaffMajorFacility smf on s.id = smf.staff.id
        join MajorFacility mf on smf.majorFacility.id = mf.id
        where mf.id = :IdMajorFacility
        and s.status = 0
""")
    List<ADProjectStaffStudentResponse> getALl(@Param("IdMajorFacility")  String IdMajorFacility);


    @Query("""
	select 
	    s.name as name,
	    s.id as id,
	    s.code as code,
	    s.picture as image,
	    s.emailFe as email,
	    sp.role as role
	from  StaffProject sp 
	join Staff s ON sp.staff.id = s.id 
	join Project p on sp.project.id  = p.id 
	where p.id = :idProject
	and sp.status = 0
        """)
    List<ADProjectStaffStudentResponse> getAllStaffByProject (@Param("idProject") String idProject);
}
