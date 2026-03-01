package udpm.hn.server.core.member.projectdetails.service;

import udpm.hn.server.core.common.base.ResponseObject;
import udpm.hn.server.core.member.projectdetails.model.request.MBMeJoinOrOutLabelTodoRequest;

public interface MBMeLabelTodoService {

    ResponseObject<?> joinLabelTodo(MBMeJoinOrOutLabelTodoRequest request);

    ResponseObject<?> outLabelTodo(MBMeJoinOrOutLabelTodoRequest request);

}
