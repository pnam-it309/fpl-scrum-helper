package udpm.hn.server.core.member.projectdetails.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import udpm.hn.server.core.member.projectdetails.model.response.MBMeMemberProjectResponse;
import udpm.hn.server.repository.StaffProjectRepository;

import java.util.List;
import java.util.Optional;

public interface MBMeMemberProjectRepository extends StaffProjectRepository {

    @Query("""
    SELECT 
        COALESCE(st.id, s.id) AS id,
        COALESCE(st.name, s.name) AS name,
        COALESCE(st.emailFpt, s.email) AS email,
        COALESCE(st.picture, s.image) AS image,
        CASE WHEN tv.id IS NOT NULL THEN true ELSE false END AS isAdded
    FROM StaffProject sp
    LEFT JOIN sp.staff st
    LEFT JOIN sp.student s
    LEFT JOIN Assign tv ON tv.staffProject.id = sp.id AND tv.todo.id = :idTodo
    WHERE sp.project.id = :idProject AND sp.status = 0
""")
    List<MBMeMemberProjectResponse> findAllMembersWithAddedFlag(
            @Param("idProject") String idProject,
            @Param("idTodo") String idTodo
    );


    @Query("""
    SELECT sp.id
    FROM StaffProject sp
    WHERE (sp.student.id = :idMember OR sp.staff.id = :idMember)
      AND sp.project.id = :idProject
""")
    String findIdByMemberIdAndProjectId(@Param("idMember") String idMember,
                                        @Param("idProject") String idProject);


    @Query("SELECT sp.id FROM StaffProject sp " +
            "LEFT JOIN sp.staff s " +
            "LEFT JOIN sp.student st " +
            "WHERE (s.emailFpt = :email AND s.status = 0) " +
            "   OR (st.email = :email AND st.status = 0)")
    Optional<String> findIdStaffProjectByEmailUser(@Param("email") String email);

    @Query("""
    SELECT 
        COALESCE(st.id, s.id) AS id,
        COALESCE(st.name, s.name) AS name,
        COALESCE(st.emailFpt, s.email) AS email,
        COALESCE(st.picture, s.image) AS image
    FROM StaffProject sp
    LEFT JOIN sp.staff st
    LEFT JOIN sp.student s
    WHERE sp.project.id = :idProject
      AND sp.status = 0
""")
    List<MBMeMemberProjectResponse> getAllFilterMemberProject(@Param("idProject") String idProject);

}
