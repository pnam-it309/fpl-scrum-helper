package udpm.hn.server.core.member.projectdetails.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.*;
import udpm.hn.server.core.common.base.ResponseObject;
import udpm.hn.server.core.member.projectdetails.model.request.*;
import udpm.hn.server.core.member.projectdetails.service.MBMeCommentService;
import udpm.hn.server.infrastructure.constant.MappingConstants;

@Slf4j
@RestController
@RequestMapping(MappingConstants.API_PROJECT_DETAILS_COMMENT_TODO)
@RequiredArgsConstructor
public class MBMeCommentController {

    private final MBMeCommentService mbMeCommentService;

    @MessageMapping("/create-comment")
    @SendTo("/topic/create-comment")
    public ResponseObject<?> addComment(MBMeCreateCommentRequest request) {
        return mbMeCommentService.addCommment(request);
    }

    @GetMapping
    public ResponseObject<?> getAllCommentByIdTodo(final MBMeFindCommentRequest request){
        return mbMeCommentService.getAllCommentByIdTodo(request);
    }

    @GetMapping("/tag-member")
    public ResponseObject<?> getAllMemberInProject(final MBMeFindTagMemberRequest request){
        return mbMeCommentService.getAllMemberInProject(request);
    }

    @MessageMapping("/update-comment")
    @SendTo("/topic/update-comment")
    public ResponseObject<?> updateComment(MBMeUpdateCommentRequest request) {
        return mbMeCommentService.updateComment(request);
    }

    @MessageMapping("/delete-comment")
    @SendTo("/topic/delete-comment")
    public ResponseObject<?> deleteComment(MBMeDeleteCommentRequest request) {
        return mbMeCommentService.deleteComment(request);
    }

}
