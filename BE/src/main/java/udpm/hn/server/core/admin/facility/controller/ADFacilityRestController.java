package udpm.hn.server.core.admin.facility.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import udpm.hn.server.core.admin.facility.model.request.ADCreateUpdateFacilityRequest;
import udpm.hn.server.core.admin.facility.model.request.ADFacilityRequest;
import udpm.hn.server.core.admin.facility.service.ADFacilityService;
import udpm.hn.server.infrastructure.constant.MappingConstants;
import udpm.hn.server.utils.Helper;

@RestController
@RequestMapping(MappingConstants.API_ADMIN_FACILITY)
@RequiredArgsConstructor
public class ADFacilityRestController {
    private final ADFacilityService facilityService;

    @GetMapping
    public ResponseEntity<?> getAll(ADFacilityRequest request) {
        return Helper.createResponseEntity(facilityService.getAllFacility(request));
    }

    @PostMapping("/add")
    public ResponseEntity<?> create(@RequestBody ADCreateUpdateFacilityRequest request) {
        return Helper.createResponseEntity(facilityService.createFacility(request));
    }

    @PutMapping("/update/{facilityId}")
    public ResponseEntity<?> update(@PathVariable String facilityId, @RequestBody ADCreateUpdateFacilityRequest request) {
        return Helper.createResponseEntity(facilityService.updateFacility(facilityId, request));
    }

    @PutMapping("/{facilityId}/change-status")
    public ResponseEntity<?> changeStatus(@PathVariable String facilityId) {
        return Helper.createResponseEntity(facilityService.changeFacilityStatus(facilityId));
    }

    @GetMapping("/detail/{facilityId}")
    public ResponseEntity<?> getById(@PathVariable String facilityId) {
        return Helper.createResponseEntity(facilityService.getFacilityById(facilityId));
    }
}
