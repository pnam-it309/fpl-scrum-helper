package udpm.hn.server.core.member.projectdetails.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import udpm.hn.server.core.member.projectdetails.model.response.MBMeResourceResponse;
import udpm.hn.server.repository.ResourceRepository;

import java.util.List;

public interface MBMeResourceRepository extends ResourceRepository {

    @Query("""
        SELECT r.id AS id, r.name AS name, r.url AS url, r.todo.id AS todoId, r.createdDate AS createdDate
        FROM Resource r
        WHERE r.todo.id = :idTodo
        AND r.status = 0
        ORDER BY r.createdDate DESC
        """)
    List<MBMeResourceResponse> getAll(@Param("idTodo") String idTodo);

    String id(String id);
}
