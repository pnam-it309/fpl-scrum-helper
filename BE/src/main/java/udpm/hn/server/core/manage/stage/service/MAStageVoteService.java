package udpm.hn.server.core.manage.stage.service;

import udpm.hn.server.core.common.base.ResponseObject;
import udpm.hn.server.core.manage.stage.model.request.StageRequest;
import udpm.hn.server.core.manage.stage.model.request.StageVoteRequest;

public interface MAStageVoteService {

    ResponseObject<?> createUpdate(StageRequest request);

    ResponseObject<?> detailStageVote(String idStage);

    ResponseObject<?> deleteStage(String idStage);

    ResponseObject<?> getAllStage(StageVoteRequest request);

    ResponseObject<?> stageTakePlace(String idProject);

    ResponseObject<?> findALlTodoVoteStage(String idStageVote);


}
