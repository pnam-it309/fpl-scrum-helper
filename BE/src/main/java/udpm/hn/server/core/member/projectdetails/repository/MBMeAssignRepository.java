package udpm.hn.server.core.member.projectdetails.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import udpm.hn.server.core.member.projectdetails.model.response.MBMemberInfoTodoResponse;
import udpm.hn.server.entity.Assign;
import udpm.hn.server.entity.StaffProject;
import udpm.hn.server.entity.Todo;
import udpm.hn.server.repository.AssignRepository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MBMeAssignRepository extends AssignRepository {

    @Query("""
    SELECT 
        COALESCE(st.id, s.id) AS id,
        COALESCE(st.name, s.name) AS name,
        COALESCE(st.emailFpt, s.email) AS email,
        COALESCE(st.picture, s.image) AS image
    FROM Assign a
    JOIN a.staffProject sp
    LEFT JOIN sp.staff st
    LEFT JOIN sp.student s
    WHERE a.todo.id = :idTodo
""")
    List<MBMemberInfoTodoResponse> getAllMemberByIdTodo(@Param("idTodo") String idTodo);


    @Query("""
    SELECT tv
    FROM Assign tv
    WHERE tv.staffProject.id = :idMember AND tv.todo.id = :idTodo
""")
    Assign findByMemberIdAndTodoId(@Param("idMember") String idMember, @Param("idTodo") String idTodo);

    @Query("""
    SELECT tv.id
    FROM Assign tv
    WHERE tv.todo.id = :idTodo
      AND tv.staffProject.id = :idStaffProject
""")
    Optional<String> findIdAssignByIdTodoAndIdStaffProject(@Param("idTodo") String idTodo,
                                                             @Param("idStaffProject") String idStaffProject);

    boolean existsByTodoAndStaffProject(Todo todo, StaffProject staffProject);

    @Query("""
            SELECT tv.todo.id 
            FROM TodoVote tv 
             JOIN StaffProject st ON tv.staffProject.id = st.id
             JOIN StageVote sv on sv.id = tv.stageVote.id
            WHERE st.id = :idStaff and st.project.id = :idProject
            and (:time BETWEEN sv.startTime AND sv.endTime)
            """)
    List<String> getIDTodoByIdStaffProject(@Param("idStaff") String idStaff,@Param("idProject") String idProject,@Param("time") Long time);
}
