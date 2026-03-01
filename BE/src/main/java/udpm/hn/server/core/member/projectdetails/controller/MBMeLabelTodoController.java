package udpm.hn.server.core.member.projectdetails.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import udpm.hn.server.core.common.base.ResponseObject;
import udpm.hn.server.core.member.projectdetails.model.request.MBMeJoinOrOutLabelTodoRequest;
import udpm.hn.server.core.member.projectdetails.service.MBMeLabelTodoService;
import udpm.hn.server.infrastructure.constant.MappingConstants;

@Slf4j
@RestController
@RequestMapping(MappingConstants.API_PROJECT_DETAILS_LABEL_TODO)
@RequiredArgsConstructor
public class MBMeLabelTodoController {

    private final MBMeLabelTodoService mbMeLabelTodoService;

    @MessageMapping("/join-label-project-todo")
    @SendTo("/topic/join-project-todo")
    public ResponseObject<?> joinLabelTodo(MBMeJoinOrOutLabelTodoRequest request) {
        return mbMeLabelTodoService.joinLabelTodo(request);
    }

    @MessageMapping("/out-label-project-todo")
    @SendTo("/topic/out-project-todo")
    public ResponseObject<?> outLabelTodo(MBMeJoinOrOutLabelTodoRequest request) {
        return  mbMeLabelTodoService.outLabelTodo(request);
    }

}
