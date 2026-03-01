package udpm.hn.server.core.member.projectdetails.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import udpm.hn.server.core.common.base.ResponseObject;
import udpm.hn.server.core.member.projectdetails.model.request.*;
import udpm.hn.server.core.member.projectdetails.service.MBMeTodoListService;
import udpm.hn.server.infrastructure.constant.MappingConstants;
import udpm.hn.server.utils.Helper;

@RestController
@RequestMapping(MappingConstants.API_PROJECT_DETAILS_TODO_LIST)
@RequiredArgsConstructor
public class MBMeTodoListController {

    private final MBMeTodoListService mbMeTodoListService;

    @MessageMapping("/update-index-todo-list/{projectId}")
    @SendTo("/topic/update-index-todo-list")
    public ResponseObject<?> updateIndexTodoList(MBMeUpdateTodoListRequest request,
                                                 @DestinationVariable String projectId) {
        request.setIdProject(projectId);
        return mbMeTodoListService.updateIndexTodoList(request);
    }

    @MessageMapping("/create-todo-list/{projectId}")
    @SendTo("/topic/create-todo-list")
    public ResponseObject<?> createTodoList(MBMeCreateTodoListRequest request,
                                         @DestinationVariable String projectId,
                                         StompHeaderAccessor headerAccessor) {
        return mbMeTodoListService.createTodoList(request, headerAccessor);
    }

    @MessageMapping("/delete-todo-list/{projectId}")
    @SendTo("/topic/delete-todo-list")
    public ResponseObject<?> deleteTodoList( MBMeDeleteTodoListRequest request,
                                         @DestinationVariable String projectId) {
        return mbMeTodoListService.deleteTodoList(request);
    }

    @MessageMapping("/update-name-todo-list/{projectId}")
    @SendTo("/topic/update-name-todo-list")
    public ResponseObject<?> updateNameTodoList( MBMeUpdateNameTodoListRequest request,
                                             @DestinationVariable String projectId) {
        return mbMeTodoListService.updateNameTodoList(request);
    }

    @GetMapping("/all-todolist/by-id-phase-project")
    public ResponseObject<?> getAllTodoListByIdPhaseProject(@RequestParam("idPhaseProject") String idPhaseProject) {
        return mbMeTodoListService.getAllTodoListByIdPhaseProject(idPhaseProject);
    }


    @GetMapping("/details-project/by-id-phase-project")
    public ResponseEntity<?> getProjectByIdPhaseProject(@RequestParam("idPhaseProject") String idPhaseProject) {
        return  Helper.createResponseEntity(mbMeTodoListService.getProjectByIdPhaseProject(idPhaseProject));
    }

}
