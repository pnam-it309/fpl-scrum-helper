package udpm.hn.server.core.manage.project.service;

import jakarta.validation.Valid;
import udpm.hn.server.core.common.base.ResponseObject;
import udpm.hn.server.core.manage.project.model.request.MaCreateOrUpdateTodoListProject;
import udpm.hn.server.core.manage.project.model.request.MaTodoListProjectRequest;
import udpm.hn.server.core.manage.project.model.request.MaUpdateTodoProjectRequest;

public interface MaTodoListProjectService {

    ResponseObject<?> getAllTodoList(MaTodoListProjectRequest request, String id);

    ResponseObject<?> addTodoList(@Valid MaCreateOrUpdateTodoListProject request);

    ResponseObject<?> updateTodpList(@Valid MaCreateOrUpdateTodoListProject request, String id);

    ResponseObject<?> detailTodoList(String id);

    ResponseObject<?> readCSV();

    ResponseObject<?> getAllTodoByProject(MaTodoListProjectRequest request, String id);

    ResponseObject<?> updateTodoProject(@Valid MaUpdateTodoProjectRequest request, String id);

    ResponseObject<?> getAllPhase(String id);

    ResponseObject<?> updateTodoByPhaseProject(String todoId, String newPhaseId, String projectId);

}
