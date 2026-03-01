package udpm.hn.server.core.manage.phase.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import udpm.hn.server.core.common.base.ResponseObject;
import udpm.hn.server.core.manage.phase.model.request.MACreatePhaseRequest;
import udpm.hn.server.core.manage.phase.model.request.MAPhaseRequest;
import udpm.hn.server.core.manage.phase.model.request.MATodoByPhase;
import udpm.hn.server.core.manage.phase.model.request.StartAndSprintRequest;
import udpm.hn.server.core.manage.phase.service.MAPhaseService;
import udpm.hn.server.infrastructure.constant.MappingConstants;
import udpm.hn.server.utils.Helper;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping(MappingConstants.API_MANAGE_PHASE_PROJECT)
public class MAPhaseRestController {

    private final MAPhaseService maPhaseService;

    @GetMapping
    public ResponseEntity<?> getAllPhase(MAPhaseRequest request){
        log.info("Danh sách request nhận về trong getAll Phase : ===>",request.toString());
        return Helper.createResponseEntity(maPhaseService.getAllPhase(request));
    }

//    @GetMapping("/{id}")
//    public ResponseEntity<?> getAllPhase(@PathVariable String id){
//        return Helper.createResponseEntity(maPhaseService.detailPhase(id));
//    }

    @GetMapping("/statistics/{idProject}")
    public ResponseEntity<?> getAllPhaseStatistics(@PathVariable String idProject){
        return Helper.createResponseEntity(maPhaseService.getAllPhase(idProject));
    }

    @GetMapping("/phase-handle-success/{idProject}")
    public ResponseEntity<?> getPhaseSuccess(@PathVariable String idProject){

        log.info(idProject +"idproject");
        return Helper.createResponseEntity(maPhaseService.findByPhaseStatus(idProject));
    }


    @GetMapping("/sprint")
    public ResponseEntity<?> getAllSprint(MAPhaseRequest request){
        return Helper.createResponseEntity(maPhaseService.getAllSprint(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getAllPhaseByIdProject(@PathVariable String id, MAPhaseRequest request) {
        ResponseObject<?> response = maPhaseService.getAllPhaseByProjectId(id);

        if (response == null || response.getStatus() == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy dữ liệu");
        }

        return ResponseEntity.status(response.getStatus()).body(response);
    }


    @MessageMapping("/create-phase")
    @SendTo("/topic/create-phase")
    public ResponseEntity<?> createPhase(MACreatePhaseRequest request){
        return Helper.createResponseEntity(maPhaseService.createPhase(request));
    }

    @MessageMapping("/create-todo-by-phase")
    @SendTo("/topic/create-todo-by-phase")
    public ResponseEntity<?> createTodoByPhase(MATodoByPhase request){
        return Helper.createResponseEntity(maPhaseService.createTodoByPhase(request));
    }

    @MessageMapping("/delete-phase/{id}")
    @SendTo("/topic/delete-phase")
    public ResponseEntity<?> deletePhase(@DestinationVariable String id){
        return Helper.createResponseEntity(maPhaseService.deletePhase(id));
    }

    @MessageMapping("/delete-todo-by-phase/{id}")
    @SendTo("/topic/delete-todo-by-phase")
    public ResponseEntity<?> deleteTodoByPhase(@DestinationVariable String id){
        return Helper.createResponseEntity(maPhaseService.deleteTodoByPhase(id));
    }

    @MessageMapping("/update-phase/{id}")
    @SendTo("/topic/update-phase")
    public ResponseEntity<?> updatePhase(@DestinationVariable String id,MACreatePhaseRequest request){
        return Helper.createResponseEntity(maPhaseService.updatePhase(id,request));
    }

    @MessageMapping("/update-status-phase/{id}")
    @SendTo("/topic/update-status-phase")
    public ResponseEntity<?> updateStatusPhase(@DestinationVariable String id, @Payload Integer statusPhase){
        return Helper.createResponseEntity(maPhaseService.updateStatusPhase(id,statusPhase));
    }

}
