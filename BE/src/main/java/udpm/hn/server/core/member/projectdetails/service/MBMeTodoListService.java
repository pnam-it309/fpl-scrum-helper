package udpm.hn.server.core.member.projectdetails.service;

import jakarta.validation.Valid;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import udpm.hn.server.core.common.base.ResponseObject;
import udpm.hn.server.core.member.projectdetails.model.request.MBMeCreateTodoListRequest;
import udpm.hn.server.core.member.projectdetails.model.request.MBMeDeleteTodoListRequest;
import udpm.hn.server.core.member.projectdetails.model.request.MBMeUpdateNameTodoListRequest;
import udpm.hn.server.core.member.projectdetails.model.request.MBMeUpdateTodoListRequest;

public interface MBMeTodoListService {

    ResponseObject<?> updateIndexTodoList(@Valid MBMeUpdateTodoListRequest request);

    ResponseObject<?> createTodoList(@Valid MBMeCreateTodoListRequest request, StompHeaderAccessor headerAccessor);

    ResponseObject<?> deleteTodoList(@Valid MBMeDeleteTodoListRequest request);

    ResponseObject<?>  updateNameTodoList(@Valid MBMeUpdateNameTodoListRequest request);

    ResponseObject<?> getAllTodoListByIdPhaseProject(String idPhaseProject);

    ResponseObject<?> getProjectByIdPhaseProject(String idPhaseProject);

}
