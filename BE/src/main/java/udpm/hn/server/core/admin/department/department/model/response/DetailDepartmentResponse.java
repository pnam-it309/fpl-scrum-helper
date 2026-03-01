package udpm.hn.server.core.admin.department.department.model.response;

import org.springframework.beans.factory.annotation.Value;
import udpm.hn.server.core.common.base.IsIdentify;

public interface DetailDepartmentResponse extends IsIdentify {
    @Value("#{target.departmentCode}")
    String getDepartmentCode();

    @Value("#{target.departmentName}")
    String getDepartmentName();
}
