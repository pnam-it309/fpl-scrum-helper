package udpm.hn.server.core.member.vote.model.response;

public interface MBStageVoteResponse {
    String getId();
    Long getStartTime();
    Long getEndTime();
    String getPhaseId();
    String getNamePhaseProject();
}
