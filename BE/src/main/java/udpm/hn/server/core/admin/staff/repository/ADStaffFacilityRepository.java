package udpm.hn.server.core.admin.staff.repository;

import org.springframework.data.jpa.repository.Query;
import udpm.hn.server.core.admin.staff.model.response.ADStaffByDepartmentFacility;
import udpm.hn.server.repository.FacilityRepository;

import java.util.List;

public interface ADStaffFacilityRepository extends FacilityRepository {

    @Query(value = """
            select  
                f.id as id,
                f.name as nameFacility 
            from 
                Facility f
                """)
    List<ADStaffByDepartmentFacility> getAll();

}