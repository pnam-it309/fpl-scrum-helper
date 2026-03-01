package udpm.hn.server.core.admin.activitylog.service;

import udpm.hn.server.core.admin.activitylog.modal.request.ADActivityLogRequest;
import udpm.hn.server.core.common.base.ResponseObject;

public interface ADActivityLogService {

    ResponseObject<?> getAllActivityLog(ADActivityLogRequest request);

}
