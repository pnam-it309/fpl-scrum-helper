package udpm.hn.server.core.admin.department.department.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import udpm.hn.server.core.admin.department.department.model.request.ADCreateOrUpdateMajorRequest;
import udpm.hn.server.core.admin.department.department.model.request.ADMajorRequest;
import udpm.hn.server.core.admin.department.department.service.ADMajorService;
import udpm.hn.server.infrastructure.constant.MappingConstants;
import udpm.hn.server.utils.Helper;

@RestController
@RequestMapping(MappingConstants.API_ADMIN_MAJOR)
@RequiredArgsConstructor
public class ADMajorRestController {

    private final ADMajorService adMajorService;

    @GetMapping("/get-all-major/{departmentId}")
    public ResponseEntity<?> getAllMajor(@PathVariable String departmentId, ADMajorRequest request) {
        return Helper.createResponseEntity(adMajorService.getAllMajor(departmentId, request));
    }

    @PostMapping("/add-major")
    public ResponseEntity<?> addMajor(@RequestBody ADCreateOrUpdateMajorRequest request) {
        return Helper.createResponseEntity(adMajorService.addMajor(request));
    }

    @PutMapping("/update-major/{id}")
    public ResponseEntity<?> updateMajor(@RequestBody ADCreateOrUpdateMajorRequest request, @PathVariable String id) {
        return Helper.createResponseEntity(adMajorService.updateMajor(request, id));
    }

    @PutMapping("/delete-major/{id}")
    public ResponseEntity<?> deleteMajor(@PathVariable String id) {
        return Helper.createResponseEntity(adMajorService.deleteMajor(id));
    }

    @GetMapping("/detail-major/{id}")
    public ResponseEntity<?> detailMajor(@PathVariable String id){
        return Helper.createResponseEntity(adMajorService.detailMajor(id));
    }
}
