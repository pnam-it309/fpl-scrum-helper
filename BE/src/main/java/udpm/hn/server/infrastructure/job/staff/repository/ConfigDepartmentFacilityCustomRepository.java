package udpm.hn.server.infrastructure.job.staff.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import udpm.hn.server.repository.DepartmentFacilityRepository;

import java.util.List;

@Repository
public interface ConfigDepartmentFacilityCustomRepository extends DepartmentFacilityRepository {

    @Query(
            value = """
            SELECT CONCAT(d.name,' - ',m.name,' - ',f.code) AS majorFacilityName
            FROM MajorFacility mf
            JOIN DepartmentFacility df ON df.id = mf.departmentFacility.id
            JOIN Major m ON m.id = mf.major.id
            JOIN Department d ON d.id = df.department.id
            JOIN Facility f ON f.id = df.facility.id
            WHERE df.status = 0
            AND mf.status = 0                  
            AND df.facility.id LIKE :idFacility
           """
    )
    List<String> findAllByIdFacility(String idFacility);

}
