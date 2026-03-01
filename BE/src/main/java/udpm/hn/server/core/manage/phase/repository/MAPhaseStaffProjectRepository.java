package udpm.hn.server.core.manage.phase.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import udpm.hn.server.repository.StaffProjectRepository;

import java.util.List;

@Repository
public interface MAPhaseStaffProjectRepository extends StaffProjectRepository {

    @Query(value= """
        SELECT s.emailFe
        FROM Staff s
        JOIN StaffProject sp on sp.staff.id = s.id
        JOIN Project p on p.id = sp.project.id  
        JOIN PhaseProject pp on pp.project.id = p.id
        WHERE pp.id = :idPhase
    """)
    List<String> getStaffByPhaseProject(@Param("idPhase") String idPhase);

    @Query(value= """
        SELECT s.email
        FROM Student s
        JOIN StaffProject sp on sp.student.id = s.id
        JOIN Project p on p.id = sp.project.id  
        JOIN PhaseProject pp on pp.project.id = p.id
        WHERE pp.id = :idPhase
    """)
    List<String> getStudentByPhaseProject(@Param("idPhase") String idPhase);

}
