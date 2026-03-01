package udpm.hn.server.core.manage.project.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import udpm.hn.server.core.admin.project.service.ADProjectService;
import udpm.hn.server.core.manage.project.model.request.MaProjectCreateRequest;
import udpm.hn.server.core.manage.project.model.request.MaProjectSearchRequest;
import udpm.hn.server.core.manage.project.model.request.MaRestartProjectRequest;
import udpm.hn.server.core.manage.project.service.MaProjectService;
import udpm.hn.server.infrastructure.constant.MappingConstants;
import udpm.hn.server.utils.Helper;

@RestController
@RequiredArgsConstructor
@RequestMapping(MappingConstants.API_MANAGE_PROJECT)
public class MaProjectRestController {

    private final MaProjectService maProjectService;

    private final ADProjectService adProjectService;

    @GetMapping
    public ResponseEntity<?> getAllProject(MaProjectSearchRequest request){
        return Helper.createResponseEntity(maProjectService.getAllProject(request));
    }

    @GetMapping("/facility")
    public ResponseEntity<?> getAllFacility(){
        return Helper.createResponseEntity(maProjectService.getAllFacility());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> detailProject(@PathVariable String id){
        return Helper.createResponseEntity(maProjectService.detailProject(id));
    }

    @GetMapping("/department-facility/{idFacility}")
    public ResponseEntity<?> getAllDepartmentFacility(@PathVariable String idFacility){
        return Helper.createResponseEntity(maProjectService.getAllDepartmentFacility(idFacility));
    }

    @GetMapping("/summary/{id}")
    public ResponseEntity<?> getDetailSummaryProject(@PathVariable String id) {
        return Helper.createResponseEntity(maProjectService.getDetailSummaryProject(id));
    }

    @GetMapping("/amount-of-phase-by-status/{id}")
    public ResponseEntity<?> getAmountOfPhaseByStatus(@PathVariable String id) {
        return Helper.createResponseEntity(maProjectService.getAmountOfPhaseByStatus(id));
    }

    @GetMapping("/amount-of-todo-by-phase/{id}")
    public ResponseEntity<?> getAmountOfTodoByPhase(@PathVariable String id) {
        return Helper.createResponseEntity(maProjectService.getAmountOfTodoByPhase(id));
    }

    @GetMapping("/finish-early/{id}/{emailLogin}")
    public ResponseEntity<?> finishEarlyProject(@PathVariable String id,@PathVariable String emailLogin) {
        return Helper.createResponseEntity(maProjectService.finishEarlyProject(id,emailLogin));
    }

    @PostMapping
    public ResponseEntity<?> createProject( @RequestBody MaProjectCreateRequest request){
        return  Helper.createResponseEntity(maProjectService.createProject(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateProject(@PathVariable String id, @RequestBody MaProjectCreateRequest request) {
        return Helper.createResponseEntity(maProjectService.updateProject(id, request));
    }

    @PutMapping("/restart-project/{id}")
    public ResponseEntity<?> restartProject(@PathVariable String id, @RequestBody MaRestartProjectRequest request) {
        return Helper.createResponseEntity(maProjectService.restartProject(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProject(@PathVariable String id){
        return Helper.createResponseEntity(maProjectService.deleteProject(id));
    }
}
