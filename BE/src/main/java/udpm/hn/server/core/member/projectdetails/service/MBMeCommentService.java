package udpm.hn.server.core.member.projectdetails.service;

import jakarta.validation.Valid;
import udpm.hn.server.core.common.base.ResponseObject;
import udpm.hn.server.core.member.projectdetails.model.request.*;

public interface MBMeCommentService {

    ResponseObject<?> addCommment(@Valid MBMeCreateCommentRequest request);

    ResponseObject<?> getAllCommentByIdTodo(MBMeFindCommentRequest request);

    ResponseObject<?> updateComment(@Valid MBMeUpdateCommentRequest request);

    ResponseObject<?> deleteComment(@Valid MBMeDeleteCommentRequest request);

    ResponseObject<?> getAllMemberInProject(MBMeFindTagMemberRequest request);

}
