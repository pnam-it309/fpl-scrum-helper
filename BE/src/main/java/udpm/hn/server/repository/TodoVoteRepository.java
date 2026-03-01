package udpm.hn.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import udpm.hn.server.entity.StaffProject;
import udpm.hn.server.entity.Todo;
import udpm.hn.server.entity.TodoVote;

import java.util.List;
import java.util.Optional;

public interface TodoVoteRepository extends JpaRepository<TodoVote,String> {

    Optional<TodoVote> findByTodoAndStaffProjectAndStageVoteId(Todo todo, StaffProject staffProject, String stageVoteId);

    @Query(value = """
            SELECT td FROM TodoVote td WHERE td.todo.id = :todoId
            """)
    List<TodoVote> getAllAssignByIdTodo(@Param("todoId") String todoId);

    @Query("""
    SELECT 
        CASE 
            WHEN sp.staff IS NOT NULL THEN sp.staff.id
            ELSE sp.student.id
        END
    FROM StaffProject sp
    WHERE sp.id = :idStaffProject
""")
    Optional<String> getUserIdByStaffProjectId(@Param("idStaffProject") String idStaffProject);

    @Query("""
    SELECT 
        CASE 
            WHEN sp.staff IS NOT NULL THEN sp.staff.emailFpt
            ELSE sp.student.email
        END
    FROM StaffProject sp
    LEFT JOIN sp.staff s
    LEFT JOIN sp.student st
    WHERE sp.id = :idStaffProject
""")
    Optional<String> getEmailUserByStaffProjectId(@Param("idStaffProject") String idStaffProject);


}
