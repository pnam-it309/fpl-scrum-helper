package udpm.hn.server.core.admin.department.departmentfacility.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import udpm.hn.server.core.admin.department.departmentfacility.model.request.CreateMajorFacilityRequest;
import udpm.hn.server.core.admin.department.departmentfacility.model.request.MajorFacilityRequest;
import udpm.hn.server.core.admin.department.departmentfacility.service.MajorFacilityService;
import udpm.hn.server.infrastructure.constant.MappingConstants;
import udpm.hn.server.utils.Helper;

@RestController
@RequestMapping(MappingConstants.API_ADMIN_MAJOR_FACILITY)
@RequiredArgsConstructor
public class MajorFacilityRestController {

    private final MajorFacilityService majorFacilityService;

    @GetMapping("/get-all/{departmentFacilityId}")
    public ResponseEntity<?> getMajorFacilities(MajorFacilityRequest request) {
        return Helper.createResponseEntity(majorFacilityService.getAllMajorFacilities(request));
    }

    @PostMapping("/add")
    public ResponseEntity<?> createMajorFacility(@RequestBody CreateMajorFacilityRequest request) {
        return Helper.createResponseEntity(majorFacilityService.createMajorFacility(request));
    }

    @PutMapping("/update/{majorFacilityId}")
    public ResponseEntity<?> updateMajorFacility(@PathVariable String majorFacilityId, @RequestBody CreateMajorFacilityRequest request) {
        return Helper.createResponseEntity(majorFacilityService.updateMajorFacility(majorFacilityId, request));
    }

    @GetMapping("/{majorFacilityId}")
    public ResponseEntity<?> getMajorFacilityById(@PathVariable String majorFacilityId) {
        return Helper.createResponseEntity(majorFacilityService.getMajorFacilityById(majorFacilityId));
    }

    @GetMapping("/major/{departmentId}")
    public ResponseEntity<?> getMajorFacilities(@PathVariable String departmentId) {
        return Helper.createResponseEntity(majorFacilityService.getAllMajors(departmentId));
    }

    @PutMapping("/status-majorFacility/{id}")
    public ResponseEntity<?> changeStatus(@PathVariable String id) {
        return Helper.createResponseEntity(majorFacilityService.changeStatus(id));
    }

}
