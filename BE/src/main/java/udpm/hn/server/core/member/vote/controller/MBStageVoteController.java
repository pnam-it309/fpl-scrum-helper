package udpm.hn.server.core.member.vote.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import udpm.hn.server.core.member.vote.model.request.MBStageVoteRequest;
import udpm.hn.server.core.member.vote.service.MBStageVoteService;
import udpm.hn.server.infrastructure.constant.MappingConstants;
import udpm.hn.server.utils.Helper;

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping(MappingConstants.API_MEMBER_TODO_VOTE)
public class MBStageVoteController {

    private final MBStageVoteService mbStageVoteService;

    @GetMapping("/get-voting-is-on-going")
    public ResponseEntity<?> getVotingIsOnGoing( MBStageVoteRequest request) {
        return Helper.createResponseEntity(mbStageVoteService.getVotingIsOnGoing(request));
    }

    @GetMapping("/get-up-coming-vote")
    public ResponseEntity<?> GetUpcomingVote( MBStageVoteRequest request) {
        return Helper.createResponseEntity(mbStageVoteService.GetUpcomingVote(request));
    }
}
