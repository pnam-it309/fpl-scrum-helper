package udpm.hn.server.core.member.projectdetails.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import udpm.hn.server.core.common.base.ResponseObject;
import udpm.hn.server.core.member.projectdetails.model.request.MBMeChangeCoverTodoRequest;
import udpm.hn.server.core.member.projectdetails.model.request.MBMeCreateImageRequest;
import udpm.hn.server.core.member.projectdetails.model.request.MBMeDeleteImageRequest;
import udpm.hn.server.core.member.projectdetails.model.request.MBMeUpdateNameImageRequest;
import udpm.hn.server.core.member.projectdetails.model.response.MBUpdateBackgroundProject;
import udpm.hn.server.core.member.projectdetails.service.MBMeImageService;
import udpm.hn.server.infrastructure.constant.MappingConstants;
import udpm.hn.server.utils.Helper;

@Slf4j
@RestController
@RequestMapping(MappingConstants.API_PROJECT_DETAILS_IMAGE)
@RequiredArgsConstructor
public class MBMeImageController {

    private final MBMeImageService mbMeImageService;

    @GetMapping("/detail/background-image/{idProject}")
    public ResponseEntity<?> getBackgroundByIdProject(@PathVariable("idProject") String idProject ) {
        return Helper.createResponseEntity(mbMeImageService.getBackgroundByIdProject(idProject));
    }

    @MessageMapping("/create-image")
        @SendTo("/topic/create-image")
    public ResponseObject<?> createImage( MBMeCreateImageRequest request,
                                    StompHeaderAccessor headerAccessor) {
        return mbMeImageService.add(request, headerAccessor);
    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(@RequestBody MultipartFile file) {
        return Helper.createResponseEntity(mbMeImageService.uploadFile(file));
    }

    @GetMapping("/detail/{id}")
    public ResponseObject<?> findById(@PathVariable("id") String id) {
        return mbMeImageService.findById(id);
    }

    @GetMapping("/{idTodo}")
    public ResponseObject<?> getAllByIdTodo(@PathVariable("idTodo") String idTodo) {
        return mbMeImageService.getAllByIdTodo(idTodo);
    }

    @MessageMapping("/update-name-image")
    @SendTo("/topic/update-name-image")
    public ResponseObject<?> updateNameImage( MBMeUpdateNameImageRequest request,
                                          StompHeaderAccessor headerAccessor) {
        return mbMeImageService.updateNameImage(request, headerAccessor);
    }

    @MessageMapping("/delete-image")
    @SendTo("/topic/delete-image")
    public ResponseObject<?> deleteImage( MBMeDeleteImageRequest request,
                                      StompHeaderAccessor headerAccessor) {
        return mbMeImageService.deleteImage(request, headerAccessor);
    }

    @MessageMapping("/change-cover-image")
    @SendTo("/topic/change-cover-image")
    public ResponseObject<?> changeCoverTodo( MBMeChangeCoverTodoRequest request,
                                          StompHeaderAccessor headerAccessor) {
        return mbMeImageService.changeCoverTodo(request, headerAccessor);
    }

    @MessageMapping("/update-background-project")
    @SendTo("/topic/update-background-project")
    public ResponseObject<?> updateNameImage( MBUpdateBackgroundProject request) {
        return mbMeImageService.updateBackgroundProject(request);
    }

}
