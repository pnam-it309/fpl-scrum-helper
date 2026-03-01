package udpm.hn.server.core.admin.project.model.response;

public interface TodoStatusByProject {
    String getProjectName();
    Integer getProjectStatus();
    Integer getTotalTodo();
}
