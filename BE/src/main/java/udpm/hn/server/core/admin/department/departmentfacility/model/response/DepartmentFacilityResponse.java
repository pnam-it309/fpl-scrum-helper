package udpm.hn.server.core.admin.department.departmentfacility.model.response;

import udpm.hn.server.core.common.base.HasOrderNumber;

public interface DepartmentFacilityResponse extends HasOrderNumber {

    String getDepartmentFacilityId();

    String getFacilityId();

    String getFacilityName();

    String getDepartmentFacilityStatus();

    Long getCreatedDate();

    String getFacilityCode();
}
