package udpm.hn.server.core.admin.activitylog.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import udpm.hn.server.core.admin.activitylog.modal.request.ADActivityLogRequest;
import udpm.hn.server.core.admin.activitylog.service.ADActivityLogService;
import udpm.hn.server.infrastructure.constant.MappingConstants;
import udpm.hn.server.utils.Helper;

@RestController
@RequestMapping(MappingConstants.API_ADMIN_ACTIVITY_LOG)
@RequiredArgsConstructor
public class ADActivityLogController {

    private final ADActivityLogService adActivityLogService;

    @GetMapping
    public ResponseEntity<?> getAllActivityLog(ADActivityLogRequest request) {
        return Helper.createResponseEntity(adActivityLogService.getAllActivityLog(request));
    }

}
