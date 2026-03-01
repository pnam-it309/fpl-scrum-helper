package udpm.hn.server.core.admin.department.department.model.response;

import org.springframework.beans.factory.annotation.Value;
import udpm.hn.server.core.common.base.HasOrderNumber;
import udpm.hn.server.core.common.base.IsIdentify;

public interface DepartmentReponse extends IsIdentify, HasOrderNumber {

    @Value("#{target.departmentId}")
    String getDepartmentId();

    @Value("#{target.departmentCode}")
    String getDepartmentCode();

    @Value("#{target.departmentName}")
    String getDepartmentName();

    @Value("#{target.departmentStatus}")
    String getDepartmentStatus();

    @Value("#{target.createdDate}")
    String getCreatedDate();

}
