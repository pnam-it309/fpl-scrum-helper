package udpm.hn.server.core.manage.report.service.impl;

import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import udpm.hn.server.core.common.base.ResponseObject;
import udpm.hn.server.core.manage.report.model.request.MAReportRequest;
import udpm.hn.server.core.manage.report.model.request.MAReportUserRequest;
import udpm.hn.server.core.manage.report.repository.MAReportRepository;
import udpm.hn.server.core.manage.user.model.response.MAUserProjectResponse;
import udpm.hn.server.core.manage.user.repository.MAUserProject;
import udpm.hn.server.entity.HistoryImportReportCompensation;
import udpm.hn.server.entity.Holiday;
import udpm.hn.server.entity.Project;
import udpm.hn.server.entity.Role;
import udpm.hn.server.infrastructure.constant.EntityStatus;
import udpm.hn.server.infrastructure.doc.ADReportProject.repository.DocADProjectRepository;
import udpm.hn.server.infrastructure.job.report.BaseReportDay;
import udpm.hn.server.infrastructure.job.report.WordGenerator;
import udpm.hn.server.repository.*;
import udpm.hn.server.utils.UserContextHelper;

import java.io.IOException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
public class MAReportServiceImplTest {
    @Mock private MAReportRepository reportRepository;
    @Mock private DocADProjectRepository docRepo;
    @Mock private MAUserProject userProjectRepo;
    @Mock
    HistoryImportReportCompensationRepository historyRepo;
    @Mock
    RoleRepository roleRepo;
    @Mock
    StaffRepository staffRepo;
    @Mock
    UserContextHelper userHelper;
    @InjectMocks
    private MAReportServiceImpl service;
    @Mock private HolidayRepository holidayRepository;
    @Mock private ProjectRepository projectRepository;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllReport_Success() {
        MAReportRequest request = new MAReportRequest();
        request.setTime(Long.valueOf(String.valueOf(System.currentTimeMillis())));
        request.setIdProject("project-1");

        when(reportRepository.getAllReport(any(Pageable.class), anyLong(), anyLong(), eq("project-1")))
                .thenReturn(new PageImpl<>(Collections.emptyList()));

        ResponseObject<?> response = service.getAllReport(request);

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals("Lấy dũ liệu báo cáo thành công", response.getMessage());
    }

    @Test
    void testReadFileDoc_Success() {
        MAReportUserRequest req = new MAReportUserRequest();
        req.setIdUser(List.of("u1"));

        MAUserProjectResponse mockUser = mock(MAUserProjectResponse.class);
        when(mockUser.getCodeStudent()).thenReturn("SV001");
        when(mockUser.getNameStudent()).thenReturn("Nguyen Van A");

        when(userProjectRepo.listUser("p1", "u1")).thenReturn(Optional.of(mockUser));

        try (MockedStatic<WordGenerator> mockStatic = mockStatic(WordGenerator.class)) {
            mockStatic.when(() -> WordGenerator.createWordDocument(
                    any(), any(), any(), any(), any(), any()
            )).thenReturn("MockData".getBytes());

            byte[] result = service.readFileDoc("p1", req);
            assertNotNull(result);
            assertTrue(result.length > 0);
        }
    }


    @Test
    void testReadFileDoc_EmptyUserList() {
        MAReportUserRequest req = new MAReportUserRequest();
        req.setIdUser(List.of("invalid"));

        when(userProjectRepo.listUser("p1", "invalid")).thenReturn(Optional.empty());

        byte[] result = service.readFileDoc("p1", req);
        assertNotNull(result);
        assertEquals(22, result.length);
    }

//    @Test
//    void testExportReport_Success() throws Exception {
//        MAReportUserRequest req = new MAReportUserRequest();
//        req.setIdUser(List.of("u1"));
//
//        MAUserProjectResponse mockUser = mock(MAUserProjectResponse.class);
//        when(mockUser.getCodeStudent()).thenReturn("SV001");
//        when(mockUser.getNameStudent()).thenReturn("Nguyen Van A");
//
//        when(userProjectRepo.listUser("project-1", "u1")).thenReturn(Optional.of(mockUser));
//
//        HttpServletResponse response = mock(HttpServletResponse.class);
//        when(response.getWriter()).thenReturn(mock(PrintWriter.class));
//
//        ResponseObject<?> result = service.exportReport(response, "project-1", req);
//
//        assertEquals(HttpStatus.OK, result.getStatus());
//        assertEquals("Xuất báo cáo thành công", result.getMessage());
//    }
//
//    @Test
//    void testExportReport_ExceptionThrown() {
//        MAReportUserRequest req = new MAReportUserRequest();
//        req.setIdUser(List.of("u1"));
//
//        when(userProjectRepo.listUser("project-1", "u1")).thenThrow(new RuntimeException("Failed"));
//
//        HttpServletResponse response = mock(HttpServletResponse.class);
//        ResponseObject<?> result = service.exportReport(response, "project-1", req);
//
//        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatus());
//        assertTrue(result.getMessage().contains("Xuất báo cáo thất bại"));
//    }

    @Test
    void testFindAllReport_ProjectNotFound() {
        MAReportRequest request = new MAReportRequest();
        request.setIdProject("not-exist");
        when(docRepo.findById("not-exist")).thenReturn(Optional.empty());

        ResponseObject<?> res = service.findAllReport(request);
        assertEquals(HttpStatus.NO_CONTENT, res.getStatus());
        assertEquals("Không tìm thấy project", res.getMessage());
    }

    @Test
    void testFindAllReport_ProjectFound() {
        MAReportRequest request = new MAReportRequest();
        request.setIdProject("project-1");
        Project project = new Project();
        project.setStartTime(1L); project.setEndTime(2L);
        when(docRepo.findById("project-1")).thenReturn(Optional.of(project));
        when(reportRepository.getAllReport(anyLong(), anyLong(), eq("project-1"))).thenReturn(List.of());
        ResponseObject<?> res = service.findAllReport(request);
        assertEquals(HttpStatus.OK, res.getStatus());
        assertEquals("Lấy dữ liệu thành công", res.getMessage());
    }

    @Test
    void testGetAllReports_NoReport() {
        when(reportRepository.countAllReports()).thenReturn(0L);
        ResponseObject<?> res = service.getAllReports();
        assertEquals(HttpStatus.NO_CONTENT, res.getStatus());
    }

    @Test
    void testGetAllReports_HasReport() {
        when(reportRepository.countAllReports()).thenReturn(5L);
        ResponseObject<?> res = service.getAllReports();
        assertEquals(HttpStatus.OK, res.getStatus());
    }

    @Test
    void testGetAllReports_Exception() {
        when(reportRepository.countAllReports()).thenThrow(new RuntimeException("err"));
        ResponseObject<?> res = service.getAllReports();
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, res.getStatus());
    }

    @Test
    void testReadFileDoc_ThrowsIOException() {
        MAReportUserRequest req = new MAReportUserRequest();
        req.setIdUser(List.of("u1"));
        MAUserProjectResponse mockUser = mock(MAUserProjectResponse.class);
        when(mockUser.getCodeStudent()).thenReturn("SV001");
        when(mockUser.getNameStudent()).thenReturn("Nguyen Van A");
        when(userProjectRepo.listUser("p1", "u1")).thenReturn(Optional.of(mockUser));

        try (MockedStatic<WordGenerator> mockStatic = mockStatic(WordGenerator.class)) {
            mockStatic.when(() -> WordGenerator.createWordDocument(any(), any(), any(), any(), any(), any()))
                    .thenThrow(new IOException("IO error"));

            assertThrows(RuntimeException.class, () -> service.readFileDoc("p1", req));
        }
    }

    @Test
    void testReadFileDoc_EmptyIdUserList() {
        MAReportUserRequest req = new MAReportUserRequest();
        req.setIdUser(Collections.emptyList());
        byte[] result = service.readFileDoc("p1", req);
        assertNotNull(result);
        // Có thể assertEquals(22, result.length); // tương tự empty
    }

    @Test
    void testReadFileDoc_IdUserEmptyList() {
        MAReportUserRequest req = new MAReportUserRequest();
        req.setIdUser(Collections.emptyList());
        byte[] result = service.readFileDoc("p1", req);
        assertNotNull(result);
        // Có thể assertEquals(22, result.length); // nếu trả về mảng byte rỗng hoặc default
    }

    @Test
    void testGetAllReport_TimeNull() {
        MAReportRequest req = new MAReportRequest();
        req.setTime(null); // hoặc không set gì
        req.setIdProject("project-1");
        assertThrows(NullPointerException.class, () -> service.getAllReport(req));
    }

    @Test
    void testFindAllReport_ProjectFound_EmptyList() {
        MAReportRequest request = new MAReportRequest();
        request.setIdProject("project-1");
        Project project = new Project();
        project.setStartTime(1L); project.setEndTime(2L);
        when(docRepo.findById("project-1")).thenReturn(Optional.of(project));
        when(reportRepository.getAllReport(anyLong(), anyLong(), eq("project-1"))).thenReturn(Collections.emptyList());
        ResponseObject<?> res = service.findAllReport(request);
        assertEquals(HttpStatus.OK, res.getStatus());
        assertTrue(res.getData() instanceof List && ((List<?>) res.getData()).isEmpty());
    }

    // --- exportReport ---
    @Test
    void testExportReport_Success() {
        MAReportUserRequest req = new MAReportUserRequest();
        req.setIdUser(List.of("u1"));
        MAUserProjectResponse mockUser = mock(MAUserProjectResponse.class);
        when(mockUser.getCodeStudent()).thenReturn("SV001");
        when(mockUser.getNameStudent()).thenReturn("Nguyen Van A");
        when(userProjectRepo.listUser("p1", "u1")).thenReturn(Optional.of(mockUser));

        HttpServletResponse response = mock(HttpServletResponse.class);

        ResponseObject<?> res = service.exportReport(response, "p1", req);

        assertEquals(HttpStatus.OK, res.getStatus());
        assertEquals("Xuất báo cáo thành công", res.getMessage());
    }

    @Test
    void testExportReport_Exception() {
        MAReportUserRequest req = new MAReportUserRequest();
        req.setIdUser(List.of("u1"));
        when(userProjectRepo.listUser("p1", "u1")).thenThrow(new RuntimeException("err"));

        HttpServletResponse response = mock(HttpServletResponse.class);

        ResponseObject<?> res = service.exportReport(response, "p1", req);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, res.getStatus());
        assertTrue(res.getMessage().contains("Xuất báo cáo thất bại"));
    }

    // --- createMultipleHolidays ---
    @Test
    void testCreateMultipleHolidays_ProjectNotFound() {
        when(projectRepository.findProjectById("p1")).thenReturn(Optional.empty());

        ResponseObject<?> res = service.createMultipleHolidays(new udpm.hn.server.core.manage.report.model.request.HolidayCreateRequest(), "p1");

        assertEquals(HttpStatus.NOT_FOUND, res.getStatus());
    }

    @Test
    void testCreateMultipleHolidays_NewHoliday() {
        Project project = new Project(); project.setId("p1");
        when(projectRepository.findProjectById("p1")).thenReturn(Optional.of(project));
        when(holidayRepository.findByDateInDayAndProjectId(anyLong(), anyLong(), eq("p1"))).thenReturn(Collections.emptyList());

        udpm.hn.server.core.manage.report.model.request.HolidayCreateRequest req = new udpm.hn.server.core.manage.report.model.request.HolidayCreateRequest();
        req.setDates(List.of(System.currentTimeMillis()));
        req.setDescribe("Nghỉ lễ");

        ResponseObject<?> res = service.createMultipleHolidays(req, "p1");

        assertEquals(HttpStatus.CREATED, res.getStatus());
        assertTrue(res.getMessage().contains("Đã thêm hoặc cập nhật"));
        verify(holidayRepository).saveAll(anyList());
    }

    @Test
    void testCreateMultipleHolidays_ExistInactive_ShouldReactivate() {
        Project project = new Project(); project.setId("p1");
        Holiday old = new Holiday();
        old.setStatus(EntityStatus.INACTIVE);
        when(projectRepository.findProjectById("p1")).thenReturn(Optional.of(project));
        when(holidayRepository.findByDateInDayAndProjectId(anyLong(), anyLong(), eq("p1"))).thenReturn(List.of(old));

        udpm.hn.server.core.manage.report.model.request.HolidayCreateRequest req = new udpm.hn.server.core.manage.report.model.request.HolidayCreateRequest();
        req.setDates(List.of(System.currentTimeMillis()));
        req.setDescribe("abc");

        ResponseObject<?> res = service.createMultipleHolidays(req, "p1");

        assertEquals(HttpStatus.CREATED, res.getStatus());
        verify(holidayRepository).saveAll(anyList());
    }

    @Test
    void testCreateMultipleHolidays_ExistActive_ShouldConflict() {
        Project project = new Project(); project.setId("p1");
        Holiday old = new Holiday(); old.setStatus(EntityStatus.ACTIVE);
        when(projectRepository.findProjectById("p1")).thenReturn(Optional.of(project));
        when(holidayRepository.findByDateInDayAndProjectId(anyLong(), anyLong(), eq("p1"))).thenReturn(List.of(old));

        udpm.hn.server.core.manage.report.model.request.HolidayCreateRequest req = new udpm.hn.server.core.manage.report.model.request.HolidayCreateRequest();
        req.setDates(List.of(System.currentTimeMillis()));

        ResponseObject<?> res = service.createMultipleHolidays(req, "p1");

        assertEquals(HttpStatus.CONFLICT, res.getStatus());
    }

    // --- updateHolidaysStatus ---
    @Test
    void testUpdateHolidaysStatus_ProjectNotFound() {
        when(projectRepository.findProjectById("p1")).thenReturn(Optional.empty());

        ResponseObject<?> res = service.updateHolidaysStatus(List.of(1L), "p1");

        assertEquals(HttpStatus.NOT_FOUND, res.getStatus());
    }

    @Test
    void testUpdateHolidaysStatus_Conflict() {
        Project project = new Project(); project.setId("p1");
        Holiday h = new Holiday();
        h.setDate(System.currentTimeMillis());
        h.setCode("X");
        when(projectRepository.findProjectById("p1")).thenReturn(Optional.of(project));
        when(holidayRepository.findByDateInDayAndProjectId(anyLong(), anyLong(), eq("p1"))).thenReturn(List.of(h));
        when(holidayRepository.existsByCode(any())).thenReturn(true);

        ResponseObject<?> res = service.updateHolidaysStatus(List.of(System.currentTimeMillis()), "p1");

        assertEquals(HttpStatus.CONFLICT, res.getStatus());
    }

    @Test
    void testUpdateHolidaysStatus_UpdateSuccess() {
        Project project = new Project(); project.setId("p1");
        Holiday h = new Holiday();
        h.setDate(System.currentTimeMillis());
        h.setCode("code");
        when(projectRepository.findProjectById("p1")).thenReturn(Optional.of(project));
        when(holidayRepository.findByDateInDayAndProjectId(anyLong(), anyLong(), eq("p1"))).thenReturn(List.of(h));
        when(holidayRepository.existsByCode(any())).thenReturn(false);

        ResponseObject<?> res = service.updateHolidaysStatus(List.of(System.currentTimeMillis()), "p1");

        assertEquals(HttpStatus.OK, res.getStatus());
        assertTrue(res.getMessage().contains("Cập nhật"));
        verify(holidayRepository).saveAll(anyList());
    }

    @Test
    void testUpdateHolidaysStatus_NoUpdate() {
        Project project = new Project(); project.setId("p1");
        when(projectRepository.findProjectById("p1")).thenReturn(Optional.of(project));
        when(holidayRepository.findByDateInDayAndProjectId(anyLong(), anyLong(), eq("p1"))).thenReturn(Collections.emptyList());

        ResponseObject<?> res = service.updateHolidaysStatus(List.of(System.currentTimeMillis()), "p1");

        assertEquals(HttpStatus.OK, res.getStatus());
        assertEquals("Không có ngày nào được cập nhật.", res.getMessage());
    }
    @Test
    void testCreateMultipleHolidays_MultipleDays() {
        Project project = new Project(); project.setId("p1");
        when(projectRepository.findProjectById("p1")).thenReturn(Optional.of(project));
        when(holidayRepository.findByDateInDayAndProjectId(anyLong(), anyLong(), eq("p1")))
                .thenReturn(Collections.emptyList());

        udpm.hn.server.core.manage.report.model.request.HolidayCreateRequest req =
                new udpm.hn.server.core.manage.report.model.request.HolidayCreateRequest();
        req.setDates(List.of(System.currentTimeMillis(), System.currentTimeMillis() + 86400000));
        req.setDescribe("Nghỉ nhiều ngày");

        ResponseObject<?> res = service.createMultipleHolidays(req, "p1");

        assertEquals(HttpStatus.CREATED, res.getStatus());
        assertTrue(res.getMessage().contains("2 ngày nghỉ")); // hoặc ít nhất >1
        verify(holidayRepository).saveAll(anyList());
    }
    @Test
    void testCreateMultipleHolidays_MultipleDays_ShouldSaveTwo() {
        Project project = new Project(); project.setId("p1");
        when(projectRepository.findProjectById("p1")).thenReturn(Optional.of(project));
        // luôn trả empty để tạo holiday mới
        when(holidayRepository.findByDateInDayAndProjectId(anyLong(), anyLong(), eq("p1")))
                .thenReturn(Collections.emptyList());

        udpm.hn.server.core.manage.report.model.request.HolidayCreateRequest req =
                new udpm.hn.server.core.manage.report.model.request.HolidayCreateRequest();
        long today = System.currentTimeMillis();
        long tomorrow = today + 86400000;
        req.setDates(List.of(today, tomorrow));
        req.setDescribe("Nghỉ nhiều ngày");

        ResponseObject<?> res = service.createMultipleHolidays(req, "p1");

        assertEquals(HttpStatus.CREATED, res.getStatus());
        assertTrue(res.getMessage().contains("2"));
    }
    @Test
    void testExportReport_BaseReportThrowsException() {
        MAReportUserRequest req = new MAReportUserRequest();
        req.setIdUser(List.of("u1"));

        MAUserProjectResponse mockUser = mock(MAUserProjectResponse.class);
        when(mockUser.getCodeStudent()).thenReturn("S01");
        when(mockUser.getNameStudent()).thenReturn("Nguyen Van B");
        when(userProjectRepo.listUser("p1", "u1")).thenReturn(Optional.of(mockUser));

        HttpServletResponse response = mock(HttpServletResponse.class);

        // fake BaseReportDay bằng cách mock new BaseReportDay(...)
        try (MockedConstruction<BaseReportDay> mocked = mockConstruction(BaseReportDay.class,
                (mock, context) -> {
                    doThrow(new IOException("IO error")).when(mock).export(any());
                })) {

            ResponseObject<?> res = service.exportReport(response, "p1", req);

            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, res.getStatus());
            assertTrue(res.getMessage().contains("Xuất báo cáo thất bại"));
        }
    }

    @Test
    void testReadFileDoc_WordGeneratorReturnsEmptyBytes() {
        MAReportUserRequest req = new MAReportUserRequest();
        req.setIdUser(List.of("u1"));

        MAUserProjectResponse mockUser = mock(MAUserProjectResponse.class);
        when(mockUser.getCodeStudent()).thenReturn("S02");
        when(mockUser.getNameStudent()).thenReturn("Tran Van C");
        when(userProjectRepo.listUser("p1", "u1")).thenReturn(Optional.of(mockUser));

        try (MockedStatic<WordGenerator> mockStatic = mockStatic(WordGenerator.class)) {
            mockStatic.when(() -> WordGenerator.createWordDocument(any(), any(), any(), any(), any(), any()))
                    .thenReturn(new byte[0]); // trả về mảng rỗng

            byte[] result = service.readFileDoc("p1", req);

            assertNotNull(result);
            // vì file zip vẫn được tạo, nhưng data rỗng
            assertTrue(result.length > 0);
        }
    }
    @Test
    void testUpdateHolidaysStatus_CodeSame_ShouldInactivate() {
        Project project = new Project(); project.setId("p1");
        when(projectRepository.findProjectById("p1")).thenReturn(Optional.of(project));

        long timestamp = System.currentTimeMillis();
        String correctCode = "N1_1_2024_p1";

        Holiday h = new Holiday();
        h.setDate(timestamp);
        h.setCode(correctCode); // code trùng → nhánh equals

        when(holidayRepository.findByDateInDayAndProjectId(anyLong(), anyLong(), eq("p1")))
                .thenReturn(List.of(h));

        ResponseObject<?> res = service.updateHolidaysStatus(List.of(timestamp), "p1");

        assertEquals(HttpStatus.OK, res.getStatus());
        assertEquals("Cập nhật ngày nghỉ thành công!", res.getMessage());
        verify(holidayRepository).saveAll(anyList());
    }
    @Test
    void testCreateMultipleHolidays_ExistingMultipleHolidays_ShouldUseFirst() {
        Project project = new Project(); project.setId("p1");
        when(projectRepository.findProjectById("p1")).thenReturn(Optional.of(project));

        Holiday h1 = new Holiday(); h1.setStatus(EntityStatus.ACTIVE);
        Holiday h2 = new Holiday(); h2.setStatus(EntityStatus.INACTIVE);

        // repo trả về nhiều holiday
        when(holidayRepository.findByDateInDayAndProjectId(anyLong(), anyLong(), eq("p1")))
                .thenReturn(List.of(h1, h2));

        udpm.hn.server.core.manage.report.model.request.HolidayCreateRequest req =
                new udpm.hn.server.core.manage.report.model.request.HolidayCreateRequest();
        req.setDates(List.of(System.currentTimeMillis()));

        ResponseObject<?> res = service.createMultipleHolidays(req, "p1");

        // phải conflict vì holiday đầu tiên là ACTIVE
        assertEquals(HttpStatus.CONFLICT, res.getStatus());
    }
    @Test
    void testUpdateHolidaysStatus_MultipleHolidaysInOneDay() {
        Project project = new Project(); project.setId("p1");
        when(projectRepository.findProjectById("p1")).thenReturn(Optional.of(project));

        long ts = System.currentTimeMillis();
        Holiday h1 = new Holiday(); h1.setDate(ts); h1.setCode("C1");
        Holiday h2 = new Holiday(); h2.setDate(ts); h2.setCode("C2");

        when(holidayRepository.findByDateInDayAndProjectId(anyLong(), anyLong(), eq("p1")))
                .thenReturn(List.of(h1, h2));
        when(holidayRepository.existsByCode(any())).thenReturn(false);

        ResponseObject<?> res = service.updateHolidaysStatus(List.of(ts), "p1");

        assertEquals(HttpStatus.OK, res.getStatus());
        verify(holidayRepository).saveAll(argThat(list -> ((Collection<?>) list).size() == 2));

    }

}
