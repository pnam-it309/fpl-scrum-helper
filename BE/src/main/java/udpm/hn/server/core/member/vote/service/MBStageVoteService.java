package udpm.hn.server.core.member.vote.service;

import udpm.hn.server.core.common.base.ResponseObject;
import udpm.hn.server.core.member.vote.model.request.MBStageVoteRequest;

public interface MBStageVoteService {

    ResponseObject<?> getVotingIsOnGoing(MBStageVoteRequest request);

    ResponseObject<?> GetUpcomingVote(MBStageVoteRequest request);

}
