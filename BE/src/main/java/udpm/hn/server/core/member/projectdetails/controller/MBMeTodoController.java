package udpm.hn.server.core.member.projectdetails.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.web.bind.annotation.*;
import udpm.hn.server.core.common.base.ResponseObject;
import udpm.hn.server.core.member.projectdetails.model.request.*;
import udpm.hn.server.core.member.projectdetails.service.MBMeTodoService;
import udpm.hn.server.infrastructure.constant.MappingConstants;
import udpm.hn.server.utils.Helper;
import udpm.hn.server.utils.StatusPhase;

@Slf4j
@RestController
@RequestMapping(MappingConstants.API_PROJECT_DETAILS_TODO)
@RequiredArgsConstructor
public class MBMeTodoController {

    private final MBMeTodoService mbMeTodoService;

    @GetMapping("/board")
    public ResponseEntity<?> getTodoBoard(MBMeFilterTodoRequest request) {
        return Helper.createResponseEntity(mbMeTodoService.getAllBoard(request));
    }

    @GetMapping("/find-todo")
    public ResponseObject<?> findTodoById(MBFilterTodoModalRequest request) {
        return mbMeTodoService.findTodoById(request);
    }

    @GetMapping("/get-all-detail")
    public ResponseObject<?> getAllDetailTodo(MBFilterTodoModalRequest request) {
        return mbMeTodoService.getAllDetailTodo(request);
    }

    @GetMapping("/check-todochild")
    public ResponseObject<?> getAllCheckTodoChild(MBCheckTodoChildRequest request) {
        return mbMeTodoService.getAllCheckTodoChild(request);
    }

    @MessageMapping("/update-index-todo/{projectId}/{periodId}")
    @SendTo("/topic/update-index-todo")
    public ResponseObject<?> updateIndexTodo(MBMeUpdateIndexTodoRequest request,
                                             @ModelAttribute MBDesVarProjectIdAndPeriodIdRequest des
    ) {
        return mbMeTodoService.updateIndexTodoViewTable(request);
    }

    @MessageMapping("/create-todo/{projectId}/{periodId}")
    @SendTo("/topic/create-todo")
    public ResponseObject<?> createTodo(MBMeCreateTodoRequest request,
                                        @ModelAttribute MBDesVarProjectIdAndPeriodIdRequest des) {
        return mbMeTodoService.createTodo(request);
    }

    @MessageMapping("/create-todochild-checklist")
    @SendTo("/topic/create-todochild-checklist")
    public ResponseObject<?> createTodoChildChecklist( MBMeCreateDetailTodoChildRequest request) {
        return mbMeTodoService.createTodoChildChecklist(request);
    }

    @MessageMapping("/edit-todochild-checklist")
    @SendTo("/topic/edit-todochild-checklist")
    public ResponseObject<?> editTodoChildChecklist( MBMeEditDetailTodoChildRequest request) {
        return mbMeTodoService.editTodoChildChecklist(request);
    }

    @MessageMapping("/delete-todo-checklist")
    @SendTo("/topic/delete-todo-checklist")
    public ResponseObject<?> deleteTodoChecklist( MBMeDeleteDetailTodoChildRequest request) {
        return mbMeTodoService.deleteDetailTodoChild(request);
    }

    @MessageMapping("/update-status-todochild-checklist")
    @SendTo("/topic/update-status-todochild-checklist")
    public ResponseObject<?> updateStatusTodoChild( MBHandleStatusTodoChildRequest request) {
        return mbMeTodoService.handleStatusTodoChildChange(request);
    }

    @MessageMapping("/update-descriptions-todo")
    @SendTo("/topic/update-descriptions-todo")
    public ResponseObject<?> updateDescriptionsTodo( MBMeUpdateDescriptionsTodoRequest request,
                                                 StompHeaderAccessor headerAccessor
    ) {
        return mbMeTodoService.updateDescriptionsTodo(request, headerAccessor);
    }

    @MessageMapping("/update-name-todo")
    @SendTo("/topic/update-name-todo")
    public ResponseObject<?> updateNameTodo( MBMeUpdateNameTodoRequest request,
                                                     StompHeaderAccessor headerAccessor
    ) {
        return mbMeTodoService.updateNameTodo(request, headerAccessor);
    }

    @MessageMapping("/update-deadline-todo")
    @SendTo("/topic/update-deadline-todo")
    public ResponseObject<?> updateDeadlineTodo(MBMeUpdateDeadlineTodoRequest request,
                                             StompHeaderAccessor headerAccessor) {
        return mbMeTodoService.updateDeadlineTodo(request, headerAccessor);
    }

    @MessageMapping("/delete-deadline-todo")
    @SendTo("/topic/delete-deadline-todo")
    public ResponseObject<?> deleteDeadlineTodo(MBMeDeleteDeadlineTodoRequest request,
                                             StompHeaderAccessor headerAccessor) {
        return mbMeTodoService.deleteDeadlineTodo(request, headerAccessor);
    }

    @MessageMapping("/update-complete-todo")
    @SendTo("/topic/update-complete-todo")
    public ResponseObject<?> updateCompleteTodo(MBMeUpdateCompleteTodoRequest request
    ) {
        return mbMeTodoService.updateCompleteTodo(request);
    }

    @MessageMapping("/update-priority-todo")
    @SendTo("/topic/todo")
    public ResponseObject<?> updatePriorityLevel( MBMeUpdateTodoRequest request,
                                              StompHeaderAccessor headerAccessor) {
        return mbMeTodoService.updatePriorityLevel(request, headerAccessor);
    }

    @MessageMapping("/update-progress-todo")
    @SendTo("/topic/update-progress-todo")
    public ResponseObject<?> updateProgress( MBMeUpdateProgressTodoRequest request,
                                             StompHeaderAccessor headerAccessor) {
        return mbMeTodoService.updateProgress(request, headerAccessor);
    }

    @MessageMapping("/sort-todo-priority")
    @SendTo("/topic/sort-todo-priority")
    public ResponseObject<?> sortTodoPriority(MBMeSortTodoRequest request
    ) {
        return mbMeTodoService.sortTodoPriority(request);
    }

    @MessageMapping("/sort-todo-deadline")
    @SendTo("/topic/sort-todo-deadline")
    public ResponseObject<?> sortTodoDeadline(MBMeSortTodoRequest request
    ) {
        return mbMeTodoService.sortTodoDeadline(request);
    }

    @MessageMapping("/sort-todo-created-date")
    @SendTo("/topic/sort-todo-created-date")
    public ResponseObject<?> sortTodoCreatedDate(MBMeSortTodoRequest request
    ) {
        return mbMeTodoService.sortTodoCreatedDate(request);
    }

    @MessageMapping("/sort-todo-progress")
    @SendTo("/topic/sort-todo-progress")
    public ResponseObject<?> sortTodoProgress(MBMeSortTodoRequest request
    ) {
        return mbMeTodoService.sortTodoProgress(request);
    }

    @MessageMapping("/sort-todo-name")
    @SendTo("/topic/sort-todo-name")
    public ResponseObject<?> sortTodoName(MBMeSortTodoRequest request
    ) {
        return mbMeTodoService.sortTodoName(request);
    }

    @GetMapping("/status-phase/{idPhase}")
    public ResponseEntity<?> getStatusByIdPhase(@PathVariable("idPhase") String idPhase) {
        return Helper.createResponseEntity(mbMeTodoService.getStatusByIdPhase(idPhase));
    }


}
