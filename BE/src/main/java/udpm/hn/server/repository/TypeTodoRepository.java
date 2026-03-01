package udpm.hn.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import udpm.hn.server.entity.TypeTodo;

public interface TypeTodoRepository extends JpaRepository<TypeTodo,String> {

    boolean existsByTypeIgnoreCase(String type);

}
