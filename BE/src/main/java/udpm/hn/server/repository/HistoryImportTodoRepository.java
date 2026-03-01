package udpm.hn.server.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import udpm.hn.server.entity.HistoryImportTodo;

@Repository
public interface HistoryImportTodoRepository extends JpaRepository<HistoryImportTodo, String> {

    @Query("""
    SELECT h FROM HistoryImportTodo h 
    WHERE h.project.id = :idProject AND h.email = :email
""")
    Page<HistoryImportTodo> findByProjectIdAndEmail(
            @Param("idProject") String projectId,
            @Param("email") String email,
            Pageable pageable
    );

}
