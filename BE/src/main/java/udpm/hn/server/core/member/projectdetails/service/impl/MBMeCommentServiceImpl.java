package udpm.hn.server.core.member.projectdetails.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import udpm.hn.server.core.common.base.PageableObject;
import udpm.hn.server.core.common.base.ResponseObject;
import udpm.hn.server.core.member.projectdetails.model.request.*;
import udpm.hn.server.core.member.projectdetails.repository.MBMeCommentRepository;
import udpm.hn.server.core.member.projectdetails.repository.MBMeMemberProjectRepository;
import udpm.hn.server.core.member.projectdetails.repository.MBMeStaffProjectRepository;
import udpm.hn.server.core.member.projectdetails.repository.MBMeTodoRepository;
import udpm.hn.server.core.member.projectdetails.service.MBMeCommentService;
import udpm.hn.server.entity.Comment;
import udpm.hn.server.entity.StaffProject;
import udpm.hn.server.entity.Todo;
import udpm.hn.server.infrastructure.constant.EntityStatus;
import udpm.hn.server.utils.Helper;

import java.util.Optional;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class MBMeCommentServiceImpl implements MBMeCommentService {

    private final MBMeCommentRepository mbMeCommentRepository;

    private final MBMeTodoRepository mbMeTodoRepository;

    private final MBMeMemberProjectRepository mbMeMemberProjectRepository;

    private final MBMeStaffProjectRepository mbMeStaffProjectRepository;

    @Override
    public ResponseObject<?> addCommment(MBMeCreateCommentRequest request) {

        Optional<Todo> todoOld = mbMeTodoRepository.findById(request.getIdTodo());

        if (todoOld.isEmpty()) {
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Không tìm thấy todo");
        }

//        Optional<String> idOptional = mbMeMemberProjectRepository.findIdStaffProjectByEmailUser(request.getEmail());

        Optional<String> idStaffProject = mbMeStaffProjectRepository.findIdStaffProjectByIdUserAndIdProject(request.getIdUser(),request.getIdProject());

        if (idStaffProject.isEmpty()) {
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Không tìm thấy idProjectStaff");
        }

        Optional<StaffProject> staffProjectOld = mbMeMemberProjectRepository.findById(idStaffProject.get());

        if (staffProjectOld.isEmpty()) {
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Không tìm thấy staffProject");
        }

        Comment comment = new Comment();
        comment.setStaffProject(staffProjectOld.get());
        comment.setTodo(todoOld.get());
        comment.setContent(request.getContent());
        comment.setStatusEdit(0);
        mbMeCommentRepository.save(comment);

        return new ResponseObject<>(request.getMentionedEmails(), HttpStatus.OK, "Thêm mới comment thành công");

    }

    @Override
    public ResponseObject<?> getAllCommentByIdTodo(MBMeFindCommentRequest request) {
        Pageable pageable = Helper.createPageableComment(request, "createdDate");
        return new ResponseObject<>(
                PageableObject.of(mbMeCommentRepository.getAllCommentByIdTodo(pageable,request)),
                HttpStatus.OK,
                "Get all category successfully"
        );
    }

    @Override
    public ResponseObject<?> updateComment(MBMeUpdateCommentRequest request) {
        Optional<Comment> commentFind = mbMeCommentRepository.findById(request.getId());
        if (!commentFind.isPresent()) {
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND,"không tìm thấy comnent nào");
        }
        if (!commentFind.get().getStaffProject().getId().equals(request.getIdStaffProject())) {
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND,"không tìm thấy thành viên dự án");
        }

        commentFind.get().setContent(request.getContent());
        commentFind.get().setStatusEdit(1);
        return new ResponseObject<>(mbMeCommentRepository.save(commentFind.get())
                ,HttpStatus.OK,
                "update comment thành công");

    }

    @Override
    public ResponseObject<?> deleteComment(MBMeDeleteCommentRequest request) {
        Optional<Comment> commentFind = mbMeCommentRepository.findById(request.getId());

        if (!commentFind.isPresent()) {
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND,"Comment không tồn tại ");
        }

        if (!commentFind.get().getStaffProject().getId().equals(request.getIdStaffProject())) {
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND,"Người dùng không xác định ");
        }

        commentFind.get().setStatus(EntityStatus.INACTIVE);

        return new ResponseObject<>(mbMeCommentRepository.save(commentFind.get())
                ,HttpStatus.OK,
                "Xóa comment thành công");

    }

    @Override
    public ResponseObject<?> getAllMemberInProject(MBMeFindTagMemberRequest request) {
        return new ResponseObject<>(
                mbMeCommentRepository.getUsersByProject(request.getIdProject(),request.getEmail()),
                HttpStatus.OK,
                "Get all member in project successfully"
        );
    }
}
