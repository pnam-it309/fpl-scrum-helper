package udpm.hn.server.infrastructure.job.reportcompensation.repository;

import org.springframework.stereotype.Repository;
import udpm.hn.server.entity.Student;
import udpm.hn.server.repository.StudentRepository;

import java.util.Optional;

@Repository
public interface StudentReportRepository extends StudentRepository {
    Optional<Student> findByEmail(String email);

}
