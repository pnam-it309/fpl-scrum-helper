package udpm.hn.server.core.admin.department.departmentfacility.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import udpm.hn.server.core.admin.department.departmentfacility.model.request.CreateOrUpdateDepartmentFacilityRequest;
import udpm.hn.server.core.admin.department.departmentfacility.model.request.FindFacilityDetailRequest;
import udpm.hn.server.core.admin.department.departmentfacility.service.DepartmentFacilityService;
import udpm.hn.server.infrastructure.constant.MappingConstants;
import udpm.hn.server.utils.Helper;

@RestController
@RequestMapping(MappingConstants.API_ADMIN_DEPARTMENT_FACILITY)
@RequiredArgsConstructor
public class DepartmemtFacilityRestController {

    private final DepartmentFacilityService departmentFacilityService;

    @GetMapping("/get-all-department-facility/{id}")
    public ResponseEntity<?> getAllDepartmentFacility(@PathVariable String id, FindFacilityDetailRequest request) {
        return Helper.createResponseEntity(departmentFacilityService.getAllDepartmentFacility(id, request));
    }

    @PostMapping("/add-department-facility")
    public ResponseEntity<?> addDepartmentFacility(@RequestBody CreateOrUpdateDepartmentFacilityRequest request) {
        return Helper.createResponseEntity(departmentFacilityService.addDepartmentFacility(request));
    }

    @PutMapping("/update-department-facility/{id}")
    public ResponseEntity<?> updateDepartmentFacility(@PathVariable String id, @RequestBody CreateOrUpdateDepartmentFacilityRequest request) {
        return Helper.createResponseEntity(departmentFacilityService.updateDepartmentFacility(request, id));
    }

    @GetMapping("/get-list-facility")
    public ResponseEntity<?> getListFacilityName() {
        return Helper.createResponseEntity(departmentFacilityService.getListFacility());
    }

    @GetMapping("/get-department-name/{departmentId}")
    public ResponseEntity<?> getDepartmentName(@PathVariable String departmentId) {
        return Helper.createResponseEntity(departmentFacilityService.getDepartmentName(departmentId));
    }

    @GetMapping("/detail-department-facility/{id}")
    public ResponseEntity<?> detailDepartmentFacility(@PathVariable String id){
        return Helper.createResponseEntity(departmentFacilityService.detailDepartmentFacility(id));
    }

    @PutMapping("/status-departmentFacility/{id}")
    public ResponseEntity<?> changeStatus(@PathVariable String id) {
        return Helper.createResponseEntity(departmentFacilityService.changeStatus(id));
    }
}
