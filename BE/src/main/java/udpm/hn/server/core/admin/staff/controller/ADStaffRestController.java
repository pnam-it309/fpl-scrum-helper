package udpm.hn.server.core.admin.staff.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import udpm.hn.server.core.admin.staff.model.request.ADCreateStaffFDM;
import udpm.hn.server.core.admin.staff.model.request.ADCreateStaffRequest;
import udpm.hn.server.core.admin.staff.model.request.ADStaffRequest;
import udpm.hn.server.core.admin.staff.model.request.ADUpdateStaffFDMRequest;
import udpm.hn.server.core.admin.staff.model.request.RoleStaffRequest ;
import udpm.hn.server.core.admin.staff.service.ADStaffFacilityService;
import udpm.hn.server.core.admin.staff.service.ADStaffService;
import udpm.hn.server.infrastructure.constant.MappingConstants;
import udpm.hn.server.utils.Helper;

import java.io.FileNotFoundException;
import java.io.IOException;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping(MappingConstants.API_ADMIN_STAFF)
public class ADStaffRestController {

    private  final ADStaffService adStaffService;

    private  final ADStaffFacilityService adStaffFacilityService ;

    @GetMapping
    public ResponseEntity<?> getAllStaff( ADStaffRequest request) {
        log.info("Request nhận vào trong listStaff:{}",request.toString());
        return Helper.createResponseEntity(adStaffService.getAllStaff(request));
    }

    @GetMapping("/role-by-staff/{idStaff}")
    public ResponseEntity<?> getFacilities(@PathVariable("idStaff") String idStaff) {
        return Helper.createResponseEntity(adStaffService.getRoleByStaff(idStaff));
    }

    @GetMapping("/role-staff")
    public ResponseEntity<?> getAllRole(){
        return  Helper.createResponseEntity(adStaffService.getAllRole());
    }

    @GetMapping("/detailStaff/{id}")
    public ResponseEntity<?> detailStaff(@PathVariable String id){
        return Helper.createResponseEntity(adStaffService.detailStaff(id));
    }

    @GetMapping("/staff-no-project")
    public ResponseEntity<?> getAllStaffNoProject(){
        return Helper.createResponseEntity(adStaffService.getAllStaffNoProject());
    }

    @GetMapping("/detailStaff/department-major/{id}")
    public ResponseEntity<?> staffByDepartmentMajor(@PathVariable String id){
        return Helper.createResponseEntity(adStaffService.staffByDepartmentFacility(id));
    }

    @GetMapping("/facility")
    public ResponseEntity<?> getAllFacility(){
        return Helper.createResponseEntity(adStaffFacilityService.getAllFacility());
    }

    @GetMapping("/department/{idFacility}")
    public ResponseEntity<?> getAllDepartmentByFacility(@PathVariable("idFacility") String idFacility){
        return Helper.createResponseEntity(adStaffService.getAllDepartmentByFacility(idFacility));
    }

    @GetMapping("/major-department/{id}")
    public ResponseEntity<?> getAllMajor(@PathVariable String id){
        return Helper.createResponseEntity(adStaffService.getMajorByDepartment(id));
    }

    @GetMapping("/staff-role-by-staff/{idStaff}")
    public ResponseEntity<?> getStaffRoleByStaff(@PathVariable String idStaff) {
        return Helper.createResponseEntity(adStaffService.getStaffRoleByStaff(idStaff));
    }

    @PostMapping
    public ResponseEntity<?> createStaff(ADCreateStaffRequest request){
        return Helper.createResponseEntity(adStaffService.createStaff(request));
    }

    @PostMapping("/major-department-facility")
    public ResponseEntity<?> createStaffFDM(ADCreateStaffFDM request){
        return Helper.createResponseEntity(adStaffService.createStaffByFDM(request));
    }

    @PostMapping("/create-staff-role")
    public ResponseEntity<?> createStaffRole(@RequestBody RoleStaffRequest request){
        return Helper.createResponseEntity(adStaffService.createUpdateRoleByStaff(request));
    }


    @PutMapping("/{id}")
    public ResponseEntity<?> updateStaff(@PathVariable String id, ADCreateStaffRequest request){
        return Helper.createResponseEntity(adStaffService.updateStaff(id,request));
    }

    @PutMapping("/major-department-facility")
    public ResponseEntity<?> updateMajorDepartmentFacility(ADUpdateStaffFDMRequest request){
        return Helper.createResponseEntity(adStaffService.updateStaffByFDM(request));
    }


    @DeleteMapping("/{id}/{emailLogin}")
    public ResponseEntity<?> deleteStaff(@PathVariable String id,@PathVariable String emailLogin){
        return Helper.createResponseEntity(adStaffService.deleteStaff(id,emailLogin));
    }

    @DeleteMapping("/staff-facility/{id}/{emailLogin}")
    public ResponseEntity<?> deleteStaffFacility(@PathVariable String id,@PathVariable String emailLogin){
        return Helper.createResponseEntity(adStaffService.deleteStaffByFDM(id,emailLogin));
    }

    @DeleteMapping("/delete-role-staff")
    public ResponseEntity<?> deleteStaffRole(@RequestBody  RoleStaffRequest request){

        return Helper.createResponseEntity(adStaffService.deleteRoleByStaff(request));

    }

    @GetMapping("/history")
    public ResponseEntity<?> getChangeHistory(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "50") int size
    ) throws FileNotFoundException {
        return Helper.createResponseEntity(adStaffService.getLogsImportStaff(page, size));
    }

    @GetMapping("/csv-file")
    public ResponseEntity<Resource> downloadCsvFile() {
        try {
            Resource csvResource = adStaffService.getAllCsv();
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=staff_data.csv")
                    .contentType(MediaType.parseMediaType("text/csv"))
                    .body(csvResource);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/count-staff")
    public ResponseEntity<?> getAllStaffCount(){
        return Helper.createResponseEntity(adStaffService.getAllStaffCount());
    }

}