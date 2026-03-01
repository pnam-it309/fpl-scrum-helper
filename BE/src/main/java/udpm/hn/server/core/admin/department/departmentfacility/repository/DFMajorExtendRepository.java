package udpm.hn.server.core.admin.department.departmentfacility.repository;

import org.springframework.data.jpa.repository.Query;
import udpm.hn.server.repository.MajorRepository;

import java.util.List;

public interface DFMajorExtendRepository extends MajorRepository {
    @Query(
            value = """
                SELECT
                    m.id AS majorId,
                    m.name AS majorName
                FROM
                    Major m
                WHERE
                    m.department.id = :departmentId
                    and m.status = 0
                """
    )
    List<ListMajorResponse> findAllByDepartmentId(String departmentId);
}
