package udpm.hn.server.core.manage.project.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import udpm.hn.server.core.admin.project.model.request.ADProjectSTRequest;
import udpm.hn.server.core.common.base.ResponseObject;
import udpm.hn.server.core.manage.project.model.request.MaProjectCreateRequest;
import udpm.hn.server.core.manage.project.model.request.MaProjectSearchRequest;
import udpm.hn.server.core.manage.project.model.request.MaRestartProjectRequest;
import udpm.hn.server.core.manage.project.model.response.MAProjectDetailSummaryResponse;
import udpm.hn.server.core.manage.project.model.response.MaPhaseSummaryResponse;
import udpm.hn.server.core.manage.project.model.response.MaProjectResponse;
import udpm.hn.server.core.manage.project.model.response.MaTodoByPhaseSummaryResponse;
import udpm.hn.server.core.manage.project.repository.*;
import udpm.hn.server.entity.*;
import udpm.hn.server.infrastructure.constant.EntityStatus;
import udpm.hn.server.infrastructure.constant.StatusProject;
import udpm.hn.server.repository.*;
import udpm.hn.server.utils.StatusPhase;
import udpm.hn.server.utils.UserContextHelper;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Page;
@ExtendWith(MockitoExtension.class)
public class MaProjectServiceImplTest {

    @InjectMocks
    private MaProjectServiceImpl service;

    @Mock private MaProjectRepository maProjectRepository;
    @Mock private MaProjectDFRepository projectDFRepository;
    @Mock private MajorFacilityRepository majorFacilityRepository;
    @Mock private CategoryRepository categoryRepository;
    @Mock private MaProjectStudentRepository maProjectStudentRepository;
    @Mock private MaProjectStaffRepository maProjectStaffRepository;
    @Mock private StaffProjectRepository staffProjectRepository;
    @Mock private ProjectRepository projectRepository;
    @Mock private FacilityRepository facilityRepository;
    @Mock private UserContextHelper userContextHelper;
    @Mock private MaTodoProjectRepository maTodoProjectRepository;
    @Mock private MaPhaseProjectRepository maPhaseProjectRepository;
    @Mock private MAUserProjectRepository maUserProjectRepository;

    @Mock
    private ActivityLogRepository activityLogRepository;


    private final String projectId = "testProjectId";

    @BeforeEach
    void setUp() {}

    @Test
    void testGetAllFacility() {
        List<Facility> facilities = List.of(new Facility());
        when(facilityRepository.findAll()).thenReturn(facilities);

        ResponseObject<?> response = service.getAllFacility();

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(facilities, response.getData());
    }

    @Test
    void testDetailProject_found() {
        Project mockProject = new Project();
        when(maProjectRepository.findById(projectId)).thenReturn(Optional.of(mockProject));

        ResponseObject<?> response = service.detailProject(projectId);

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(mockProject, response.getData());
    }

    @Test
    void testDetailProject_notFound() {
        when(maProjectRepository.findById(projectId)).thenReturn(Optional.empty());

        ResponseObject<?> response = service.detailProject(projectId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatus());
        assertNull(response.getData());
    }

    @Test
    void testFinishEarlyProject_found() {
        // Arrange
        String projectId = "1";
        String emailLogin = "manager@example.com";

        Project project = new Project();
        project.setId(projectId);
        project.setName("Dự án A");

        when(maProjectRepository.findProjectById(projectId)).thenReturn(Optional.of(project));

        // Act
        ResponseObject<?> response = service.finishEarlyProject(projectId, emailLogin);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatus());

        assertEquals(StatusProject.DA_DIEN_RA, project.getStatusProject());
        assertNotNull(project.getActualEndDate());

        verify(maProjectRepository).save(project);
        verify(activityLogRepository).save(any(ActivityLog.class));
    }


    @Test
    void testFinishEarlyProject_notFound() {
        String emailLogin = "manager@example.com";

        when(maProjectRepository.findProjectById(projectId)).thenReturn(Optional.empty());

        ResponseObject<?> response = service.finishEarlyProject(projectId, emailLogin);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatus());

    }

    @Test
    void testRestartProject_Found() {
        // Arrange
        Project project = new Project();
        project.setName("Project A");  // Để dòng log không bị null
        String projectId = "123";

        MaRestartProjectRequest request = new MaRestartProjectRequest();
        request.setCompletionDate(System.currentTimeMillis());
        request.setEmailLogin("manager@example.com");

        when(maProjectRepository.findProjectById(projectId)).thenReturn(Optional.of(project));

        // Act
        ResponseObject<?> response = service.restartProject(projectId, request);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(StatusProject.DANG_DIEN_RA, project.getStatusProject());
        verify(maProjectRepository).save(project);
        verify(activityLogRepository).save(any(ActivityLog.class));
    }


    @Test
    void testRestartProject_notFound() {
        MaRestartProjectRequest request = new MaRestartProjectRequest();
        when(maProjectRepository.findProjectById(projectId)).thenReturn(Optional.empty());

        ResponseObject<?> response = service.restartProject(projectId, request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatus());
    }

    @Test
    void testGetAmountOfPhaseByStatus() {
        String projectId = "p1";

        // Tạo mock response với lenient để tránh lỗi UnnecessaryStubbingException
        MaPhaseSummaryResponse mockResponse = mock(MaPhaseSummaryResponse.class, withSettings().lenient());
        lenient().when(mockResponse.getAmount()).thenReturn(5);
        lenient().when(mockResponse.getStatus()).thenReturn(StatusPhase.DA_HOAN_THANH);

        List<MaPhaseSummaryResponse> mockList = List.of(mockResponse);

        // Mock repository trả về danh sách mock
        when(maPhaseProjectRepository.getAmountOfPhaseByStatus(projectId)).thenReturn(mockList);

        // Gọi service
        ResponseObject<?> response = service.getAmountOfPhaseByStatus(projectId);

        // Kiểm tra kết quả
        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(mockList, response.getData());
    }


    @Test
    void testGetAmountOfTodoByPhase() {
        String projectId = "p1";

        MaTodoByPhaseSummaryResponse mockResponse = mock(MaTodoByPhaseSummaryResponse.class, withSettings().lenient());
        lenient().when(mockResponse.getAmount()).thenReturn(3);
        lenient().when(mockResponse.getNamePhase()).thenReturn("Phase 1");

        List<MaTodoByPhaseSummaryResponse> mockList = List.of(mockResponse);

        // Mock repository
        when(maTodoProjectRepository.getAmountOfTodoByPhase(projectId)).thenReturn(mockList);

        // Gọi service
        ResponseObject<?> response = service.getAmountOfTodoByPhase(projectId);

        // Kiểm tra kết quả
        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(mockList, response.getData());
    }



    @Test
    void testGetAllProject() {
        MaProjectSearchRequest request = new MaProjectSearchRequest();
        request.setStatus(StatusProject.DANG_DIEN_RA.ordinal());

        MaProjectResponse projectResponse = mock(MaProjectResponse.class);
        Page<MaProjectResponse> pageResponse = new PageImpl<>(List.of(projectResponse), PageRequest.of(0, 10), 1);

        when(maProjectRepository.getAllProject(any(Pageable.class), eq(request), eq(StatusProject.DANG_DIEN_RA), any(UserContextHelper.class)))
                .thenReturn(pageResponse);

        ResponseObject<?> response = service.getAllProject(request);

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(pageResponse, response.getData());
        assertEquals("Lấy thông tin project thành công", response.getMessage());
    }

    @Test
    void testGetAllProject_StatusNull() {
        MaProjectSearchRequest request = new MaProjectSearchRequest(); // Không set status

        Page<MaProjectResponse> pageResponse = new PageImpl<>(List.of());
        when(maProjectRepository.getAllProject(any(Pageable.class), eq(request), isNull(), any(UserContextHelper.class)))
                .thenReturn(pageResponse);

        ResponseObject<?> response = service.getAllProject(request);

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(pageResponse, response.getData());
    }

    @Test
    void testGetDetailSummaryProject_Found() {
        String id = "p1";
        MAProjectDetailSummaryResponse mockDetail = mock(MAProjectDetailSummaryResponse.class, withSettings().lenient());
        when(maProjectRepository.getDetailSummaryProject(id)).thenReturn(Optional.of(mockDetail));
        when(maUserProjectRepository.getAmountStaff(id)).thenReturn(2);
        when(maUserProjectRepository.getAmountStudent(id)).thenReturn(3);

        ResponseObject<?> response = service.getDetailSummaryProject(id);
        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(mockDetail, response.getData());
        assertEquals("Lấy dữ liệu tổng kết dự án thành công", response.getMessage());
    }

    @Test
    void testGetDetailSummaryProject_NotFound() {
        String id = "not-exist";
        when(maProjectRepository.getDetailSummaryProject(id)).thenReturn(Optional.empty());

        ResponseObject<?> response = service.getDetailSummaryProject(id);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatus());
        assertNull(response.getData());
        assertEquals("Không tìm thấy dự án", response.getMessage());
    }

    @Test
    void testCreateProject_Null() {
        MaProjectCreateRequest req = new MaProjectCreateRequest();
        assertNull(service.createProject(req));
    }

    @Test
    void testDeleteProject_Null() {
        assertNull(service.deleteProject("id123"));
    }
    @Test
    void testUpdateProject_AssignRoleDEV() {
        MaProjectCreateRequest req = new MaProjectCreateRequest();
        req.setIdMajorFacility("mf1");
        req.setIdCategory("cate1");
        ADProjectSTRequest member = new ADProjectSTRequest();
        member.setEmail("dev@example.com");
        member.setRole("DEV");
        req.setListMembers(List.of(member));
        req.setStartTime(System.currentTimeMillis() - 1000);
        req.setEndTime(System.currentTimeMillis() + 10000);

        Project project = new Project(); project.setId("p1");
        when(projectRepository.findById("p1")).thenReturn(Optional.of(project));
        when(majorFacilityRepository.findById("mf1")).thenReturn(Optional.of(new MajorFacility()));
        when(categoryRepository.findById("cate1")).thenReturn(Optional.of(new Category()));
        when(maProjectStaffRepository.findByEmailFe("dev@example.com")).thenReturn(Optional.of(new Staff()));

        ResponseObject<?> res = service.updateProject("p1", req);
        assertEquals(HttpStatus.OK, res.getStatus());
    }

    @Test
    void testUpdateProject_AssignRoleTester() {
        MaProjectCreateRequest req = new MaProjectCreateRequest();
        req.setIdMajorFacility("mf1");
        req.setIdCategory("cate1");
        ADProjectSTRequest member = new ADProjectSTRequest();
        member.setEmail("tester@example.com");
        member.setRole("OTHER"); // sẽ vào nhánh TESTER
        req.setListMembers(List.of(member));
        req.setStartTime(System.currentTimeMillis() - 1000);
        req.setEndTime(System.currentTimeMillis() + 10000);

        Project project = new Project(); project.setId("p1");
        when(projectRepository.findById("p1")).thenReturn(Optional.of(project));
        when(majorFacilityRepository.findById("mf1")).thenReturn(Optional.of(new MajorFacility()));
        when(categoryRepository.findById("cate1")).thenReturn(Optional.of(new Category()));
        when(maProjectStaffRepository.findByEmailFe("tester@example.com")).thenReturn(Optional.empty());
        when(maProjectStaffRepository.findByEmailFpt("tester@example.com")).thenReturn(Optional.of(new Staff()));

        ResponseObject<?> res = service.updateProject("p1", req);
        assertEquals(HttpStatus.OK, res.getStatus());
    }

    @Test
    void testUpdateProject_AssignStudent() {
        MaProjectCreateRequest req = new MaProjectCreateRequest();
        req.setIdMajorFacility("mf1");
        req.setIdCategory("cate1");
        ADProjectSTRequest member = new ADProjectSTRequest();
        member.setEmail("student@example.com");
        member.setRole("QUAN_Li");
        req.setListMembers(List.of(member));
        req.setStartTime(System.currentTimeMillis() - 1000);
        req.setEndTime(System.currentTimeMillis() + 10000);

        Project project = new Project(); project.setId("p1");
        when(projectRepository.findById("p1")).thenReturn(Optional.of(project));
        when(majorFacilityRepository.findById("mf1")).thenReturn(Optional.of(new MajorFacility()));
        when(categoryRepository.findById("cate1")).thenReturn(Optional.of(new Category()));
        when(maProjectStaffRepository.findByEmailFe(any())).thenReturn(Optional.empty());
        when(maProjectStaffRepository.findByEmailFpt(any())).thenReturn(Optional.empty());
        when(maProjectStudentRepository.findByEmail("student@example.com")).thenReturn(Optional.of(new Student()));

        ResponseObject<?> res = service.updateProject("p1", req);
        assertEquals(HttpStatus.OK, res.getStatus());
    }

    @Test
    void testUpdateProject_UpdateExistingStaffProject() {
        MaProjectCreateRequest req = new MaProjectCreateRequest();
        req.setIdMajorFacility("mf1");
        req.setIdCategory("cate1");
        ADProjectSTRequest member = new ADProjectSTRequest();
        member.setEmail("staff@example.com");
        member.setRole("DEV");
        req.setListMembers(List.of(member));
        req.setStartTime(System.currentTimeMillis() - 1000);
        req.setEndTime(System.currentTimeMillis() + 10000);

        Project project = new Project(); project.setId("p1");
        Staff staff = new Staff(); staff.setEmailFe("staff@example.com");
        StaffProject existingSP = new StaffProject(); existingSP.setStaff(staff);

        when(projectRepository.findById("p1")).thenReturn(Optional.of(project));
        when(majorFacilityRepository.findById("mf1")).thenReturn(Optional.of(new MajorFacility()));
        when(categoryRepository.findById("cate1")).thenReturn(Optional.of(new Category()));
        when(maProjectStaffRepository.findByEmailFe("staff@example.com")).thenReturn(Optional.of(staff));
        when(staffProjectRepository.findByStaffAndProject(staff, project)).thenReturn(Optional.of(existingSP));
        when(staffProjectRepository.findByProject(project)).thenReturn(List.of(existingSP));

        ResponseObject<?> res = service.updateProject("p1", req);
        assertEquals(HttpStatus.OK, res.getStatus());
        verify(staffProjectRepository, atLeastOnce()).save(any());
    }

    @Test
    void testUpdateProject_UpdateExistingStudentProject() {
        MaProjectCreateRequest req = new MaProjectCreateRequest();
        req.setIdMajorFacility("mf1");
        req.setIdCategory("cate1");
        ADProjectSTRequest member = new ADProjectSTRequest();
        member.setEmail("stu@example.com");
        member.setRole("DEV");
        req.setListMembers(List.of(member));
        req.setStartTime(System.currentTimeMillis() - 1000);
        req.setEndTime(System.currentTimeMillis() + 10000);

        Project project = new Project(); project.setId("p1");
        Student student = new Student(); student.setEmail("stu@example.com");
        StaffProject existingSP = new StaffProject(); existingSP.setStudent(student);

        when(projectRepository.findById("p1")).thenReturn(Optional.of(project));
        when(majorFacilityRepository.findById("mf1")).thenReturn(Optional.of(new MajorFacility()));
        when(categoryRepository.findById("cate1")).thenReturn(Optional.of(new Category()));
        when(maProjectStaffRepository.findByEmailFe(any())).thenReturn(Optional.empty());
        when(maProjectStaffRepository.findByEmailFpt(any())).thenReturn(Optional.empty());
        when(maProjectStudentRepository.findByEmail("stu@example.com")).thenReturn(Optional.of(student));
        when(staffProjectRepository.findByStudentAndProject(student, project)).thenReturn(Optional.of(existingSP));
        when(staffProjectRepository.findByProject(project)).thenReturn(List.of(existingSP));

        ResponseObject<?> res = service.updateProject("p1", req);
        assertEquals(HttpStatus.OK, res.getStatus());
        verify(staffProjectRepository, atLeastOnce()).save(any());
    }

    @Test
    void testUpdateProject_RemoveOldStaffProject() {
        MaProjectCreateRequest req = new MaProjectCreateRequest();
        req.setIdMajorFacility("mf1");
        req.setIdCategory("cate1");
        req.setListMembers(List.of()); // rỗng để trigger toDelete
        req.setStartTime(System.currentTimeMillis() - 1000);
        req.setEndTime(System.currentTimeMillis() + 10000);

        Project project = new Project(); project.setId("p1");
        Staff staff = new Staff(); staff.setEmailFe("old@example.com");
        StaffProject oldSP = new StaffProject(); oldSP.setStaff(staff); oldSP.setStatus(EntityStatus.ACTIVE);

        when(projectRepository.findById("p1")).thenReturn(Optional.of(project));
        when(majorFacilityRepository.findById("mf1")).thenReturn(Optional.of(new MajorFacility()));
        when(categoryRepository.findById("cate1")).thenReturn(Optional.of(new Category()));
        when(staffProjectRepository.findByProject(project)).thenReturn(List.of(oldSP));

        ResponseObject<?> res = service.updateProject("p1", req);
        assertEquals(HttpStatus.OK, res.getStatus());
        verify(staffProjectRepository).saveAll(any());
    }

    @Test
    void testUpdateProject_SetStatusChuaDienRa() {
        MaProjectCreateRequest req = new MaProjectCreateRequest();
        req.setIdMajorFacility("mf1");
        req.setIdCategory("cate1");
        req.setListMembers(Collections.emptyList());
        req.setStartTime(System.currentTimeMillis() + 10000); // tương lai
        req.setEndTime(System.currentTimeMillis() + 20000);

        Project project = new Project(); project.setId("p1");

        when(projectRepository.findById("p1")).thenReturn(Optional.of(project));
        when(majorFacilityRepository.findById("mf1")).thenReturn(Optional.of(new MajorFacility()));
        when(categoryRepository.findById("cate1")).thenReturn(Optional.of(new Category()));
        when(staffProjectRepository.findByProject(project)).thenReturn(Collections.emptyList());

        ResponseObject<?> res = service.updateProject("p1", req);

        assertEquals(HttpStatus.OK, res.getStatus());
        assertEquals(StatusProject.CHUA_DIEN_RA, project.getStatusProject());
    }

    @Test
    void testUpdateProject_SetStatusDaDienRa() {
        MaProjectCreateRequest req = new MaProjectCreateRequest();
        req.setIdMajorFacility("mf1");
        req.setIdCategory("cate1");
        req.setListMembers(Collections.emptyList());
        req.setStartTime(System.currentTimeMillis() - 20000);
        req.setEndTime(System.currentTimeMillis() - 10000); // quá khứ

        Project project = new Project(); project.setId("p1");

        when(projectRepository.findById("p1")).thenReturn(Optional.of(project));
        when(majorFacilityRepository.findById("mf1")).thenReturn(Optional.of(new MajorFacility()));
        when(categoryRepository.findById("cate1")).thenReturn(Optional.of(new Category()));
        when(staffProjectRepository.findByProject(project)).thenReturn(Collections.emptyList());

        ResponseObject<?> res = service.updateProject("p1", req);

        assertEquals(HttpStatus.OK, res.getStatus());
        assertEquals(StatusProject.DA_DIEN_RA, project.getStatusProject());
    }

    @Test
    void testFinishEarlyProject_LogContent() {
        Project project = new Project();
        project.setId("p1");
        project.setName("Dự án ABC");

        when(maProjectRepository.findProjectById("p1")).thenReturn(Optional.of(project));

        ResponseObject<?> res = service.finishEarlyProject("p1", "manager@test.com");

        assertEquals(HttpStatus.OK, res.getStatus());
        assertEquals(StatusProject.DA_DIEN_RA, project.getStatusProject());
        verify(activityLogRepository).save(argThat(log ->
                log.getContent().contains("Kết thúc sớm dự án")));
    }

    @Test
    void testRestartProject_ResetActualEndDate() {
        Project project = new Project();
        project.setId("p1");
        project.setName("Project Restart");
        project.setActualEndDate(System.currentTimeMillis());

        MaRestartProjectRequest req = new MaRestartProjectRequest();
        req.setCompletionDate(System.currentTimeMillis() + 10000);
        req.setEmailLogin("manager@test.com");

        when(maProjectRepository.findProjectById("p1")).thenReturn(Optional.of(project));

        ResponseObject<?> res = service.restartProject("p1", req);

        assertEquals(HttpStatus.OK, res.getStatus());
        assertNull(project.getActualEndDate()); // reset về null
        assertEquals(StatusProject.DANG_DIEN_RA, project.getStatusProject());
    }



    // --- createProject & deleteProject (trả null) ---
    @Test
    void testCreateProject_ShouldReturnNull() {
        MaProjectCreateRequest req = new MaProjectCreateRequest();
        assertNull(service.createProject(req));
    }

    @Test
    void testDeleteProject_ShouldReturnNull() {
        assertNull(service.deleteProject("pid"));
    }

    // --- updateProject: branch currentTime < startTime ---
    @Test
    void testUpdateProject_StatusChuaDienRa() {
        MaProjectCreateRequest req = new MaProjectCreateRequest();
        req.setIdMajorFacility("mf1");
        req.setIdCategory("cate1");
        req.setListMembers(Collections.emptyList());
        req.setStartTime(System.currentTimeMillis() + 10_000);
        req.setEndTime(System.currentTimeMillis() + 20_000);

        Project project = new Project(); project.setId("p1");

        when(projectRepository.findById("p1")).thenReturn(Optional.of(project));
        when(majorFacilityRepository.findById("mf1")).thenReturn(Optional.of(new MajorFacility()));
        when(categoryRepository.findById("cate1")).thenReturn(Optional.of(new Category()));
        when(staffProjectRepository.findByProject(project)).thenReturn(Collections.emptyList());

        ResponseObject<?> res = service.updateProject("p1", req);

        assertEquals(HttpStatus.OK, res.getStatus());
        assertEquals(StatusProject.CHUA_DIEN_RA, project.getStatusProject());
    }

    // --- updateProject: branch currentTime > endTime ---
    @Test
    void testUpdateProject_StatusDaDienRa() {
        MaProjectCreateRequest req = new MaProjectCreateRequest();
        req.setIdMajorFacility("mf1");
        req.setIdCategory("cate1");
        req.setListMembers(Collections.emptyList());
        req.setStartTime(System.currentTimeMillis() - 20_000);
        req.setEndTime(System.currentTimeMillis() - 10_000);

        Project project = new Project(); project.setId("p1");

        when(projectRepository.findById("p1")).thenReturn(Optional.of(project));
        when(majorFacilityRepository.findById("mf1")).thenReturn(Optional.of(new MajorFacility()));
        when(categoryRepository.findById("cate1")).thenReturn(Optional.of(new Category()));
        when(staffProjectRepository.findByProject(project)).thenReturn(Collections.emptyList());

        ResponseObject<?> res = service.updateProject("p1", req);

        assertEquals(HttpStatus.OK, res.getStatus());
        assertEquals(StatusProject.DA_DIEN_RA, project.getStatusProject());
    }

    // --- updateProject: member email rỗng ---
    @Test
    void testUpdateProject_MemberEmailEmpty_ShouldSkip() {
        MaProjectCreateRequest req = new MaProjectCreateRequest();
        req.setIdMajorFacility("mf1");
        req.setIdCategory("cate1");
        ADProjectSTRequest member = new ADProjectSTRequest();
        member.setEmail(""); // email rỗng
        req.setListMembers(List.of(member));
        req.setStartTime(System.currentTimeMillis() - 1000);
        req.setEndTime(System.currentTimeMillis() + 1000);

        Project project = new Project(); project.setId("p1");

        when(projectRepository.findById("p1")).thenReturn(Optional.of(project));
        when(majorFacilityRepository.findById("mf1")).thenReturn(Optional.of(new MajorFacility()));
        when(categoryRepository.findById("cate1")).thenReturn(Optional.of(new Category()));
        when(staffProjectRepository.findByProject(project)).thenReturn(Collections.emptyList());

        ResponseObject<?> res = service.updateProject("p1", req);

        assertEquals(HttpStatus.OK, res.getStatus());
        assertEquals("Cập nhật dự án thành công", res.getMessage());
    }

    // --- finishEarlyProject: verify log content ---
    @Test
    void testFinishEarlyProject_ShouldLogContent() {
        Project project = new Project();
        project.setId("p1");
        project.setName("Dự án ABC");

        when(maProjectRepository.findProjectById("p1")).thenReturn(Optional.of(project));

        ResponseObject<?> res = service.finishEarlyProject("p1", "manager@test.com");

        assertEquals(HttpStatus.OK, res.getStatus());
        verify(activityLogRepository).save(argThat(log ->
                log.getContent().contains("Kết thúc sớm dự án")));
    }

    // --- restartProject: reset actualEndDate ---
    @Test
    void testRestartProject_ShouldResetActualEndDate() {
        Project project = new Project();
        project.setId("p1");
        project.setName("Project Restart");
        project.setActualEndDate(System.currentTimeMillis());

        MaRestartProjectRequest req = new MaRestartProjectRequest();
        req.setCompletionDate(System.currentTimeMillis() + 10000);
        req.setEmailLogin("manager@test.com");

        when(maProjectRepository.findProjectById("p1")).thenReturn(Optional.of(project));

        ResponseObject<?> res = service.restartProject("p1", req);

        assertEquals(HttpStatus.OK, res.getStatus());
        assertNull(project.getActualEndDate());
        assertEquals(StatusProject.DANG_DIEN_RA, project.getStatusProject());
    }


}
