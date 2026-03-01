package udpm.hn.server.core.admin.project.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import udpm.hn.server.core.admin.project.model.request.ADProjectCreateRequest;
import udpm.hn.server.core.admin.project.model.request.ADProjectSearchRequest;
import udpm.hn.server.core.admin.project.service.ADProjectService;
import udpm.hn.server.infrastructure.constant.MappingConstants;
import udpm.hn.server.utils.Helper;

@RestController
@RequiredArgsConstructor
@RequestMapping(MappingConstants.API_ADMIN_PROJECT)
public class ADProjectRestController {

    private final ADProjectService adProjectService;

    @GetMapping
    public ResponseEntity<?> getAllProject(ADProjectSearchRequest request){
        return Helper.createResponseEntity(adProjectService.getAllProject(request));
    }

    @GetMapping("/facility")
    public ResponseEntity<?> getAllFacility(){
        return Helper.createResponseEntity(adProjectService.getAllFacility());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> detailProject(@PathVariable String id){
        return Helper.createResponseEntity(adProjectService.detailProject(id));
    }

    @GetMapping("/department-facility/{idFacility}")
    public ResponseEntity<?> getAllDepartmentFacility(@PathVariable String idFacility){
        return Helper.createResponseEntity(adProjectService.getAllDepartmentFacility(idFacility));
    }

    @PostMapping
    public ResponseEntity<?> createProject( @RequestBody ADProjectCreateRequest request){
        return  Helper.createResponseEntity(adProjectService.createProject(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateProject(@PathVariable String id, @RequestBody ADProjectCreateRequest request){
        return  Helper.createResponseEntity(adProjectService.updateProject(id,request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProject(@PathVariable String id){
        return Helper.createResponseEntity(adProjectService.deleteProject(id));
    }

    @GetMapping("/project-statistics")
    public ResponseEntity<?> countProjectByStatus(){
        return Helper.createResponseEntity(adProjectService.countProjectByStatus());
    }

    @GetMapping("/project-statistics/participants")
    public ResponseEntity<?> getProjectParticipantCounts() {
        return Helper.createResponseEntity(adProjectService.getProjectParticipantCounts());
    }

    @GetMapping("/project-statistics/project-todo-counts")
    public ResponseEntity<?> getProjectTodoCounts() {
        return ResponseEntity.ok(adProjectService.getProjectTodoCounts());
    }

    @GetMapping("/project-statistics/count-total-projects")
    public ResponseEntity<?> getCountTotalProjects() {
        return ResponseEntity.ok(adProjectService.countTotalProjects());
    }

    @GetMapping("/project-statistics/project-by-facility")
    public ResponseEntity<?> getProjectCountByFacility() {
        return ResponseEntity.ok(adProjectService.countProjectsByFacility());
    }

    @GetMapping("/project-statistics/project-by-department")
    public ResponseEntity<?> getProjectCountByDepartment() {
        return ResponseEntity.ok(adProjectService.countProjectsByDepartment());
    }

    @GetMapping("/project-statistics/task-by-status-project")
    public ResponseEntity<?> getTaskByStatusAndProject() {
        return ResponseEntity.ok(adProjectService.countTaskByStatusAndProject());
    }
}

