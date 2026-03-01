package udpm.hn.server.core.admin.facility.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import udpm.hn.server.core.admin.facility.model.request.ADFacilityRequest;
import udpm.hn.server.core.admin.facility.model.response.ADFacilityResponse;
import udpm.hn.server.entity.Facility;
import udpm.hn.server.repository.FacilityRepository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FacilityExtendRepository extends FacilityRepository {

    @Query(value = """
            SELECT 
                ROW_NUMBER() OVER (ORDER BY fa.createdDate DESC ) AS orderNumber,
                fa.id AS id, 
                fa.name AS facilityName, 
                fa.code AS facilityCode, 
                fa.status AS facilityStatus, 
                fa.createdDate AS createdDate
            FROM 
                Facility fa
            WHERE 
                (:#{#request.facilityName} IS NULL OR fa.name LIKE CONCAT('%',:#{#request.facilityName},'%') 
                 OR fa.code LIKE CONCAT('%',:#{#request.facilityName},'%'))
                AND (:#{#request.facilityStatus} IS NULL OR fa.status = :#{#request.facilityStatus})
                order by fa.createdDate DESC 
        """ , countQuery = """
            SELECT 
                COUNT(DISTINCT fa.id) 
            FROM 
                Facility fa
            WHERE 
                (:#{#request.facilityName} IS NULL OR fa.name LIKE CONCAT('%',:#{#request.facilityName},'%'))
                AND (:#{#request.facilityStatus} IS NULL OR fa.status = :#{#request.facilityStatus})
            
            """
    )
    Page<ADFacilityResponse> getAllFacility(Pageable pageable, @Param("request") ADFacilityRequest request);

    @Query(
            value = """
                    SELECT
                        fa.id AS id,
                        fa.code AS facilityCode,
                        fa.name AS facilityName,
                        fa.status AS facilityStatus,
                        fa.createdDate AS createdDate
                    FROM
                        Facility fa
                    WHERE
                        fa.id = :facilityId
                    """)
    Optional<ADFacilityResponse> getDetailFacilityById(String facilityId);

    List<Facility> findAllByName(String name);

    boolean existsByNameAndIdNot(String name,String id);
}
