package udpm.hn.server.core.admin.department.departmentfacility.repository;

import lombok.Getter;
import lombok.Setter;
import udpm.hn.server.core.admin.department.departmentfacility.model.response.FacilityDepartmentInfoResponse;
import udpm.hn.server.core.common.base.PageableObject;

@Getter
@Setter
public class MajorFacilitiesResponse {

    PageableObject<?> majorFacilities;

    FacilityDepartmentInfoResponse facilityDepartmentInfo;

}
