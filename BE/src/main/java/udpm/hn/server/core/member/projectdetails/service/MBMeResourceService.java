package udpm.hn.server.core.member.projectdetails.service;

import jakarta.validation.Valid;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import udpm.hn.server.core.common.base.ResponseObject;
import udpm.hn.server.core.member.projectdetails.model.request.MBMeCreateResourceRequest;
import udpm.hn.server.core.member.projectdetails.model.request.MBMeDeleteResourceRequest;
import udpm.hn.server.core.member.projectdetails.model.request.MBMeUpdateResourceRequest;

import java.util.List;

public interface MBMeResourceService {

    ResponseObject<?> getAll(String idTodo);

    ResponseObject<?> create(@Valid MBMeCreateResourceRequest request, StompHeaderAccessor headerAccessor);

    ResponseObject<?> update(@Valid MBMeUpdateResourceRequest request, StompHeaderAccessor headerAccessor);

    ResponseObject<?> delete(@Valid MBMeDeleteResourceRequest request, StompHeaderAccessor headerAccessor);

}
