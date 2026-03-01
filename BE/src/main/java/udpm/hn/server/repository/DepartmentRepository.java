package udpm.hn.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import udpm.hn.server.entity.Department;

import java.util.Optional;

public interface DepartmentRepository extends JpaRepository<Department,String> {

    Optional<Department> findByName(String nameDepartment);


}
