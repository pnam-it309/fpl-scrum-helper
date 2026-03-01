package udpm.hn.server.core.manage.phase.service;

import udpm.hn.server.core.common.base.ResponseObject;
import udpm.hn.server.core.manage.phase.model.request.MACreatePhaseRequest;
import udpm.hn.server.core.manage.phase.model.request.MAPhaseRequest;
import udpm.hn.server.core.manage.phase.model.request.MATodoByPhase;
import udpm.hn.server.core.manage.phase.model.request.StartAndSprintRequest;

public interface MAPhaseService  {
    ResponseObject<?> getAllPhase(MAPhaseRequest request);

    ResponseObject<?> findByPhaseStatus(String idProject);

    ResponseObject<?> getAllPhase(String idProject);

    ResponseObject<?> getAllSprint(MAPhaseRequest request);

    ResponseObject<?> getAllPhaseByProjectId(String id);

    ResponseObject<?> createPhase(MACreatePhaseRequest request);
    ResponseObject<?> detailPhase(String id);

    ResponseObject<?> createPhaseTOdoList(MACreatePhaseRequest request);

    ResponseObject<?> createTodoByPhase(MATodoByPhase request);

    ResponseObject<?> updatePhase(String id,MACreatePhaseRequest request);

    ResponseObject<?> updateStatusPhase(String id, Integer indexStatus);


    ResponseObject<?> deletePhase(String id);

    ResponseObject<?> deleteTodoByPhase(String id);

}
