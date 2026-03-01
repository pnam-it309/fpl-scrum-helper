package udpm.hn.server.core.member.phase.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.*;
import udpm.hn.server.core.common.base.ResponseObject;
import udpm.hn.server.core.manage.project.model.request.MaTodoListProjectRequest;
import udpm.hn.server.core.member.phase.model.request.MBCreatePhaseRequest;
import udpm.hn.server.core.member.phase.model.request.MBPhaseRequest;
import udpm.hn.server.core.member.phase.model.request.MBTodoByPhase;
import udpm.hn.server.core.member.phase.service.MBPhaseService;
import udpm.hn.server.infrastructure.constant.MappingConstants;
import udpm.hn.server.utils.Helper;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping(MappingConstants.API_MEMBER_PHASE_PROJECT)
public class MBPhaseRestController {

    private final MBPhaseService maPhaseService;

    @GetMapping("/{idProject}")
    public ResponseEntity<?> getAllPhase(MBPhaseRequest request, @PathVariable String idProject){
        return Helper.createResponseEntity(maPhaseService.getAllPhase(request, idProject));
    }

    @GetMapping("/detail/{idProject}")
    public ResponseEntity<?> detail( @PathVariable String idProject){
        return Helper.createResponseEntity(maPhaseService.detail( idProject));
    }

//    @GetMapping("/get-all-phase/{id}")
//    public ResponseEntity<?> getAllPhase(@PathVariable String id){
//        return Helper.createResponseEntity(maPhaseService.getAllPhase(id));
//    }

    @GetMapping("/get-all-todo/{id}")
    public ResponseEntity<?> getAllTodoProject(@PathVariable String id, MBPhaseRequest request){
        return Helper.createResponseEntity(maPhaseService.getAllTodoByProject(request, id));
    }

    @PutMapping("/update-phase-todo/{todoId}/{newPhaseId}/{projectId}")
    public ResponseEntity<?> updatePhaseTodo(@PathVariable String todoId, @PathVariable String newPhaseId, @PathVariable String projectId){
        return Helper.createResponseEntity(maPhaseService.updateTodoByPhaseProject(todoId, newPhaseId, projectId));
    }

    @GetMapping("/get-all-phase/{id}")
    public ResponseEntity<?> getAllPhase(@PathVariable String id){
        return Helper.createResponseEntity(maPhaseService.getAllPhase(id));
    }

}
