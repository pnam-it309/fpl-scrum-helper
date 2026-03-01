package udpm.hn.server.core.admin.department.department.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import udpm.hn.server.core.admin.department.department.model.request.ADCreateOrUpdateDepartmentRequest;
import udpm.hn.server.core.admin.department.department.model.request.DepartmentSearchRequest;
import udpm.hn.server.core.admin.department.department.service.DepartmentService;
import udpm.hn.server.infrastructure.constant.MappingConstants;
import udpm.hn.server.utils.Helper;

@RestController
@RequestMapping(MappingConstants.API_ADMIN_DEPARTMENT)
@RequiredArgsConstructor
public class ADDepartmentRestController {

    private final DepartmentService departmentService;

    @GetMapping("/get-all-department")
    public ResponseEntity<?> getAllDepartment(DepartmentSearchRequest request){
        return Helper.createResponseEntity(departmentService.getAllDepartment(request));
    }

    @PostMapping("/add-department")
    public ResponseEntity<?> addDepartment(@RequestBody ADCreateOrUpdateDepartmentRequest departmentRequest){
        return Helper.createResponseEntity(departmentService.addDepartment(departmentRequest));
    }

    @PutMapping("/update-department/{id}")
    public ResponseEntity<?> updateDepartment(@RequestBody ADCreateOrUpdateDepartmentRequest departmentRequest, @PathVariable String id){
        return Helper.createResponseEntity(departmentService.updateDepartment(departmentRequest, id));
    }

    @GetMapping("/detail-department/{id}")
    public ResponseEntity<?> detailDepartMent(@PathVariable String id){
        return Helper.createResponseEntity(departmentService.detailDepartment(id));
    }

    @PutMapping("/status-department/{id}")
    public ResponseEntity<?> deleteMajor(@PathVariable String id) {
        return Helper.createResponseEntity(departmentService.changeStatus(id));
    }
}
