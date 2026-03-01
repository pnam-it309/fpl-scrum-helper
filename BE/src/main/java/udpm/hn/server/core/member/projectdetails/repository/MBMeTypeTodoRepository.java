package udpm.hn.server.core.member.projectdetails.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import udpm.hn.server.core.member.projectdetails.model.response.MBTypeTodoResponse;
import udpm.hn.server.entity.TypeTodo;
import udpm.hn.server.repository.TypeTodoRepository;

import java.util.List;
import java.util.Optional;

public interface MBMeTypeTodoRepository extends TypeTodoRepository {

    @Query(
            value = """
        SELECT c.id AS id,
               c.type AS type,
               c.status AS status
        FROM TypeTodo c
        WHERE c.status = 0
        """
    )
    List<MBTypeTodoResponse> getAllTypeTodo();

    TypeTodo type(String type);

    @Query(
            value = """
    SELECT c.id AS id,
           c.type AS type,
           c.status AS status
    FROM TypeTodo c
    WHERE c.status = 0 AND c.id = :id
    """
    )
    Optional<MBTypeTodoResponse> getTypeTodoById(@Param("id") String id);

}
