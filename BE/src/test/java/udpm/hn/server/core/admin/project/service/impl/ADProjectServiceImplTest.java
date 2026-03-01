package udpm.hn.server.core.admin.project.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.batch.BatchAutoConfiguration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import udpm.hn.server.core.admin.department.departmentfacility.model.response.DepartmentFacilityResponse;
import udpm.hn.server.core.admin.project.model.request.ADProjectCreateRequest;
import udpm.hn.server.core.admin.project.model.request.ADProjectSTRequest;
import udpm.hn.server.core.admin.project.model.request.ADProjectSearchRequest;
import udpm.hn.server.core.admin.project.model.response.*;
import udpm.hn.server.core.admin.project.repository.ADProjectRepository;
import udpm.hn.server.core.admin.project.repository.ADProjectStaffRepository;
import udpm.hn.server.core.admin.project.repository.ADProjectStudentRepository;
import udpm.hn.server.core.admin.project.repository.ProjectDFRepository;
import udpm.hn.server.core.common.base.ResponseObject;
import udpm.hn.server.entity.*;
import udpm.hn.server.infrastructure.constant.EntityStatus;
import udpm.hn.server.infrastructure.constant.StatusProject;
import udpm.hn.server.repository.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@EnableAutoConfiguration(exclude = {BatchAutoConfiguration.class})
public class ADProjectServiceImplTest {

    @InjectMocks
    private ADProjectServiceImpl adProjectService;

    @Mock
    private ADProjectRepository adProjectRepository;
    @Mock private ProjectDFRepository projectDFRepository;
    @Mock private MajorFacilityRepository majorFacilityRepository;
    @Mock private CategoryRepository categoryRepository;
    @Mock private ADProjectStudentRepository adProjectStudentRepository;
    @Mock private ADProjectStaffRepository adProjectStaffRepository;
    @Mock private StaffProjectRepository staffProjectRepository;
    @Mock private ProjectRepository projectRepository;
    @Mock private FacilityRepository facilityRepository;

    @Mock
    private ActivityLogRepository activityLogRepository;

    private ADProjectSearchRequest searchRequest;
    private ADProjectCreateRequest createRequest;
    private Project project;
    private MajorFacility majorFacility;
    private Category category;
    private Staff staff;
    private Student student;

    @BeforeEach
    void setup() {
        // setup dữ liệu mẫu cho test
        searchRequest = new ADProjectSearchRequest();
        createRequest = new ADProjectCreateRequest();
        project = new Project();
        majorFacility = new MajorFacility();
        category = new Category();
        staff = new Staff();
        student = new Student();
    }

    @Test
    void testGetAllProject_success() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<ADProjectResponse> projectPage = new PageImpl<>(List.of());

        when(adProjectRepository.getAllProject(any(), any(), any())).thenReturn(projectPage);

        ResponseObject<?> response = adProjectService.getAllProject(searchRequest);

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals("Lấy thông tin project thành công", response.getMessage());
    }

    @Test
    void testCreateProject_onlySaveProject() {
        ADProjectSTRequest member = new ADProjectSTRequest();
        member.setEmail("ngolucky45@gmail.com");
        member.setRole("QUAN_LY");

        ADProjectCreateRequest request = new ADProjectCreateRequest();
        request.setNameProject("Test Project");
        request.setCodeProject("TST01");
        request.setDescriptions("Test Description");
        request.setStartTime(System.currentTimeMillis() + 10_000);
        request.setEndTime(System.currentTimeMillis() + 60_000);
        request.setIdCategory("cat1");
        request.setIdMajorFacility("mf1");
        request.setListMembers(List.of(member));
        request.setEmailLogin("vunvph49243@gmail.com");

        MajorFacility majorFacility = new MajorFacility();
        majorFacility.setId("mf1");

        Category category = new Category();
        category.setId("cat1");

        Staff creator = new Staff();
        creator.setId("staff1");
        creator.setEmailFe("vantruong22082005@gmail.com");

        Staff memberStaff = new Staff();
        memberStaff.setId("staff2");
        memberStaff.setEmailFe("ngolucky45@gmail.com");

        Project savedProject = new Project();
        savedProject.setId("proj1");

        when(projectRepository.findByCode("TST01")).thenReturn(Optional.empty());
        when(majorFacilityRepository.findById("mf1")).thenReturn(Optional.of(majorFacility));
        when(categoryRepository.findById("cat1")).thenReturn(Optional.of(category));
        when(adProjectStaffRepository.findByEmailFe("vantruong22082005@gmail.com")).thenReturn(Optional.of(creator));
        when(adProjectStaffRepository.findByEmailFe("ngolucky45@gmail.com")).thenReturn(Optional.of(memberStaff));
        when(projectRepository.save(any(Project.class))).thenReturn(savedProject);
        when(staffProjectRepository.saveAll(anyList())).thenReturn(new ArrayList<>());
        when(activityLogRepository.save(any())).thenReturn(null);

        ResponseObject<?> response = adProjectService.createProject(request);

        verify(projectRepository, times(1)).save(any(Project.class));
        verify(staffProjectRepository, times(1)).saveAll(anyList());
        verify(activityLogRepository, times(1)).save(any());

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals("Thêm dự án thành công", response.getMessage());
    }


    @Test
    void testCreateProject_majorFacilityNotFound() {
        createRequest.setIdMajorFacility("nonexistent");
        when(majorFacilityRepository.findById("nonexistent")).thenReturn(Optional.empty());

        ResponseObject<?> response = adProjectService.createProject(createRequest);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatus());
        assertEquals("Không tìm thấy chuyên ngành cơ sở", response.getMessage());
    }

    @Test
    void testDeleteProject_success() {
        when(adProjectRepository.findById("project123")).thenReturn(Optional.of(project));
        when(adProjectRepository.save(any(Project.class))).thenReturn(project);

        ResponseObject<?> response = adProjectService.deleteProject("project123");

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals("Xóa thành công dự án", response.getMessage());
    }

    @Test
    void testCountTotalProjects_success() {
        when(adProjectRepository.countTotalProjects()).thenReturn(10L);
        when(adProjectRepository.countOverdueProjects(Mockito.anyInt())).thenReturn(2L);

        when(adProjectRepository.countProjectsGroupedByStatus())
                .thenReturn(List.of(
                        new Object[]{(byte) 0, 3L}, // CHUA_DIEN_RA
                        new Object[]{(byte) 1, 5L}, // DANG_DIEN_RA
                        new Object[]{(byte) 2, 2L}  // DA_DIEN_RA
                ));

        ResponseObject<?> response = adProjectService.countTotalProjects();

        assertEquals(HttpStatus.OK, response.getStatus());
        ADProjectOverview overview = (ADProjectOverview) response.getData();
        assertEquals(10L, overview.getTotalProjects());
        assertEquals(2L, overview.getOverdue());
    }

    @Test
    void testCountProjectsByFacility_success() {
        FacilityProjectCountResponse mockResponse = Mockito.mock(FacilityProjectCountResponse.class);

        when(mockResponse.getFacilityName()).thenReturn("Facility A");
        when(mockResponse.getTotal()).thenReturn(3L);

        when(adProjectRepository.countProjectsByFacility()).thenReturn(List.of(mockResponse));

        ResponseObject<?> response = adProjectService.countProjectsByFacility();

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals("Thống kê tổng số dự án theo cơ sở", response.getMessage());

        List<?> data = (List<?>) response.getData();
        FacilityProjectCountResponse result = (FacilityProjectCountResponse) data.get(0);

        assertEquals("Facility A", result.getFacilityName());
        assertEquals(3L, result.getTotal());
    }


//    @Test
//    void testUpdateProject_projectNotFound() {
//        ADProjectCreateRequest updateRequest = new ADProjectCreateRequest();
//        updateRequest.setIdProject("invalidId");
//
//        Mockito.when(adProjectRepository.findById("invalidId")).thenReturn(Optional.empty());
//
//        ResponseObject<?> response = adProjectService.updateProject(updateRequest);
//
//        assertEquals(HttpStatus.NOT_FOUND, response.getStatus());
//        assertEquals("Không tìm thấy dự án", response.getMessage());
//    }
//
//    @Test
//    void testUpdateProject_success() {
//        ADProjectUpdateRequest updateRequest = new ADProjectUpdateRequest();
//        updateRequest.setIdProject("project1");
//        updateRequest.setIdMajorFacility("123");
//        updateRequest.setIdCategory("cat1");
//        updateRequest.setNameProject("Updated Project");
//
//        ADProjectSTRequest member = new ADProjectSTRequest();
//        member.setEmail("staff@fpt.edu.vn");
//        member.setRole("Lead");
//
//        updateRequest.setListMembers(List.of(member));
//
//        Mockito.when(adProjectRepository.findById("project1")).thenReturn(Optional.of(project));
//        Mockito.when(majorFacilityRepository.findById("123")).thenReturn(Optional.of(majorFacility));
//        Mockito.when(categoryRepository.findById("cat1")).thenReturn(Optional.of(category));
//        Mockito.when(adProjectStaffRepository.findByEmailFe("staff@fpt.edu.vn")).thenReturn(Optional.of(staff));
//        Mockito.when(staffProjectRepository.saveAll(Mockito.anyList())).thenReturn(null);
//        Mockito.when(projectRepository.save(Mockito.any(Project.class))).thenReturn(project);
//
//        ResponseObject<?> response = adProjectService.updateProject(updateRequest);
//
//        assertEquals(HttpStatus.OK, response.getStatus());
//        assertEquals("Cập nhật dự án thành công", response.getMessage());
//    }
//
//    @Test
//    void testUpdateProject_memberNotFound() {
//        ADProjectUpdateRequest updateRequest = new ADProjectUpdateRequest();
//        updateRequest.setIdProject("project1");
//        updateRequest.setIdMajorFacility("123");
//        updateRequest.setIdCategory("cat1");
//
//        ADProjectSTRequest member = new ADProjectSTRequest();
//        member.setEmail("nonexistent@fpt.edu.vn");
//
//        updateRequest.setListMembers(List.of(member));
//
//        Mockito.when(adProjectRepository.findById("project1")).thenReturn(Optional.of(project));
//        Mockito.when(majorFacilityRepository.findById("123")).thenReturn(Optional.of(majorFacility));
//        Mockito.when(categoryRepository.findById("cat1")).thenReturn(Optional.of(category));
//        Mockito.when(adProjectStaffRepository.findByEmailFe("nonexistent@fpt.edu.vn")).thenReturn(Optional.empty());
//
//        ResponseObject<?> response = adProjectService.updateProject(updateRequest);
//
//        assertEquals(HttpStatus.NOT_FOUND, response.getStatus());
//        assertEquals("Không tìm thấy thành viên", response.getMessage());
//    }

    @Test
    void testGetAllDepartmentFacility_success() {
        // Mock đúng interface ADProjectDFResponse
        ADProjectDFResponse mockResponse = Mockito.mock(ADProjectDFResponse.class);
        when(mockResponse.getId()).thenReturn("abc123");
        when(mockResponse.getNameMajor()).thenReturn("CNTT");
        when(mockResponse.getNameDepartment()).thenReturn("Khoa học máy tính");

        // Trả về danh sách mock
        List<ADProjectDFResponse> mockList = List.of(mockResponse);
        when(projectDFRepository.getAllDepartmentFacility("123")).thenReturn(mockList);

        // Gọi service
        ResponseObject<?> response = adProjectService.getAllDepartmentFacility("123");

        // Kiểm tra kết quả
        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals("Lấy danh sách bộ môn cơ sở thành công", response.getMessage());

        List<?> dataList = (List<?>) response.getData();
        assertEquals(1, dataList.size());

        ADProjectDFResponse result = (ADProjectDFResponse) dataList.get(0);
        assertEquals("abc123", result.getId());
        assertEquals("CNTT", result.getNameMajor());
        assertEquals("Khoa học máy tính", result.getNameDepartment());
    }


    @Test
    void testCountProjectsByDepartment_success() {
        DepartmentProjectCountResponse mockResponse = Mockito.mock(DepartmentProjectCountResponse.class);

        Mockito.when(mockResponse.getDepartmentName()).thenReturn("Dept A");
        Mockito.when(mockResponse.getTotal()).thenReturn(3L);

        List<DepartmentProjectCountResponse> mockList = List.of(mockResponse);
        Mockito.when(adProjectRepository.countProjectsByDepartment()).thenReturn(mockList);

        ResponseObject<?> response = adProjectService.countProjectsByDepartment();

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals("Thống kê tổng số dự án theo bộ môn", response.getMessage());

        List<?> dataList = (List<?>) response.getData();
        assertEquals(1, dataList.size());

        DepartmentProjectCountResponse result = (DepartmentProjectCountResponse) dataList.get(0);
        assertEquals("Dept A", result.getDepartmentName());
        assertEquals(3L, result.getTotal());
    }


    @Test
    void testGetProjectTodoCounts_success() {
        ADProjectTodoCountResponse project1 = Mockito.mock(ADProjectTodoCountResponse.class);

        Mockito.when(project1.getProjectName()).thenReturn("Project A");
        Mockito.when(project1.getTotalTodo()).thenReturn(10);

        List<ADProjectTodoCountResponse> todoList = List.of(project1);

        Mockito.when(adProjectRepository.countTodoByProjectName()).thenReturn(todoList);

        // Gọi service
        ResponseObject<?> response = adProjectService.getProjectTodoCounts();

        // Kiểm tra kết quả
        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals("Thống kê số lượng công việc theo dự án thành công", response.getMessage());

        List<?> resultData = (List<?>) response.getData();
        assertEquals(1, resultData.size());

        ADProjectTodoCountResponse result = (ADProjectTodoCountResponse) resultData.get(0);
        assertEquals("Project A", result.getProjectName());
        assertEquals(10, result.getTotalTodo());
    }

    @Test
    void testCreateProject_codeExist() {
        ADProjectCreateRequest req = new ADProjectCreateRequest();
        req.setCodeProject("PJ1");
        when(projectRepository.findByCode("PJ1")).thenReturn(Optional.of(new Project()));
        ResponseObject<?> res = adProjectService.createProject(req);
        assertEquals(HttpStatus.NOT_FOUND, res.getStatus());
    }

    @Test
    void testCreateProject_categoryNotFound() {
        ADProjectCreateRequest req = new ADProjectCreateRequest();
        req.setCodeProject("PJ2");
        req.setIdMajorFacility("mf1");
        req.setIdCategory("cat1");
        lenient().when(projectRepository.findByCode("PJ2"))
                .thenReturn(Optional.empty());
        lenient().when(majorFacilityRepository.findById("mf1"))
                .thenReturn(Optional.of(new MajorFacility()));
        when(categoryRepository.findById("cat1")).thenReturn(Optional.empty());
        ResponseObject<?> res = adProjectService.createProject(req);
        assertEquals(HttpStatus.NOT_FOUND, res.getStatus());
    }

    @Test
    void testDetailProject_notFound() {
        when(adProjectRepository.findById("notfound")).thenReturn(Optional.empty());
        ResponseObject<?> res = adProjectService.detailProject("notfound");
        assertEquals(HttpStatus.NOT_FOUND, res.getStatus());
    }
    @Test
    void testCountProjectByStatus_empty() {
        when(adProjectRepository.countProjectByStatus()).thenReturn(new ArrayList<>());
        ResponseObject<?> res = adProjectService.countProjectByStatus();
        assertEquals(HttpStatus.OK, res.getStatus());
    }

    @Test
    void testGetProjectParticipantCounts_empty() {
        when(adProjectRepository.getProjectParticipantCounts()).thenReturn(new ArrayList<>());
        ResponseObject<?> res = adProjectService.getProjectParticipantCounts();
        assertEquals(HttpStatus.OK, res.getStatus());
    }
    @Test
    void testGetProjectTodoCounts_exceptionHandled() {
        when(adProjectRepository.countTodoByProjectName())
                .thenThrow(new RuntimeException("err"));

        ResponseObject<?> res = adProjectService.getProjectTodoCounts();
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, res.getStatus());
        assertEquals("Lỗi khi thống kê công việc", res.getMessage());
        assertNull(res.getData());
    }


    @Test
    void testUpdateProject_notFound() {
        ADProjectCreateRequest req = new ADProjectCreateRequest();
        req.setCodeProject("CODE123");
        req.setIdMajorFacility("mf1");
        req.setIdCategory("c1");
        req.setStartTime(Long.valueOf(System.currentTimeMillis() + 1000));
        req.setEndTime(Long.valueOf(System.currentTimeMillis() + 2000));
        req.setListMembers(Collections.emptyList());
        when(projectRepository.findById("pid")).thenReturn(Optional.empty());

        ResponseObject<?> res = adProjectService.updateProject("pid", req);

        assertEquals(HttpStatus.NOT_FOUND, res.getStatus());
    }


    @Test
    void testUpdateProject_codeDuplicate() {
        Project project = new Project();
        project.setCode("ABC");
        ADProjectCreateRequest req = new ADProjectCreateRequest();
        req.setCodeProject("DEF"); // khác code hiện tại
        when(projectRepository.findById("pid")).thenReturn(Optional.of(project));
        when(projectRepository.findByCode("DEF")).thenReturn(Optional.of(new Project()));
        ResponseObject<?> res = adProjectService.updateProject("pid", req);
        assertEquals(HttpStatus.CONFLICT, res.getStatus());
    }

    @Test
    void testUpdateProject_memberNotFound() {
        Project project = new Project();
        project.setCode("ABC");
        ADProjectCreateRequest req = new ADProjectCreateRequest();
        req.setCodeProject("ABC");
        req.setIdMajorFacility("mf");
        req.setIdCategory("cat");
        ADProjectSTRequest member = new ADProjectSTRequest();
        member.setEmail("notfound@test.com");
        req.setListMembers(List.of(member));
        when(projectRepository.findById("pid")).thenReturn(Optional.of(project));
        when(majorFacilityRepository.findById("mf")).thenReturn(Optional.of(new MajorFacility()));
        when(categoryRepository.findById("cat")).thenReturn(Optional.of(new Category()));
        when(adProjectStaffRepository.findByEmailFe("notfound@test.com")).thenReturn(Optional.empty());
        when(adProjectStaffRepository.findByEmailFpt("notfound@test.com")).thenReturn(Optional.empty());
        when(adProjectStudentRepository.findByEmail("notfound@test.com")).thenReturn(Optional.empty());
        ResponseObject<?> res = adProjectService.updateProject("pid", req);
        assertEquals(HttpStatus.NOT_FOUND, res.getStatus());
    }
    @Test
    void testCountTotalProjects_emptyList() {
        when(adProjectRepository.countTotalProjects()).thenReturn(0L);
        when(adProjectRepository.countOverdueProjects(anyInt())).thenReturn(0L);
        when(adProjectRepository.countProjectsGroupedByStatus()).thenReturn(Collections.emptyList());
        ResponseObject<?> res = adProjectService.countTotalProjects();
        assertEquals(HttpStatus.OK, res.getStatus());
    }
    @Test
    void testCountProjectsByFacility_empty() {
        when(adProjectRepository.countProjectsByFacility()).thenReturn(Collections.emptyList());
        ResponseObject<?> res = adProjectService.countProjectsByFacility();
        assertEquals(HttpStatus.OK, res.getStatus());
    }

    @Test
    void testCountProjectsByDepartment_empty() {
        when(adProjectRepository.countProjectsByDepartment()).thenReturn(Collections.emptyList());
        ResponseObject<?> res = adProjectService.countProjectsByDepartment();
        assertEquals(HttpStatus.OK, res.getStatus());
    }

    @Test
    void testCountTaskByStatusAndProject_empty() {
        when(adProjectRepository.countTaskByStatusAndProject()).thenReturn(Collections.emptyList());
        ResponseObject<?> res = adProjectService.countTaskByStatusAndProject();
        assertEquals(HttpStatus.OK, res.getStatus());
    }
    @Test
    void testGetAllFacility_success() {
        Facility facility = new Facility();
        facility.setId("f1");
        facility.setName("Facility 1");
        when(facilityRepository.findAll()).thenReturn(List.of(facility));

        ResponseObject<?> res = adProjectService.getAllFacility();

        assertEquals(HttpStatus.OK, res.getStatus());
        List<?> data = (List<?>) res.getData();
        assertEquals(1, data.size());
        Facility result = (Facility) data.get(0);
        assertEquals("Facility 1", result.getName());
    }

    @Test
    void testDeleteProject_notFound() {
        when(adProjectRepository.findById("idX")).thenReturn(Optional.empty());
        ResponseObject<?> res = adProjectService.deleteProject("idX");
        assertEquals(HttpStatus.NOT_FOUND, res.getStatus());
    }


    @Test
    void testCountTaskByStatusAndProject_withData() {
        TodoStatusByProject mockResp = Mockito.mock(TodoStatusByProject.class);
        when(mockResp.getProjectName()).thenReturn("P1");

        when(adProjectRepository.countTaskByStatusAndProject())
                .thenReturn(List.of(mockResp));

        ResponseObject<?> res = adProjectService.countTaskByStatusAndProject();
        assertEquals(HttpStatus.OK, res.getStatus());
        List<?> data = (List<?>) res.getData();
        assertEquals(1, data.size());
        TodoStatusByProject result = (TodoStatusByProject) data.get(0);
        assertEquals("P1", result.getProjectName());
    }

    @Test
    void testCreateProject_memberEmailEmpty() {
        ADProjectSTRequest member = new ADProjectSTRequest();
        member.setEmail(""); // email rỗng
        ADProjectCreateRequest req = new ADProjectCreateRequest();
        req.setCodeProject("P10");
        req.setIdMajorFacility("mf");
        req.setIdCategory("cat");
        req.setListMembers(List.of(member));

        when(projectRepository.findByCode("P10")).thenReturn(Optional.empty());
        when(majorFacilityRepository.findById("mf")).thenReturn(Optional.of(new MajorFacility()));
        when(categoryRepository.findById("cat")).thenReturn(Optional.of(new Category()));
        when(projectRepository.save(any(Project.class))).thenReturn(new Project());
        when(staffProjectRepository.saveAll(anyList())).thenReturn(Collections.emptyList());

        ResponseObject<?> res = adProjectService.createProject(req);
        assertEquals(HttpStatus.OK, res.getStatus());
    }

    @Test
    void testUpdateProject_memberEmailEmpty() {
        Project project = new Project();
        project.setCode("ABC");
        ADProjectSTRequest member = new ADProjectSTRequest();
        member.setEmail(""); // email rỗng
        ADProjectCreateRequest req = new ADProjectCreateRequest();
        req.setCodeProject("ABC");
        req.setIdMajorFacility("mf");
        req.setIdCategory("cat");
        req.setListMembers(List.of(member));

        when(projectRepository.findById("pid")).thenReturn(Optional.of(project));
        when(majorFacilityRepository.findById("mf")).thenReturn(Optional.of(new MajorFacility()));
        when(categoryRepository.findById("cat")).thenReturn(Optional.of(new Category()));

        ResponseObject<?> res = adProjectService.updateProject("pid", req);
        assertEquals(HttpStatus.OK, res.getStatus());
    }


    @Test
    void testUpdateProject_existingStudentUpdated() {
        Project project = new Project();
        project.setCode("ABC");
        Student student = new Student();
        student.setEmail("stu@fpt.edu.vn");

        ADProjectSTRequest member = new ADProjectSTRequest();
        member.setEmail("stu@fpt.edu.vn");
        member.setRole("DEV");

        ADProjectCreateRequest req = new ADProjectCreateRequest();
        req.setCodeProject("ABC");
        req.setIdMajorFacility("mf");
        req.setIdCategory("cat");
        req.setListMembers(List.of(member));

        StaffProject existingSP = new StaffProject();
        existingSP.setStudent(student);
        existingSP.setProject(project);

        when(projectRepository.findById("pid")).thenReturn(Optional.of(project));
        when(majorFacilityRepository.findById("mf")).thenReturn(Optional.of(new MajorFacility()));
        when(categoryRepository.findById("cat")).thenReturn(Optional.of(new Category()));
        when(adProjectStaffRepository.findByEmailFe("stu@fpt.edu.vn")).thenReturn(Optional.empty());
        when(adProjectStaffRepository.findByEmailFpt("stu@fpt.edu.vn")).thenReturn(Optional.empty());
        when(adProjectStudentRepository.findByEmail("stu@fpt.edu.vn")).thenReturn(Optional.of(student));
        when(staffProjectRepository.findByStudentAndProject(student, project)).thenReturn(Optional.of(existingSP));

        ResponseObject<?> res = adProjectService.updateProject("pid", req);
        assertEquals(HttpStatus.OK, res.getStatus());
    }

    @Test
    void testCreateProject_memberStaffNotFoundButStudentFound() {
        ADProjectSTRequest member = new ADProjectSTRequest();
        member.setEmail("student@fpt.edu.vn");
        member.setRole("DEV");

        ADProjectCreateRequest req = new ADProjectCreateRequest();
        req.setCodeProject("P99");
        req.setIdMajorFacility("mf");
        req.setIdCategory("cat");
        req.setListMembers(List.of(member));

        Student student = new Student();
        student.setEmail("student@fpt.edu.vn");

        when(projectRepository.findByCode("P99")).thenReturn(Optional.empty());
        when(majorFacilityRepository.findById("mf")).thenReturn(Optional.of(new MajorFacility()));
        when(categoryRepository.findById("cat")).thenReturn(Optional.of(new Category()));
        when(adProjectStaffRepository.findByEmailFe("student@fpt.edu.vn")).thenReturn(Optional.empty());
        when(adProjectStaffRepository.findByEmailFpt("student@fpt.edu.vn")).thenReturn(Optional.empty());
        when(adProjectStudentRepository.findByEmail("student@fpt.edu.vn")).thenReturn(Optional.of(student));
        when(projectRepository.save(any(Project.class))).thenReturn(new Project());
        when(staffProjectRepository.saveAll(anyList())).thenReturn(Collections.emptyList());

        ResponseObject<?> res = adProjectService.createProject(req);
        assertEquals(HttpStatus.OK, res.getStatus());
    }

    @Test
    void testCreateProject_memberStaffAndStudentNotFound() {
        ADProjectSTRequest member = new ADProjectSTRequest();
        member.setEmail("unknown@fpt.edu.vn");

        ADProjectCreateRequest req = new ADProjectCreateRequest();
        req.setCodeProject("P100");
        req.setIdMajorFacility("mf");
        req.setIdCategory("cat");
        req.setListMembers(List.of(member));

        when(projectRepository.findByCode("P100")).thenReturn(Optional.empty());
        when(majorFacilityRepository.findById("mf")).thenReturn(Optional.of(new MajorFacility()));
        when(categoryRepository.findById("cat")).thenReturn(Optional.of(new Category()));
        when(adProjectStaffRepository.findByEmailFe("unknown@fpt.edu.vn")).thenReturn(Optional.empty());
        when(adProjectStaffRepository.findByEmailFpt("unknown@fpt.edu.vn")).thenReturn(Optional.empty());
        when(adProjectStudentRepository.findByEmail("unknown@fpt.edu.vn")).thenReturn(Optional.empty());
        when(projectRepository.save(any(Project.class))).thenReturn(new Project());
        when(staffProjectRepository.saveAll(anyList())).thenReturn(Collections.emptyList());

        ResponseObject<?> res = adProjectService.createProject(req);
        assertEquals(HttpStatus.OK, res.getStatus()); // vẫn thành công, member bị skip
    }

    @Test
    void testUpdateProject_removeOldMember() {
        Project project = new Project();
        project.setCode("ABC");
        project.setId("pid");

        Staff staff = new Staff();
        staff.setEmailFe("staff@fpt.edu.vn");

        ADProjectSTRequest member = new ADProjectSTRequest();
        member.setEmail("new@fpt.edu.vn");
        member.setRole("DEV");

        ADProjectCreateRequest req = new ADProjectCreateRequest();
        req.setCodeProject("ABC");
        req.setIdMajorFacility("mf");
        req.setIdCategory("cat");
        req.setListMembers(List.of(member));

        StaffProject oldMember = new StaffProject();
        oldMember.setProject(project);
        oldMember.setStaff(staff);

        when(projectRepository.findById("pid")).thenReturn(Optional.of(project));
        when(majorFacilityRepository.findById("mf")).thenReturn(Optional.of(new MajorFacility()));
        when(categoryRepository.findById("cat")).thenReturn(Optional.of(new Category()));
        when(adProjectStaffRepository.findByEmailFe("new@fpt.edu.vn")).thenReturn(Optional.of(new Staff()));
        when(staffProjectRepository.findByProject(project)).thenReturn(List.of(oldMember));

        ResponseObject<?> res = adProjectService.updateProject("pid", req);
        assertEquals(HttpStatus.OK, res.getStatus());
        verify(staffProjectRepository, atLeastOnce()).saveAll(anyList()); // old member bị set INACTIVE
    }

    @Test
    void testUpdateProject_newStaffSavedWhenNotExistBefore() {
        Project project = new Project();
        project.setCode("ABC");

        Staff staff = new Staff();
        staff.setEmailFe("staff@fpt.edu.vn");

        ADProjectSTRequest member = new ADProjectSTRequest();
        member.setEmail("staff@fpt.edu.vn");
        member.setRole("DEV");

        ADProjectCreateRequest req = new ADProjectCreateRequest();
        req.setCodeProject("ABC");
        req.setIdMajorFacility("mf");
        req.setIdCategory("cat");
        req.setListMembers(List.of(member));

        when(projectRepository.findById("pid")).thenReturn(Optional.of(project));
        when(majorFacilityRepository.findById("mf")).thenReturn(Optional.of(new MajorFacility()));
        when(categoryRepository.findById("cat")).thenReturn(Optional.of(new Category()));
        when(adProjectStaffRepository.findByEmailFe("staff@fpt.edu.vn")).thenReturn(Optional.of(staff));
        when(staffProjectRepository.findByStaffAndProject(staff, project)).thenReturn(Optional.empty());

        ResponseObject<?> res = adProjectService.updateProject("pid", req);
        assertEquals(HttpStatus.OK, res.getStatus());
        verify(staffProjectRepository, atLeastOnce()).save(any(StaffProject.class));
    }
    @Test
    void testCountProjectByStatus_withData() {
        List<Object[]> mockList = new ArrayList<>();
        mockList.add(new Object[]{"DANG_DIEN_RA", 5L});

        when(adProjectRepository.countProjectByStatus()).thenReturn(mockList);

        ResponseObject<?> res = adProjectService.countProjectByStatus();

        assertEquals(HttpStatus.OK, res.getStatus());
        Map<?, ?> map = (Map<?, ?>) res.getData();
        assertTrue(map.containsKey("DANG_DIEN_RA"));
        assertEquals(5L, map.get("DANG_DIEN_RA"));
    }

    @Test
    void testGetProjectParticipantCounts_withData() {
        List<Object[]> mockList = new ArrayList<>();
        mockList.add(new Object[]{"Project A", 10L});

        when(adProjectRepository.getProjectParticipantCounts()).thenReturn(mockList);

        ResponseObject<?> res = adProjectService.getProjectParticipantCounts();

        assertEquals(HttpStatus.OK, res.getStatus());
        List<?> data = (List<?>) res.getData();
        Map<?, ?> item = (Map<?, ?>) data.get(0);
        assertEquals("Project A", item.get("projectName"));
        assertEquals(10L, item.get("totalParticipants"));
    }

    @Test
    void testUpdateProject_deleteOldMembers() {
        Project project = new Project();
        project.setCode("ABC");
        ADProjectCreateRequest req = new ADProjectCreateRequest();
        req.setCodeProject("ABC");
        req.setIdMajorFacility("mf");
        req.setIdCategory("cat");
        req.setListMembers(List.of()); // không có thành viên mới

        StaffProject oldSp = new StaffProject();
        Staff oldStaff = new Staff();
        oldStaff.setEmailFe("old@test.com");
        oldSp.setStaff(oldStaff);
        oldSp.setProject(project);

        when(projectRepository.findById("pid")).thenReturn(Optional.of(project));
        when(majorFacilityRepository.findById("mf")).thenReturn(Optional.of(new MajorFacility()));
        when(categoryRepository.findById("cat")).thenReturn(Optional.of(new Category()));
        when(staffProjectRepository.findByProject(project)).thenReturn(List.of(oldSp));
        when(projectRepository.save(any(Project.class))).thenReturn(project);

        ResponseObject<?> res = adProjectService.updateProject("pid", req);

        assertEquals(HttpStatus.OK, res.getStatus());
        verify(staffProjectRepository, times(1)).saveAll(anyList()); // old member bị inactive
    }

    @Test
    void testDetailProject_found() {
        Project project = new Project();
        project.setId("p1");
        project.setName("Demo Project");

        when(adProjectRepository.findById("p1")).thenReturn(Optional.of(project));

        ResponseObject<?> res = adProjectService.detailProject("p1");

        assertEquals(HttpStatus.OK, res.getStatus());
        assertEquals("Lấy thông tin dự án thành công!", res.getMessage());
        Project data = (Project) res.getData();
        assertEquals("Demo Project", data.getName());
    }
    @Test
    void testCreateProject_withStudentMember() {
        ADProjectSTRequest member = new ADProjectSTRequest();
        member.setEmail("student@test.com");
        member.setRole("DEV");

        ADProjectCreateRequest req = new ADProjectCreateRequest();
        req.setCodeProject("CP01");
        req.setNameProject("Project CP01");
        req.setIdMajorFacility("mf1");
        req.setIdCategory("cat1");
        req.setStartTime(System.currentTimeMillis() - 5000);
        req.setEndTime(System.currentTimeMillis() + 5000);
        req.setListMembers(List.of(member));
        req.setEmailLogin("admin@test.com");

        Project project = new Project();
        project.setId("p1");

        when(projectRepository.findByCode("CP01")).thenReturn(Optional.empty());
        when(majorFacilityRepository.findById("mf1")).thenReturn(Optional.of(new MajorFacility()));
        when(categoryRepository.findById("cat1")).thenReturn(Optional.of(new Category()));
        when(projectRepository.save(any(Project.class))).thenReturn(project);
        when(adProjectStaffRepository.findByEmailFe("student@test.com")).thenReturn(Optional.empty());
        when(adProjectStaffRepository.findByEmailFpt("student@test.com")).thenReturn(Optional.empty());
        when(adProjectStudentRepository.findByEmail("student@test.com")).thenReturn(Optional.of(new Student()));
        when(staffProjectRepository.saveAll(anyList())).thenReturn(Collections.emptyList());
        when(activityLogRepository.save(any())).thenReturn(new ActivityLog());

        ResponseObject<?> res = adProjectService.createProject(req);
        assertEquals(HttpStatus.OK, res.getStatus());
        assertEquals("Thêm dự án thành công", res.getMessage());
    }

    @Test
    void testUpdateProject_success() {
        Project existing = new Project();
        existing.setCode("OLD");
        existing.setId("p1");

        ADProjectSTRequest member = new ADProjectSTRequest();
        member.setEmail("staff@test.com");
        member.setRole("TESTER");

        ADProjectCreateRequest req = new ADProjectCreateRequest();
        req.setCodeProject("OLD"); // giữ nguyên code để qua check duplicate
        req.setIdMajorFacility("mf1");
        req.setIdCategory("cat1");
        req.setStartTime(System.currentTimeMillis() - 1000);
        req.setEndTime(System.currentTimeMillis() + 1000);
        req.setNameProject("Updated Project");
        req.setListMembers(List.of(member));
        req.setEmailLogin("admin@test.com");

        when(projectRepository.findById("p1")).thenReturn(Optional.of(existing));
        when(majorFacilityRepository.findById("mf1")).thenReturn(Optional.of(new MajorFacility()));
        when(categoryRepository.findById("cat1")).thenReturn(Optional.of(new Category()));
        when(adProjectStaffRepository.findByEmailFe("staff@test.com")).thenReturn(Optional.of(new Staff()));
        when(staffProjectRepository.findByProject(existing)).thenReturn(Collections.emptyList());
        when(staffProjectRepository.saveAll(anyList())).thenReturn(Collections.emptyList());
        when(projectRepository.save(any(Project.class))).thenReturn(existing);
        when(activityLogRepository.save(any())).thenReturn(new ActivityLog());

        ResponseObject<?> res = adProjectService.updateProject("p1", req);

        assertEquals(HttpStatus.OK, res.getStatus());
        assertEquals("Cập nhật dự án thành công", res.getMessage());
    }

    @Test
    void testUpdateProject_memberEmailNull() {
        Project existing = new Project();
        existing.setCode("CODE");
        existing.setId("p1");

        ADProjectSTRequest member = new ADProjectSTRequest();
        member.setEmail(null); // email null => skip

        ADProjectCreateRequest req = new ADProjectCreateRequest();
        req.setCodeProject("CODE");
        req.setIdMajorFacility("mf1");
        req.setIdCategory("cat1");
        req.setStartTime(System.currentTimeMillis());
        req.setEndTime(System.currentTimeMillis() + 1000);
        req.setListMembers(List.of(member));

        when(projectRepository.findById("p1")).thenReturn(Optional.of(existing));
        when(majorFacilityRepository.findById("mf1")).thenReturn(Optional.of(new MajorFacility()));
        when(categoryRepository.findById("cat1")).thenReturn(Optional.of(new Category()));
        when(staffProjectRepository.findByProject(existing)).thenReturn(Collections.emptyList());
        when(projectRepository.save(any(Project.class))).thenReturn(existing);

        ResponseObject<?> res = adProjectService.updateProject("p1", req);
        assertEquals(HttpStatus.OK, res.getStatus());
    }

    @Test
    void testUpdateProject_existingStaffUpdated() {
        Project existing = new Project();
        existing.setCode("CODE");
        existing.setId("p1");

        Staff staff = new Staff();
        staff.setEmailFe("staff@test.com");
        StaffProject oldSP = new StaffProject();
        oldSP.setStaff(staff);

        ADProjectSTRequest member = new ADProjectSTRequest();
        member.setEmail("staff@test.com");
        member.setRole("DEV");

        ADProjectCreateRequest req = new ADProjectCreateRequest();
        req.setCodeProject("CODE");
        req.setIdMajorFacility("mf1");
        req.setIdCategory("cat1");
        req.setStartTime(System.currentTimeMillis());
        req.setEndTime(System.currentTimeMillis() + 1000);
        req.setListMembers(List.of(member));

        when(projectRepository.findById("p1")).thenReturn(Optional.of(existing));
        when(majorFacilityRepository.findById("mf1")).thenReturn(Optional.of(new MajorFacility()));
        when(categoryRepository.findById("cat1")).thenReturn(Optional.of(new Category()));
        when(adProjectStaffRepository.findByEmailFe("staff@test.com")).thenReturn(Optional.of(staff));
        when(staffProjectRepository.findByProject(existing)).thenReturn(Collections.emptyList());
        when(staffProjectRepository.findByStaffAndProject(staff, existing)).thenReturn(Optional.of(oldSP));
        when(projectRepository.save(any(Project.class))).thenReturn(existing);

        ResponseObject<?> res = adProjectService.updateProject("p1", req);
        assertEquals(HttpStatus.OK, res.getStatus());
    }

    @Test
    void testUpdateProject_removeInactiveMember() {
        Project existing = new Project();
        existing.setCode("CODE");
        existing.setId("p1");

        Staff staff = new Staff();
        staff.setEmailFe("old@test.com");
        StaffProject oldSP = new StaffProject();
        oldSP.setStaff(staff);

        ADProjectSTRequest member = new ADProjectSTRequest();
        member.setEmail("new@test.com");
        member.setRole("DEV");

        ADProjectCreateRequest req = new ADProjectCreateRequest();
        req.setCodeProject("CODE");
        req.setIdMajorFacility("mf1");
        req.setIdCategory("cat1");
        req.setStartTime(System.currentTimeMillis());
        req.setEndTime(System.currentTimeMillis() + 1000);
        req.setListMembers(List.of(member));

        when(projectRepository.findById("p1")).thenReturn(Optional.of(existing));
        when(majorFacilityRepository.findById("mf1")).thenReturn(Optional.of(new MajorFacility()));
        when(categoryRepository.findById("cat1")).thenReturn(Optional.of(new Category()));
        when(adProjectStaffRepository.findByEmailFe("new@test.com")).thenReturn(Optional.of(new Staff()));
        when(staffProjectRepository.findByProject(existing)).thenReturn(List.of(oldSP));
        when(staffProjectRepository.saveAll(anyList())).thenReturn(Collections.emptyList());
        when(projectRepository.save(any(Project.class))).thenReturn(existing);

        ResponseObject<?> res = adProjectService.updateProject("p1", req);
        assertEquals(HttpStatus.OK, res.getStatus());
    }

    @Test
    void testCreateProject_roleDEV() {
        ADProjectSTRequest member = new ADProjectSTRequest();
        member.setEmail("dev@test.com");
        member.setRole("DEV");

        ADProjectCreateRequest req = new ADProjectCreateRequest();
        req.setNameProject("Proj DEV");
        req.setCodeProject("CDEV");
        req.setIdMajorFacility("mf1");
        req.setIdCategory("cat1");
        req.setStartTime(System.currentTimeMillis() + 1000);
        req.setEndTime(System.currentTimeMillis() + 5000);
        req.setListMembers(List.of(member));

        when(projectRepository.findByCode("CDEV")).thenReturn(Optional.empty());
        when(majorFacilityRepository.findById("mf1")).thenReturn(Optional.of(new MajorFacility()));
        when(categoryRepository.findById("cat1")).thenReturn(Optional.of(new Category()));
        when(adProjectStaffRepository.findByEmailFe("dev@test.com")).thenReturn(Optional.of(new Staff()));
        when(projectRepository.save(any())).thenReturn(new Project());

        ResponseObject<?> res = adProjectService.createProject(req);
        assertEquals(HttpStatus.OK, res.getStatus());
    }

    @Test
    void testCreateProject_roleTESTER() {
        ADProjectSTRequest member = new ADProjectSTRequest();
        member.setEmail("tester@test.com");
        member.setRole("ANYTHING"); // không phải QUAN_Li hay DEV -> mặc định TESTER

        ADProjectCreateRequest req = new ADProjectCreateRequest();
        req.setNameProject("Proj Tester");
        req.setCodeProject("CTEST");
        req.setIdMajorFacility("mf1");
        req.setIdCategory("cat1");
        req.setStartTime(System.currentTimeMillis() + 1000);
        req.setEndTime(System.currentTimeMillis() + 5000);
        req.setListMembers(List.of(member));

        when(projectRepository.findByCode("CTEST")).thenReturn(Optional.empty());
        when(majorFacilityRepository.findById("mf1")).thenReturn(Optional.of(new MajorFacility()));
        when(categoryRepository.findById("cat1")).thenReturn(Optional.of(new Category()));
        when(adProjectStaffRepository.findByEmailFe("tester@test.com")).thenReturn(Optional.of(new Staff()));
        when(projectRepository.save(any())).thenReturn(new Project());

        ResponseObject<?> res = adProjectService.createProject(req);
        assertEquals(HttpStatus.OK, res.getStatus());
    }

    @Test
    void testUpdateProject_roleQUANLI() {
        Project existing = new Project();
        existing.setCode("CUPD");
        existing.setId("p1");

        ADProjectSTRequest member = new ADProjectSTRequest();
        member.setEmail("quanli@test.com");
        member.setRole("QUAN_Li"); // sẽ gán role QUAN_Li

        ADProjectCreateRequest req = new ADProjectCreateRequest();
        req.setCodeProject("CUPD");
        req.setIdMajorFacility("mf1");
        req.setIdCategory("cat1");
        req.setStartTime(System.currentTimeMillis());
        req.setEndTime(System.currentTimeMillis() + 1000);
        req.setListMembers(List.of(member));

        when(projectRepository.findById("p1")).thenReturn(Optional.of(existing));
        when(majorFacilityRepository.findById("mf1")).thenReturn(Optional.of(new MajorFacility()));
        when(categoryRepository.findById("cat1")).thenReturn(Optional.of(new Category()));
        when(adProjectStaffRepository.findByEmailFe("quanli@test.com")).thenReturn(Optional.of(new Staff()));
        when(staffProjectRepository.findByProject(existing)).thenReturn(Collections.emptyList());
        when(projectRepository.save(any())).thenReturn(existing);

        ResponseObject<?> res = adProjectService.updateProject("p1", req);
        assertEquals(HttpStatus.OK, res.getStatus());
    }

    @Test
    void testUpdateProject_roleTESTERDefault() {
        Project existing = new Project();
        existing.setCode("CUPD2");
        existing.setId("p2");

        ADProjectSTRequest member = new ADProjectSTRequest();
        member.setEmail("testerupd@test.com");
        member.setRole("SOMETHING"); // không khớp => TESTER

        ADProjectCreateRequest req = new ADProjectCreateRequest();
        req.setCodeProject("CUPD2");
        req.setIdMajorFacility("mf1");
        req.setIdCategory("cat1");
        req.setStartTime(System.currentTimeMillis());
        req.setEndTime(System.currentTimeMillis() + 1000);
        req.setListMembers(List.of(member));

        when(projectRepository.findById("p2")).thenReturn(Optional.of(existing));
        when(majorFacilityRepository.findById("mf1")).thenReturn(Optional.of(new MajorFacility()));
        when(categoryRepository.findById("cat1")).thenReturn(Optional.of(new Category()));
        when(adProjectStaffRepository.findByEmailFe("testerupd@test.com")).thenReturn(Optional.of(new Staff()));
        when(staffProjectRepository.findByProject(existing)).thenReturn(Collections.emptyList());
        when(projectRepository.save(any())).thenReturn(existing);

        ResponseObject<?> res = adProjectService.updateProject("p2", req);
        assertEquals(HttpStatus.OK, res.getStatus());
    }
    @Test
    void testUpdateProject_statusChuaDienRa() {
        Project project = new Project();
        project.setCode("UPD1");

        ADProjectCreateRequest req = new ADProjectCreateRequest();
        req.setCodeProject("UPD1");
        req.setIdMajorFacility("mf1");
        req.setIdCategory("cat1");
        req.setStartTime(System.currentTimeMillis() + 10_000);
        req.setEndTime(System.currentTimeMillis() + 20_000);
        req.setListMembers(Collections.emptyList());

        when(projectRepository.findById("pid")).thenReturn(Optional.of(project));
        when(majorFacilityRepository.findById("mf1")).thenReturn(Optional.of(new MajorFacility()));
        when(categoryRepository.findById("cat1")).thenReturn(Optional.of(new Category()));
        when(staffProjectRepository.findByProject(project)).thenReturn(Collections.emptyList());

        ResponseObject<?> res = adProjectService.updateProject("pid", req);
        assertEquals(HttpStatus.OK, res.getStatus());
        assertEquals(StatusProject.CHUA_DIEN_RA, project.getStatusProject());
    }

    @Test
    void testUpdateProject_statusDangDienRa() {
        long now = System.currentTimeMillis();
        Project project = new Project();
        project.setCode("UPD2");

        ADProjectCreateRequest req = new ADProjectCreateRequest();
        req.setCodeProject("UPD2");
        req.setIdMajorFacility("mf1");
        req.setIdCategory("cat1");
        req.setStartTime(now - 1000);
        req.setEndTime(now + 2000);
        req.setListMembers(Collections.emptyList());

        when(projectRepository.findById("pid2")).thenReturn(Optional.of(project));
        when(majorFacilityRepository.findById("mf1")).thenReturn(Optional.of(new MajorFacility()));
        when(categoryRepository.findById("cat1")).thenReturn(Optional.of(new Category()));
        when(staffProjectRepository.findByProject(project)).thenReturn(Collections.emptyList());

        ResponseObject<?> res = adProjectService.updateProject("pid2", req);
        assertEquals(StatusProject.DANG_DIEN_RA, project.getStatusProject());
    }

    @Test
    void testUpdateProject_statusDaDienRa() {
        long now = System.currentTimeMillis();
        Project project = new Project();
        project.setCode("UPD3");

        ADProjectCreateRequest req = new ADProjectCreateRequest();
        req.setCodeProject("UPD3");
        req.setIdMajorFacility("mf1");
        req.setIdCategory("cat1");
        req.setStartTime(now - 20_000);
        req.setEndTime(now - 10_000);
        req.setListMembers(Collections.emptyList());

        when(projectRepository.findById("pid3")).thenReturn(Optional.of(project));
        when(majorFacilityRepository.findById("mf1")).thenReturn(Optional.of(new MajorFacility()));
        when(categoryRepository.findById("cat1")).thenReturn(Optional.of(new Category()));
        when(staffProjectRepository.findByProject(project)).thenReturn(Collections.emptyList());

        ResponseObject<?> res = adProjectService.updateProject("pid3", req);
        assertEquals(StatusProject.DA_DIEN_RA, project.getStatusProject());
    }
    @Test
    void testCreateProject_statusChuaDienRa() {
        ADProjectCreateRequest req = new ADProjectCreateRequest();
        req.setCodeProject("CHUA");
        req.setIdMajorFacility("mf1");
        req.setIdCategory("cat1");
        req.setNameProject("Proj Chua");
        req.setStartTime(System.currentTimeMillis() + 10_000); // start > now
        req.setEndTime(System.currentTimeMillis() + 20_000);

        when(projectRepository.findByCode("CHUA")).thenReturn(Optional.empty());
        when(majorFacilityRepository.findById("mf1")).thenReturn(Optional.of(new MajorFacility()));
        when(categoryRepository.findById("cat1")).thenReturn(Optional.of(new Category()));

        Project saved = new Project();
        when(projectRepository.save(any(Project.class))).thenReturn(saved);

        ResponseObject<?> res = adProjectService.createProject(req);

        assertEquals(HttpStatus.OK, res.getStatus());
        verify(projectRepository).save(argThat(p ->
                p.getStatusProject() == StatusProject.CHUA_DIEN_RA
        ));
    }

    @Test
    void testCreateProject_statusDangDienRa() {
        long now = System.currentTimeMillis();
        ADProjectCreateRequest req = new ADProjectCreateRequest();
        req.setCodeProject("DANG");
        req.setIdMajorFacility("mf1");
        req.setIdCategory("cat1");
        req.setNameProject("Proj Dang");
        req.setStartTime(now - 1000);
        req.setEndTime(now + 5000);

        when(projectRepository.findByCode("DANG")).thenReturn(Optional.empty());
        when(majorFacilityRepository.findById("mf1")).thenReturn(Optional.of(new MajorFacility()));
        when(categoryRepository.findById("cat1")).thenReturn(Optional.of(new Category()));

        Project saved = new Project();
        when(projectRepository.save(any(Project.class))).thenReturn(saved);

        ResponseObject<?> res = adProjectService.createProject(req);

        assertEquals(HttpStatus.OK, res.getStatus());
        verify(projectRepository).save(argThat(p ->
                p.getStatusProject() == StatusProject.DANG_DIEN_RA
        ));
    }

    @Test
    void testCreateProject_statusDaDienRa() {
        long now = System.currentTimeMillis();
        ADProjectCreateRequest req = new ADProjectCreateRequest();
        req.setCodeProject("DA");
        req.setIdMajorFacility("mf1");
        req.setIdCategory("cat1");
        req.setNameProject("Proj Da");
        req.setStartTime(now - 20_000);
        req.setEndTime(now - 10_000);

        when(projectRepository.findByCode("DA")).thenReturn(Optional.empty());
        when(majorFacilityRepository.findById("mf1")).thenReturn(Optional.of(new MajorFacility()));
        when(categoryRepository.findById("cat1")).thenReturn(Optional.of(new Category()));

        Project saved = new Project();
        when(projectRepository.save(any(Project.class))).thenReturn(saved);

        ResponseObject<?> res = adProjectService.createProject(req);

        assertEquals(HttpStatus.OK, res.getStatus());
        verify(projectRepository).save(argThat(p ->
                p.getStatusProject() == StatusProject.DA_DIEN_RA
        ));
    }


    @Test
    void testDeleteProject_found() {
        Project project = new Project();
        project.setStatus(EntityStatus.ACTIVE);

        when(adProjectRepository.findById("pid")).thenReturn(Optional.of(project));
        when(adProjectRepository.save(any(Project.class))).thenReturn(project);

        ResponseObject<?> res = adProjectService.deleteProject("pid");

        assertEquals(HttpStatus.OK, res.getStatus());
        assertEquals("Xóa thành công dự án", res.getMessage());
        assertEquals(EntityStatus.INACTIVE, project.getStatus());
    }



    @Test
    void testDetailProject_notFoundAgain() {
        when(adProjectRepository.findById("p2")).thenReturn(Optional.empty());

        ResponseObject<?> res = adProjectService.detailProject("p2");

        assertEquals(HttpStatus.NOT_FOUND, res.getStatus());
        assertNull(res.getData());
    }

    @Test
    void testCountProjectsByFacility_withData() {
        FacilityProjectCountResponse mock1 = mock(FacilityProjectCountResponse.class);
        when(mock1.getFacilityName()).thenReturn("Cơ sở A");
        when(mock1.getTotal()).thenReturn(5L);

        when(adProjectRepository.countProjectsByFacility()).thenReturn(List.of(mock1));

        ResponseObject<?> res = adProjectService.countProjectsByFacility();

        assertEquals(HttpStatus.OK, res.getStatus());
        List<?> list = (List<?>) res.getData();
        FacilityProjectCountResponse item = (FacilityProjectCountResponse) list.get(0);
        assertEquals("Cơ sở A", item.getFacilityName());
        assertEquals(5L, item.getTotal());
    }

    @Test
    void testCountProjectsByDepartment_withData() {
        DepartmentProjectCountResponse mock1 = mock(DepartmentProjectCountResponse.class);
        when(mock1.getDepartmentName()).thenReturn("Khoa CNTT");
        when(mock1.getTotal()).thenReturn(7L);

        when(adProjectRepository.countProjectsByDepartment()).thenReturn(List.of(mock1));

        ResponseObject<?> res = adProjectService.countProjectsByDepartment();

        assertEquals(HttpStatus.OK, res.getStatus());
        List<?> list = (List<?>) res.getData();
        DepartmentProjectCountResponse item = (DepartmentProjectCountResponse) list.get(0);
        assertEquals("Khoa CNTT", item.getDepartmentName());
        assertEquals(7L, item.getTotal());
    }

    

}
