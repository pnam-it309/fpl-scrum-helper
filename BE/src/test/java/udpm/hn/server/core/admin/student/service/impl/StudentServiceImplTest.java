package udpm.hn.server.core.admin.student.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.FileNotFoundException;
import java.util.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.data.domain.Pageable;
import udpm.hn.server.core.admin.student.model.request.ADCreateOrUpdateStudentRequest;
import udpm.hn.server.core.admin.student.model.request.ADCreateStudentFDMRequest;
import udpm.hn.server.core.admin.student.model.request.ADUpdateStudentFDMRequest;
import udpm.hn.server.core.admin.student.model.request.StudentRequest;
import udpm.hn.server.core.admin.student.model.response.StudentResponse;
import udpm.hn.server.core.admin.student.repository.*;
import udpm.hn.server.core.common.base.ResponseObject;
import udpm.hn.server.entity.*;
import udpm.hn.server.infrastructure.log.LogService;
import udpm.hn.server.repository.ActivityLogRepository;

@ExtendWith(MockitoExtension.class)
public class StudentServiceImplTest {

    @Mock private ADStudentExtendRepository adStudentExtendRepository;
    @Mock private ADStudentDepartmentRepository adStudentDepartmentRepository;
    @Mock private ADStudentMajorRepository adStudentMajorRepository;
    @Mock private ADDepartmentFacilityRepository adDepartmentFacilityRepository;
    @Mock private ADStudentMajorFacilityRepository adStudentMajorFacilityRepository;
    @Mock private ADStudentByDeparmentFacilityRepository adStudentByDeparmentFacilityRepository;

    @Mock
    private ActivityLogRepository activityLogRepository;


    @InjectMocks
    private StudentServiceImpl service;

    private ADCreateOrUpdateStudentRequest request;
    private Student student;

    @BeforeEach
    void setUp() {
        request = new ADCreateOrUpdateStudentRequest();
        request.setCode("SV001");
        request.setEmail("sv@example.com");
        request.setName("Nguyen Van A");

        student = new Student();
        student.setId("1");
        student.setCode("SV001");
        student.setEmail("sv@example.com");
        student.setName("Nguyen Van A");
    }

    @Test
    void testAddStudent_Success() {
        when(adStudentExtendRepository.existsByCode(any())).thenReturn(false);
        when(adStudentExtendRepository.existsByEmail(any())).thenReturn(false);

        request.setCode("SV001");
        request.setEmail("a@example.com");
        request.setName("Nguyen Van A");
        request.setEmailLogin("admin@fpt.edu.vn");

        ResponseObject<?> response = service.addStudent(request);

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals("Thêm sinh viên thành công", response.getMessage());

        verify(adStudentExtendRepository).save(any(Student.class));
        verify(activityLogRepository).save(any(ActivityLog.class));
    }


    @Test
    void testAddStudent_ExistCode() {
        when(adStudentExtendRepository.existsByCode(any())).thenReturn(true);

        ResponseObject<?> response = service.addStudent(request);

        assertEquals(HttpStatus.NOT_ACCEPTABLE, response.getStatus());
        assertEquals("Mã sinh viên đã tồn tại!", response.getMessage());
    }

    @Test
    void testAddStudent_ExistEmail() {
        when(adStudentExtendRepository.existsByCode(any())).thenReturn(false);
        when(adStudentExtendRepository.existsByEmail(any())).thenReturn(true);

        ResponseObject<?> response = service.addStudent(request);

        assertEquals(HttpStatus.NOT_ACCEPTABLE, response.getStatus());
        assertEquals("Email sinh viên đã tồn tại!", response.getMessage());
    }

    @Test
    void testUpdateStudent_Success() {
        // Arrange
        Student student = new Student();
        student.setId("1");
        student.setCode("SV001");
        student.setEmail("a@example.com");
        student.setName("Old Name");

        ADCreateOrUpdateStudentRequest request = new ADCreateOrUpdateStudentRequest();
        request.setCode("SV001"); // Trùng code hiện tại
        request.setEmail("a@example.com"); // Trùng email hiện tại
        request.setName("Nguyen Van A");
        request.setEmailLogin("admin@fpt.edu.vn");

        // Stub findById trả về student
        when(adStudentExtendRepository.findById("1")).thenReturn(Optional.of(student));

        // Act
        ResponseObject<?> response = service.updateStudent(request, "1");

        // Assert
        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals("Cập nhật sinh viên thành công", response.getMessage());

        verify(adStudentExtendRepository).save(any(Student.class));
        verify(activityLogRepository).save(any(ActivityLog.class));
    }



    @Test
    void testUpdateStudent_NotFound() {
        when(adStudentExtendRepository.findById("1")).thenReturn(Optional.empty());

        ResponseObject<?> response = service.updateStudent(request, "1");

        assertEquals(HttpStatus.NOT_ACCEPTABLE, response.getStatus());
        assertEquals("Sinh viên không tồn tại", response.getMessage());
    }

    @Test
    void testDetailStudent() {
        StudentResponse studentResponse = mock(StudentResponse.class);
        when(adStudentExtendRepository.getDetailStudent("1")).thenReturn(studentResponse);

        ResponseObject<?> response = service.detailStudent("1");

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals("Lấy thành công chi tiết sinh viên", response.getMessage());
        assertNotNull(response.getData());
        assertSame(studentResponse, response.getData());
    }


//    @Test
//    void testDeleteStudent_Success() {
//        when(adStudentExtendRepository.findById("1")).thenReturn(Optional.of(student));
//
//        ResponseObject<?> response = service.deleteStudent("1");
//
//        assertEquals(HttpStatus.OK, response.getStatus());
//        assertEquals("Xóa sinh viên thành công", response.getMessage());
//    }
//
//    @Test
//    void testDeleteStudent_NotFound() {
//        when(adStudentExtendRepository.findById("1")).thenReturn(Optional.empty());
//
//        ResponseObject<?> response = service.deleteStudent("1");
//
//        assertEquals(HttpStatus.NOT_FOUND, response.getStatus());
//        assertEquals("Không tìm thấy sinh nhân viên này", response.getMessage());
//    }

    @Test
    void testUpdateStudent_ExistNewCode() {
        Student student = new Student();
        student.setId("1");
        student.setCode("SV001");
        student.setEmail("a@example.com");
        student.setName("Old Name");

        ADCreateOrUpdateStudentRequest request = new ADCreateOrUpdateStudentRequest();
        request.setCode("SV002"); // Khác code hiện tại
        request.setEmail("a@example.com");
        request.setName("Nguyen Van A");
        request.setEmailLogin("admin@fpt.edu.vn");

        when(adStudentExtendRepository.findById("1")).thenReturn(Optional.of(student));
        when(adStudentExtendRepository.existsByCode("SV002")).thenReturn(true);

        ResponseObject<?> response = service.updateStudent(request, "1");

        assertEquals(HttpStatus.NOT_ACCEPTABLE, response.getStatus());
        assertTrue(response.getMessage().contains("Mã Sinh viên đã tồn tại"));
    }

    @Test
    void testUpdateStudent_ExistNewEmail() {
        Student student = new Student();
        student.setId("1");
        student.setCode("SV001");
        student.setEmail("a@example.com");
        student.setName("Old Name");

        ADCreateOrUpdateStudentRequest request = new ADCreateOrUpdateStudentRequest();
        request.setCode("SV001");
        request.setEmail("b@example.com"); // Khác email hiện tại
        request.setName("Nguyen Van A");
        request.setEmailLogin("admin@fpt.edu.vn");

        when(adStudentExtendRepository.findById("1")).thenReturn(Optional.of(student));
        when(adStudentExtendRepository.existsByEmail("b@example.com")).thenReturn(true);

        ResponseObject<?> response = service.updateStudent(request, "1");

        assertEquals(HttpStatus.NOT_ACCEPTABLE, response.getStatus());
        assertTrue(response.getMessage().contains("Email sinh viên đã tồn tại"));
    }


    @Test
    void testStudentByDepartmentFacility_NotFound() {
        when(adStudentByDeparmentFacilityRepository.studentByDepartmentFacility("1")).thenReturn(Optional.empty());

        ResponseObject<?> response = service.studentByDepartmentFacility("1");
        assertEquals(HttpStatus.NOT_FOUND, response.getStatus());
        assertTrue(response.getMessage().contains("Không tìm thấy sinh viên"));
    }
    @Test
    void testAddStudent_TrimEmail() {
        when(adStudentExtendRepository.existsByCode(any())).thenReturn(false);
        when(adStudentExtendRepository.existsByEmail(any())).thenReturn(false);

        request.setEmail("  a@example.com  ");
        request.setCode("SV003");
        request.setName("Test Trim");
        request.setEmailLogin("admin@fpt.edu.vn");

        ResponseObject<?> response = service.addStudent(request);

        assertEquals(HttpStatus.OK, response.getStatus());
        // Có thể kiểm tra thêm student lưu xuống có bị thừa dấu cách không.
        verify(adStudentExtendRepository).save(argThat(student ->
                student.getEmail().equals("a@example.com")
        ));
    }
    @Test
    void testUpdateStudent_ChangeCodeAndEmail_NoExist() {
        Student student = new Student();
        student.setId("1");
        student.setCode("SV001");
        student.setEmail("old@example.com");
        student.setName("Old Name");

        ADCreateOrUpdateStudentRequest request = new ADCreateOrUpdateStudentRequest();
        request.setCode("SV123"); // Khác code cũ
        request.setEmail("new@example.com"); // Khác email cũ
        request.setName("Nguyen Van A");
        request.setEmailLogin("admin@fpt.edu.vn");

        when(adStudentExtendRepository.findById("1")).thenReturn(Optional.of(student));
        when(adStudentExtendRepository.existsByCode("SV123")).thenReturn(false);
        when(adStudentExtendRepository.existsByEmail("new@example.com")).thenReturn(false);

        ResponseObject<?> response = service.updateStudent(request, "1");

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals("Cập nhật sinh viên thành công", response.getMessage());
    }
    @Test
    void testDeleteStudent_Success() {
        when(adStudentExtendRepository.findById("1")).thenReturn(Optional.of(student));
        // Thêm emailLogin nếu cần
        ResponseObject<?> response = service.deleteStudent("1", "admin@fpt.edu.vn");
        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals("Xóa sinh viên thành công", response.getMessage());
        verify(adStudentExtendRepository).save(any(Student.class));
        verify(activityLogRepository).save(any(ActivityLog.class));
    }

    @Test
    void testDeleteStudent_NotFound() {
        when(adStudentExtendRepository.findById("1")).thenReturn(Optional.empty());
        ResponseObject<?> response = service.deleteStudent("1", "admin@fpt.edu.vn");
        assertEquals(HttpStatus.NOT_FOUND, response.getStatus());
        assertEquals("Không tìm thấy sinh nhân viên này", response.getMessage());
    }

    @Test
    void testFindAllStudent() {
        when(adStudentExtendRepository.findAll()).thenReturn(Collections.emptyList());

        ResponseObject<?> response = service.findAllStudent();

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals("Lấy dữ liệu thành công", response.getMessage());
        assertNotNull(response.getData());
    }
    @Test
    void testDeleteStudentByIdStudent_Success() {
        Student s = new Student();
        s.setEmail("email@gmail.com");
        when(adStudentExtendRepository.findById("id1")).thenReturn(Optional.of(s));

        ResponseObject<?> response = service.deleteStudentByIdStudent("id1", "admin@fpt.edu.vn");
        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals("Xóa sinh viên khỏi chuyên ngành cơ sở thành công", response.getMessage());
        verify(adStudentExtendRepository).save(any(Student.class));
        verify(activityLogRepository).save(any(ActivityLog.class));
    }

    @Test
    void testDeleteStudentByIdStudent_NotFound() {
        when(adStudentExtendRepository.findById("id1")).thenReturn(Optional.empty());
        RuntimeException e = assertThrows(RuntimeException.class, () -> {
            service.deleteStudentByIdStudent("id1", "admin@fpt.edu.vn");
        });
        assertTrue(e.getMessage().contains("Không tìm thấy sinh viên có Id"));
    }
    @Test
    void testCreateStudentByFDM_FacilityNotFound() {
        ADCreateStudentFDMRequest req = new ADCreateStudentFDMRequest();
        req.setIdFacility("f1");
        when(adStudentByDeparmentFacilityRepository.findById("f1")).thenReturn(Optional.empty());

        ResponseObject<?> response = service.createStudentByFDM(req);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatus());
        assertEquals("Không tìm thấy cơ sở này", response.getMessage());
    }
    @Test
    void testCreateStudentByFDM_Success() {
        ADCreateStudentFDMRequest req = new ADCreateStudentFDMRequest();
        req.setIdFacility("f1"); req.setIdDepartment("d1"); req.setIdMajor("m1"); req.setIdStudentDetail("s1");
        req.setEmailLogin("admin@fpt.edu.vn");

        Facility facility = new Facility();
        Department department = new Department();
        Major major = new Major(); major.setName("CNTT");
        DepartmentFacility df = new DepartmentFacility();
        MajorFacility mf = new MajorFacility();
        Student student = new Student(); student.setEmail("sv@email.com");

        when(adStudentByDeparmentFacilityRepository.findById("f1")).thenReturn(Optional.of(facility));
        when(adStudentDepartmentRepository.findById("d1")).thenReturn(Optional.of(department));
        when(adStudentMajorRepository.findById("m1")).thenReturn(Optional.of(major));
        when(adDepartmentFacilityRepository.findByDepartmentAndFacility(department, facility)).thenReturn(Optional.of(df));
        when(adStudentMajorFacilityRepository.findByMajorAndDepartmentFacility(major, df)).thenReturn(Optional.of(mf));
        when(adStudentExtendRepository.findById("s1")).thenReturn(Optional.of(student));

        ResponseObject<?> response = service.createStudentByFDM(req);

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals("Thêm thành công chuyên ngành bộ môn cơ sở", response.getMessage());
        verify(adStudentExtendRepository).save(any(Student.class));
        verify(activityLogRepository).save(any(ActivityLog.class));
    }
    @Test
    void testUpdateStudentByFDM_FacilityNotFound() {
        ADUpdateStudentFDMRequest req = new ADUpdateStudentFDMRequest();
        req.setIdFacility("f1");
        when(adStudentByDeparmentFacilityRepository.findById("f1")).thenReturn(Optional.empty());

        ResponseObject<?> response = service.updateStudentByFDM(req);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatus());
        assertEquals("Không tìm thấy cơ sở này!", response.getMessage());
    }

    @Test
    void testCreateStudentByFDM_DepartmentNotFound() {
        ADCreateStudentFDMRequest req = new ADCreateStudentFDMRequest();
        req.setIdFacility("f1"); req.setIdDepartment("d1");
        Facility facility = new Facility();
        when(adStudentByDeparmentFacilityRepository.findById("f1")).thenReturn(Optional.of(facility));
        when(adStudentDepartmentRepository.findById("d1")).thenReturn(Optional.empty());
        ResponseObject<?> response = service.createStudentByFDM(req);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatus());
        assertEquals("Không tìm thấy bộ môn này", response.getMessage());
    }

    @Test
    void testCreateStudentByFDM_MajorNotFound() {
        ADCreateStudentFDMRequest req = new ADCreateStudentFDMRequest();
        req.setIdFacility("f1"); req.setIdDepartment("d1"); req.setIdMajor("m1");
        Facility facility = new Facility();
        Department department = new Department();
        when(adStudentByDeparmentFacilityRepository.findById("f1")).thenReturn(Optional.of(facility));
        when(adStudentDepartmentRepository.findById("d1")).thenReturn(Optional.of(department));
        when(adStudentMajorRepository.findById("m1")).thenReturn(Optional.empty());
        ResponseObject<?> response = service.createStudentByFDM(req);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatus());
        assertEquals("Không tìm thấy chuyên ngành này", response.getMessage());
    }

    @Test
    void testCreateStudentByFDM_DepartmentFacilityNotFound() {
        ADCreateStudentFDMRequest req = new ADCreateStudentFDMRequest();
        req.setIdFacility("f1"); req.setIdDepartment("d1"); req.setIdMajor("m1");
        Facility facility = new Facility();
        Department department = new Department();
        Major major = new Major();
        when(adStudentByDeparmentFacilityRepository.findById("f1")).thenReturn(Optional.of(facility));
        when(adStudentDepartmentRepository.findById("d1")).thenReturn(Optional.of(department));
        when(adStudentMajorRepository.findById("m1")).thenReturn(Optional.of(major));
        when(adDepartmentFacilityRepository.findByDepartmentAndFacility(department, facility)).thenReturn(Optional.empty());
        ResponseObject<?> response = service.createStudentByFDM(req);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatus());
        assertEquals("Không tìm thấy bộ môn cơ sở này ", response.getMessage());
    }

    @Test
    void testCreateStudentByFDM_MajorFacilityNotFound() {
        ADCreateStudentFDMRequest req = new ADCreateStudentFDMRequest();
        req.setIdFacility("f1"); req.setIdDepartment("d1"); req.setIdMajor("m1");
        Facility facility = new Facility();
        Department department = new Department();
        Major major = new Major();
        DepartmentFacility df = new DepartmentFacility();
        when(adStudentByDeparmentFacilityRepository.findById("f1")).thenReturn(Optional.of(facility));
        when(adStudentDepartmentRepository.findById("d1")).thenReturn(Optional.of(department));
        when(adStudentMajorRepository.findById("m1")).thenReturn(Optional.of(major));
        when(adDepartmentFacilityRepository.findByDepartmentAndFacility(department, facility)).thenReturn(Optional.of(df));
        when(adStudentMajorFacilityRepository.findByMajorAndDepartmentFacility(major, df)).thenReturn(Optional.empty());
        ResponseObject<?> response = service.createStudentByFDM(req);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatus());
        assertEquals("Không tìm thấy chuyên nghành cơ sở này ", response.getMessage());
    }

    @Test
    void testCreateStudentByFDM_StudentNotFound() {
        ADCreateStudentFDMRequest req = new ADCreateStudentFDMRequest();
        req.setIdFacility("f1"); req.setIdDepartment("d1"); req.setIdMajor("m1"); req.setIdStudentDetail("sid");
        Facility facility = new Facility();
        Department department = new Department();
        Major major = new Major();
        DepartmentFacility df = new DepartmentFacility();
        MajorFacility mf = new MajorFacility();
        when(adStudentByDeparmentFacilityRepository.findById("f1")).thenReturn(Optional.of(facility));
        when(adStudentDepartmentRepository.findById("d1")).thenReturn(Optional.of(department));
        when(adStudentMajorRepository.findById("m1")).thenReturn(Optional.of(major));
        when(adDepartmentFacilityRepository.findByDepartmentAndFacility(department, facility)).thenReturn(Optional.of(df));
        when(adStudentMajorFacilityRepository.findByMajorAndDepartmentFacility(major, df)).thenReturn(Optional.of(mf));
        when(adStudentExtendRepository.findById("sid")).thenReturn(Optional.empty());
        ResponseObject<?> response = service.createStudentByFDM(req);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatus());
        assertEquals("Không tìm thấy sinh viên này ", response.getMessage());
    }
    @Test
    void testUpdateStudentByFDM_DepartmentNotFound() {
        ADUpdateStudentFDMRequest req = new ADUpdateStudentFDMRequest();
        req.setIdFacility("f1"); req.setIdDepartment("d1");
        Facility facility = new Facility();
        when(adStudentByDeparmentFacilityRepository.findById("f1")).thenReturn(Optional.of(facility));
        when(adStudentDepartmentRepository.findById("d1")).thenReturn(Optional.empty());
        ResponseObject<?> response = service.updateStudentByFDM(req);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatus());
        assertEquals("Không tìm thấy bộ môn này!", response.getMessage());
    }

    @Test
    void testUpdateStudentByFDM_MajorNotFound() {
        ADUpdateStudentFDMRequest req = new ADUpdateStudentFDMRequest();
        req.setIdFacility("f1"); req.setIdDepartment("d1"); req.setIdMajor("m1");
        Facility facility = new Facility();
        Department department = new Department();
        when(adStudentByDeparmentFacilityRepository.findById("f1")).thenReturn(Optional.of(facility));
        when(adStudentDepartmentRepository.findById("d1")).thenReturn(Optional.of(department));
        when(adStudentMajorRepository.findById("m1")).thenReturn(Optional.empty());
        ResponseObject<?> response = service.updateStudentByFDM(req);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatus());
        assertEquals("Không tìm thấy chuyên ngành này!", response.getMessage());
    }

    @Test
    void testUpdateStudentByFDM_DepartmentFacilityNotFound() {
        ADUpdateStudentFDMRequest req = new ADUpdateStudentFDMRequest();
        req.setIdFacility("f1"); req.setIdDepartment("d1"); req.setIdMajor("m1");
        Facility facility = new Facility();
        Department department = new Department();
        Major major = new Major();
        when(adStudentByDeparmentFacilityRepository.findById("f1")).thenReturn(Optional.of(facility));
        when(adStudentDepartmentRepository.findById("d1")).thenReturn(Optional.of(department));
        when(adStudentMajorRepository.findById("m1")).thenReturn(Optional.of(major));
        when(adDepartmentFacilityRepository.findByDepartmentAndFacility(department, facility)).thenReturn(Optional.empty());
        ResponseObject<?> response = service.updateStudentByFDM(req);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatus());
        assertEquals("Không tìm thấy bộ môn cơ sở này!", response.getMessage());
    }

    @Test
    void testUpdateStudentByFDM_MajorFacilityNotFound() {
        ADUpdateStudentFDMRequest req = new ADUpdateStudentFDMRequest();
        req.setIdFacility("f1"); req.setIdDepartment("d1"); req.setIdMajor("m1");
        Facility facility = new Facility();
        Department department = new Department();
        Major major = new Major();
        DepartmentFacility df = new DepartmentFacility();
        when(adStudentByDeparmentFacilityRepository.findById("f1")).thenReturn(Optional.of(facility));
        when(adStudentDepartmentRepository.findById("d1")).thenReturn(Optional.of(department));
        when(adStudentMajorRepository.findById("m1")).thenReturn(Optional.of(major));
        when(adDepartmentFacilityRepository.findByDepartmentAndFacility(department, facility)).thenReturn(Optional.of(df));
        when(adStudentMajorFacilityRepository.findByMajorAndDepartmentFacility(major, df)).thenReturn(Optional.empty());
        ResponseObject<?> response = service.updateStudentByFDM(req);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatus());
        assertEquals("Không tìm thấy chuyên nghành cơ sở này!", response.getMessage());
    }

    @Test
    void testUpdateStudentByFDM_StudentNotFound() {
        ADUpdateStudentFDMRequest req = new ADUpdateStudentFDMRequest();
        req.setIdFacility("f1"); req.setIdDepartment("d1"); req.setIdMajor("m1"); req.setIdStudentDetail("sid");
        Facility facility = new Facility();
        Department department = new Department();
        Major major = new Major();
        DepartmentFacility df = new DepartmentFacility();
        MajorFacility mf = new MajorFacility();
        when(adStudentByDeparmentFacilityRepository.findById("f1")).thenReturn(Optional.of(facility));
        when(adStudentDepartmentRepository.findById("d1")).thenReturn(Optional.of(department));
        when(adStudentMajorRepository.findById("m1")).thenReturn(Optional.of(major));
        when(adDepartmentFacilityRepository.findByDepartmentAndFacility(department, facility)).thenReturn(Optional.of(df));
        when(adStudentMajorFacilityRepository.findByMajorAndDepartmentFacility(major, df)).thenReturn(Optional.of(mf));
        when(adStudentExtendRepository.findById("sid")).thenReturn(Optional.empty());
        ResponseObject<?> response = service.updateStudentByFDM(req);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatus());
        assertEquals("Không tìm thấy sinh viên này ", response.getMessage());
    }

    @Test
    void testUpdateStudentByFDM_DepartmentFacilityUpdateNotFound() {
        ADUpdateStudentFDMRequest req = new ADUpdateStudentFDMRequest();
        req.setIdFacility("f1"); req.setIdDepartment("d1"); req.setIdMajor("m1"); req.setIdStudentDetail("sid");
        req.setIdUpdateDepartment("updD"); req.setIdUpdateFacility("updF");
        Facility facility = new Facility();
        Department department = new Department();
        Major major = new Major();
        DepartmentFacility df = new DepartmentFacility();
        MajorFacility mf = new MajorFacility();
        Student student = new Student();
        when(adStudentByDeparmentFacilityRepository.findById("f1")).thenReturn(Optional.of(facility));
        when(adStudentDepartmentRepository.findById("d1")).thenReturn(Optional.of(department));
        when(adStudentMajorRepository.findById("m1")).thenReturn(Optional.of(major));
        when(adDepartmentFacilityRepository.findByDepartmentAndFacility(department, facility)).thenReturn(Optional.of(df));
        when(adStudentMajorFacilityRepository.findByMajorAndDepartmentFacility(major, df)).thenReturn(Optional.of(mf));
        when(adStudentExtendRepository.findById("sid")).thenReturn(Optional.of(student));
        when(adDepartmentFacilityRepository.findByDepartmentAndFacilityAndStudent("updD", "updF")).thenReturn(Optional.empty());
        ResponseObject<?> response = service.updateStudentByFDM(req);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatus());
        assertEquals("Không tìm thấy bộ môn cơ sở này!", response.getMessage());
    }

    @Test
    void testUpdateStudentByFDM_MajorUpdateNotFound() {
        ADUpdateStudentFDMRequest req = new ADUpdateStudentFDMRequest();
        req.setIdFacility("f1"); req.setIdDepartment("d1"); req.setIdMajor("m1"); req.setIdStudentDetail("sid");
        req.setIdUpdateDepartment("updD"); req.setIdUpdateFacility("updF"); req.setIdUpdateMajor("updM");
        Facility facility = new Facility();
        Department department = new Department();
        Major major = new Major();
        DepartmentFacility df = new DepartmentFacility();
        MajorFacility mf = new MajorFacility();
        Student student = new Student();
        DepartmentFacility updDf = new DepartmentFacility();
        when(adStudentByDeparmentFacilityRepository.findById("f1")).thenReturn(Optional.of(facility));
        when(adStudentDepartmentRepository.findById("d1")).thenReturn(Optional.of(department));
        when(adStudentMajorRepository.findById("m1")).thenReturn(Optional.of(major));
        when(adDepartmentFacilityRepository.findByDepartmentAndFacility(department, facility)).thenReturn(Optional.of(df));
        when(adStudentMajorFacilityRepository.findByMajorAndDepartmentFacility(major, df)).thenReturn(Optional.of(mf));
        when(adStudentExtendRepository.findById("sid")).thenReturn(Optional.of(student));
        when(adDepartmentFacilityRepository.findByDepartmentAndFacilityAndStudent("updD", "updF")).thenReturn(Optional.of(updDf));
        when(adStudentMajorRepository.findById("updM")).thenReturn(Optional.empty());
        ResponseObject<?> response = service.updateStudentByFDM(req);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatus());
        assertEquals("Không tìm thấy chuyên ngành này!", response.getMessage());
    }

    @Test
    void testUpdateStudentByFDM_MajorFacilityUpdateNotFound() {
        ADUpdateStudentFDMRequest req = new ADUpdateStudentFDMRequest();
        req.setIdFacility("f1"); req.setIdDepartment("d1"); req.setIdMajor("m1"); req.setIdStudentDetail("sid");
        req.setIdUpdateDepartment("updD"); req.setIdUpdateFacility("updF"); req.setIdUpdateMajor("updM");
        Facility facility = new Facility();
        Department department = new Department();
        Major major = new Major();
        DepartmentFacility df = new DepartmentFacility();
        MajorFacility mf = new MajorFacility();
        Student student = new Student();
        DepartmentFacility updDf = new DepartmentFacility();
        Major updMajor = new Major();
        when(adStudentByDeparmentFacilityRepository.findById("f1")).thenReturn(Optional.of(facility));
        when(adStudentDepartmentRepository.findById("d1")).thenReturn(Optional.of(department));
        when(adStudentMajorRepository.findById("m1")).thenReturn(Optional.of(major));
        when(adDepartmentFacilityRepository.findByDepartmentAndFacility(department, facility)).thenReturn(Optional.of(df));
        when(adStudentMajorFacilityRepository.findByMajorAndDepartmentFacility(major, df)).thenReturn(Optional.of(mf));
        when(adStudentExtendRepository.findById("sid")).thenReturn(Optional.of(student));
        when(adDepartmentFacilityRepository.findByDepartmentAndFacilityAndStudent("updD", "updF")).thenReturn(Optional.of(updDf));
        when(adStudentMajorRepository.findById("updM")).thenReturn(Optional.of(updMajor));
        when(adStudentMajorFacilityRepository.findByMajorAndDepartmentFacility(updMajor, updDf)).thenReturn(Optional.empty());
        ResponseObject<?> response = service.updateStudentByFDM(req);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatus());
        assertEquals("Không tìm thấy chuyên ngành cơ sở này!", response.getMessage());
    }

}