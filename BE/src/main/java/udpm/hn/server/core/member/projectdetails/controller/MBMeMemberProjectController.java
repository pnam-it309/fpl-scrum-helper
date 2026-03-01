package udpm.hn.server.core.member.projectdetails.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.*;
import udpm.hn.server.core.common.base.ResponseObject;
import udpm.hn.server.core.member.projectdetails.model.request.MBMeCreateOrDeleteTodoVoteRequest;
import udpm.hn.server.core.member.projectdetails.service.MBMeMemberProjectService;
import udpm.hn.server.infrastructure.constant.MappingConstants;
import udpm.hn.server.utils.Helper;

@RestController
@RequestMapping(MappingConstants.API_PROJECT_DETAILS_MEMBER_PROJECT)
@RequiredArgsConstructor
public class MBMeMemberProjectController {

    private final MBMeMemberProjectService mbMeMemberProjectService;

    @GetMapping("/{idProject}/{idTodo}")
    public ResponseObject<?> getAllMemberProject(@PathVariable("idProject") String idProject,
                                                 @PathVariable("idTodo") String idTodo) {
        return mbMeMemberProjectService.getAllMemberProject(idProject,idTodo);
    }

    @MessageMapping("/join-todoVote")
    @SendTo("/topic/join-todoVote")
    public ResponseObject<?> joinTodoVoteMemberProject(MBMeCreateOrDeleteTodoVoteRequest request) {
        return mbMeMemberProjectService.joinTodoVoteMemberProject(request);
    }

    @MessageMapping("/out-todoVote")
    @SendTo("/topic/out-todoVote")
    public ResponseObject<?> outTodoVoteMemberProject(MBMeCreateOrDeleteTodoVoteRequest request) {
        return mbMeMemberProjectService.outTodoVoteMemberProject(request);
    }

    @GetMapping("/filter/{idProject}")
    public ResponseObject<?> getAllFilterMemberProject(@PathVariable("idProject") String idProject) {
        return mbMeMemberProjectService.getAllFilterMemberProject(idProject);
    }

}
