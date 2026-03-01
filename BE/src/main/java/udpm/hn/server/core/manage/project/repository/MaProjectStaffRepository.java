package udpm.hn.server.core.manage.project.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import udpm.hn.server.core.manage.project.model.response.MaProjectStaffStudentResponse;
import udpm.hn.server.repository.StaffRepository;

import java.util.List;

@Repository
public interface MaProjectStaffRepository extends StaffRepository {
    @Query("""
        SELECT 
            s.id as id,
            s.name as name,
            s.code as code,
            s.emailFpt as email,
            s.picture as image
        from Staff s 
        join StaffMajorFacility smf on s.id = smf.staff.id
        join MajorFacility mf on smf.majorFacility.id = mf.id
        where mf.id = :IdMajorFacility
""")
    List<MaProjectStaffStudentResponse> getALl(@Param("IdMajorFacility")  String IdMajorFacility);


    @Query("""
	select 
	    s.name as name,
	    s.id as id,
	    s.code as code,
	    s.picture as image,
	    s.emailFpt as email,
	    sp.role as role
	from  StaffProject sp 
	join Staff s ON sp.staff.id = s.id 
	join Project p on sp.project.id  = p.id 
	where p.id = :idProject
	and sp.status = 0
        """)

    List<MaProjectStaffStudentResponse> getAllStaffByProject (@Param("idProject") String idProject);
}