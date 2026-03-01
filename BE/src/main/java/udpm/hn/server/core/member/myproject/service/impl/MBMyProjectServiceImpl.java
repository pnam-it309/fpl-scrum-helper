package udpm.hn.server.core.member.myproject.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.validation.annotation.Validated;
import udpm.hn.server.core.common.base.PageableObject;
import udpm.hn.server.core.common.base.ResponseObject;
import udpm.hn.server.core.manage.project.model.request.MaProjectSearchRequest;
import udpm.hn.server.core.member.myproject.model.request.MBMyProjectSearchRequest;
import udpm.hn.server.core.member.myproject.repository.MBMyProjectRepository;
import udpm.hn.server.core.member.myproject.service.MBMyProjectService;
import udpm.hn.server.infrastructure.constant.StatusProject;
import udpm.hn.server.utils.Helper;

@Repository
@Validated
@RequiredArgsConstructor
public class MBMyProjectServiceImpl implements MBMyProjectService {

    private final MBMyProjectRepository mbMyProjectRepository;


    @Override
    public ResponseObject<?> getAllMyProject(MaProjectSearchRequest request) {
        StatusProject status = null;
        if (request.getStatus() != null) {
            status = StatusProject.values()[request.getStatus()];
        }

        Pageable page = Helper.createPageable(request, "createdDate");
        return new ResponseObject<>(mbMyProjectRepository.getAllMyProject(request,page , status), HttpStatus.OK, "Lấy thông tin project thành công");
    }
}
