package udpm.hn.server.core.manage.project.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import udpm.hn.server.core.common.base.ResponseObject;
import udpm.hn.server.core.manage.project.service.MaTodoService;
import udpm.hn.server.infrastructure.constant.MappingConstants;
import udpm.hn.server.utils.Helper;

@RestController
@RequiredArgsConstructor
@RequestMapping(MappingConstants.API_MANAGE_TODO)
public class MaTodoController {

    @Autowired
    private MaTodoService maTodoService;

    @GetMapping("/count-todo-by-todo-list-all-project")
    public ResponseObject countTodoByTodoListAllProject(@RequestParam("projectId") String project){
        return new ResponseObject(maTodoService.countTodoByTodoListAllProject(project));
    }

    @GetMapping("/count-todo-by-due-date-all-project")
    public ResponseObject countTodoByDueDateAllProject(@RequestParam("projectId") String projectId,
                                                       @RequestParam("statusTodo") Integer statusTodo){
        return new ResponseObject(maTodoService.countTodoByDueDateAllProject(projectId,statusTodo));
    }

    @GetMapping("/count-todo-by-no-due-date-all-project")
    public ResponseObject countTodoByNoDueDateAllProject(@RequestParam("projectId") String projectId){
        return new ResponseObject(maTodoService.countTodoByNoDueDateAllProject(projectId));
    }

    @GetMapping("/count-todo-by-label-all-project")
    public ResponseObject countTodoByLabelAllProject(@RequestParam("projectId") String projectId) {
        return new ResponseObject(maTodoService.countTodoByLabelAllProject(projectId));
    }

    @GetMapping("/count-todo-by-no-label-all-project")
    public ResponseObject countTodoByNoLabelAllProject(@RequestParam("projectId") String projectId) {
        return new ResponseObject(maTodoService.countTodoByNoLabelAllProject(projectId));
    }

    @GetMapping("/todo-by-phase/count-todo-by-todo-list-phase")
    public ResponseObject countTodoByTodoListPeriod(@RequestParam("projectId") String projectId,
                                                    @RequestParam("phaseId") String phaseId) {
        return new ResponseObject(maTodoService.countTodoByTodoListPhase(projectId, phaseId));
    }

    @GetMapping("/count-todo-by-due-date-period")
    public ResponseObject countTodoByDueDatePeriod(@RequestParam("projectId") String projectId,
                                                   @RequestParam("periodId") String periodId,
                                                   @RequestParam("statusTodo") Integer statusTodo) {
        return new ResponseObject(maTodoService.countTodoByDueDatePhase(projectId, periodId, statusTodo));
    }

    @GetMapping("/count-todo-by-no-due-date-period")
    public ResponseObject countTodoByNoDueDatePeriod(@RequestParam("projectId") String projectId,
                                                     @RequestParam("periodId") String periodId) {
        return new ResponseObject(maTodoService.countTodoByNoDueDatePhase(projectId, periodId));
    }

    @GetMapping("/count-todo-by-priority-level")
    public ResponseObject countTodoByPriorityLevel(@RequestParam("projectId") String projectId,
                                                   @RequestParam("priorityLevel") Integer priorityLevel) {
        return new ResponseObject(maTodoService.countTodoByPriorityLevel(projectId,priorityLevel));
    }

    @GetMapping("/count-todo-by-todo-status/{id}")
    public ResponseEntity<?> countTodoByStatusTodo(@PathVariable("id") String idProject) {
        return Helper.createResponseEntity(maTodoService.countTodoByTodoStatus(idProject));
    }
}
