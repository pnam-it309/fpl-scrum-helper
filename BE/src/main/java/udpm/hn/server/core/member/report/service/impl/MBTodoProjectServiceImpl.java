package udpm.hn.server.core.member.report.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import udpm.hn.server.core.common.base.PageableObject;
import udpm.hn.server.core.common.base.ResponseObject;
import udpm.hn.server.core.member.report.model.request.MBTodoProjectRequest;
import udpm.hn.server.core.member.report.repository.MBTodoProjectRepository;
import udpm.hn.server.core.member.report.service.MBTodoProjectService;
import udpm.hn.server.utils.Helper;
import udpm.hn.server.utils.UserContextHelper;

@Service
@Slf4j
@RequiredArgsConstructor
public class MBTodoProjectServiceImpl implements MBTodoProjectService {

    private final UserContextHelper userContextHelper;

    private final MBTodoProjectRepository todoProjectRepository;

    @Override
    public ResponseObject<?> getAllTodoByProjectAndStaffProject(MBTodoProjectRequest request, String idProject) {
        Pageable pageable = Helper.createPageable(request, "createdDate");
        return new ResponseObject<>(
                PageableObject.of(todoProjectRepository.getAllTodoByProjectAndStaffProject(pageable,idProject, userContextHelper, request)),
                HttpStatus.OK,
                "Lấy thành công danh sách"
        );
    }
}
