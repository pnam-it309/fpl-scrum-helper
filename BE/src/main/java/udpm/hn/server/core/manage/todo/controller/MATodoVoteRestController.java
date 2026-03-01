package udpm.hn.server.core.manage.todo.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import udpm.hn.server.core.manage.todo.model.request.MAVoteTodoRequest;
import udpm.hn.server.core.manage.todo.service.MATodoVoteService;
import udpm.hn.server.infrastructure.constant.MappingConstants;
import udpm.hn.server.utils.Helper;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping(MappingConstants.API_MANAGE_TODO_VOTE)
public class MATodoVoteRestController {

    private final MATodoVoteService maTodoVoteService;

    @GetMapping
    public ResponseEntity<?> getAllTodoVote(){
        return Helper.createResponseEntity(maTodoVoteService.getAllVote());
    }

    @MessageMapping("/create-todo-vote")
    @SendTo("/topic/create-todo-vote")
    public ResponseEntity<?> createTodoVote(MAVoteTodoRequest request){
        return Helper.createResponseEntity(maTodoVoteService.createVote(request));
    }

    @MessageMapping("/delete-todo-vote")
    @SendTo("/topic/delete-todo-vote")
    public ResponseEntity<?> delete(MAVoteTodoRequest request){
        return Helper.createResponseEntity(maTodoVoteService.deleteTodoVote(request));
    }

    @GetMapping("/count-todo-voted-by-priority-level/{id}")
    public ResponseEntity<?> countTodoVotedByPriorityLevel(@PathVariable("id") String idProject){
        return Helper.createResponseEntity(maTodoVoteService.countTodoVotedByPriorityLevel(idProject));
    }

    @GetMapping("/find-all-voted-todos/{id}")
    public ResponseEntity<?> findAllVotedTodos(@PathVariable("id") String idProject){
        return Helper.createResponseEntity(maTodoVoteService.findAllVotedTodos(idProject));
    }

}
