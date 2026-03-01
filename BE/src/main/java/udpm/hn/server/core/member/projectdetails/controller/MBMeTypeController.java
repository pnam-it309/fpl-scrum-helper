package udpm.hn.server.core.member.projectdetails.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import udpm.hn.server.core.member.projectdetails.service.MBMeLabelService;
import udpm.hn.server.core.member.projectdetails.service.MBTypeService;
import udpm.hn.server.infrastructure.constant.MappingConstants;
import udpm.hn.server.utils.Helper;

@Slf4j
@RestController
@RequestMapping(MappingConstants.API_PROJECT_DETAILS_TYPE_PROJECT)
@RequiredArgsConstructor
public class MBMeTypeController {

    private final MBTypeService mbTypeService;

    @GetMapping("/list")
    public ResponseEntity<?> getAllType() {
        return Helper.createResponseEntity(mbTypeService.getAllType());
    }

}
