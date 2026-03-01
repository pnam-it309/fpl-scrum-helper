package udpm.hn.server.core.member.projectdetails.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.web.bind.annotation.*;
import udpm.hn.server.core.common.base.ResponseObject;
import udpm.hn.server.core.member.projectdetails.model.request.MBMeCreateLabelProjectRequest;
import udpm.hn.server.core.member.projectdetails.model.request.MBMeDeleteLabelProjectRequest;
import udpm.hn.server.core.member.projectdetails.model.request.MBMeUpdateLabelProjectRequest;
import udpm.hn.server.core.member.projectdetails.service.MBMeLabelService;
import udpm.hn.server.infrastructure.constant.MappingConstants;
import udpm.hn.server.utils.Helper;

@Slf4j
@RestController
@RequestMapping(MappingConstants.API_PROJECT_DETAILS_LABEL_PROJECT)
@RequiredArgsConstructor
public class MBMeLabelController {

    private final MBMeLabelService mbMeLabelService;

    @GetMapping
    public ResponseEntity<?> getAllLabelByIdTodo(@RequestParam("idTodo") String idTodo) {
        return Helper.createResponseEntity(mbMeLabelService.getAllLabelByIdTodo(idTodo));
    }

    @GetMapping("/list")
    public ResponseEntity<?> getAllLabelByIdProject(@RequestParam("idProject") String idProject,
                                                    @RequestParam("idTodo") String idTodo) {
        return Helper.createResponseEntity(mbMeLabelService.getAllByIdProject(idProject,idTodo));
    }

    @GetMapping("/detail/{id}")
    public ResponseEntity<?> detail(@PathVariable("id") String id) {
        return Helper.createResponseEntity(mbMeLabelService.detail(id));
    }

    @MessageMapping("/create-label-project")
    @SendTo("/topic/create-label-project")
    public ResponseObject<?> createLabel( MBMeCreateLabelProjectRequest request,
                                 StompHeaderAccessor headerAccessor) {
        log.info("vaof create label ");
        return mbMeLabelService.create(request, headerAccessor);
    }

    @MessageMapping("/update-label-project")
    @SendTo("/topic/update-label-project")
    public ResponseObject<?> updateLabel( MBMeUpdateLabelProjectRequest request,
                                 StompHeaderAccessor headerAccessor) {
        return mbMeLabelService.update(request, headerAccessor);
    }

    @MessageMapping("/delete-label-project")
    @SendTo("/topic/delete-label-project")
    public ResponseObject<?> deleteLabel( MBMeDeleteLabelProjectRequest request,
                                 StompHeaderAccessor headerAccessor) {
        return mbMeLabelService.delete(request, headerAccessor);
    }

    @GetMapping("/list-search")
    public ResponseEntity<?> getAllLabelSearchByIdProject(@RequestParam("idProject") String idProject) {
        return Helper.createResponseEntity(mbMeLabelService.getAllLabelSearchByIdProject(idProject));
    }



}
