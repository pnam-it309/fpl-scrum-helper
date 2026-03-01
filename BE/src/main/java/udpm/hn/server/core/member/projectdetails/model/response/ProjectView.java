package udpm.hn.server.core.member.projectdetails.model.response;

public interface ProjectView {
    String getId();
    String getCode();
    String getName();
    Long getStartTime();
    Long getEndTime();
    Long getActualEndDate();
    String getDescriptions();
    Float getProgress();
    String getBackgroundColor();
    String getBackgroundImage();
    Integer getStatusProject();
    String getPhaseProjectName();
}
