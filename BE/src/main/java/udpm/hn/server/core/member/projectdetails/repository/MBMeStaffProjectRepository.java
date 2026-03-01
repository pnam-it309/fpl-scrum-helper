package udpm.hn.server.core.member.projectdetails.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import udpm.hn.server.entity.StaffProject;
import udpm.hn.server.repository.StaffProjectRepository;

import java.util.List;
import java.util.Optional;

public interface MBMeStaffProjectRepository extends StaffProjectRepository {

    @Query("SELECT sp.id FROM StaffProject sp " +
            "LEFT JOIN sp.staff s " +
            "LEFT JOIN sp.student st " +
            "WHERE( (s.emailFpt = :email AND s.status = 0) " +
            "   OR (st.email = :email AND st.status = 0) )AND sp.project.id = :idProject")
    Optional<String> findIdStaffProjectByEmailUserAndIdProject(@Param("email") String email
            ,@Param("idProject") String idProject);

    @Query("SELECT sp.id FROM StaffProject sp " +
            "LEFT JOIN sp.staff s " +
            "LEFT JOIN sp.student st " +
            "WHERE( (s.id = :idUser AND s.status = 0) " +
            "OR (st.id = :idUser AND st.status = 0)) AND sp.project.id = :idProject")
    Optional<String> findIdStaffProjectByIdUserAndIdProject(@Param("idUser") String idUser
            ,@Param("idProject") String idProject);


    @Query("""
    SELECT sp FROM StaffProject sp 
    WHERE sp.project.id = :projectId
""")
    List<StaffProject> findAllByProjectId(@Param("projectId") String projectId);


}
