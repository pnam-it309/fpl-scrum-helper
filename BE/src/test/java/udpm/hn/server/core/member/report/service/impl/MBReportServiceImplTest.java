package udpm.hn.server.core.member.report.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import udpm.hn.server.core.common.base.PageableObject;
import udpm.hn.server.core.common.base.ResponseObject;
import udpm.hn.server.core.member.report.model.request.MBReportRequest;
import udpm.hn.server.core.member.report.model.request.UpdateReportSettingRequest;
import udpm.hn.server.core.member.report.model.response.MBReportResponse;
import udpm.hn.server.core.member.report.model.response.MBReportSettingReponse;
import udpm.hn.server.core.member.report.model.response.ReportRateResponse;
import udpm.hn.server.core.member.report.repository.MBReportRepository;
import udpm.hn.server.core.member.report.repository.MBReportSetting;
import udpm.hn.server.core.member.report.repository.MBTodoProjectRepository;
import udpm.hn.server.core.member.report.service.MBReportService;
import udpm.hn.server.entity.*;
import udpm.hn.server.infrastructure.constant.EntityStatus;
import udpm.hn.server.infrastructure.constant.StatusReport;
import udpm.hn.server.repository.HolidayRepository;
import udpm.hn.server.repository.PhaseProjectRepository;
import udpm.hn.server.repository.ProjectRepository;
import udpm.hn.server.repository.StaffProjectRepository;
import udpm.hn.server.utils.UserContextHelper;

import java.lang.reflect.Method;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class MBReportServiceImplTest {

    @Mock private MBReportRepository mbReportRepository;
    @Mock private MBTodoProjectRepository todoProjectRepository;
    @Mock private StaffProjectRepository staffProjectRepository;
    @Mock private ProjectRepository projectRepository;
    @Mock private UserContextHelper userContextHelper;

    @InjectMocks
    private MBReportServiceImpl mbReportService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllReports_Success() {
        MBReportRequest request = new MBReportRequest();
        request.setReportTime(1720000000000L);  // ví dụ timestamp

        String idProject = "project123";

        MBReportResponse mockResponse = new MBReportResponse() {



        }; // tạo object mô phỏng
        Page<MBReportResponse> mockPage = new PageImpl<>(List.of(mockResponse));

        when(mbReportRepository.getAll(any(Pageable.class), eq(idProject), eq(userContextHelper), eq(request)))
                .thenReturn(mockPage);

        ResponseObject<?> response = mbReportService.getAll(request, idProject);

        assertEquals(HttpStatus.OK, response.getStatus());
        assertNotNull(response.getData());

        // Nếu cần, có thể ép kiểu kiểm tra Page nội dung:
        PageableObject<?> pageableObject = (PageableObject<?>) response.getData();
        assertEquals(1, pageableObject.getData().size());
    }





    @Test
    void testAddReport_Success() {
        MBReportRequest request = new MBReportRequest();
        request.setWorkDoneToday("work");
        request.setObstacles("none");
        request.setWorkPlanTomorrow("plan");

        String idProject = "project123";

        StaffProject staffProject = new StaffProject();
        Project project = new Project();

        when(staffProjectRepository.getStaffProjectByStaffAndProject(userContextHelper, idProject))
                .thenReturn(Optional.of(staffProject));
        when(projectRepository.findProjectById(idProject))
                .thenReturn(Optional.of(project));

        ResponseObject<?> response = mbReportService.add(request, idProject);

        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertNull(response.getData());
        verify(mbReportRepository, times(1)).save(any(Report.class));
    }

    @Test
    void testAddReport_ProjectNotFound() {
        MBReportRequest request = new MBReportRequest();
        String idProject = "project123";

        StaffProject staffProject = new StaffProject();

        when(staffProjectRepository.getStaffProjectByStaffAndProject(userContextHelper, idProject))
                .thenReturn(Optional.of(staffProject));
        when(projectRepository.findProjectById(idProject))
                .thenReturn(Optional.empty());

        ResponseObject<?> response = mbReportService.add(request, idProject);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatus());
        assertNull(response.getData());
    }

    @Test
    void testAddReport_StaffProjectNotFound() {
        MBReportRequest request = new MBReportRequest();
        String idProject = "project123";

        when(staffProjectRepository.getStaffProjectByStaffAndProject(userContextHelper, idProject))
                .thenReturn(Optional.empty());

        ResponseObject<?> response = mbReportService.add(request, idProject);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatus());
    }



    @Test
    void testDetailByDate_ReportFound() {
        Long reportTime = 1720000000000L;
        String idProject = "project123";
        String reportId = "reportId123";

        when(userContextHelper.getCurrentUserId()).thenReturn("user1");
        when(mbReportRepository.findIdByReportTime(anyLong(), anyLong(), eq("user1"), eq(idProject)))
                .thenReturn(reportId);

        ResponseObject<?> response = mbReportService.detailByDate(reportTime, idProject);

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(reportId, response.getData());
    }

    @Test
    void testDetailByDate_ReportNotFound() {
        Long reportTime = 1720000000000L;
        String idProject = "project123";

        when(userContextHelper.getCurrentUserId()).thenReturn("user1");
        when(mbReportRepository.findIdByReportTime(anyLong(), anyLong(), eq("user1"), eq(idProject)))
                .thenReturn(null);

        ResponseObject<?> response = mbReportService.detailByDate(reportTime, idProject);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatus());
        assertNull(response.getData());
    }

    @Mock
    MBReportSetting reportSettingRepository;

    @Test
    void testGetReportSetting_Found() {
        MBReportSettingReponse setting = mock(MBReportSettingReponse.class);
        when(reportSettingRepository.getSettingByProjectId("project1"))
                .thenReturn(Optional.of(setting));
        ResponseObject<?> res = mbReportService.getReportSetting("project1");
        assertEquals(HttpStatus.OK, res.getStatus());
        assertEquals(setting, res.getData());
    }

    @Test
    void testGetReportSetting_NotFound() {
        when(reportSettingRepository.getSettingByProjectId("project2"))
                .thenReturn(Optional.empty());
        ResponseObject<?> res = mbReportService.getReportSetting("project2");
        assertEquals(HttpStatus.OK, res.getStatus());
        assertNull(res.getData());
    }

    @Test
    void testUpdateReportSetting_HasOldSetting() {
        UpdateReportSettingRequest req = new UpdateReportSettingRequest();
        req.setStopReportHour( 17L);
        ReportSetting oldSetting = new ReportSetting();
        when(reportSettingRepository.findByProject_Id("project1")).thenReturn(Optional.of(oldSetting));
        when(reportSettingRepository.save(any(ReportSetting.class))).thenReturn(oldSetting);

        ResponseObject<?> res = mbReportService.updateReportSetting("project1", req);
        assertEquals(HttpStatus.OK, res.getStatus());
        assertEquals("Cập nhật cấu hình báo cáo thành công", res.getMessage());
        assertEquals(17, ((ReportSetting)res.getData()).getStopReportHour());
    }

    @Test
    void testUpdateReportSetting_NoOldSetting_ProjectFound() {
        UpdateReportSettingRequest req = new UpdateReportSettingRequest();
        req.setStopReportHour(18L);
        when(reportSettingRepository.findByProject_Id("project2")).thenReturn(Optional.empty());
        Project prj = new Project();
        when(projectRepository.findById("project2")).thenReturn(Optional.of(prj));
        when(reportSettingRepository.save(any(ReportSetting.class))).thenAnswer(i -> i.getArguments()[0]);

        ResponseObject<?> res = mbReportService.updateReportSetting("project2", req);
        assertEquals(HttpStatus.OK, res.getStatus());
        assertEquals("Cập nhật cấu hình báo cáo thành công", res.getMessage());
        assertEquals(18, ((ReportSetting)res.getData()).getStopReportHour());
    }

    @Test
    void testUpdateReportSetting_NoOldSetting_ProjectNotFound() {
        UpdateReportSettingRequest req = new UpdateReportSettingRequest();
        req.setStopReportHour(19L);
        when(reportSettingRepository.findByProject_Id("project3")).thenReturn(Optional.empty());
        when(projectRepository.findById("project3")).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                mbReportService.updateReportSetting("project3", req)
        );
        assertTrue(ex.getMessage().contains("Không tìm thấy dự án với ID"));
    }
    @Test
    void testGetAllReports_ReportTimeNull() {
        MBReportRequest request = new MBReportRequest();
        request.setReportTime(null); // quan trọng

        String idProject = "projectX";
        Page<MBReportResponse> mockPage = new PageImpl<>(Collections.emptyList());

        when(mbReportRepository.getAll(any(Pageable.class), eq(idProject), eq(userContextHelper), eq(request)))
                .thenReturn(mockPage);

        ResponseObject<?> response = mbReportService.getAll(request, idProject);
        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    void testGetAllReports_EmptyPage() {
        MBReportRequest request = new MBReportRequest();
        String idProject = "projectZ";
        Page<MBReportResponse> mockPage = new PageImpl<>(Collections.emptyList());

        when(mbReportRepository.getAll(any(Pageable.class), eq(idProject), eq(userContextHelper), eq(request)))
                .thenReturn(mockPage);

        ResponseObject<?> response = mbReportService.getAll(request, idProject);
        assertEquals(HttpStatus.OK, response.getStatus());
        PageableObject<?> pageableObject = (PageableObject<?>) response.getData();
        assertTrue(pageableObject.getData().isEmpty());
    }
    @Test
    void testGetReportSetting_RepoReturnsNullOptional() {
        when(reportSettingRepository.getSettingByProjectId("projectX"))
                .thenReturn(Optional.ofNullable(null));
        ResponseObject<?> res = mbReportService.getReportSetting("projectX");
        assertEquals(HttpStatus.OK, res.getStatus());
        assertNull(res.getData());
    }
    @Test
    void testDetail_ReturnsNull() {
        when(mbReportRepository.detail(anyString(), anyString(), any(UserContextHelper.class)))
                .thenReturn(null);
        ResponseObject<?> res = mbReportService.detail("id", "prj");
        assertEquals(HttpStatus.OK, res.getStatus());
        assertNull(res.getData());
    }

    @Test
    void testDetailByDate_UserIdNull() {
        when(userContextHelper.getCurrentUserId()).thenReturn(null);
        when(mbReportRepository.findIdByReportTime(anyLong(), anyLong(), isNull(), anyString())).thenReturn(null);
        ResponseObject<?> res = mbReportService.detailByDate(1720000000000L, "projectY");
        assertEquals(HttpStatus.NOT_FOUND, res.getStatus());
    }
    @Test
    void testUpdateReportSetting_SaveThrowsException() {
        UpdateReportSettingRequest req = new UpdateReportSettingRequest();
        req.setStopReportHour(10L);
        ReportSetting oldSetting = new ReportSetting();
        when(reportSettingRepository.findByProject_Id("prj")).thenReturn(Optional.of(oldSetting));
        when(reportSettingRepository.save(any())).thenThrow(new RuntimeException("Save failed"));

        assertThrows(RuntimeException.class, () -> mbReportService.updateReportSetting("prj", req));
    }
    @Test
    void testUpdateReportSetting_ProjectNotFound() {
        UpdateReportSettingRequest req = new UpdateReportSettingRequest();
        req.setStopReportHour(12L);

        when(reportSettingRepository.findByProject_Id("pid")).thenReturn(Optional.empty());
        when(projectRepository.findById("pid")).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                mbReportService.updateReportSetting("pid", req)
        );
        assertTrue(ex.getMessage().contains("Không tìm thấy dự án"));
    }
    @Test
    void testUpdateReportSetting_CreateNewSetting() {
        UpdateReportSettingRequest req = new UpdateReportSettingRequest();
        req.setStopReportHour(9L);

        when(reportSettingRepository.findByProject_Id("pid")).thenReturn(Optional.empty());
        Project prj = new Project();
        when(projectRepository.findById("pid")).thenReturn(Optional.of(prj));
        when(reportSettingRepository.save(any(ReportSetting.class))).thenReturn(new ReportSetting());

        ResponseObject<?> res = mbReportService.updateReportSetting("pid", req);
        assertEquals(HttpStatus.OK, res.getStatus());
        assertTrue(res.getMessage().contains("Cập nhật"));
    }
    @Test
    void testAddReport_RequestFieldsNull() {
        MBReportRequest req = new MBReportRequest(); // all fields null
        String idProject = "p1";
        StaffProject staffProject = new StaffProject();
        Project project = new Project();
        when(staffProjectRepository.getStaffProjectByStaffAndProject(any(), eq(idProject)))
                .thenReturn(Optional.of(staffProject));
        when(projectRepository.findProjectById(idProject)).thenReturn(Optional.of(project));
        ResponseObject<?> res = mbReportService.add(req, idProject);
        assertEquals(HttpStatus.CREATED, res.getStatus());
    }

    @Test
    void testUpdateReportSetting_OptionalOfNull() {
        UpdateReportSettingRequest req = new UpdateReportSettingRequest();
        req.setStopReportHour(7L);
        when(reportSettingRepository.findByProject_Id("pid")).thenReturn(Optional.ofNullable(null));
        Project project = new Project();
        when(projectRepository.findById("pid")).thenReturn(Optional.of(project));
        when(reportSettingRepository.save(any())).thenReturn(new ReportSetting());
        ResponseObject<?> res = mbReportService.updateReportSetting("pid", req);
        assertEquals(HttpStatus.OK, res.getStatus());
    }
    @Mock private HolidayRepository holidayRepository;
    @Mock private PhaseProjectRepository phaseProjectRepository;

    @Test
    void testUpdateReport_Found() {
        MBReportRequest req = new MBReportRequest();
        req.setWorkDoneToday("done");
        req.setObstacles("obs");
        req.setWorkPlanTomorrow("plan");
        req.setHelp(true);

        Report report = new Report();
        when(mbReportRepository.findById("rid")).thenReturn(Optional.of(report));
        when(reportSettingRepository.getStopReportHour("pid"))
                .thenReturn(System.currentTimeMillis() + 100000);

        ResponseObject<?> res = mbReportService.update("rid", req, "pid");
        assertEquals(HttpStatus.CREATED, res.getStatus());
        verify(mbReportRepository).save(report);
    }

    @Test
    void testUpdateReport_NotFound() {
        when(mbReportRepository.findById("rid")).thenReturn(Optional.empty());
        MBReportRequest req = new MBReportRequest();
        ResponseObject<?> res = mbReportService.update("rid", req, "pid");
        assertEquals(HttpStatus.OK, res.getStatus());
        assertEquals("Không có báo cáo này", res.getMessage());
    }

    @Test
    void testAddReport_CreateNewSetting() {
        MBReportRequest req = new MBReportRequest();
        req.setWorkDoneToday("w");
        req.setObstacles("o");
        req.setWorkPlanTomorrow("p");
        String projectId = "p1";

        StaffProject sp = new StaffProject();
        Project prj = new Project();
        when(staffProjectRepository.getStaffProjectByStaffAndProject(userContextHelper, projectId))
                .thenReturn(Optional.of(sp));
        when(reportSettingRepository.getStopReportHour(projectId)).thenReturn(null);
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(prj));
        when(projectRepository.findProjectById(projectId)).thenReturn(Optional.of(prj));

        ResponseObject<?> res = mbReportService.add(req, projectId);
        assertEquals(HttpStatus.CREATED, res.getStatus());
        verify(reportSettingRepository).save(any(ReportSetting.class));
        verify(mbReportRepository).save(any(Report.class));
    }

    @Test
    void testUpdateReport_RequestNull() {
        Report existing = new Report();
        when(mbReportRepository.findById("rid")).thenReturn(Optional.of(existing));
        ResponseObject<?> res = mbReportService.update("rid", null, "pid");
        assertEquals(HttpStatus.CREATED, res.getStatus());
        verify(mbReportRepository, times(1)).save(existing);
    }

    @Test
    void testAddReport_RequestNull() {
        StaffProject sp = new StaffProject();
        Project pj = new Project();
        when(staffProjectRepository.getStaffProjectByStaffAndProject(userContextHelper, "pid"))
                .thenReturn(Optional.of(sp));
        when(projectRepository.findProjectById("pid")).thenReturn(Optional.of(pj));
        ResponseObject<?> res = mbReportService.add(null, "pid");
        assertEquals(HttpStatus.CREATED, res.getStatus());
    }


    @Test
    void testGetReportRateByPhase_ValidWorkDaysZero() {
        PhaseProject phase = new PhaseProject();
        long now = System.currentTimeMillis();
        phase.setStartTime(now);
        phase.setEndTime(now);
        when(phaseProjectRepository.findById("phase1")).thenReturn(Optional.of(phase));
        when(holidayRepository.countByProjectIdAndDateBetween(anyString(), anyLong(), anyLong()))
                .thenReturn(1L); // totalDays = 1, holiday = 1 => validWorkDays = 0
        when(mbReportRepository.countValidReportsInRange(anyString(), anyLong(), anyLong(), any()))
                .thenReturn(0L);

        ResponseObject<?> res = mbReportService.getReportRateByPhase("phase1", "proj1");
        assertEquals(HttpStatus.OK, res.getStatus());
        ReportRateResponse rate = (ReportRateResponse) res.getData();
        assertEquals(0.0, rate.getReportRate());
    }

    @Test
    void testGetAll_WithHolidayAndNullReportTime() {
        MBReportRequest req = new MBReportRequest();
        req.setReportTime(System.currentTimeMillis());
        String idProject = "proj1";

        MBReportResponse report1 = mock(MBReportResponse.class);
        when(report1.getReportTime()).thenReturn(1000L);

        MBReportResponse holiday1 = mock(MBReportResponse.class);
        when(holiday1.getReportTime()).thenReturn(null); // để test nullsLast

        Page<MBReportResponse> page = new PageImpl<>(List.of(report1));
        when(mbReportRepository.getAll(any(Pageable.class), eq(idProject), eq(userContextHelper), eq(req)))
                .thenReturn(page);
        when(holidayRepository.findHolidayReports(idProject)).thenReturn(List.of(holiday1));

        ResponseObject<?> res = mbReportService.getAll(req, idProject);
        assertEquals(HttpStatus.OK, res.getStatus());
        PageableObject<?> pageable = (PageableObject<?>) res.getData();
        assertEquals(2, pageable.getData().size()); // có cả report + holiday
    }
    @Test
    void testGetReportRateByPhase_WithReports() {
        PhaseProject phase = new PhaseProject();
        long now = System.currentTimeMillis();
        long oneDay = 24 * 60 * 60 * 1000;
        phase.setStartTime(now);
        phase.setEndTime(now + oneDay);
        when(phaseProjectRepository.findById("phaseX")).thenReturn(Optional.of(phase));
        when(holidayRepository.countByProjectIdAndDateBetween("proj1", phase.getStartTime(), anyLong()))
                .thenReturn(0L);
        when(mbReportRepository.countValidReportsInRange(eq("proj1"), anyLong(), anyLong(), eq(userContextHelper)))
                .thenReturn(1L);

        ResponseObject<?> res = mbReportService.getReportRateByPhase("phaseX", "proj1");
        assertEquals(HttpStatus.OK, res.getStatus());
        ReportRateResponse data = (ReportRateResponse) res.getData();
        assertTrue(data.getReportRate() > 0); // có tính toán
    }

    @Test
    void testUpdateReport_NotFoundExplicit() {
        when(mbReportRepository.findById("not-exist")).thenReturn(Optional.empty());
        ResponseObject<?> res = mbReportService.update("not-exist", new MBReportRequest(), "proj1");
        assertEquals(HttpStatus.OK, res.getStatus());
        assertEquals("Không có báo cáo này", res.getMessage());
    }

    @Test
    void testAddReport_CreateDefaultSetting() {
        MBReportRequest req = new MBReportRequest();
        req.setWorkDoneToday("work");
        String projectId = "projX";

        StaffProject sp = new StaffProject();
        Project pj = new Project();
        when(staffProjectRepository.getStaffProjectByStaffAndProject(userContextHelper, projectId))
                .thenReturn(Optional.of(sp));
        when(reportSettingRepository.getStopReportHour(projectId)).thenReturn(null); // trigger tạo default
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(pj));

        ResponseObject<?> res = mbReportService.add(req, projectId);
        assertEquals(HttpStatus.CREATED, res.getStatus());
        verify(reportSettingRepository, times(1)).save(any(ReportSetting.class));
    }

    @Test
    void testGetReportRateByPhase_NoWorkDays() {
        PhaseProject phase = new PhaseProject();
        long now = System.currentTimeMillis();
        phase.setStartTime(now);
        phase.setEndTime(now);
        when(phaseProjectRepository.findById("phaseY")).thenReturn(Optional.of(phase));
        when(holidayRepository.countByProjectIdAndDateBetween("projY", phase.getStartTime(), anyLong()))
                .thenReturn(1L); // holiday = totalDays

        when(mbReportRepository.countValidReportsInRange(eq("projY"), anyLong(), anyLong(), eq(userContextHelper)))
                .thenReturn(0L);

        ResponseObject<?> res = mbReportService.getReportRateByPhase("phaseY", "projY");
        assertEquals(HttpStatus.OK, res.getStatus());
        ReportRateResponse data = (ReportRateResponse) res.getData();
        assertEquals(0.0, data.getReportRate()); // validWorkDays = 0 → rate = 0
    }

    @Test
    void testAddReport_ProjectNotFoundWhenSettingNull() {
        MBReportRequest req = new MBReportRequest();
        String projectId = "projNo";
        StaffProject sp = new StaffProject();
        when(staffProjectRepository.getStaffProjectByStaffAndProject(userContextHelper, projectId))
                .thenReturn(Optional.of(sp));
        when(reportSettingRepository.getStopReportHour(projectId)).thenReturn(null);
        when(projectRepository.findById(projectId)).thenReturn(Optional.empty()); // trigger not found

        ResponseObject<?> res = mbReportService.add(req, projectId);
        assertEquals(HttpStatus.NOT_FOUND, res.getStatus());
        assertEquals("Không tìm thấy thông tin dự án", res.getMessage());
    }

    @Test
    void testUpdateReport_ExistingReportLateStatus() {
        String reportId = "rLate";
        MBReportRequest req = new MBReportRequest();
        req.setWorkDoneToday("work");

        Report report = new Report();
        when(mbReportRepository.findById(reportId)).thenReturn(Optional.of(report));
        // stop hour = 1ms để chắc chắn now > todayStopMillis => BÁO_CÁO_MUỘN
        when(reportSettingRepository.getStopReportHour("projLate")).thenReturn(1L);

        ResponseObject<?> res = mbReportService.update(reportId, req, "projLate");
        assertEquals(HttpStatus.CREATED, res.getStatus());
        Report updated = (Report) verify(mbReportRepository).save(any(Report.class));
    }

    @Test
    void testGetAll_HolidayMerged() {
        MBReportRequest req = new MBReportRequest();
        String projectId = "pid";
        MBReportResponse report = mock(MBReportResponse.class);
        when(report.getReportTime()).thenReturn(1000L);

        Page<MBReportResponse> mockPage = new PageImpl<>(List.of(report));
        when(mbReportRepository.getAll(any(Pageable.class), eq(projectId), eq(userContextHelper), eq(req)))
                .thenReturn(mockPage);

        // holiday repo trả về 1 holiday mock
        MBReportResponse holiday = mock(MBReportResponse.class);
        when(holiday.getReportTime()).thenReturn(2000L);
        when(holidayRepository.findHolidayReports(projectId)).thenReturn(List.of(holiday));

        ResponseObject<?> res = mbReportService.getAll(req, projectId);
        assertEquals(HttpStatus.OK, res.getStatus());
        PageableObject<?> po = (PageableObject<?>) res.getData();
        assertEquals(2, po.getData().size()); // cả report + holiday
    }

    @Test
    void testGetReportRateByPhase_PhaseNotFound() {
        when(phaseProjectRepository.findById("missing")).thenReturn(Optional.empty());
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> mbReportService.getReportRateByPhase("missing", "proj"));
        assertTrue(ex.getMessage().contains("Không tìm thấy phase"));
    }

    @Test
    void testNormalizeEndToEndOfDay_PrivateMethod() throws Exception {
        long now = System.currentTimeMillis();
        Method method = MBReportServiceImpl.class.getDeclaredMethod("normalizeEndToEndOfDay", Long.class);
        method.setAccessible(true);
        Long result = (Long) method.invoke(mbReportService, now);
        assertNotNull(result);
        assertTrue(result >= now); // end of day >= now
    }

    @Test
    void testAddReport_ProjectFoundButSettingNull() {
        MBReportRequest req = new MBReportRequest();
        req.setWorkDoneToday("a");
        String projectId = "pid1";
        StaffProject sp = new StaffProject();
        Project prj = new Project();

        when(staffProjectRepository.getStaffProjectByStaffAndProject(userContextHelper, projectId))
                .thenReturn(Optional.of(sp));
        when(reportSettingRepository.getStopReportHour(projectId)).thenReturn(null);
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(prj));
        when(reportSettingRepository.save(any())).thenReturn(new ReportSetting());

        ResponseObject<?> res = mbReportService.add(req, projectId);
        assertEquals(HttpStatus.CREATED, res.getStatus());
        verify(mbReportRepository).save(any(Report.class));
    }

    @Test
    void testUpdateReport_ReportNotFound() {
        when(mbReportRepository.findById("no")).thenReturn(Optional.empty());
        ResponseObject<?> res = mbReportService.update("no", new MBReportRequest(), "pid");
        assertEquals(HttpStatus.OK, res.getStatus());
        assertEquals("Không có báo cáo này", res.getMessage());
    }


    @Test
    void testGetReportRateByPhase_ZeroValidWorkDays() {
        String phaseId = "phase1", projectId = "proj1";
        PhaseProject phase = new PhaseProject();
        long start = System.currentTimeMillis();
        long end = start + 1000; // cùng ngày
        phase.setStartTime(start);
        phase.setEndTime(end);

        when(phaseProjectRepository.findById(phaseId)).thenReturn(Optional.of(phase));
        when(holidayRepository.countByProjectIdAndDateBetween(eq(projectId), anyLong(), anyLong()))
                .thenReturn(1L); // số ngày nghỉ = tổng số ngày

        when(mbReportRepository.countValidReportsInRange(eq(projectId), anyLong(), anyLong(), eq(userContextHelper)))
                .thenReturn(0L);

        ResponseObject<?> res = mbReportService.getReportRateByPhase(phaseId, projectId);
        assertEquals(HttpStatus.OK, res.getStatus());
        ReportRateResponse rr = (ReportRateResponse) res.getData();
        assertEquals(0.0, rr.getReportRate());
    }

    @Test
    void testGetReportRateByPhase_NormalCase() {
        String phaseId = "phase2", projectId = "proj2";
        PhaseProject phase = new PhaseProject();
        long start = System.currentTimeMillis();
        long end = start + 3 * 24 * 60 * 60 * 1000; // 3 ngày
        phase.setStartTime(start);
        phase.setEndTime(end);

        when(phaseProjectRepository.findById(phaseId)).thenReturn(Optional.of(phase));
        when(holidayRepository.countByProjectIdAndDateBetween(eq(projectId), anyLong(), anyLong()))
                .thenReturn(0L);
        when(mbReportRepository.countValidReportsInRange(eq(projectId), anyLong(), anyLong(), eq(userContextHelper)))
                .thenReturn(2L);

        ResponseObject<?> res = mbReportService.getReportRateByPhase(phaseId, projectId);
        assertEquals(HttpStatus.OK, res.getStatus());
        ReportRateResponse rr = (ReportRateResponse) res.getData();
        assertTrue(rr.getReportRate() > 0.0);
    }

    @Test
    void testUpdateReport_ReportFound_StatusLate() {
        // setup: stopReportHour nhỏ hơn now -> báo cáo muộn
        String reportId = "r123";
        MBReportRequest req = new MBReportRequest();
        req.setWorkDoneToday("x");
        req.setObstacles("y");
        req.setWorkPlanTomorrow("z");
        req.setHelp(false);

        Report existing = new Report();
        when(mbReportRepository.findById(reportId)).thenReturn(Optional.of(existing));
        // stopReportHour = 1 (quá khứ)
        when(reportSettingRepository.getStopReportHour("proj")).thenReturn(1L);

        ResponseObject<?> res = mbReportService.update(reportId, req, "proj");
        assertEquals(HttpStatus.CREATED, res.getStatus());
        assertEquals(StatusReport.BAO_CAO_MUON, existing.getStatusReport());
    }

    @Test
    void testAddReport_StatusLate() {
        MBReportRequest req = new MBReportRequest();
        req.setHelp(false);
        String projectId = "pid";

        StaffProject sp = new StaffProject();
        Project project = new Project();
        when(staffProjectRepository.getStaffProjectByStaffAndProject(userContextHelper, projectId))
                .thenReturn(Optional.of(sp));
        when(reportSettingRepository.getStopReportHour(projectId)).thenReturn(1L); // quá khứ
        when(projectRepository.findProjectById(projectId)).thenReturn(Optional.of(project));

        ResponseObject<?> res = mbReportService.add(req, projectId);
        assertEquals(HttpStatus.CREATED, res.getStatus());
        // check báo cáo muộn
    }

    @Test
    void testGetReportRateByPhase_NotFoundPhase() {
        when(phaseProjectRepository.findById("none")).thenReturn(Optional.empty());
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> mbReportService.getReportRateByPhase("none", "proj"));
        assertTrue(ex.getMessage().contains("Không tìm thấy phase"));
    }

    @Test
    void testUpdateReport_ReportNotFound_ReturnsOk() {
        when(mbReportRepository.findById("rid")).thenReturn(Optional.empty());
        ResponseObject<?> res = mbReportService.update("rid", new MBReportRequest(), "pid");
        assertEquals(HttpStatus.OK, res.getStatus());
        assertEquals("Không có báo cáo này", res.getMessage());
    }

    @Test
    void testAddReport_ProjectNotFound_WhenSettingNull() {
        MBReportRequest req = new MBReportRequest();
        String projectId = "pid";
        StaffProject sp = new StaffProject();
        when(staffProjectRepository.getStaffProjectByStaffAndProject(any(), eq(projectId)))
                .thenReturn(Optional.of(sp));
        when(reportSettingRepository.getStopReportHour(projectId)).thenReturn(null);
        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());

        ResponseObject<?> res = mbReportService.add(req, projectId);
        assertEquals(HttpStatus.NOT_FOUND, res.getStatus());
        assertEquals("Không tìm thấy thông tin dự án", res.getMessage());
    }


    @Test
    void testGetReportRateByPhase_ValidCase() {
        String phaseId = "ph1";
        String projectId = "pid";

        PhaseProject phase = new PhaseProject();
        long now = System.currentTimeMillis();
        phase.setStartTime(now);
        phase.setEndTime(now + 86400000 * 2); // 2 ngày
        when(phaseProjectRepository.findById(phaseId)).thenReturn(Optional.of(phase));
        when(holidayRepository.countByProjectIdAndDateBetween(eq(projectId), anyLong(), anyLong()))
                .thenReturn(1L);
        when(mbReportRepository.countValidReportsInRange(eq(projectId), anyLong(), anyLong(), any()))
                .thenReturn(1L);

        ResponseObject<?> res = mbReportService.getReportRateByPhase(phaseId, projectId);
        assertEquals(HttpStatus.OK, res.getStatus());
        assertTrue(res.getData() instanceof ReportRateResponse);
        ReportRateResponse rate = (ReportRateResponse) res.getData();
        assertTrue(rate.getTotalPhaseDays() >= 1);
    }

    @Test
    void testUpdateReport_DeadlineMissed() {
        String reportId = "rid";
        MBReportRequest req = new MBReportRequest();
        req.setWorkDoneToday("done");
        req.setObstacles("obs");
        req.setWorkPlanTomorrow("plan");

        Report report = new Report();
        long pastStopHour = System.currentTimeMillis() - 1000; // quá hạn
        when(mbReportRepository.findById(reportId)).thenReturn(Optional.of(report));
        when(reportSettingRepository.getStopReportHour("pid")).thenReturn(pastStopHour);

        ResponseObject<?> res = mbReportService.update(reportId, req, "pid");
        assertEquals(HttpStatus.CREATED, res.getStatus());
        assertEquals(StatusReport.BAO_CAO_MUON, report.getStatusReport());
    }

    @Test
    void testAddReport_DeadlineMissed() {
        MBReportRequest req = new MBReportRequest();
        req.setWorkDoneToday("today");
        String projectId = "pid";

        StaffProject sp = new StaffProject();
        Project prj = new Project();

        long pastStopHour = System.currentTimeMillis() - 1000; // đã quá hạn

        when(staffProjectRepository.getStaffProjectByStaffAndProject(any(), eq(projectId)))
                .thenReturn(Optional.of(sp));
        when(projectRepository.findProjectById(projectId)).thenReturn(Optional.of(prj));
        when(reportSettingRepository.getStopReportHour(projectId)).thenReturn(pastStopHour);

        ResponseObject<?> res = mbReportService.add(req, projectId);
        assertEquals(HttpStatus.CREATED, res.getStatus());
        verify(mbReportRepository).save(any(Report.class));
    }

    @Test
    void testGetReportRateByPhase_NoPhaseFound() {
        when(phaseProjectRepository.findById("ph-missing")).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> mbReportService.getReportRateByPhase("ph-missing", "pid"));
    }

    @Test
    void testGetReportRateByPhase_NoValidWorkDays() {
        String phaseId = "ph";
        String projectId = "pid";
        PhaseProject phase = new PhaseProject();
        long now = System.currentTimeMillis();
        phase.setStartTime(now);
        phase.setEndTime(now);
        when(phaseProjectRepository.findById(phaseId)).thenReturn(Optional.of(phase));
        when(holidayRepository.countByProjectIdAndDateBetween(eq(projectId), anyLong(), anyLong()))
                .thenReturn(1L); // toàn bộ ngày nghỉ
        when(mbReportRepository.countValidReportsInRange(eq(projectId), anyLong(), anyLong(), any()))
                .thenReturn(0L);

        ResponseObject<?> res = mbReportService.getReportRateByPhase(phaseId, projectId);
        ReportRateResponse rate = (ReportRateResponse) res.getData();
        assertEquals(0.0, rate.getReportRate());
    }

    @Test
    void testAddReport_ProjectNotFoundInSettingCreation() {
        MBReportRequest req = new MBReportRequest();
        String projectId = "pid";

        // StaffProject tồn tại
        when(staffProjectRepository.getStaffProjectByStaffAndProject(any(), eq(projectId)))
                .thenReturn(Optional.of(new StaffProject()));
        // Lần đầu getStopReportHour trả về null
        when(reportSettingRepository.getStopReportHour(projectId)).thenReturn(null);
        // Nhưng projectRepository.findById trả về empty
        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());

        ResponseObject<?> res = mbReportService.add(req, projectId);
        assertEquals(HttpStatus.NOT_FOUND, res.getStatus());
        assertEquals("Không tìm thấy thông tin dự án", res.getMessage());
    }

    @Test
    void testAddReport_CreateNewSettingDefaultStopHour() {
        MBReportRequest req = new MBReportRequest();
        req.setWorkDoneToday("x");
        String projectId = "pid";

        StaffProject sp = new StaffProject();
        Project prj = new Project();

        when(staffProjectRepository.getStaffProjectByStaffAndProject(any(), eq(projectId)))
                .thenReturn(Optional.of(sp));
        when(reportSettingRepository.getStopReportHour(projectId)).thenReturn(null);
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(prj));
        when(projectRepository.findProjectById(projectId)).thenReturn(Optional.of(prj));

        ResponseObject<?> res = mbReportService.add(req, projectId);
        assertEquals(HttpStatus.CREATED, res.getStatus());
        verify(reportSettingRepository).save(any(ReportSetting.class));
        verify(mbReportRepository).save(any(Report.class));
    }

    @Test
    void testUpdateReport_NotFoundSetting() {
        String reportId = "rid";
        MBReportRequest req = new MBReportRequest();
        Report report = new Report();
        when(mbReportRepository.findById(reportId)).thenReturn(Optional.of(report));
        when(reportSettingRepository.getStopReportHour("pid")).thenReturn(System.currentTimeMillis() + 10000);

        ResponseObject<?> res = mbReportService.update(reportId, req, "pid");
        assertEquals(HttpStatus.CREATED, res.getStatus());
        assertNotNull(report.getStatusReport());
    }

    @Test
    void testNormalizeEndToEndOfDay() throws Exception {
        long now = System.currentTimeMillis();
        // Gọi method private qua reflection để cover
        var method = MBReportServiceImpl.class.getDeclaredMethod("normalizeEndToEndOfDay", Long.class);
        method.setAccessible(true);
        long result = (long) method.invoke(mbReportService, now);
        assertTrue(result >= now);
    }

    @Test
    void testAddReport_ProjectNotFoundByIdWhenStaffProjectPresent() {
        MBReportRequest req = new MBReportRequest();
        String pid = "pid";

        when(staffProjectRepository.getStaffProjectByStaffAndProject(any(), eq(pid)))
                .thenReturn(Optional.of(new StaffProject()));
        when(reportSettingRepository.getStopReportHour(pid)).thenReturn(123L);
        when(projectRepository.findProjectById(pid)).thenReturn(Optional.empty());

        ResponseObject<?> res = mbReportService.add(req, pid);
        assertEquals(HttpStatus.NOT_FOUND, res.getStatus());
        assertEquals("Không tìm thấy thông tin dự án", res.getMessage());
    }

    @Test
    void testUpdateReport_StopHourInPast() {
        MBReportRequest req = new MBReportRequest();
        String pid = "pid";
        Report report = new Report();
        when(mbReportRepository.findById("rid")).thenReturn(Optional.of(report));
        // stop hour quá khứ => sẽ set StatusReport.BAO_CAO_MUON
        when(reportSettingRepository.getStopReportHour(pid)).thenReturn(System.currentTimeMillis() - 10000);

        ResponseObject<?> res = mbReportService.update("rid", req, pid);
        assertEquals(HttpStatus.CREATED, res.getStatus());
        assertEquals("Sửa báo cáo thành công", res.getMessage());
        assertEquals(StatusReport.BAO_CAO_MUON, report.getStatusReport());
    }



    @Test
    void testGetReportRateByPhase_Success() {
        String phaseId = "phase1";
        String projectId = "proj1";

        PhaseProject phase = new PhaseProject();
        phase.setStartTime(System.currentTimeMillis() - 3 * 86400000); // 3 ngày trước
        phase.setEndTime(System.currentTimeMillis());

        when(phaseProjectRepository.findById(phaseId)).thenReturn(Optional.of(phase));
        when(holidayRepository.countByProjectIdAndDateBetween(eq(projectId), anyLong(), anyLong()))
                .thenReturn(1L);
        when(mbReportRepository.countValidReportsInRange(eq(projectId), anyLong(), anyLong(), eq(userContextHelper)))
                .thenReturn(2L);

        ResponseObject<?> res = mbReportService.getReportRateByPhase(phaseId, projectId);

        assertEquals(HttpStatus.OK, res.getStatus());
        assertTrue(res.getData() instanceof ReportRateResponse);
        ReportRateResponse resp = (ReportRateResponse) res.getData();
        assertTrue(resp.getTotalPhaseDays() >= 1);
    }
    @Test
    void testAddReport_OnTime() {
        MBReportRequest req = new MBReportRequest();
        req.setWorkDoneToday("work");
        req.setObstacles("none");
        req.setWorkPlanTomorrow("plan");

        String idProject = "projOnTime";
        StaffProject staffProject = new StaffProject();
        Project project = new Project();

        when(staffProjectRepository.getStaffProjectByStaffAndProject(any(), eq(idProject)))
                .thenReturn(Optional.of(staffProject));
        when(projectRepository.findProjectById(idProject)).thenReturn(Optional.of(project));
        // stopReportHour = now + 1h -> chắc chắn reportTime <= stop => DA_BAO_CAO
        when(reportSettingRepository.getStopReportHour(idProject)).thenReturn(System.currentTimeMillis() + 3600000);

        ResponseObject<?> res = mbReportService.add(req, idProject);
        assertEquals(HttpStatus.CREATED, res.getStatus());
    }

    @Test
    void testAddReport_LateReport() {
        MBReportRequest req = new MBReportRequest();
        req.setWorkDoneToday("work late");
        String idProject = "projLate";
        StaffProject staffProject = new StaffProject();
        Project project = new Project();

        when(staffProjectRepository.getStaffProjectByStaffAndProject(any(), eq(idProject)))
                .thenReturn(Optional.of(staffProject));
        when(projectRepository.findProjectById(idProject)).thenReturn(Optional.of(project));
        // stopReportHour = quá khứ -> chắc chắn reportTime > stop => BÁO_CÁO_MUỘN
        when(reportSettingRepository.getStopReportHour(idProject)).thenReturn(System.currentTimeMillis() - 10000);

        ResponseObject<?> res = mbReportService.add(req, idProject);
        assertEquals(HttpStatus.CREATED, res.getStatus());
    }

    @Test
    void testUpdateReport_OnTime() {
        MBReportRequest req = new MBReportRequest();
        req.setWorkDoneToday("update work");

        String reportId = "rid1";
        Report report = new Report();

        when(mbReportRepository.findById(reportId)).thenReturn(Optional.of(report));
        when(reportSettingRepository.getStopReportHour("pid")).thenReturn(System.currentTimeMillis() + 10000);

        ResponseObject<?> res = mbReportService.update(reportId, req, "pid");
        assertEquals(HttpStatus.CREATED, res.getStatus());
        assertEquals(StatusReport.DA_BAO_CAO, report.getStatusReport());
    }

    @Test
    void testUpdateReport_Late() {
        MBReportRequest req = new MBReportRequest();
        req.setWorkDoneToday("update late");

        String reportId = "rid2";
        Report report = new Report();

        when(mbReportRepository.findById(reportId)).thenReturn(Optional.of(report));
        when(reportSettingRepository.getStopReportHour("pid2")).thenReturn(System.currentTimeMillis() - 10000);

        ResponseObject<?> res = mbReportService.update(reportId, req, "pid2");
        assertEquals(HttpStatus.CREATED, res.getStatus());
        assertEquals(StatusReport.BAO_CAO_MUON, report.getStatusReport());
    }


    @Test
    void testGetReportRateByPhase_ZeroWorkDays() {
        String phaseId = "phase2";
        String projectId = "proj2";

        PhaseProject phase = new PhaseProject();
        // start và end cùng 1 ngày
        long today = System.currentTimeMillis();
        phase.setStartTime(today);
        phase.setEndTime(today);

        when(phaseProjectRepository.findById(phaseId)).thenReturn(Optional.of(phase));
        // tất cả ngày đều là ngày nghỉ
        when(holidayRepository.countByProjectIdAndDateBetween(eq(projectId), anyLong(), anyLong())).thenReturn(1L);
        when(mbReportRepository.countValidReportsInRange(eq(projectId), anyLong(), anyLong(), any(UserContextHelper.class)))
                .thenReturn(0L);

        ResponseObject<?> res = mbReportService.getReportRateByPhase(phaseId, projectId);
        assertEquals(HttpStatus.OK, res.getStatus());
        assertTrue(res.getData().toString().contains("0.0"));
    }




}
