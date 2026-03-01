package udpm.hn.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import udpm.hn.server.entity.TodoChild;

@Repository
public interface TodoChildRepository extends JpaRepository<TodoChild,String> {
}
