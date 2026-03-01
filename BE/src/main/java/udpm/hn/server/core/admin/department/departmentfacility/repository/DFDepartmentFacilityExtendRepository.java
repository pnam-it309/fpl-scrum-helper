package udpm.hn.server.core.admin.department.departmentfacility.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import udpm.hn.server.core.admin.department.departmentfacility.model.request.CreateOrUpdateDepartmentFacilityRequest;
import udpm.hn.server.core.admin.department.departmentfacility.model.request.FindFacilityDetailRequest;
import udpm.hn.server.core.admin.department.departmentfacility.model.response.DepartmentFacilityResponse;
import udpm.hn.server.core.admin.department.departmentfacility.model.response.ListFacilityResponse;
import udpm.hn.server.entity.DepartmentFacility;
import udpm.hn.server.repository.DepartmentFacilityRepository;

import java.util.List;
import java.util.Optional;

public interface DFDepartmentFacilityExtendRepository extends DepartmentFacilityRepository {
    @Query(value = """
            SELECT
                ROW_NUMBER() OVER (ORDER BY df.createdDate DESC ) AS orderNumber,
               	df.id AS departmentFacilityId,
               	df.facility.id AS facilityId,
               	df.status AS departmentFacilityStatus,
               	f.name AS facilityName,
               	df.createdDate AS createdDate,
               	df.facility.code AS facilityCode
            FROM
               	DepartmentFacility df
           LEFT JOIN Facility f ON
               	f.id = df.facility.id
            WHERE
               	(df.department.id = :id) AND
               	(:#{#req.facilityName} IS NULL OR f.name LIKE CONCAT('%', :#{#req.facilityName}, '%') )
               	order by df.createdDate DESC 
            """, countQuery = """                         	    
            SELECT
                COUNT(df.id)
            FROM
                DepartmentFacility df
               LEFT JOIN Facility f ON
                f.id = df.facility.id
            WHERE
                (df.department.id = :id) AND
                (:#{#req.facilityName} IS NULL OR f.name LIKE CONCAT('%', :#{#req.facilityName}, '%') )
            """)
    Page<DepartmentFacilityResponse> getDepartmentFacilitiesByValueFind(String id, Pageable pageable, @Param("req") FindFacilityDetailRequest req);

    @Query("""
            SELECT df FROM DepartmentFacility df
            WHERE df.department.id = :#{#req.departmentId} AND df.facility.id = :#{#req.facilityId}
            """)
    Optional<DepartmentFacility> existsByIdDepartmentAndIdFacilityAndIdAdd(@Param("req") CreateOrUpdateDepartmentFacilityRequest req);


    @Query("""
            SELECT df FROM DepartmentFacility df
            WHERE df.department.id = :departmentId AND df.facility.id = :facilityId
            """)
    Optional<DepartmentFacility> existsDepartmentFacilitiesByDepartmentAndFacility(String departmentId, String facilityId);

    @Query(value = """
            SELECT
                f.id AS facilityId,
                f.name AS facilityName
            FROM
                Facility f
            WHERE
                f.status = 0
            """)
    List<ListFacilityResponse> getListFacility();

    @Query(value = """
        SELECT 
            df.id AS departmentFacilityId,
            f.name AS facilityName,
            f.id AS facilityId
        FROM
            DepartmentFacility df
        LEFT JOIN Facility f ON
            f.id = df.facility.id
        WHERE 
            df.id =:id
        """, countQuery = """
        SELECT 
            COUNT(df.id)
        FROM
            DepartmentFacility df
        LEFT JOIN Facility f ON
            f.id = df.facility.id
        WHERE 
            df.id =:id
        """)
    ListFacilityResponse getDetailDepartmentFacilityResponse(String id);
}
