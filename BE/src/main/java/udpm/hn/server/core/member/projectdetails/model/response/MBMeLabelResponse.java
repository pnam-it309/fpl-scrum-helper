package udpm.hn.server.core.member.projectdetails.model.response;

import udpm.hn.server.core.common.base.IsIdentify;


public interface MBMeLabelResponse  extends IsIdentify {

    String getCode();

    String getName();

    String getColorLabel();

    Boolean getIsChecked();
}
