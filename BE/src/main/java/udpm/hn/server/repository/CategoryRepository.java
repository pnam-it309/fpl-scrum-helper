package udpm.hn.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import udpm.hn.server.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, String> {
}
