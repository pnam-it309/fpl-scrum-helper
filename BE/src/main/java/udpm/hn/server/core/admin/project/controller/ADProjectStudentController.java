package udpm.hn.server.core.admin.project.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import udpm.hn.server.core.admin.project.service.ADProjectStudentService;
import udpm.hn.server.core.manage.project.model.request.MaUserProjectRequest;
import udpm.hn.server.core.manage.project.service.MaProjectStudentService;
import udpm.hn.server.infrastructure.constant.MappingConstants;
import udpm.hn.server.utils.Helper;

@RestController
@RequiredArgsConstructor
@RequestMapping(MappingConstants.API_ADMIN_PROJECT_STUDENT )
public class ADProjectStudentController {

    private final ADProjectStudentService adProjectStudentService;

    private final MaProjectStudentService maProjectStudentService;

    @GetMapping("/user-project")
    public ResponseEntity<?> fetchDataUserProject(MaUserProjectRequest request){
        return Helper.createResponseEntity(maProjectStudentService.getAllUserProject(request));
    }

    @GetMapping("/{idMajorFacility}")
    public ResponseEntity<?> getAllPStudent(@PathVariable String idMajorFacility){
        return Helper.createResponseEntity(adProjectStudentService.getAll(idMajorFacility));
    }


    @GetMapping("/staff/{idMajorFacility}")
    public ResponseEntity<?> getAllStaff(@PathVariable String idMajorFacility){
        return Helper.createResponseEntity(adProjectStudentService.getAllStaff(idMajorFacility));
    }

    @GetMapping("/staff-project/{idProject}")
    public ResponseEntity<?> getAllStaffByProject(@PathVariable String idProject){
        return Helper.createResponseEntity(adProjectStudentService.getAllStaffByProject(idProject));
    }

    @GetMapping("/student-project/{idProject}")
    public ResponseEntity<?> getAllStudentByProject(@PathVariable String idProject){
        return Helper.createResponseEntity(adProjectStudentService.getAllStudentByProject(idProject));
    }


}
