package udpm.hn.server.core.admin.student.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import udpm.hn.server.core.admin.staff.model.request.ADCreateStaffFDM;
import udpm.hn.server.core.admin.staff.model.request.ADUpdateStaffFDMRequest;
import udpm.hn.server.core.admin.student.model.request.ADCreateOrUpdateStudentRequest;
import udpm.hn.server.core.admin.student.model.request.ADCreateStudentFDMRequest;
import udpm.hn.server.core.admin.student.model.request.ADUpdateStudentFDMRequest;
import udpm.hn.server.core.admin.student.model.request.StudentRequest;
import udpm.hn.server.core.admin.student.service.StudentService;
import udpm.hn.server.core.common.base.ResponseObject;
import udpm.hn.server.infrastructure.constant.MappingConstants;
import udpm.hn.server.utils.Helper;

import java.io.FileNotFoundException;

@Controller
@RequiredArgsConstructor
@RequestMapping(MappingConstants.API_ADMIN_STUDENT)
public class StudentRestController {

    private final StudentService studentService;

    @Cacheable(
            value = "studentsCache",
            key = "'getAllStudent_' + #request.page + '_' + #request.size + '_' + #request.orderBy + '_' + #request.sortBy + '_' + #request.q + '_' + #request.search + '_' + #request.searchDF + '_' + #request.status"
    )
    public ResponseObject<?> getAllStudentPayload(StudentRequest request){
        return studentService.getAllStudnet(request);
    }


    @GetMapping("/get-all-student")
    public ResponseEntity<?> getAllStudent(StudentRequest request){
        var data = getAllStudentPayload(request);
        return Helper.createResponseEntity(data);
    }


    @PostMapping("/add-student")
    @CacheEvict(value = "studentsCache", allEntries = true)
    public ResponseEntity<?> addStudent(@RequestBody ADCreateOrUpdateStudentRequest request){
        return Helper.createResponseEntity(studentService.addStudent(request));
    }

    @GetMapping("/fill-all-student")
    public ResponseEntity<?> fillAllStudent(){
        return Helper.createResponseEntity(studentService.findAllStudent());
    }

    @PutMapping("/update-student/{id}")
    @CacheEvict(value = "studentsCache", allEntries = true)
    public ResponseEntity<?> updateStudent(@RequestBody ADCreateOrUpdateStudentRequest request,@PathVariable String id){
        return Helper.createResponseEntity(studentService.updateStudent(request, id));
    }

    @GetMapping("/detail-student/{id}")
    public ResponseEntity<?> detailStudent(@PathVariable String id){
        return Helper.createResponseEntity(studentService.detailStudent(id));
    }

    @GetMapping("/detailStudent/department-major/{id}")
    public ResponseEntity<?> staffByDepartmentMajor(@PathVariable String id){
        return Helper.createResponseEntity(studentService.studentByDepartmentFacility(id));
    }

    @DeleteMapping("/student-facility/{id}/{emailLogin}")
    @CacheEvict(value = "studentsCache", allEntries = true)
    public ResponseEntity<?> deleteStaffFacility(@PathVariable String id, @PathVariable String emailLogin){
        return Helper.createResponseEntity(studentService.deleteStudentByIdStudent(id,emailLogin));
    }

    @PostMapping("/major-department-facility")
    @CacheEvict(value = "studentsCache", allEntries = true)
    public ResponseEntity<?> createStaffFDM(ADCreateStudentFDMRequest request){
        return Helper.createResponseEntity(studentService.createStudentByFDM(request));
    }

    @PutMapping("/major-department-facility")
    @CacheEvict(value = "studentsCache", allEntries = true)
    public ResponseEntity<?> updateMajorDepartmentFacility(ADUpdateStudentFDMRequest request){
        return Helper.createResponseEntity(studentService.updateStudentByFDM(request));
    }

    @DeleteMapping("/{id}/{emailLogin}")
    @CacheEvict(value = "studentsCache", allEntries = true)
    public ResponseEntity<?> deleteStudent(@PathVariable String id,@PathVariable String emailLogin){
        return Helper.createResponseEntity(studentService.deleteStudent(id,emailLogin));
    }


    @GetMapping("/read/fileLog")
    public ResponseEntity<?> readCSV() {
        return Helper.createResponseEntity(studentService.readCSV());
    }

    @GetMapping("/history")
    @CacheEvict(value = "studentsCache", allEntries = true)
    public ResponseEntity<?> getChangeHistory(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "50") int size
    ) throws FileNotFoundException {
        return Helper.createResponseEntity(studentService.getLogsImportStudent(page, size));
    }

}
