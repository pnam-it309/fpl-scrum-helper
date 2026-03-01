package udpm.hn.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import udpm.hn.server.entity.Image;

@Repository
public interface ImageRepository extends JpaRepository<Image, String> {
}
