package udpm.hn.server.core.admin.student.model.response;

import udpm.hn.server.core.common.base.IsIdentify;

public interface StudentResponse extends IsIdentify {

    String getName();

    String getCode();

    String getEmail();

    String getStatus();

    String getCreatedDate();

    String getOrderNumber();

    String getMajorFacility();

}
