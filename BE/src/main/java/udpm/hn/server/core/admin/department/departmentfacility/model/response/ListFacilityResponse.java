package udpm.hn.server.core.admin.department.departmentfacility.model.response;

import udpm.hn.server.core.common.base.IsIdentify;

public interface ListFacilityResponse extends IsIdentify {

    String getDepartmentFacilityId();

    String getFacilityId();

    String getFacilityName();

}
