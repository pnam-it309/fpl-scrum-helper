package udpm.hn.server.core.permitall.Register.repository;

import org.springframework.data.jpa.repository.Query;
import udpm.hn.server.core.permitall.Register.model.response.PMFacilityLoginResponse;
import udpm.hn.server.repository.FacilityRepository;

import java.util.List;

public interface PMFacilityLoginRepository extends FacilityRepository {
    @Query(value = """
    select  
        f.id as id,
        f.name as nameFacility ,
            f.code as codeFacility 
    from 
        Facility f
    where f.status = 0 
    order by 
        case 
            when f.code = 'HA_NOI' then 0 
            else 1 
        end,
        f.name
    
    """)
    List<PMFacilityLoginResponse> getAllFacility();



}
