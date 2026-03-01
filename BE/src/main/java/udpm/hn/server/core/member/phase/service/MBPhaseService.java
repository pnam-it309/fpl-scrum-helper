package udpm.hn.server.core.member.phase.service;

import udpm.hn.server.core.common.base.ResponseObject;
import udpm.hn.server.core.manage.project.model.request.MaTodoListProjectRequest;
import udpm.hn.server.core.member.phase.model.request.MBPhaseRequest;
import udpm.hn.server.utils.UserContextHelper;

public interface MBPhaseService {
    ResponseObject<?> getAllPhase(MBPhaseRequest request, String idProject);

    ResponseObject<?> detail (String idProject);

    ResponseObject<?> getAllTodoByProject(MBPhaseRequest request, String id);

    ResponseObject<?> getAllPhase(String id);

    ResponseObject<?> updateTodoByPhaseProject(String todoId, String newPhaseId, String projectId);

}
