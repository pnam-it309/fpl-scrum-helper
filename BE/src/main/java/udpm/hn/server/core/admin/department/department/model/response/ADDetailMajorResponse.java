package udpm.hn.server.core.admin.department.department.model.response;

import org.springframework.beans.factory.annotation.Value;
import udpm.hn.server.core.common.base.IsIdentify;

public interface ADDetailMajorResponse extends IsIdentify {

    @Value("#{target.majorCode}")
    String getMajorCode();

    @Value("#{target.majorName}")
    String getMajorName();

}
