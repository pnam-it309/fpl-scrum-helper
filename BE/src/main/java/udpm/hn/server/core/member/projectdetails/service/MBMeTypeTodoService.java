package udpm.hn.server.core.member.projectdetails.service;

import jakarta.validation.Valid;
import udpm.hn.server.core.admin.category.modal.request.ADCategoryRequest;
import udpm.hn.server.core.common.base.ResponseObject;
import udpm.hn.server.core.member.projectdetails.model.request.ADCreateUpdateTypeTodoRequest;
import udpm.hn.server.core.member.projectdetails.model.request.MBJoinTypeTodoRequest;
import udpm.hn.server.core.member.projectdetails.model.request.MBTypeTodoRequest;

public interface MBMeTypeTodoService {

    ResponseObject<?> getAllTypeTodo();

    ResponseObject<?> createTypeTodo(@Valid ADCreateUpdateTypeTodoRequest request);

    ResponseObject<?> updateTypeTodo(String typeTodoId, @Valid ADCreateUpdateTypeTodoRequest request);

    ResponseObject<?> changeTypeTodoStatus(ADCreateUpdateTypeTodoRequest typeTodoId);

    ResponseObject<?> getTypeTodoById(String typeTodoId);

    ResponseObject<?> joinTypeTodo(MBJoinTypeTodoRequest typeTodoId);

}
