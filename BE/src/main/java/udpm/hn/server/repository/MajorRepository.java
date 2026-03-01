package udpm.hn.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import udpm.hn.server.entity.Major;

import java.util.Optional;

public interface MajorRepository extends JpaRepository<Major,String> {

    Optional<Major> findByName(String nameMajor);
}
