package udpm.hn.server.core.member.vote.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import udpm.hn.server.core.common.base.ResponseObject;
import udpm.hn.server.core.member.vote.model.request.MBStageVoteRequest;
import udpm.hn.server.core.member.vote.repository.MBStageVoteRepository;
import udpm.hn.server.core.member.vote.service.MBStageVoteService;

@Service
@Slf4j
@RequiredArgsConstructor
public class MBStageVoteServiceImpl implements MBStageVoteService {

    private final MBStageVoteRepository mbStageVoteRepository;

    @Override
    public ResponseObject<?> getVotingIsOnGoing(MBStageVoteRequest request) {
        Long currentTime = System.currentTimeMillis();
        return new ResponseObject<>(
                mbStageVoteRepository.findActiveStageVoteByProject(currentTime,request.getIdProject())
        , HttpStatus.OK, "lấy thành cong"
        );
    }

    @Override
    public ResponseObject<?> GetUpcomingVote(MBStageVoteRequest request) {
        Long currentTime = System.currentTimeMillis();
        return new ResponseObject<>(
                mbStageVoteRepository.findUpcomingStageVoteByProject(currentTime,request.getIdProject())
                , HttpStatus.OK, "lấy thành cong"
        );
    }

}
