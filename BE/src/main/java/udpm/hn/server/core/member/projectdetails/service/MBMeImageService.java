package udpm.hn.server.core.member.projectdetails.service;

import jakarta.validation.Valid;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.web.multipart.MultipartFile;
import udpm.hn.server.core.common.base.ResponseObject;
import udpm.hn.server.core.member.projectdetails.model.request.MBMeChangeCoverTodoRequest;
import udpm.hn.server.core.member.projectdetails.model.request.MBMeCreateImageRequest;
import udpm.hn.server.core.member.projectdetails.model.request.MBMeDeleteImageRequest;
import udpm.hn.server.core.member.projectdetails.model.request.MBMeUpdateNameImageRequest;
import udpm.hn.server.core.member.projectdetails.model.response.MBUpdateBackgroundProject;

public interface MBMeImageService {

    ResponseObject<?> add(@Valid MBMeCreateImageRequest request, StompHeaderAccessor headerAccessor);

    ResponseObject<?> uploadFile(MultipartFile file);

    ResponseObject<?> findById(String id);

    ResponseObject<?> getAllByIdTodo(String idTodo);

    ResponseObject<?> updateNameImage(@Valid MBMeUpdateNameImageRequest request, StompHeaderAccessor headerAccessor);

    ResponseObject<?> deleteImage(@Valid MBMeDeleteImageRequest request, StompHeaderAccessor headerAccessor);

    ResponseObject<?> changeCoverTodo(@Valid MBMeChangeCoverTodoRequest request, StompHeaderAccessor headerAccessor);

    ResponseObject<?> getBackgroundByIdProject(String idTodo);

    ResponseObject<?> updateBackgroundProject(@Valid MBUpdateBackgroundProject request);

}
