package udpm.hn.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import udpm.hn.server.entity.Project;
import udpm.hn.server.utils.UserContextHelper;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<Project,String> {

    Optional<Project> findById(String idProject);

    Optional<Project> findByCode(String code);

    @Query(value = """
    select p from Project p
    join StaffProject sp on p.id = sp.project.id
    join Staff s ON sp.staff.id = s.id
    where 
    (sp.staff.id = :#{#userContextHelper.currentUserId}
         OR sp.student.id = :#{#userContextHelper.currentUserId})
    and sp.project.id = :idStaffProject
    """)
    Optional<Project> getProject(UserContextHelper userContextHelper, String idStaffProject);

    Optional<Project> findProjectById(String id);

    @Query(value = """
    SELECT project_status, COUNT(*) 
    FROM project 
    WHERE status = 0
    GROUP BY project_status
    """, nativeQuery = true)
    List<Object[]> countProjectByStatus();

    @Query(value = """
    SELECT
        p.name AS project_name,
        COUNT(*) AS total_participants
    FROM
        staff_project sp
    JOIN
        project p ON p.id = sp.id_project
    WHERE
        (sp.id_staff IS NOT NULL OR sp.id_student IS NOT NULL)
        and p.status = 0
        
    GROUP BY
        p.id, p.name
    """, nativeQuery = true)
    List<Object[]> getProjectParticipantCounts();

    @Query(value = """
            SELECT 
               DISTINCT f.id
            FROM Project p
            JOIN MajorFacility mf ON p.majorFacility.id = mf.id
            JOIN DepartmentFacility df ON mf.departmentFacility.id = df.id
            JOIN Department d ON df.department.id = d.id
            JOIN Facility f ON df.facility.id = f.id
            JOIN StaffProject sp ON p.id = sp.project.id
                WHERE p.id = :idProject 
        """)
    String idFacility(@Param("idProject") String idProject);

}
