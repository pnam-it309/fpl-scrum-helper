package udpm.hn.server.core.manage.todo.service;

import org.springframework.data.repository.query.Param;
import udpm.hn.server.core.common.base.ResponseObject;
import udpm.hn.server.core.manage.todo.model.request.MAVoteTodoRequest;

public interface MATodoVoteService {

    ResponseObject<?> createVote(MAVoteTodoRequest request);

    ResponseObject<?> getAllVote( );

    ResponseObject<?> countTodoVotedByPriorityLevel(String idProject);

    ResponseObject<?> deleteTodoVote(MAVoteTodoRequest request);

    ResponseObject<?> findAllVotedTodos(String idProject);

}
