package udpm.hn.server.core.member.projectdetails.service;

import jakarta.validation.Valid;
import org.springframework.data.repository.query.Param;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import udpm.hn.server.core.common.base.ResponseObject;
import udpm.hn.server.core.member.projectdetails.model.request.*;

import java.util.Optional;

public interface MBMeTodoService {

    ResponseObject<?> getAllBoard(MBMeFilterTodoRequest request);

    ResponseObject<?> findTodoById(MBFilterTodoModalRequest request);

    ResponseObject<?> getAllDetailTodo(MBFilterTodoModalRequest request);

    ResponseObject<?> getAllCheckTodoChild(MBCheckTodoChildRequest request);

    ResponseObject<?> updateIndexTodoViewTable(@Valid MBMeUpdateIndexTodoRequest request);

    ResponseObject<?> createTodo(MBMeCreateTodoRequest request);

    ResponseObject<?> createTodoChildChecklist(@Valid MBMeCreateDetailTodoChildRequest request);

    ResponseObject<?> editTodoChildChecklist(@Valid MBMeEditDetailTodoChildRequest request);

    ResponseObject<?> deleteDetailTodoChild(@Valid MBMeDeleteDetailTodoChildRequest request);

    ResponseObject<?> handleStatusTodoChildChange(@Valid MBHandleStatusTodoChildRequest request);

    ResponseObject<?>  updateDescriptionsTodo(@Valid final MBMeUpdateDescriptionsTodoRequest request, StompHeaderAccessor headerAccessor);

    ResponseObject<?> updateNameTodo(@Valid MBMeUpdateNameTodoRequest request, StompHeaderAccessor headerAccessor);

    ResponseObject<?> updateDeadlineTodo(@Valid MBMeUpdateDeadlineTodoRequest request, StompHeaderAccessor headerAccessor);

    ResponseObject<?> deleteDeadlineTodo(@Valid MBMeDeleteDeadlineTodoRequest request, StompHeaderAccessor headerAccessor);

    ResponseObject<?> updateCompleteTodo(@Valid MBMeUpdateCompleteTodoRequest request);

    ResponseObject<?> updatePriorityLevel(@Valid MBMeUpdateTodoRequest request, StompHeaderAccessor headerAccessor);

    ResponseObject<?> updateProgress(@Valid MBMeUpdateProgressTodoRequest request, StompHeaderAccessor headerAccessor);

    ResponseObject<?> sortTodoPriority(@Valid MBMeSortTodoRequest request);

    ResponseObject<?> sortTodoDeadline(@Valid MBMeSortTodoRequest request);

    ResponseObject<?> sortTodoCreatedDate(@Valid MBMeSortTodoRequest request);

    ResponseObject<?> sortTodoName(@Valid MBMeSortTodoRequest request);

    ResponseObject<?> sortTodoProgress(@Valid MBMeSortTodoRequest request);

    ResponseObject<?> getStatusByIdPhase(String idPhase);

}
