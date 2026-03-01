package udpm.hn.server.core.member.projectdetails.service;


import udpm.hn.server.core.common.base.ResponseObject;
import udpm.hn.server.core.member.projectdetails.model.request.MBMeCreateOrDeleteTodoVoteRequest;

public interface MBMeMemberProjectService {

    ResponseObject<?> getAllMemberProject(String idProject,String idTodo);

    ResponseObject<?> joinTodoVoteMemberProject(MBMeCreateOrDeleteTodoVoteRequest request);

    ResponseObject<?> outTodoVoteMemberProject(MBMeCreateOrDeleteTodoVoteRequest request);

    ResponseObject<?> getAllFilterMemberProject(String idProject);

}
