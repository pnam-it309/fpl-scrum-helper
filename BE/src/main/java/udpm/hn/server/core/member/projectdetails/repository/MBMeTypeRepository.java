package udpm.hn.server.core.member.projectdetails.repository;

import org.springframework.data.jpa.repository.Query;
import udpm.hn.server.core.member.projectdetails.model.response.TypeTodoResponse;
import udpm.hn.server.repository.TypeTodoRepository;

import java.util.List;

public interface MBMeTypeRepository extends TypeTodoRepository {

    @Query("SELECT t.id AS id, t.type AS type FROM TypeTodo t")
    List<TypeTodoResponse> getAllTypeTodoResponse();

}
