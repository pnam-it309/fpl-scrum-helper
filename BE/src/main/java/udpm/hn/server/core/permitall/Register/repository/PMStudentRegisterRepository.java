package udpm.hn.server.core.permitall.Register.repository;

import udpm.hn.server.entity.Student;
import udpm.hn.server.infrastructure.constant.EntityStatus;
import udpm.hn.server.repository.StudentRepository;

import java.util.Optional;

public interface PMStudentRegisterRepository extends StudentRepository {

    Optional<Student> findByEmailAndStatus(String email, EntityStatus status);

    Optional<Student> findByCodeAndStatus(String code, EntityStatus status);
}
