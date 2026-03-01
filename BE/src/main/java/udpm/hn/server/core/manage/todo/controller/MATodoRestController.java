package udpm.hn.server.core.manage.todo.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.Nationalized;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import udpm.hn.server.core.manage.todo.model.request.MATodoCURequest;
import udpm.hn.server.core.manage.todo.model.request.MATodoSearchRequest;
import udpm.hn.server.core.manage.todo.model.request.MATodoStatisticsRequest;
import udpm.hn.server.core.manage.todo.service.MATodoService;
import udpm.hn.server.core.manage.todo.service.MATypeTodoService;
import udpm.hn.server.infrastructure.constant.MappingConstants;
import udpm.hn.server.utils.Helper;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping(MappingConstants.API_MANAGE_TODO)
public class MATodoRestController {

    private final MATodoService maTodoService;

    private final MATypeTodoService maTypeTodoService;

    @GetMapping
    public ResponseEntity<?> getAllTodo(MATodoSearchRequest maTodoRequest){
        return Helper.createResponseEntity(maTodoService.getAllTodo(maTodoRequest));
    }

    @GetMapping("/type")
    public ResponseEntity<?> listTypeTodo(){
        return Helper.createResponseEntity(maTypeTodoService.listTyTodo());
    }

    @GetMapping("/{idProject}")
    public ResponseEntity<?> fetchDataTodoByProject( @PathVariable("idProject") String idProject){
        return Helper.createResponseEntity(maTodoService.fetchDataTodoByProject(idProject));
    }

    @GetMapping("/statistics")
    public ResponseEntity<?> fetchAllDataTodoStatistics(MATodoStatisticsRequest request ){
        return Helper.createResponseEntity(maTodoService.getAllTodoStatistics(request));
    }

    @GetMapping("/statistics/count-todo-staffproject")
    @Nationalized
    public ResponseEntity<?> countTodoByStaffProject(MATodoStatisticsRequest request ){
        return Helper.createResponseEntity(maTodoService.countTodoByStaffProject(request));
    }

    @GetMapping("/user-todo/{id}")
    public ResponseEntity<?> getAllStaffByTodo(MATodoSearchRequest maTodoRequest,@PathVariable("id") String id){
        return Helper.createResponseEntity(maTodoService.getAllStaffByTodo(maTodoRequest,id));
    }

    @GetMapping("/todo-by-phase/{id}")
    public ResponseEntity<?> getAllTodoByPhase(MATodoSearchRequest maTodoRequest,@PathVariable("id") String id){
        return Helper.createResponseEntity(maTodoService.getAllTodoByPhase(maTodoRequest,id));
    }

    @GetMapping("/staff-project/{id}")
    public ResponseEntity<?> getAllTodo(@PathVariable String id){
        return Helper.createResponseEntity(maTodoService.dataStaffProject(id));
    }

    @MessageMapping("/create-todo")
    @SendTo("/topic/create-todo")
    public ResponseEntity<?> createTodo(MATodoCURequest request){
        log.info("request create todo : ==> {}",request.toString());
        return Helper.createResponseEntity(maTodoService.createTodo(request));
    }

    @MessageMapping("/update-todo/{id}")
    @SendTo("/topic/update-todo")
    public ResponseEntity<?> updateTodo(@DestinationVariable String id,MATodoCURequest request){
        return Helper.createResponseEntity(maTodoService.updateTodo(id,request));
    }

    @MessageMapping("/delete-todo/{id}")
    @SendTo("/topic/delete-todo")
    public ResponseEntity<?> createTodo(@DestinationVariable String id){
        return Helper.createResponseEntity(maTodoService.deleteTodo(id));
    }

}
