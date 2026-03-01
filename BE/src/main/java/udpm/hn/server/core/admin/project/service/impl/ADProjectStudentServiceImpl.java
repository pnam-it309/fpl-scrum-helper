package udpm.hn.server.core.admin.project.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import udpm.hn.server.core.admin.project.repository.ADProjectStaffRepository;
import udpm.hn.server.core.admin.project.repository.ADProjectStudentRepository;
import udpm.hn.server.core.admin.project.service.ADProjectStudentService;
import udpm.hn.server.core.common.base.ResponseObject;


@Service
@RequiredArgsConstructor
@Slf4j
public class ADProjectStudentServiceImpl implements ADProjectStudentService {

    private final ADProjectStudentRepository adProjectStudentRepository;

    private final ADProjectStaffRepository adProjectStaffRepository;
    @Override
    public ResponseObject<?> getAll(String idMajorFacility) {

        return new ResponseObject<>(adProjectStudentRepository.getAllStudent(idMajorFacility), HttpStatus.OK,"Lấy dữ liệu sinh viên thành công");
    }

    @Override
    public ResponseObject<?> getAllStaff(String idMajorFacility) {
        return new ResponseObject<>(adProjectStaffRepository.getALl(idMajorFacility),HttpStatus.OK,"Lấy dữ liệu thành công");


    }

    @Override
    public ResponseObject<?> getAllStudentByProject(String idProject) {
        return new ResponseObject<>(adProjectStudentRepository.getAllStudentByProject(idProject),HttpStatus.OK,"Lấy dữ liệu sinh viên trong dự án  thành công");
    }

    @Override
    public ResponseObject<?> getAllStaffByProject(String idProject) {
        return new ResponseObject<>(adProjectStaffRepository.getAllStaffByProject(idProject),HttpStatus.OK,"Lấy dữ liệu nhân viên theo dự án thành công");
    }
}
