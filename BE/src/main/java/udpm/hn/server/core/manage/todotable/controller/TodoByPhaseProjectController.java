package udpm.hn.server.core.manage.todotable.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import udpm.hn.server.core.manage.todotable.service.TodoByPhaseProjectService;
import udpm.hn.server.infrastructure.constant.MappingConstants;
import udpm.hn.server.utils.Helper;

import java.io.FileNotFoundException;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping(MappingConstants.API_MANAGE_TODO_LIST)
public class TodoByPhaseProjectController {

    private final TodoByPhaseProjectService todoByPhaseProjectService;

    @GetMapping("/get-todo-phase/{idProject}/{idPhase}")
    public ResponseEntity<?> getAllTodoProject(
            @PathVariable String idProject,
            @PathVariable String idPhase,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return Helper.createResponseEntity(
                todoByPhaseProjectService.getAllTodoByPhaseProject(idProject, idPhase, page, size)
        );
    }


    @GetMapping("/get-todolist-phase/{idProject}/{idPhase}")
    public ResponseEntity<?> getAllTodoListByPhaseProject(@PathVariable String idProject, @PathVariable String idPhase){
        return Helper.createResponseEntity(todoByPhaseProjectService.getAllTodoListByPhaseProject(idProject, idPhase));
    }

    @GetMapping("/index-todo/{id}")
    public ResponseEntity<?> getIndex(@PathVariable String id){
        return Helper.createResponseEntity(todoByPhaseProjectService.getIndex(id));
    }

    @GetMapping("/history/{idProject}")
    public ResponseEntity<?> getChangeHistory(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "50") int size,
            @PathVariable String idProject
    ) throws FileNotFoundException {
        return Helper.createResponseEntity(todoByPhaseProjectService.getLogsImportTodo(page, size, idProject));
    }
}
