package udpm.hn.server.core.member.projectdetails.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import udpm.hn.server.entity.LabelProjectTodo;
import udpm.hn.server.repository.LabelProjectTodoRepository;

public interface MBMeLabelTodoRepository extends LabelProjectTodoRepository {

    @Modifying
    @Transactional
    @Query(value = """
            DELETE FROM label_project_todo a
            WHERE a.label_project_id = :idlabel AND a.todo_id = :idTodo
            """, nativeQuery = true)
    void delete(@Param("idlabel") String idlabel, @Param("idTodo") String idTodo);

    @Query("""
SELECT lpt FROM LabelProjectTodo lpt 
WHERE lpt.labelProject.id = :idLabel 
AND lpt.todo.id = :idTodo
AND lpt.status =0
""")
    LabelProjectTodo findByLabelProjectIdAndTodoId(@Param("idLabel") String idLabel, @Param("idTodo") String idTodo);


}
