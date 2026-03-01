package udpm.hn.server.core.member.projectdetails.service;

import jakarta.validation.Valid;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import udpm.hn.server.core.common.base.ResponseObject;
import udpm.hn.server.core.member.projectdetails.model.request.MBMeCreateLabelProjectRequest;
import udpm.hn.server.core.member.projectdetails.model.request.MBMeDeleteLabelProjectRequest;
import udpm.hn.server.core.member.projectdetails.model.request.MBMeUpdateLabelProjectRequest;

public interface MBMeLabelService {

    ResponseObject<?> getAllLabelByIdTodo(String idTodo);

    ResponseObject<?> getAllByIdProject(String idProject,String idTodo);

    ResponseObject<?> detail(String id);

    ResponseObject<?> create(@Valid MBMeCreateLabelProjectRequest request, StompHeaderAccessor headerAccessor);

    ResponseObject<?> update(@Valid MBMeUpdateLabelProjectRequest request, StompHeaderAccessor headerAccessor);

    ResponseObject<?> delete(@Valid MBMeDeleteLabelProjectRequest request, StompHeaderAccessor headerAccessor);

    ResponseObject<?> getAllLabelSearchByIdProject(String idProject);


}
