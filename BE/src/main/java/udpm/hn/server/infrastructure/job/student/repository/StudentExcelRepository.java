package udpm.hn.server.infrastructure.job.student.repository;

import org.springframework.stereotype.Repository;
import udpm.hn.server.entity.Student;
import udpm.hn.server.repository.StudentRepository;

import java.util.List;

@Repository
public interface StudentExcelRepository extends StudentRepository {

    List<Student> findByCodeAndEmailAndName(String code, String email, String name);

    List<Student> findByCode(String codeStudent);

    List<Student> findByEmail(String email);

}
