package udpm.hn.server.core.manage.project.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import udpm.hn.server.core.common.base.ResponseObject;
import udpm.hn.server.core.manage.project.model.request.MaUserProjectRequest;
import udpm.hn.server.core.manage.project.repository.MAUserProjectRepository;
import udpm.hn.server.core.manage.project.repository.MaProjectStaffRepository;
import udpm.hn.server.core.manage.project.repository.MaProjectStudentRepository;
import udpm.hn.server.core.manage.project.service.MaProjectStudentService;
import udpm.hn.server.utils.Helper;

@Service
@RequiredArgsConstructor
@Slf4j
public class MaProjectStudentServiceImpl implements MaProjectStudentService {

    private final MAUserProjectRepository maUserProjectRepository;

    private final MaProjectStudentRepository maProjectStudentRepository;

    private final MaProjectStaffRepository maProjectStaffRepository;

    @Override
    public ResponseObject<?> getAll(String idMajorFacility) {
        return new ResponseObject<>(maProjectStudentRepository.getAllStudent(idMajorFacility), HttpStatus.OK,"Lấy dữ liệu sinh viên thành công");
    }

    @Override
    public ResponseObject<?> getAllUserProject(MaUserProjectRequest request) {
        Pageable page = Helper.createPageable(request, "createdDate");
        return new ResponseObject<>(maUserProjectRepository.getAllUserByProject(page, request.getIdProject()),HttpStatus.OK,"fetch data success");
    }

    @Override
    public ResponseObject<?> getAllStaff(String idMajorFacility) {
        return new ResponseObject<>(maProjectStaffRepository.getALl(idMajorFacility),HttpStatus.OK,"Lấy dữ liệu thành công");
    }

    @Override
    public ResponseObject<?> getAllStudentByProject(String idProject) {
        return new ResponseObject<>(maProjectStudentRepository.getAllStudentByProject(idProject),HttpStatus.OK,"Lấy dữ liệu sinh viên trong dự án  thành công");
    }

    @Override
    public ResponseObject<?> getAllStaffByProject(String idProject) {
        return new ResponseObject<>(maProjectStaffRepository.getAllStaffByProject(idProject),HttpStatus.OK,"Lấy dữ liệu nhân viên theo dự án thành công");
    }
}