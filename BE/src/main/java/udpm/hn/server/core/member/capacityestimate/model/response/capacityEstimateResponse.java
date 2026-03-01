package udpm.hn.server.core.member.capacityestimate.model.response;

public interface capacityEstimateResponse {

    String getOrderNumber();

    String getId();

    String getName();

    String getWorkday();

    String getSprint();

    String getIdPhase();

    String getIdUser();

    String getStartTime();

    String getDescription();

    String getDescriptions();

    String getEndTime();

    String getAvailableHours();

    String getAdjustedStoryPoints();

}
