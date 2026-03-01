package udpm.hn.server.core.member.phase.model.response;

import udpm.hn.server.utils.StatusPhase;

public interface MBPhaseResponse {

    String getOrderNumber();
    String getName();
    String getCode();
    String getId();
    String getStartTime();
    String getEndTime();
    String getCreateDate();
    String getDescriptions();

    StatusPhase getStatusPhase();

}
