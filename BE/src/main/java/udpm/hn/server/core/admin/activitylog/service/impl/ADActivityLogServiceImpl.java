package udpm.hn.server.core.admin.activitylog.service.impl;

import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import udpm.hn.server.core.admin.activitylog.modal.request.ADActivityLogRequest;
import udpm.hn.server.core.admin.activitylog.repository.ADActivityLogRepository;
import udpm.hn.server.core.admin.activitylog.service.ADActivityLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.validation.annotation.Validated;
import udpm.hn.server.core.common.base.PageableObject;
import udpm.hn.server.core.common.base.ResponseObject;
import udpm.hn.server.utils.Helper;

@Service
@Validated
@RequiredArgsConstructor
public class ADActivityLogServiceImpl implements ADActivityLogService {

    private final ADActivityLogRepository adActivityLogRepository;

    @Override
    public ResponseObject<?> getAllActivityLog(ADActivityLogRequest request) {
        Pageable pageable = Helper.createPageable(request, "createdDate");
        return new ResponseObject<>(
                PageableObject.of(adActivityLogRepository.getAllActivityLogs(pageable)),
                HttpStatus.OK,
                "Get all activityLog successfully"
        );
    }

}
