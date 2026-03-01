package udpm.hn.server.core.member.projectdetails.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.web.bind.annotation.*;
import udpm.hn.server.core.common.base.ResponseObject;
import udpm.hn.server.core.member.projectdetails.model.request.MBMeCreateResourceRequest;
import udpm.hn.server.core.member.projectdetails.model.request.MBMeDeleteResourceRequest;
import udpm.hn.server.core.member.projectdetails.model.request.MBMeUpdateResourceRequest;
import udpm.hn.server.core.member.projectdetails.service.MBMeResourceService;
import udpm.hn.server.infrastructure.constant.MappingConstants;

@RestController
@RequestMapping(MappingConstants.API_PROJECT_DETAILS_RESOURCE_TODO)
@RequiredArgsConstructor
public class MBMeResourceController {

    private final MBMeResourceService mbMeResourceService;

    @GetMapping
    public ResponseObject<?> getAllResourceTodo(@RequestParam("idTodo") String idTodo) {
        return mbMeResourceService.getAll(idTodo);
    }

    @MessageMapping("/create-resource")
    @SendTo("/topic/create-resource")
    public ResponseObject<?> createResourceTodo(MBMeCreateResourceRequest request,
                                 StompHeaderAccessor headerAccessor) {
        return mbMeResourceService.create(request, headerAccessor);
    }

    @MessageMapping("/update-resource")
    @SendTo("/topic/update-resource")
    public ResponseObject<?> updateResourceTodo(MBMeUpdateResourceRequest request,
                                 StompHeaderAccessor headerAccessor) {
        return mbMeResourceService.update(request, headerAccessor);
    }

    @MessageMapping("/delete-resource")
    @SendTo("/topic/delete-resource")
    public ResponseObject<?> deleteResourceTodo(MBMeDeleteResourceRequest request,
                                 StompHeaderAccessor headerAccessor) {
        return mbMeResourceService.delete(request, headerAccessor);
    }

}
