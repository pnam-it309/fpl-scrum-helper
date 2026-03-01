package udpm.hn.server.core.admin.facility.model.response;

import udpm.hn.server.core.common.base.HasOrderNumber;
import udpm.hn.server.core.common.base.IsIdentify;

public interface ADFacilityResponse extends IsIdentify, HasOrderNumber {

    String getId();

    String getFacilityName();

    String getFacilityCode();

    Integer getFacilityStatus();

    Long getCreatedDate();

}
