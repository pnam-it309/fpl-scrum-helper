package udpm.hn.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import udpm.hn.server.entity.Assign;

import java.util.List;

public interface AssignRepository extends JpaRepository<Assign, String> {

    @Query(value = """
            SELECT td FROM Assign td WHERE td.todo.id = :todoId
            """)
    List<Assign> getAllAssignByIdTodo(@Param("todoId") String todoId);

}
