package udpm.hn.server.core.manage.todotable.service;

import udpm.hn.server.core.common.base.ResponseObject;
import udpm.hn.server.core.manage.project.model.request.MaTodoListProjectRequest;

import java.io.FileNotFoundException;

public interface TodoByPhaseProjectService {
    ResponseObject<?> getAllTodoByPhaseProject(String idProject, String idPhase, int page, int size);

    ResponseObject<?> getAllTodoListByPhaseProject(String idProject, String idPhase);

    ResponseObject<?> getIndex(String id);

    ResponseObject<?> getLogsImportTodo(int page, int size, String idProject) throws FileNotFoundException;

}
