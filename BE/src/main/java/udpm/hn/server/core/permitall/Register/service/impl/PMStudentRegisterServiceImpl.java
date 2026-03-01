package udpm.hn.server.core.permitall.Register.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import udpm.hn.server.core.admin.student.repository.*;
import udpm.hn.server.core.common.base.ResponseObject;
import udpm.hn.server.core.permitall.Register.model.request.PMCreateStudentRegisterRequest;
import udpm.hn.server.core.permitall.Register.repository.PMStudentRegisterRepository;
import udpm.hn.server.core.permitall.Register.service.PMStudentRegisterService;
import udpm.hn.server.entity.*;
import udpm.hn.server.infrastructure.constant.EntityStatus;

import java.util.Optional;

@Service
@Validated
@RequiredArgsConstructor
@Slf4j
public class PMStudentRegisterServiceImpl implements PMStudentRegisterService {

    private final ADStudentDepartmentRepository adStudentDepartmentRepository;

    private final ADStudentMajorRepository adStudentMajorRepository;

    private final ADDepartmentFacilityRepository adDepartmentFacilityRepository;

    private final ADStudentMajorFacilityRepository adStudentMajorFacilityRepository;

    private final ADStudentByDeparmentFacilityRepository adStudentByDeparmentFacilityRepository;

    private final PMStudentRegisterRepository pmStudentRegisterRepository;

    @Override
    public ResponseObject<?> registerStudent(PMCreateStudentRegisterRequest request) {
        Optional<Student> optionalStudentEmail = pmStudentRegisterRepository.findByEmailAndStatus(request.getEmail(), EntityStatus.ACTIVE);
        if (!optionalStudentEmail.isEmpty()) {
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Email sinh viên đã tồn tại!");
        }


        Optional<Student> optionalStudentCode = pmStudentRegisterRepository.findByCodeAndStatus(request.getCode(), EntityStatus.ACTIVE);
        if (!optionalStudentCode.isEmpty()) {
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Mã sinh viên đã tồn tại!");
        }




        Optional<Facility> optionalFacility = adStudentByDeparmentFacilityRepository.findById(request.getIdFacility());
        if (optionalFacility.isEmpty()) {
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Không tìm thấy cơ sở này!");
        }

        Optional<Department> optionalDepartment = adStudentDepartmentRepository.findById(request.getIdDepartment());
        if (optionalDepartment.isEmpty()) {
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Không tìm thấy bộ môn này!");
        }

        Optional<Major> optionalMajor = adStudentMajorRepository.findById(request.getIdMajor());
        if (optionalMajor.isEmpty()) {
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Không tìm thấy chuyên ngành này!");
        }

        Optional<DepartmentFacility> optionalDepartmentFacility = adDepartmentFacilityRepository.findByDepartmentAndFacility(optionalDepartment.get(), optionalFacility.get());
        if (optionalDepartmentFacility.isEmpty()) {
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Không tìm thấy bộ môn cơ sở này ");
        }

        Optional<MajorFacility> optionalMajorFacility = adStudentMajorFacilityRepository.findByMajorAndDepartmentFacility(optionalMajor.get(), optionalDepartmentFacility.get());
        if (optionalMajorFacility.isEmpty()) {
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Không tìm thấy chuyên nghành cơ sở này ");
        }

        Student student = new Student();
        student.setCode(request.getCode());
        student.setName(request.getName());
        student.setEmail(request.getEmail());
        student.setMajorFacility(optionalMajorFacility.get());
        pmStudentRegisterRepository.save(student);
        return new ResponseObject<>(student, HttpStatus.OK, "Thêm thành công sinh viên!");

    }
}
