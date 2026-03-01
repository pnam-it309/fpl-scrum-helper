package udpm.hn.server.core.admin.department.department.model.response;

import org.springframework.beans.factory.annotation.Value;
import udpm.hn.server.core.common.base.HasOrderNumber;
import udpm.hn.server.core.common.base.IsIdentify;

public interface ADMajorResponse extends IsIdentify, HasOrderNumber {

    String getMajorId();

    String getMajorName();

    String getMajorCode();

    String getMajorStatus();

    Long getCreatedDate();

}
