package udpm.hn.server.core.manage.project.service;

import udpm.hn.server.core.common.base.ResponseObject;
import udpm.hn.server.core.manage.project.model.response.MaTodoSummaryResponse;
import udpm.hn.server.core.manage.project.model.response.MeDataDashboardLabelResponse;
import udpm.hn.server.core.manage.project.model.response.MeDataDashboardTodoListResponse;

import java.util.List;

public interface MaTodoService {

    List<MeDataDashboardTodoListResponse> countTodoByTodoListAllProject(String projectId);

    Integer countTodoByDueDateAllProject(String projectId, Integer statusTodo);

    Integer countTodoByNoDueDateAllProject(String projectTodo);

    List<MeDataDashboardLabelResponse> countTodoByLabelAllProject(String projectId);

    Integer countTodoByNoLabelAllProject(String projectId);

    List<MeDataDashboardTodoListResponse> countTodoByTodoListPhase(String projectId, String phaseId);

    Integer countTodoByDueDatePhase(String projectId, String phaseId, Integer statusTodo);

    Integer countTodoByNoDueDatePhase(String projectId, String phaseId);

    Integer countTodoByPriorityLevel(String projectId, Integer priorityLevel);

    ResponseObject<?> countTodoByTodoStatus(String id);
}
