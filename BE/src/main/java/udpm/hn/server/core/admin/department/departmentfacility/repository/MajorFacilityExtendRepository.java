package udpm.hn.server.core.admin.department.departmentfacility.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import udpm.hn.server.core.admin.department.departmentfacility.model.request.MajorFacilityRequest;
import udpm.hn.server.core.admin.department.departmentfacility.model.response.FacilityDepartmentInfoResponse;
import udpm.hn.server.core.admin.department.departmentfacility.model.response.MajorFacilityDetailResponse;
import udpm.hn.server.core.admin.department.departmentfacility.model.response.MajorFacilityResponse;
import udpm.hn.server.entity.MajorFacility;
import udpm.hn.server.repository.MajorFacilityRepository;

import java.util.Optional;

@Repository
public interface MajorFacilityExtendRepository extends MajorFacilityRepository {

    @Query(
            value = """
                    SELECT
                        ROW_NUMBER() OVER(
                        ORDER BY mf.createdDate DESC) AS orderNumber,
                        mf.id as majorFacilityId,
                        mf.status as majorFacilityStatus,
                        m.name as majorName,
                        df.id as departmentFacilityId,
                        m.id as majorId,
                        m.code as majorCode
                    FROM
                        MajorFacility mf
                    LEFT JOIN Major m ON mf.major.id = m.id
                    LEFT JOIN DepartmentFacility df ON mf.departmentFacility.id = df.id
                    LEFT JOIN Facility f ON df.facility.id = f.id
                    LEFT JOIN Department d ON df.department.id = d.id
                    WHERE
                        df.id = :#{#request.departmentFacilityId}
                    AND (:#{#request.majorName} IS NULL OR m.name LIKE CONCAT('%', :#{#request.majorName}, '%'))
                                        ORDER BY mf.createdDate DESC
                    """,
            countQuery = """
                    SELECT
                        COUNT(mf.id)
                    FROM
                        MajorFacility mf
                    LEFT JOIN Major m ON mf.major.id = m.id
                    LEFT JOIN DepartmentFacility df ON mf.departmentFacility.id = df.id
                    LEFT JOIN Facility f ON df.facility.id = f.id
                    WHERE
                        df.id = :#{#request.departmentFacilityId}
                    AND (:#{#request.majorName} IS NULL OR m.name LIKE CONCAT('%', :#{#request.majorName}, '%'))
                    """
    )
    Page<MajorFacilityResponse> findAllMajorFacilities(MajorFacilityRequest request, Pageable pageable);

    @Query(
            value = """
                    SELECT
                             f.name as facilityName,
                             d.id as departmentId,
                             d.name as departmentName
                    FROM
                        DepartmentFacility df
                    LEFT JOIN Facility f ON df.facility.id = f.id
                    LEFT JOIN Department d ON df.department.id = d.id
                    WHERE
                        df.id = :departmentFacilityId
                    """
    )
    FacilityDepartmentInfoResponse getFacilityDepartmentInfo(String departmentFacilityId);

    @Query(
            value = """
                    SELECT
                        mf.id as id,
                        m.id as majorId
                    FROM
                        MajorFacility mf
                    LEFT JOIN Major m ON mf.major.id = m.id
                    LEFT JOIN DepartmentFacility df ON mf.departmentFacility.id = df.id
                    LEFT JOIN Facility f ON df.facility.id = f.id
                    WHERE
                        mf.id = :majorFacilityId
                    """
    )
    MajorFacilityDetailResponse findMajorFacilityById(String majorFacilityId);

    Optional<MajorFacility> findByMajor_IdAndDepartmentFacility_Id(String majorId, String departmentFacilityId);

    Optional<MajorFacility> findMajorFacilitiesByMajorId(String majorId);

}
