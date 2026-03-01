package udpm.hn.server.core.admin.department.departmentfacility.model.response;

import org.springframework.beans.factory.annotation.Value;
import udpm.hn.server.core.common.base.HasOrderNumber;
import udpm.hn.server.core.common.base.IsIdentify;

public interface MajorFacilityResponse extends IsIdentify, HasOrderNumber {

    @Value("#{target.majorName}")
    String getMajorName();

    String getMajorFacilityId();

    String getMajorId();

    FacilityDepartmentInfoResponse getFacilityDepartmentInfo();

    String getMajorCode();

    String getMajorFacilityStatus();

}
