package udpm.hn.server.core.admin.student.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import udpm.hn.server.core.admin.student.model.request.ADCreateOrUpdateStudentRequest;
import udpm.hn.server.core.admin.student.model.request.ADCreateStudentFDMRequest;
import udpm.hn.server.core.admin.student.model.request.ADUpdateStudentFDMRequest;
import udpm.hn.server.core.admin.student.model.request.StudentRequest;
import udpm.hn.server.core.admin.student.repository.*;
import udpm.hn.server.core.admin.student.service.StudentService;
import udpm.hn.server.core.common.base.PageableObject;
import udpm.hn.server.core.common.base.ResponseObject;
import udpm.hn.server.entity.*;
import udpm.hn.server.infrastructure.constant.EntityStatus;
import udpm.hn.server.infrastructure.job.student.commonio.CsvHistoryWriter;
import udpm.hn.server.infrastructure.job.student.model.response.HistoryImportStudentResponse;
import udpm.hn.server.repository.ActivityLogRepository;
import udpm.hn.server.repository.HistoryImportStudentRepository;
import udpm.hn.server.repository.RoleRepository;
import udpm.hn.server.repository.StaffRepository;
import udpm.hn.server.infrastructure.log.LogEntry;
import udpm.hn.server.infrastructure.log.LogService;
import udpm.hn.server.utils.Helper;
import udpm.hn.server.utils.UserContextHelper;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.Optional;

@Service
@Validated
@RequiredArgsConstructor
@Slf4j
public class StudentServiceImpl implements StudentService {

    private final ADStudentExtendRepository adStudentExtendRepository;

    private final ADStudentDepartmentRepository adStudentDepartmentRepository;

    private final ADStudentMajorRepository adStudentMajorRepository;

    private final ADDepartmentFacilityRepository adDepartmentFacilityRepository;

    private final ADStudentMajorFacilityRepository adStudentMajorFacilityRepository;

    private final ADStudentByDeparmentFacilityRepository adStudentByDeparmentFacilityRepository;

    private final LogService logService;

    private final StaffRepository staffRepository;

    private final CsvHistoryWriter csvHistoryWriter;

    private final UserContextHelper userContextHelper;

    private final RoleRepository roleRepository;

    private final HistoryImportStudentRepository historyImportStudentRepository;

    private final ActivityLogRepository activityLogRepository;

    @Override
    public ResponseObject<?> getAllStudnet(StudentRequest request) {
        Pageable pageable = Helper.createPageable(request,"createdDate");

        return new ResponseObject<>(
                PageableObject.of(adStudentExtendRepository.getAllStudent(pageable, request)),
                HttpStatus.OK,
                "Lấy danh sách sinh viên thành công"
        );
    }

    @Override
    public ResponseObject<?> findAllStudent() {
        return new ResponseObject<>(adStudentExtendRepository.findAll(),HttpStatus.OK,"Lấy dữ liệu thành công");
    }


    @Override
    public ResponseObject<?> addStudent(ADCreateOrUpdateStudentRequest request) {
        String code = Helper.generateCodeFromName((request.getCode().trim()));
        request.setCode(code);
        request.setEmail(request.getEmail().replaceAll("\\s+"," "));

        boolean checkExistCode = adStudentExtendRepository.existsByCode(code);
        boolean checkExistEmail = adStudentExtendRepository.existsByEmail(request.getEmail().trim());

        if (checkExistCode){
            return new ResponseObject<>(null,HttpStatus.NOT_ACCEPTABLE, "Mã sinh viên đã tồn tại!");
        }

        if (checkExistEmail){
            return new ResponseObject<>(null,HttpStatus.NOT_ACCEPTABLE, "Email sinh viên đã tồn tại!");
        }

        Student student = new Student();
        student.setStatus(EntityStatus.ACTIVE);
        student.setCode(code);
        student.setName(request.getName().trim());
        student.setEmail(request.getEmail().trim());
        this.adStudentExtendRepository.save(student);

        ActivityLog activityLog = new ActivityLog();
        activityLog.setRole("ADMIN");
        activityLog.setExecutorEmail(request.getEmailLogin());
        activityLog.setContent("Đã thêm sinh viên có email: "+request.getEmail());
        activityLogRepository.save(activityLog);

        return new ResponseObject<>(null, HttpStatus.OK,"Thêm sinh viên thành công");
    }

    @Override
    public ResponseObject<?> updateStudent(ADCreateOrUpdateStudentRequest request, String id) {
        String code = Helper.generateCodeFromName((request.getCode().trim()));
        request.setCode(code);
        request.setEmail(request.getEmail().replaceAll("\\s+"," "));

        Optional<Student> studnetOptional = adStudentExtendRepository.findById(id);

        if (studnetOptional.isEmpty()) {
            return new ResponseObject<>(null, HttpStatus.NOT_ACCEPTABLE, "Sinh viên không tồn tại");
        }

        Student updateStudent = studnetOptional.get();
        if (!updateStudent.getCode().trim().equalsIgnoreCase(code)) {
            if (adStudentExtendRepository.existsByCode(code)) {
                return new ResponseObject<>(null, HttpStatus.NOT_ACCEPTABLE, "Mã Sinh viên đã tồn tại: " +
                        code);
            }
        }

        if (!updateStudent.getEmail().trim().equalsIgnoreCase(request.getEmail().trim())) {
            if (adStudentExtendRepository.existsByEmail(request.getEmail().trim())) {
                return new ResponseObject<>(null, HttpStatus.NOT_ACCEPTABLE, "Email sinh viên đã tồn tại: " +
                        request.getEmail().trim());
            }
        }

        updateStudent.setCode(code);
        updateStudent.setName(request.getName().trim());
        updateStudent.setEmail(request.getEmail().trim());
        this.adStudentExtendRepository.save(updateStudent);

        ActivityLog activityLog = new ActivityLog();
        activityLog.setRole("ADMIN");
        activityLog.setExecutorEmail(request.getEmailLogin());
        activityLog.setContent("Đã sửa  sinh viên có email: "+updateStudent.getEmail());
        activityLogRepository.save(activityLog);

        return new ResponseObject<>(null, HttpStatus.OK, "Cập nhật sinh viên thành công");
    }

    @Override
    public ResponseObject<?> detailStudent(String id) {
        return new ResponseObject<>(
                adStudentExtendRepository.getDetailStudent(id),
                HttpStatus.OK,
                "Lấy thành công chi tiết sinh viên"
        );
    }

    @Override
    public ResponseObject<?> studentByDepartmentFacility(String id) {
        return adStudentByDeparmentFacilityRepository.studentByDepartmentFacility(id)
                .map(student -> new ResponseObject<>(student, HttpStatus.OK,
                "Lấy thông sinh viên thành công"))
                .orElseGet(() -> new ResponseObject<>(
                        null,
                        HttpStatus.NOT_FOUND,
                        "Không tìm thấy sinh viên có Id "+id));
    }

    @Override
    public ResponseObject<?> deleteStudentByIdStudent(String studentId,String emailLogin) {
        Optional<Student> studentOpt = adStudentExtendRepository.findById(studentId);
        if (studentOpt.isPresent()) {
            Student student = studentOpt.get();
            student.setMajorFacility(null);
            adStudentExtendRepository.save(student);


            ActivityLog activityLog = new ActivityLog();
            activityLog.setRole("ADMIN");
            activityLog.setExecutorEmail(emailLogin);
            activityLog.setContent("Đã xóa chuyên ngành bộ môn cơ sở của" +
                    " sinh viên có email: "+studentOpt.get().getEmail());
            activityLogRepository.save(activityLog);

            return  new ResponseObject<>(studentOpt, HttpStatus.OK, "Xóa sinh viên khỏi chuyên ngành cơ sở thành công");
        } else {
            throw new RuntimeException("Không tìm thấy sinh viên có Id : " + studentId);
        }

    }

    @Override
    public ResponseObject<?> createStudentByFDM(ADCreateStudentFDMRequest request) {


        Optional<Facility> optionalFacility = adStudentByDeparmentFacilityRepository.findById(request.getIdFacility());
        if (optionalFacility.isEmpty()) {
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Không tìm thấy cơ sở này");
        }

        Optional<Department> optionalDepartment = adStudentDepartmentRepository.findById(request.getIdDepartment());
        if (optionalDepartment.isEmpty()) {
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Không tìm thấy bộ môn này");
        }

        Optional<Major> optionalMajor = adStudentMajorRepository.findById(request.getIdMajor());
        if (optionalMajor.isEmpty()) {
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Không tìm thấy chuyên ngành này");
        }

        Optional<DepartmentFacility> optionalDepartmentFacility = adDepartmentFacilityRepository.findByDepartmentAndFacility(optionalDepartment.get(), optionalFacility.get());
        if (optionalDepartmentFacility.isEmpty()) {
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Không tìm thấy bộ môn cơ sở này ");
        }

        Optional<MajorFacility> optionalMajorFacility = adStudentMajorFacilityRepository.findByMajorAndDepartmentFacility(optionalMajor.get(), optionalDepartmentFacility.get());
        if (optionalMajorFacility.isEmpty()) {
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Không tìm thấy chuyên nghành cơ sở này ");
        }

        Optional<Student> optionalStudent = adStudentExtendRepository.findById(request.getIdStudentDetail());
        if (optionalStudent.isEmpty()) {
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Không tìm thấy sinh viên này ");
        }
        Student student = optionalStudent.get();
        student.setMajorFacility(optionalMajorFacility.get());

        adStudentExtendRepository.save(student);

        ActivityLog activityLog = new ActivityLog();
        activityLog.setRole("ADMIN");
        activityLog.setExecutorEmail(request.getEmailLogin());
        activityLog.setContent("Đã thêm Chuyên ngành - Bộ môn - Cơ sở :"+optionalMajor.get().getName()+
                " - "+optionalDepartment.get().getName()+" - "+
                optionalFacility.get().getName()+". Cho sinh viên có email : "+student.getEmail());
        activityLogRepository.save(activityLog);

        return new ResponseObject<>(null, HttpStatus.OK, "Thêm thành công chuyên ngành bộ môn cơ sở");
    }

    @Override
    public ResponseObject<?> updateStudentByFDM(ADUpdateStudentFDMRequest request) {


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
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Không tìm thấy bộ môn cơ sở này!");
        }

        Optional<MajorFacility> optionalMajorFacility = adStudentMajorFacilityRepository.findByMajorAndDepartmentFacility(optionalMajor.get(), optionalDepartmentFacility.get());
        if (optionalMajorFacility.isEmpty()) {
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Không tìm thấy chuyên nghành cơ sở này!");
        }

        Optional<Student> optionalStudent = adStudentExtendRepository.findById(request.getIdStudentDetail());
        if (optionalStudent.isEmpty()) {
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Không tìm thấy sinh viên này ");
        }

        Optional<DepartmentFacility> optionalDepartmentFacilityUpdate = adDepartmentFacilityRepository.findByDepartmentAndFacilityAndStudent(request.getIdUpdateDepartment(), request.getIdUpdateFacility());
        if (optionalDepartmentFacilityUpdate.isEmpty()) {
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Không tìm thấy bộ môn cơ sở này!");
        }

        Optional<Major> optionalMajorUpdate = adStudentMajorRepository.findById(request.getIdUpdateMajor());
        if (optionalMajorUpdate.isEmpty()) {
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Không tìm thấy chuyên ngành này!");
        }

        Optional<MajorFacility> optionalMajorFacilityUpdate = adStudentMajorFacilityRepository.findByMajorAndDepartmentFacility(optionalMajorUpdate.get(), optionalDepartmentFacilityUpdate.get());
        if (optionalMajorFacilityUpdate.isEmpty()) {
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Không tìm thấy chuyên ngành cơ sở này!");
        }

        Student student = optionalStudent.get();
        student.setMajorFacility(optionalMajorFacilityUpdate.get());

        adStudentExtendRepository.save(student);

        ActivityLog activityLog = new ActivityLog();
        activityLog.setRole("ADMIN");
        activityLog.setExecutorEmail(request.getEmailLogin());
        activityLog.setContent("Đã sửa Chuyên ngành - Bộ môn - Cơ sở thành :"+optionalMajor.get().getName()+
                " - "+optionalDepartment.get().getName()+" - "+
                optionalFacility.get().getName()+". Cho sinh viên có email : "+student.getEmail());
        activityLogRepository.save(activityLog);

        return new ResponseObject<>(null, HttpStatus.OK, "Sửa thành công chuyên ngành bộ môn cơ sở");
    }

    @Override
    public ResponseObject<?> deleteStudent(String id,String emailLogin) {
        Optional<Student> optionalStudent = adStudentExtendRepository.findById(id);

        if (optionalStudent.isEmpty()) {
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Không tìm thấy sinh nhân viên này");
        }

        Student student = optionalStudent.get();
        student.setStatus(EntityStatus.INACTIVE);
        adStudentExtendRepository.save(student);

        ActivityLog activityLog = new ActivityLog();
        activityLog.setRole("ADMIN");
        activityLog.setExecutorEmail(emailLogin);
        activityLog.setContent("Đã xóa sinh viên có email: "+student.getEmail());
        activityLogRepository.save(activityLog);

        return new ResponseObject<>(null, HttpStatus.OK, "Xóa sinh viên thành công");
    }
    public ResponseObject<?> readCSV() {
        String filePath = "src/main/resources/log-accountability-index/nvv.csv";

        try {
            List<LogEntry> logEntries = logService.readCSV(filePath);
            return new ResponseObject<>(logEntries, HttpStatus.OK, "Đọc file thành công!");
        } catch (FileNotFoundException e) {
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Không tìm thấy file!");
        }
    }

    @Override
    public ResponseObject<?> getLogsImportStudent(int page, int size) {
        if (page < 1) {
            page = 1;
        }

        String staffId = userContextHelper.getCurrentUserId();
        List<Role> roles = roleRepository.findRoleByStaff(staffId);
        String email = staffRepository.getEmailFpt(staffId);

        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("createdDate").descending());

        Page<HistoryImportStudent> historyPage = historyImportStudentRepository.findByEmail(email,pageable);

        Page<HistoryImportStudentResponse> responsePage = historyPage.map(history ->
                new HistoryImportStudentResponse(
                        history.getId(),
                        email,
                        history.getMessage(),
                        roles,
                        history.getCreatedDate()
                )
        );

        return ResponseObject.successForward(
                PageableObject.of(responsePage),
                "Lấy lịch sử import sinh viên thành công"
        );
    }


}
