package udpm.hn.server.core.manage.project.controller;

import io.jsonwebtoken.Header;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import udpm.hn.server.core.manage.project.model.request.MaCreateOrUpdateTodoListProject;
import udpm.hn.server.core.manage.project.model.request.MaTodoListProjectRequest;
import udpm.hn.server.core.manage.project.model.request.MaUpdateTodoProjectRequest;
import udpm.hn.server.core.manage.project.service.MaTodoListProjectService;
import udpm.hn.server.infrastructure.constant.MappingConstants;
import udpm.hn.server.utils.Helper;

@Controller
@RequiredArgsConstructor
@RequestMapping(MappingConstants.API_MANAGE_TODO_LIST)
public class MaTodoListProjectRestController {

    private final MaTodoListProjectService maTodoListProjectService;

    @GetMapping("/get-all-todolist/{id}")
    public ResponseEntity<?> getAll(@PathVariable String id, MaTodoListProjectRequest request){
        return Helper.createResponseEntity(maTodoListProjectService.getAllTodoList(request, id));
    }

    @GetMapping("/get-all-phase/{id}")
    public ResponseEntity<?> getAllPhase(@PathVariable String id){
        return Helper.createResponseEntity(maTodoListProjectService.getAllPhase(id));
    }

    @GetMapping("/get-all-todo/{id}")
    public ResponseEntity<?> getAllTodoProject(@PathVariable String id, MaTodoListProjectRequest request){
        return Helper.createResponseEntity(maTodoListProjectService.getAllTodoByProject(request, id));
    }

    @PostMapping("/add")
    public ResponseEntity<?> add(@RequestBody MaCreateOrUpdateTodoListProject request){
        return Helper.createResponseEntity(maTodoListProjectService.addTodoList(request));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> update(@PathVariable String id,@RequestBody MaCreateOrUpdateTodoListProject request){
        return Helper.createResponseEntity(maTodoListProjectService.updateTodpList(request, id));
    }

    @GetMapping("/detail/{id}")
    public ResponseEntity<?> getAll(@PathVariable String id){
        return Helper.createResponseEntity(maTodoListProjectService.detailTodoList(id));
    }

    @GetMapping("/read/fileLog")
    public ResponseEntity<?> readCSV() {
        return Helper.createResponseEntity(maTodoListProjectService.readCSV());
    }

    @PutMapping("/update-todo/{id}")
    public ResponseEntity<?> updateTodo(@PathVariable String id,@RequestBody MaUpdateTodoProjectRequest request){
        return Helper.createResponseEntity(maTodoListProjectService.updateTodoProject(request, id));
    }

    @PutMapping("/update-phase-todo/{todoId}/{newPhaseId}/{projectId}")
    public ResponseEntity<?> updatePhaseTodo(@PathVariable String todoId, @PathVariable String newPhaseId, @PathVariable String projectId){
        return Helper.createResponseEntity(maTodoListProjectService.updateTodoByPhaseProject(todoId, newPhaseId, projectId));
    }
}
