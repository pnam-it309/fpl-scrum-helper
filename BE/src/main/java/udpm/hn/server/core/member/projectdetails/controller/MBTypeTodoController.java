package udpm.hn.server.core.member.projectdetails.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.*;
import udpm.hn.server.core.admin.category.modal.request.ADCategoryRequest;
import udpm.hn.server.core.admin.category.modal.request.ADCreateUpdateCategoryRequest;
import udpm.hn.server.core.admin.category.service.ADCategoryService;
import udpm.hn.server.core.member.projectdetails.model.request.ADCreateUpdateTypeTodoRequest;
import udpm.hn.server.core.member.projectdetails.model.request.MBJoinTypeTodoRequest;
import udpm.hn.server.core.member.projectdetails.model.request.MBTypeTodoRequest;
import udpm.hn.server.core.member.projectdetails.service.MBMeTypeTodoService;
import udpm.hn.server.infrastructure.constant.MappingConstants;
import udpm.hn.server.utils.Helper;

@RestController
@RequestMapping(MappingConstants.API_PROJECT_DETAILS_TYPE_TODO)
@RequiredArgsConstructor
public class MBTypeTodoController {

    private final MBMeTypeTodoService mbMeTypeTodoService;

    @GetMapping
    public ResponseEntity<?> getAllTypeTodo() {
        return Helper.createResponseEntity(mbMeTypeTodoService.getAllTypeTodo());
    }

    @MessageMapping("/create-type-todo")
    @SendTo("/topic/create-type-todo")
    public ResponseEntity<?> createTypeTodo(@RequestBody ADCreateUpdateTypeTodoRequest request) {
        return Helper.createResponseEntity(mbMeTypeTodoService.createTypeTodo(request));
    }

    @PutMapping("/update/{typeId}")
    public ResponseEntity<?> updateTypeTodo(@PathVariable String typeId, @RequestBody ADCreateUpdateTypeTodoRequest request) {
        return Helper.createResponseEntity(mbMeTypeTodoService.updateTypeTodo(typeId, request));
    }

    @MessageMapping("/change-type-todo")
    @SendTo("/topic/change-type-todo")
    public ResponseEntity<?> changeStatusTypeTodo(@RequestBody ADCreateUpdateTypeTodoRequest request) {
        return Helper.createResponseEntity(mbMeTypeTodoService.changeTypeTodoStatus(request));
    }

    @GetMapping("/detail/{typeId}")
    public ResponseEntity<?> getByIdTypeTodo(@PathVariable String typeId) {
        return Helper.createResponseEntity(mbMeTypeTodoService.getTypeTodoById(typeId));
    }

    @MessageMapping("/join-type-todo")
    @SendTo("/topic/join-type-todo")
    public ResponseEntity<?> JoinTypeTodo(@RequestBody MBJoinTypeTodoRequest request) {
        return Helper.createResponseEntity(mbMeTypeTodoService.joinTypeTodo(request));
    }

}
