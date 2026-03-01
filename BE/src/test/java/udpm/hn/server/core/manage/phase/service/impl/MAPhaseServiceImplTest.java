package udpm.hn.server.core.manage.phase.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import udpm.hn.server.core.common.base.ResponseObject;
import udpm.hn.server.core.manage.phase.model.request.MACreatePhaseRequest;
import udpm.hn.server.core.manage.phase.model.request.MAPhaseRequest;
import udpm.hn.server.core.manage.phase.model.request.MATodoByPhase;
import udpm.hn.server.core.manage.phase.model.response.MAPhaseResponse;
import udpm.hn.server.core.manage.phase.repository.*;
import udpm.hn.server.core.manage.todo.repository.MAListTodoProjectRepository;
import udpm.hn.server.core.manage.todo.repository.MATodoRepository;
import udpm.hn.server.entity.*;
import udpm.hn.server.infrastructure.configemail.AsyncEmailService;
import udpm.hn.server.infrastructure.configemail.EmailSender;
import udpm.hn.server.infrastructure.constant.EntityStatus;
import udpm.hn.server.infrastructure.constant.StatusTodo;
import udpm.hn.server.repository.*;
import udpm.hn.server.utils.StatusPhase;
import udpm.hn.server.utils.TodoListHelper;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class MAPhaseServiceImplTest {

    @InjectMocks
    private MAPhaseServiceImpl maPhaseService;

    @Mock private MAPhaseRepository maPhaseRepository;
    @Mock private ToDoRepository toDoRepository;
    @Mock private PhaseTodoProjectRepository phaseTodoProjectRepository;
    @Mock private MAListTodoProjectRepository maListTodoProjectRepository;
    @Mock private TodoListHelper todoListHelper;
    @Mock private ProjectRepository projectRepository;
    @Mock private MATodoRepository maTodoRepository;
    @Mock private EmailSender emailService;
    @Mock private MAPhaseStaffProjectRepository maPhaseStaffProjectRepository;
    @Mock private VelocityRecordRepository velocityRecordRepository;
    @Mock private CapacityEstimateRepository capacityEstimateRepository;
    @Mock private PhaseProjectRepository phaseProjectRepository; // optional, if used
    @Mock private AsyncEmailService asyncEmailService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllPhase() {
        MAPhaseRequest request = new MAPhaseRequest();
        request.setIdProject("project-id");
        Pageable pageable = Pageable.unpaged();

        when(maPhaseRepository.getAllPhaseProject(any(), eq("project-id")))
                .thenReturn(new PageImpl<>(Collections.emptyList()));

        ResponseObject<?> response = maPhaseService.getAllPhase(request);

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals("Lấy dự liệu giai đoạn thành công", response.getMessage());
    }

    @Test
    void testFindByPhaseStatus_NotFound() {
        when(maPhaseRepository.findAll()).thenReturn(Collections.emptyList());

        ResponseObject<?> response = maPhaseService.findByPhaseStatus("nonexistent");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatus());
    }

    @Test
    void testCreatePhase_Success() {
        MACreatePhaseRequest request = new MACreatePhaseRequest();
        request.setIdProject("project-id");
        request.setCode("P01");
        request.setName("Giai đoạn 1");

        Project project = new Project();
        project.setId("project-id");

        when(projectRepository.findById("project-id")).thenReturn(Optional.of(project));
        when(todoListHelper.genCodeTodoList("project-id")).thenReturn("TD01");
        when(todoListHelper.genIndexTodoList("project-id")).thenReturn((byte) 1);
        when(maPhaseRepository.getAllPhaseByProjectId("project-id"))
                .thenReturn(List.of(mock(MAPhaseResponse.class), mock(MAPhaseResponse.class)));
        when(maPhaseRepository.countPhaseProjectByIdProject(anyString(), any())).thenReturn(1);

        ResponseObject<?> response = maPhaseService.createPhase(request);

        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertEquals("Thêm giai đoạn thành công", response.getMessage());
    }

    @Test
    void testDeletePhase_NotFound() {
        when(maPhaseRepository.findById("invalid-id")).thenReturn(Optional.empty());

        ResponseObject<?> response = maPhaseService.deletePhase("invalid-id");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatus());
        assertEquals("Không tìm thấy giai đoạn", response.getMessage());
    }

    @Test
    void testDeleteTodoByPhase_NotFound() {
        when(toDoRepository.findById("missing-id")).thenReturn(Optional.empty());

        ResponseObject<?> response = maPhaseService.deleteTodoByPhase("missing-id");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatus());
    }

    @Test
    void testGetAllPhaseByIdProject() {
        String idProject = "project-123";

        // Tạo mock MAPhaseResponse (interface, nên phải dùng Mockito.mock)
        MAPhaseResponse mockResponse = mock(MAPhaseResponse.class);
        List<MAPhaseResponse> mockPhases = List.of(mockResponse);

        // Mock đúng kiểu dữ liệu trả về
        when(maPhaseRepository.getAllPhaseProjectStatistics(idProject)).thenReturn(mockPhases);

        // Gọi hàm service
        ResponseObject<?> response = maPhaseService.getAllPhase(idProject);

        // Kiểm tra kết quả
        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals("Lấy dữ liệu thành công", response.getMessage());
        assertEquals(mockPhases, response.getData());
    }


    @Test
    void testGetAllSprint_Success() {
        MAPhaseRequest request = new MAPhaseRequest();
        request.setIdProject("project-123");
        request.setStatus(StatusPhase.DANG_LAM.ordinal());
        request.setTime(System.currentTimeMillis());
        request.setSearch("Sprint");

        // Mock response
        MAPhaseResponse mockPhase = mock(MAPhaseResponse.class);
        List<MAPhaseResponse> content = List.of(mockPhase);
        Page<MAPhaseResponse> mockPage = new PageImpl<>(content);

        when(maPhaseRepository.getAllSprintProject(
                any(Pageable.class),
                eq("project-123"),
                eq(StatusPhase.DANG_LAM),
                anyLong(),
                anyLong(),
                eq("Sprint")
        )).thenReturn(mockPage);

        ResponseObject<?> response = maPhaseService.getAllSprint(request);

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals("Lấy dữ liệu giai đoạn thành công", response.getMessage());
        assertEquals(mockPage, response.getData());
    }

    @Test
    void testGetAllPhaseByProjectId_Success() {
        String projectId = "abc123";

        // Tạo mock response đúng kiểu
        MAPhaseResponse mockResponse = mock(MAPhaseResponse.class);
        List<MAPhaseResponse> mockPhases = List.of(mockResponse);

        // Sửa kiểu dữ liệu trong mock
        when(maPhaseRepository.getAllPhaseByProjectId(projectId)).thenReturn(mockPhases);

        ResponseObject<?> response = maPhaseService.getAllPhaseByProjectId(projectId);

        assertEquals(mockPhases, response.getData());
    }


    @Test
    void testDetailPhase_Success() {
        String phaseId = "phase123";
        Optional<PhaseProject> phaseOpt = Optional.of(new PhaseProject());

        when(maPhaseRepository.findById(phaseId)).thenReturn(phaseOpt);

        ResponseObject<?> response = maPhaseService.detailPhase(phaseId);

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals("Lấy dữ liệu thành công", response.getMessage());
        assertEquals(phaseOpt, response.getData());
    }

    @Test
    void testCreatePhaseTodoList_ReturnsNull() {
        MACreatePhaseRequest request = new MACreatePhaseRequest();
        assertNull(maPhaseService.createPhaseTOdoList(request));
    }

    @Test
    void testCreateTodoByPhase_Success() {
        MATodoByPhase request = new MATodoByPhase();
        request.setIdProject("project1");
        request.setIdPhase("phase1");
        request.setIdTodo(List.of("todo1"));
        request.setIndex("1");

        Project project = new Project(); project.setId("project1");
        PhaseProject phase = new PhaseProject(); phase.setId("phase1");
        Todo todo = new Todo(); todo.setId("todo1");

        when(projectRepository.findById("project1")).thenReturn(Optional.of(project));
        when(maPhaseRepository.findById("phase1")).thenReturn(Optional.of(phase));
        when(maListTodoProjectRepository.findTodoListNotStratedByProjectAndPhase(any(), any(), any()))
                .thenReturn(List.of());
        when(maListTodoProjectRepository.save(any())).thenReturn(new TodoList());
        when(toDoRepository.findAllById(List.of("todo1"))).thenReturn(List.of(todo));
        when(phaseTodoProjectRepository.existsDistinctByTodoAndPhaseProject(todo, phase)).thenReturn(false);

        ResponseObject<?> response = maPhaseService.createTodoByPhase(request);

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals("Thêm công việc vào giai đoạn thành công", response.getMessage());
    }

    @Test
    void testUpdatePhase_Success() {
        String id = "phase1";
        MACreatePhaseRequest request = new MACreatePhaseRequest();
        request.setName("Phase Updated");
        request.setCode("P01");
        request.setStartTime(Long.valueOf("1000000"));
        request.setEndTime(Long.valueOf("2000000"));
        request.setIdProject("project1");

        PhaseProject phase = new PhaseProject();
        Project project = new Project();

        when(maPhaseRepository.findById(id)).thenReturn(Optional.of(phase));
        when(projectRepository.findById("project1")).thenReturn(Optional.of(project));
        when(maPhaseRepository.save(any())).thenReturn(phase);

        ResponseObject<?> response = maPhaseService.updatePhase(id, request);

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals("Cập nhật thành công giai đoạn", response.getMessage());
    }


    @Test
    void testUpdatePhase_ProjectNotFound() {
        String id = "phase1";
        MACreatePhaseRequest request = new MACreatePhaseRequest();
        request.setIdProject("project1");

        PhaseProject phase = new PhaseProject();
        when(maPhaseRepository.findById(id)).thenReturn(Optional.of(phase));
        when(projectRepository.findById("project1")).thenReturn(Optional.empty());

        ResponseObject<?> response = maPhaseService.updatePhase(id, request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatus());
        assertEquals("Không tìm thấy project", response.getMessage());
    }
    @Test
    void testUpdatePhase_PhaseNotFound() {
        String id = "phase1";
        MACreatePhaseRequest request = new MACreatePhaseRequest();

        when(maPhaseRepository.findById(id)).thenReturn(Optional.empty());

        ResponseObject<?> response = maPhaseService.updatePhase(id, request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatus());
        assertEquals("Không tìm thấy giai đoạn công việc này", response.getMessage());
    }


    @Test
    void testCreatePhase_ProjectNotFound() {
        MACreatePhaseRequest request = new MACreatePhaseRequest();
        request.setIdProject("not-exist");

        when(projectRepository.findById("not-exist")).thenReturn(Optional.empty());

        ResponseObject<?> response = maPhaseService.createPhase(request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatus());
        assertEquals("Không tìm thấy project", response.getMessage());
    }
    @Test
    void testCreateTodoByPhase_PhaseOrProjectNotFound() {
        MATodoByPhase request = new MATodoByPhase();
        request.setIdProject("not-exist");
        request.setIdPhase("not-exist-phase");

        when(projectRepository.findById("not-exist")).thenReturn(Optional.empty());
        ResponseObject<?> response1 = maPhaseService.createTodoByPhase(request);
        assertEquals(HttpStatus.NOT_FOUND, response1.getStatus());

        when(projectRepository.findById("project1")).thenReturn(Optional.of(new Project()));
        when(maPhaseRepository.findById("not-exist-phase")).thenReturn(Optional.empty());
        request.setIdProject("project1");
        ResponseObject<?> response2 = maPhaseService.createTodoByPhase(request);
        assertEquals(HttpStatus.BAD_REQUEST, response2.getStatus());
        assertEquals(HttpStatus.BAD_REQUEST, response1.getStatus());

    }
    @Test
    void testDeletePhase_Success() {
        String id = "phase1";
        PhaseProject phase = new PhaseProject();
        when(maPhaseRepository.findById(id)).thenReturn(Optional.of(phase));
        doNothing().when(maPhaseRepository).deleteById(id);

        ResponseObject<?> response = maPhaseService.deletePhase(id);

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals("Xóa thành công giai đoạn", response.getMessage());
    }
    @Test
    void testDeleteTodoByPhase_Success() {
        String id = "todo1";
        Todo todo = new Todo();
        when(toDoRepository.findById(id)).thenReturn(Optional.of(todo));
        doNothing().when(toDoRepository).deleteById(id);

        ResponseObject<?> response = maPhaseService.deleteTodoByPhase(id);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatus());
        assertEquals("Không tìm thấy giai đoạn ", response.getMessage());
    }

    @Test
    void testUpdatePhase_EndTimeNotAfterStartTime() {
        String id = "phase1";
        MACreatePhaseRequest request = new MACreatePhaseRequest();
        request.setIdProject("project1");
        request.setStartTime(1000L);
        request.setEndTime(900L); // endTime < startTime

        PhaseProject phase = new PhaseProject();
        Project project = new Project();

        when(maPhaseRepository.findById(id)).thenReturn(Optional.of(phase));
        when(projectRepository.findById("project1")).thenReturn(Optional.of(project));

        ResponseObject<?> response = maPhaseService.updatePhase(id, request);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatus());
        assertEquals("Thời gian kết thúc phải lớn hơn thời gian bắt đầu", response.getMessage());
    }
    @Test
    void testCreateTodoByPhase_IdTodoNullOrEmpty() {
        MATodoByPhase request = new MATodoByPhase();
        request.setIdProject("p1");
        request.setIdPhase("phase1");
        request.setIdTodo(null); // hoặc request.setIdTodo(Collections.emptyList());

        ResponseObject<?> response = maPhaseService.createTodoByPhase(request);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatus());
        assertEquals("Danh sách công việc không được để trống", response.getMessage());
    }
    @Test
    void testCreateTodoByPhase_TodoNotFound() {
        MATodoByPhase request = new MATodoByPhase();
        request.setIdProject("project1");
        request.setIdPhase("phase1");
        request.setIdTodo(List.of("todo1", "todo2"));
        request.setIndex("1");

        Project project = new Project(); project.setId("project1");
        PhaseProject phase = new PhaseProject(); phase.setId("phase1");

        when(projectRepository.findById("project1")).thenReturn(Optional.of(project));
        when(maPhaseRepository.findById("phase1")).thenReturn(Optional.of(phase));
        when(maListTodoProjectRepository.findTodoListNotStratedByProjectAndPhase(any(), any(), any())).thenReturn(List.of(new TodoList()));
        when(toDoRepository.findAllById(List.of("todo1", "todo2"))).thenReturn(List.of(new Todo())); // Chỉ 1 todo

        ResponseObject<?> response = maPhaseService.createTodoByPhase(request);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatus());
        assertEquals("Một hoặc nhiều công việc không tồn tại", response.getMessage());
    }
    @Test
    void testDeleteTodoByPhase_PhaseTodoProjectNotFound() {
        String id = "todo1";
        Todo todo = new Todo();
        when(toDoRepository.findById(id)).thenReturn(Optional.of(todo));
        when(phaseTodoProjectRepository.findByTodo(todo)).thenReturn(Optional.empty());

        ResponseObject<?> response = maPhaseService.deleteTodoByPhase(id);

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals("Không tìm thấy giai đoạn", response.getMessage());
    }
    @Test
    void testDetailPhase_NotFound() {
        String phaseId = "not-exist";
        when(maPhaseRepository.findById(phaseId)).thenReturn(Optional.empty());

        ResponseObject<?> response = maPhaseService.detailPhase(phaseId);

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals("Lấy dữ liệu thành công", response.getMessage());
        assertEquals(Optional.empty(), response.getData());
    }

    @Test
    void testCreatePhase_FirstPhase() {
        MACreatePhaseRequest request = new MACreatePhaseRequest();
        request.setIdProject("project-id");

        Project project = new Project();
        project.setId("project-id");

        when(projectRepository.findById("project-id")).thenReturn(Optional.of(project));
        when(maPhaseRepository.findAll()).thenReturn(Collections.emptyList());
        when(maPhaseRepository.getAllPhaseByProjectId("project-id"))
                .thenReturn(List.of(mock(MAPhaseResponse.class)));
        when(todoListHelper.genCodeTodoList("project-id")).thenReturn("TD01");
        when(todoListHelper.genIndexTodoList("project-id")).thenReturn((byte) 1);
        when(maPhaseRepository.countPhaseProjectByIdProject(anyString(), any())).thenReturn(0);

        ResponseObject<?> response = maPhaseService.createPhase(request);

        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertEquals("Thêm giai đoạn thành công", response.getMessage());
    }
    @Test
    void testUpdatePhase_DescriptionNull() {
        String id = "phase1";
        MACreatePhaseRequest request = new MACreatePhaseRequest();
        request.setName("Phase Updated");
        request.setCode("P01");
        request.setStartTime(1000L);
        request.setEndTime(2000L);
        request.setIdProject("project1");
        request.setDescriptions(null); // Không có description

        PhaseProject phase = new PhaseProject();
        Project project = new Project();

        when(maPhaseRepository.findById(id)).thenReturn(Optional.of(phase));
        when(projectRepository.findById("project1")).thenReturn(Optional.of(project));
        when(maPhaseRepository.save(any())).thenReturn(phase);

        ResponseObject<?> response = maPhaseService.updatePhase(id, request);

        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    void testCreateTodoByPhase_IdTodoEmpty() {
        MATodoByPhase request = new MATodoByPhase();
        request.setIdProject("p1");
        request.setIdPhase("phase1");
        request.setIdTodo(Collections.emptyList());

        ResponseObject<?> response = maPhaseService.createTodoByPhase(request);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatus());
    }
    @Test
    void testFindByPhaseStatus_Found() {
        Project project = new Project(); project.setId("p1");
        PhaseProject phase = new PhaseProject();
        phase.setId("phase1");
        phase.setProject(project);
        phase.setStatusPhase(StatusPhase.DANG_LAM);

        when(maPhaseRepository.findAll()).thenReturn(List.of(phase));

        ResponseObject<?> response = maPhaseService.findByPhaseStatus("p1");

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals("Tìm thấy", response.getMessage());
        assertEquals(phase, response.getData());
    }

    @Test
    void testUpdateStatusPhase_PhaseNotFound() {
        when(maPhaseRepository.findById("phaseX")).thenReturn(Optional.empty());

        ResponseObject<?> response = maPhaseService.updateStatusPhase("phaseX", StatusPhase.DANG_LAM.ordinal());

        assertEquals(HttpStatus.NOT_FOUND, response.getStatus());
    }



    @Test
    void testUpdateStatusPhase_ToDaHoanThanh_UpdateStoryPoint() {
        Project project = new Project(); project.setId("p1");
        PhaseProject phase = new PhaseProject(); phase.setId("ph1");
        phase.setProject(project);
        phase.setStatusPhase(StatusPhase.DANG_LAM);

        Todo todo = new Todo();
        todo.setStoryPoint((short) 5);
        TodoList list = new TodoList(); list.setPhaseProject(phase);
        todo.setTodoList(list);
        todo.setStatusTodo(StatusTodo.DA_HOAN_THANH);

        when(maPhaseRepository.findById("ph1")).thenReturn(Optional.of(phase));
        when(velocityRecordRepository.findAll()).thenReturn(Collections.emptyList());
        when(toDoRepository.findAll()).thenReturn(List.of(todo));
        when(projectRepository.findProjectById("p1")).thenReturn(Optional.of(project));
        MAPhaseResponse mockRes = mock(MAPhaseResponse.class);
        when(maPhaseRepository.getAllPhaseByProjectId("p1")).thenReturn(List.of(mockRes));


        ResponseObject<?> response = maPhaseService.updateStatusPhase("ph1", StatusPhase.DA_HOAN_THANH.ordinal());

        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    void testUpdateStatusPhase_CountAllPhaseZero() {
        Project project = new Project(); project.setId("p1"); project.setName("Proj");
        PhaseProject phase = new PhaseProject(); phase.setId("ph1");
        phase.setProject(project);
        phase.setStatusPhase(StatusPhase.CHUA_HOAN_THANH);

        when(maPhaseRepository.findById("ph1")).thenReturn(Optional.of(phase));
        when(velocityRecordRepository.findAll()).thenReturn(Collections.emptyList());
        when(capacityEstimateRepository.findAll()).thenReturn(Collections.emptyList());
        when(projectRepository.findProjectById("p1")).thenReturn(Optional.of(project));
        when(maPhaseRepository.getAllPhaseByProjectId("p1")).thenReturn(Collections.emptyList());

        ResponseObject<?> response = maPhaseService.updateStatusPhase("ph1", StatusPhase.DANG_LAM.ordinal());

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(0.0f, project.getProgress());
    }




    @Test
    void testTb_VelocityNormalAndZero() {
        when(velocityRecordRepository.avgActualPoint("p1")).thenReturn(10f);
        when(velocityRecordRepository.avgActualWorkingDay("p1")).thenReturn(5f);
        Double v1 = maPhaseServiceTestHelper_tb("p1");
        assertEquals(2.0, v1);

        when(velocityRecordRepository.avgActualWorkingDay("p1")).thenReturn(0f);
        Double v2 = maPhaseServiceTestHelper_tb("p1");
        assertEquals(1.0, v2);
    }

    // helper để gọi tb qua reflection
    private Double maPhaseServiceTestHelper_tb(String projectId) {
        try {
            var m = MAPhaseServiceImpl.class.getDeclaredMethod("tb", String.class);
            m.setAccessible(true);
            return (Double) m.invoke(maPhaseService, projectId);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    @Test
    void testUpdateStatusPhase_ToDangLam_SendEmail() {
        Project project = new Project(); project.setId("p1"); project.setName("Proj");
        PhaseProject phase = new PhaseProject(); phase.setId("ph1");
        phase.setProject(project);
        phase.setStatusPhase(StatusPhase.CHUA_HOAN_THANH);

        when(maPhaseRepository.findById("ph1")).thenReturn(Optional.of(phase));
        when(velocityRecordRepository.findAll()).thenReturn(Collections.emptyList());
        when(maPhaseStaffProjectRepository.getStaffByPhaseProject("ph1")).thenReturn(List.of("s1@mail.com"));
        when(maPhaseStaffProjectRepository.getStudentByPhaseProject("ph1")).thenReturn(List.of("st1@mail.com"));
        when(capacityEstimateRepository.findAll()).thenReturn(Collections.emptyList());
        when(projectRepository.findProjectById("p1")).thenReturn(Optional.of(project));
        MAPhaseResponse mockRes = mock(MAPhaseResponse.class);
        when(maPhaseRepository.getAllPhaseByProjectId("p1")).thenReturn(List.of(mockRes));

        ResponseObject<?> res = maPhaseService.updateStatusPhase("ph1", StatusPhase.DANG_LAM.ordinal());

        assertEquals(HttpStatus.OK, res.getStatus());
        verify(asyncEmailService).sendEmailToStaffs(any(), anyString(), anyString(), anyString());
        verify(asyncEmailService).sendEmailToStudents(any(), anyString(), anyString(), anyString());
    }

    @Test
    void testUpdateStatusPhase_ToDaHoanThanh_StoryPoint() {
        Project project = new Project(); project.setId("p1");
        PhaseProject phase = new PhaseProject(); phase.setId("ph1");
        phase.setProject(project);
        phase.setStatusPhase(StatusPhase.DANG_LAM);

        Todo todo = new Todo();
        todo.setStoryPoint((short) 5);
        TodoList list = new TodoList(); list.setPhaseProject(phase);
        todo.setTodoList(list);
        todo.setStatusTodo(StatusTodo.DA_HOAN_THANH);

        when(maPhaseRepository.findById("ph1")).thenReturn(Optional.of(phase));
        when(velocityRecordRepository.findAll()).thenReturn(Collections.emptyList());
        when(toDoRepository.findAll()).thenReturn(List.of(todo));
        when(projectRepository.findProjectById("p1")).thenReturn(Optional.of(project));
        MAPhaseResponse mockRes = mock(MAPhaseResponse.class);
        when(maPhaseRepository.getAllPhaseByProjectId("p1")).thenReturn(List.of(mockRes));

        ResponseObject<?> res = maPhaseService.updateStatusPhase("ph1", StatusPhase.DA_HOAN_THANH.ordinal());

        assertEquals(HttpStatus.OK, res.getStatus());
    }

    @Test
    void testUpdateStatusPhase_CountAllPhaseZero1() {
        Project project = new Project(); project.setId("p1");
        PhaseProject phase = new PhaseProject(); phase.setId("ph1");
        phase.setProject(project);

        when(maPhaseRepository.findById("ph1")).thenReturn(Optional.of(phase));
        when(velocityRecordRepository.findAll()).thenReturn(Collections.emptyList());
        when(projectRepository.findProjectById("p1")).thenReturn(Optional.of(project));
        when(maPhaseRepository.getAllPhaseByProjectId("p1")).thenReturn(Collections.emptyList());

        ResponseObject<?> res = maPhaseService.updateStatusPhase("ph1", StatusPhase.DANG_LAM.ordinal());

        assertEquals(HttpStatus.OK, res.getStatus());
        assertEquals(0.0f, project.getProgress());
    }

    @Test
    void testUpdateStatusPhase_IndexStatusNull() {
        Project project = new Project(); project.setId("p1");
        PhaseProject phase = new PhaseProject(); phase.setId("ph1");
        phase.setProject(project);

        when(maPhaseRepository.findById("ph1")).thenReturn(Optional.of(phase));
        when(velocityRecordRepository.findAll()).thenReturn(Collections.emptyList());
        when(projectRepository.findProjectById("p1")).thenReturn(Optional.of(project));
        MAPhaseResponse mockRes = mock(MAPhaseResponse.class);
        when(maPhaseRepository.getAllPhaseByProjectId("p1")).thenReturn(List.of(mockRes));

        ResponseObject<?> res = maPhaseService.updateStatusPhase("ph1", null);

        assertEquals(HttpStatus.OK, res.getStatus());
    }

    @Test
    void testUpdateStatusPhase_ExistingVelocityRecord() {
        Project project = new Project(); project.setId("p1");
        PhaseProject phase = new PhaseProject(); phase.setId("ph1");
        phase.setProject(project);

        VelocityRecord vr = new VelocityRecord(); vr.setPhaseProject(phase);

        when(maPhaseRepository.findById("ph1")).thenReturn(Optional.of(phase));
        when(velocityRecordRepository.findAll()).thenReturn(List.of(vr));
        when(projectRepository.findProjectById("p1")).thenReturn(Optional.of(project));
        MAPhaseResponse mockRes = mock(MAPhaseResponse.class);
        when(maPhaseRepository.getAllPhaseByProjectId("p1")).thenReturn(List.of(mockRes));

        ResponseObject<?> res = maPhaseService.updateStatusPhase("ph1", StatusPhase.QUA_HAN.ordinal());

        assertEquals(HttpStatus.OK, res.getStatus());
    }

    @Test
    void testDeletePhase_Success1() {
        PhaseProject phase = new PhaseProject(); phase.setId("ph1");
        when(maPhaseRepository.findById("ph1")).thenReturn(Optional.of(phase));
        ResponseObject<?> res = maPhaseService.deletePhase("ph1");
        assertEquals(HttpStatus.OK, res.getStatus());
        assertEquals(EntityStatus.INACTIVE, phase.getStatus());
        verify(maPhaseRepository).save(phase);
    }


// ---- private helpers ----

    @Test
    void testTb_Branches() throws Exception {
        when(velocityRecordRepository.avgActualPoint("p1")).thenReturn(10f);
        when(velocityRecordRepository.avgActualWorkingDay("p1")).thenReturn(5f);
        Double v1 = invokeTb("p1");
        assertEquals(2.0, v1);

        when(velocityRecordRepository.avgActualWorkingDay("p1")).thenReturn(0f);
        Double v2 = invokeTb("p1");
        assertEquals(1.0, v2);
    }

    private Double invokeTb(String projectId) throws Exception {
        var m = MAPhaseServiceImpl.class.getDeclaredMethod("tb", String.class);
        m.setAccessible(true);
        return (Double) m.invoke(maPhaseService, projectId);
    }
    @Test
    void testCommit_WithCapacityEstimate() throws Exception {
        Project project = new Project(); project.setId("p1");
        PhaseProject phase = new PhaseProject(); phase.setId("ph1");
        phase.setProject(project);

        CapacityEstimate ce1 = new CapacityEstimate();
        ce1.setPhaseProject(phase);
        ce1.setWorkday(Short.valueOf("5"));
        ce1.setStatus(EntityStatus.ACTIVE);

        CapacityEstimate ce2 = new CapacityEstimate();
        ce2.setPhaseProject(phase);
        ce2.setWorkday(Short.valueOf("3"));
        ce2.setStatus(EntityStatus.ACTIVE);

        when(capacityEstimateRepository.findAll()).thenReturn(List.of(ce1, ce2));
        when(velocityRecordRepository.avgActualPoint("p1")).thenReturn(10f);
        when(velocityRecordRepository.avgActualWorkingDay("p1")).thenReturn(5f);

        Double result = invokeCommit(phase);
        assertEquals(16.0, result); // velocity=2, totalWorkday=8
    }


    private Double invokeCommit(PhaseProject phase) throws Exception {
        var m = MAPhaseServiceImpl.class.getDeclaredMethod("commit", PhaseProject.class);
        m.setAccessible(true);
        return (Double) m.invoke(maPhaseService, phase);
    }

    @Test
    void testStoryPoint_WithTodos() throws Exception {
        PhaseProject phase = new PhaseProject(); phase.setId("ph1");

        Todo todo1 = new Todo();
        todo1.setStoryPoint((short) 3);
        todo1.setStatusTodo(StatusTodo.DA_HOAN_THANH);
        TodoList list1 = new TodoList(); list1.setPhaseProject(phase);
        todo1.setTodoList(list1);

        Todo todo2 = new Todo();
        todo2.setStoryPoint((short) 2);
        todo2.setStatusTodo(StatusTodo.QUA_HAN);
        TodoList list2 = new TodoList(); list2.setPhaseProject(phase);
        todo2.setTodoList(list2);

        when(toDoRepository.findAll()).thenReturn(List.of(todo1, todo2));

        Double result = invokeStoryPoint(phase);
        assertEquals(5.0, result);
    }

    private Double invokeStoryPoint(PhaseProject phase) throws Exception {
        var m = MAPhaseServiceImpl.class.getDeclaredMethod("StoryPoint", PhaseProject.class);
        m.setAccessible(true);
        return (Double) m.invoke(maPhaseService, phase);
    }

    @Test
    void testUpdateStatusPhase_ToQuaHan_UpdateStoryPoint() {
        Project project = new Project(); project.setId("p1");
        PhaseProject phase = new PhaseProject(); phase.setId("ph1");
        phase.setProject(project);
        phase.setStatusPhase(StatusPhase.DANG_LAM);

        Todo todo = new Todo();
        todo.setStoryPoint((short) 4);
        TodoList list = new TodoList(); list.setPhaseProject(phase);
        todo.setTodoList(list);
        todo.setStatusTodo(StatusTodo.QUA_HAN);

        when(maPhaseRepository.findById("ph1")).thenReturn(Optional.of(phase));
        when(velocityRecordRepository.findAll()).thenReturn(Collections.emptyList());
        when(toDoRepository.findAll()).thenReturn(List.of(todo));
        when(projectRepository.findProjectById("p1")).thenReturn(Optional.of(project));
        MAPhaseResponse mockRes = mock(MAPhaseResponse.class);
        when(maPhaseRepository.getAllPhaseByProjectId("p1")).thenReturn(List.of(mockRes));

        ResponseObject<?> res = maPhaseService.updateStatusPhase("ph1", StatusPhase.QUA_HAN.ordinal());

        assertEquals(HttpStatus.OK, res.getStatus());
    }

    @Test
    void testUpdateStatusPhase_WithExistingVelocityRecord() {
        Project project = new Project(); project.setId("p1");
        PhaseProject phase = new PhaseProject(); phase.setId("ph1");
        phase.setProject(project);

        VelocityRecord vr = new VelocityRecord();
        vr.setPhaseProject(phase);

        when(maPhaseRepository.findById("ph1")).thenReturn(Optional.of(phase));
        when(velocityRecordRepository.findAll()).thenReturn(List.of(vr));
        when(projectRepository.findProjectById("p1")).thenReturn(Optional.of(project));
        MAPhaseResponse mockRes = mock(MAPhaseResponse.class);
        when(maPhaseRepository.getAllPhaseByProjectId("p1")).thenReturn(List.of(mockRes));

        ResponseObject<?> res = maPhaseService.updateStatusPhase("ph1", StatusPhase.DANG_LAM.ordinal());

        assertEquals(HttpStatus.OK, res.getStatus());
        verify(velocityRecordRepository).save(vr);
    }

    @Test
    void testDeletePhase_NotFoundProject() {
        when(maPhaseRepository.findById("xx")).thenReturn(Optional.empty());
        ResponseObject<?> res = maPhaseService.deletePhase("xx");
        assertEquals(HttpStatus.NOT_FOUND, res.getStatus());
        assertEquals("Không tìm thấy giai đoạn", res.getMessage());
    }

    @Test
    void testDeletePhase_SetInactive() {
        PhaseProject phase = new PhaseProject(); phase.setId("ph1");
        when(maPhaseRepository.findById("ph1")).thenReturn(Optional.of(phase));

        ResponseObject<?> res = maPhaseService.deletePhase("ph1");

        assertEquals(HttpStatus.OK, res.getStatus());
        assertEquals(EntityStatus.INACTIVE, phase.getStatus());
        verify(maPhaseRepository).save(phase);
    }

    @Test
    void testCreatePhase_ProjectNotFound_ShouldReturnNotFound() {
        MACreatePhaseRequest req = new MACreatePhaseRequest();
        req.setIdProject("x");

        when(projectRepository.findById("x")).thenReturn(Optional.empty());

        ResponseObject<?> res = maPhaseService.createPhase(req);

        assertEquals(HttpStatus.NOT_FOUND, res.getStatus());
        assertEquals("Không tìm thấy project", res.getMessage());
    }

    @Test
    void testCreatePhase_FirstPhase_ShouldCreateWithDefaultValues() {
        Project project = new Project(); project.setId("p1");

        MACreatePhaseRequest req = new MACreatePhaseRequest();
        req.setIdProject("p1");

        when(projectRepository.findById("p1")).thenReturn(Optional.of(project));
        when(maPhaseRepository.findAll()).thenReturn(Collections.emptyList());
        when(maPhaseRepository.getAllPhaseByProjectId("p1"))
                .thenReturn(List.of(mock(MAPhaseResponse.class)));
        when(todoListHelper.genCodeTodoList("p1")).thenReturn("TD01");
        when(todoListHelper.genIndexTodoList("p1")).thenReturn((byte) 1);
        when(maPhaseRepository.countPhaseProjectByIdProject(eq("p1"), any())).thenReturn(0);

        ResponseObject<?> res = maPhaseService.createPhase(req);

        assertEquals(HttpStatus.CREATED, res.getStatus());
        assertEquals("Thêm giai đoạn thành công", res.getMessage());
    }

    @Test
    void testCreatePhase_WithExistingPhases_ShouldIncrementName() {
        Project project = new Project(); project.setId("p1");

        PhaseProject oldPhase = new PhaseProject();
        oldPhase.setId("old");
        oldPhase.setProject(project);
        oldPhase.setEndTime(System.currentTimeMillis());

        MACreatePhaseRequest req = new MACreatePhaseRequest();
        req.setIdProject("p1");

        when(projectRepository.findById("p1")).thenReturn(Optional.of(project));
        when(maPhaseRepository.findAll()).thenReturn(List.of(oldPhase));
        when(maPhaseRepository.getAllPhaseByProjectId("p1"))
                .thenReturn(List.of(mock(MAPhaseResponse.class), mock(MAPhaseResponse.class)));
        when(todoListHelper.genCodeTodoList("p1")).thenReturn("TD02");
        when(todoListHelper.genIndexTodoList("p1")).thenReturn((byte) 2);
        when(maPhaseRepository.countPhaseProjectByIdProject(eq("p1"), any())).thenReturn(1);

        ResponseObject<?> res = maPhaseService.createPhase(req);

        assertEquals(HttpStatus.CREATED, res.getStatus());
        assertEquals("Thêm giai đoạn thành công", res.getMessage());
    }

    @Test
    void testDetailPhase_Found() {
        PhaseProject phase = new PhaseProject(); phase.setId("ph1");
        when(maPhaseRepository.findById("ph1")).thenReturn(Optional.of(phase));

        ResponseObject<?> res = maPhaseService.detailPhase("ph1");

        assertEquals(HttpStatus.OK, res.getStatus());
        assertEquals(phase, ((Optional<?>) res.getData()).get());
    }

    @Test
    void testDetailPhase_NotFound1() {
        when(maPhaseRepository.findById("xx")).thenReturn(Optional.empty());

        ResponseObject<?> res = maPhaseService.detailPhase("xx");

        assertEquals(HttpStatus.OK, res.getStatus());
        assertEquals(Optional.empty(), res.getData());
    }

    @Test
    void testCreateTodoByPhase_IdTodoNull_ShouldReturnBadRequest() {
        MATodoByPhase req = new MATodoByPhase();
        req.setIdProject("p1");
        req.setIdPhase("ph1");
        req.setIdTodo(null);

        ResponseObject<?> res = maPhaseService.createTodoByPhase(req);

        assertEquals(HttpStatus.BAD_REQUEST, res.getStatus());
        assertEquals("Danh sách công việc không được để trống", res.getMessage());
    }

    @Test
    void testCreateTodoByPhase_ProjectNotFound_ShouldReturnNotFound() {
        MATodoByPhase req = new MATodoByPhase();
        req.setIdProject("xx");
        req.setIdPhase("ph1");
        req.setIdTodo(List.of("t1"));

        when(projectRepository.findById("xx")).thenReturn(Optional.empty());

        ResponseObject<?> res = maPhaseService.createTodoByPhase(req);

        assertEquals(HttpStatus.NOT_FOUND, res.getStatus());
    }

    @Test
    void testCreateTodoByPhase_PhaseNotFound_ShouldReturnNotFound() {
        MATodoByPhase req = new MATodoByPhase();
        req.setIdProject("p1");
        req.setIdPhase("xx");
        req.setIdTodo(List.of("t1"));

        when(projectRepository.findById("p1")).thenReturn(Optional.of(new Project()));
        when(maPhaseRepository.findById("xx")).thenReturn(Optional.empty());

        ResponseObject<?> res = maPhaseService.createTodoByPhase(req);

        assertEquals(HttpStatus.NOT_FOUND, res.getStatus());
    }

    @Test
    void testCreateTodoByPhase_TodoNotFound_ShouldReturnNotFound() {
        Project project = new Project(); project.setId("p1");
        PhaseProject phase = new PhaseProject(); phase.setId("ph1"); phase.setProject(project);

        MATodoByPhase req = new MATodoByPhase();
        req.setIdProject("p1");
        req.setIdPhase("ph1");
        req.setIdTodo(List.of("t1", "t2"));
        req.setIndex("1");

        when(projectRepository.findById("p1")).thenReturn(Optional.of(project));
        when(maPhaseRepository.findById("ph1")).thenReturn(Optional.of(phase));
        when(maListTodoProjectRepository.findTodoListNotStratedByProjectAndPhase(any(), any(), any()))
                .thenReturn(List.of(new TodoList()));
        when(toDoRepository.findAllById(List.of("t1", "t2"))).thenReturn(List.of(new Todo())); // chỉ 1 todo

        ResponseObject<?> res = maPhaseService.createTodoByPhase(req);

        assertEquals(HttpStatus.NOT_FOUND, res.getStatus());
        assertEquals("Một hoặc nhiều công việc không tồn tại", res.getMessage());
    }

    @Test
    void testCreateTodoByPhase_Success_ShouldAssignTodo() {
        Project project = new Project(); project.setId("p1");
        PhaseProject phase = new PhaseProject(); phase.setId("ph1"); phase.setProject(project);
        Todo todo = new Todo(); todo.setId("t1");

        MATodoByPhase req = new MATodoByPhase();
        req.setIdProject("p1");
        req.setIdPhase("ph1");
        req.setIdTodo(List.of("t1"));
        req.setIndex("1");
        req.setIdUser("u1");
        req.setNameUser("User A");
        req.setUrlPath("/url");

        when(projectRepository.findById("p1")).thenReturn(Optional.of(project));
        when(maPhaseRepository.findById("ph1")).thenReturn(Optional.of(phase));
        when(maListTodoProjectRepository.findTodoListNotStratedByProjectAndPhase(any(), any(), any()))
                .thenReturn(Collections.emptyList());
        when(maListTodoProjectRepository.save(any())).thenReturn(new TodoList());
        when(toDoRepository.findAllById(List.of("t1"))).thenReturn(List.of(todo));
        when(phaseTodoProjectRepository.existsDistinctByTodoAndPhaseProject(todo, phase)).thenReturn(false);

        ResponseObject<?> res = maPhaseService.createTodoByPhase(req);

        assertEquals(HttpStatus.OK, res.getStatus());
        assertEquals("Thêm công việc vào giai đoạn thành công", res.getMessage());
    }

    @Test
    void testCreatePhaseTOdoList_ReturnNull() {
        MACreatePhaseRequest req = new MACreatePhaseRequest();
        assertNull(maPhaseService.createPhaseTOdoList(req));
    }

    @Test
    void testUpdatePhase_PhaseNotFound_ShouldReturnNotFound() {
        MACreatePhaseRequest req = new MACreatePhaseRequest();
        req.setIdProject("p1");

        when(maPhaseRepository.findById("xx")).thenReturn(Optional.empty());

        ResponseObject<?> res = maPhaseService.updatePhase("xx", req);

        assertEquals(HttpStatus.NOT_FOUND, res.getStatus());
        assertEquals("Không tìm thấy giai đoạn công việc này", res.getMessage());
    }

    @Test
    void testUpdatePhase_ProjectNotFound_ShouldReturnNotFound() {
        MACreatePhaseRequest req = new MACreatePhaseRequest();
        req.setIdProject("p1");

        PhaseProject phase = new PhaseProject(); phase.setId("ph1");
        when(maPhaseRepository.findById("ph1")).thenReturn(Optional.of(phase));
        when(projectRepository.findById("p1")).thenReturn(Optional.empty());

        ResponseObject<?> res = maPhaseService.updatePhase("ph1", req);

        assertEquals(HttpStatus.NOT_FOUND, res.getStatus());
        assertEquals("Không tìm thấy project", res.getMessage());
    }

    @Test
    void testUpdatePhase_EndTimeBeforeStartTime_ShouldReturnBadRequest() {
        MACreatePhaseRequest req = new MACreatePhaseRequest();
        req.setIdProject("p1");
        req.setStartTime(2000L);
        req.setEndTime(1000L);

        PhaseProject phase = new PhaseProject(); phase.setId("ph1");
        when(maPhaseRepository.findById("ph1")).thenReturn(Optional.of(phase));
        when(projectRepository.findById("p1")).thenReturn(Optional.of(new Project()));

        ResponseObject<?> res = maPhaseService.updatePhase("ph1", req);

        assertEquals(HttpStatus.BAD_REQUEST, res.getStatus());
        assertEquals("Thời gian kết thúc phải lớn hơn thời gian bắt đầu", res.getMessage());
    }

    @Test
    void testUpdatePhase_SuccessWithDescription() {
        MACreatePhaseRequest req = new MACreatePhaseRequest();
        req.setIdProject("p1");
        req.setName("Phase X");
        req.setCode("PX");
        req.setStartTime(1000L);
        req.setEndTime(2000L);
        req.setDescriptions("Desc");

        Project project = new Project(); project.setId("p1");
        PhaseProject phase = new PhaseProject(); phase.setId("ph1");

        when(maPhaseRepository.findById("ph1")).thenReturn(Optional.of(phase));
        when(projectRepository.findById("p1")).thenReturn(Optional.of(project));
        when(maPhaseRepository.save(any())).thenReturn(phase);

        ResponseObject<?> res = maPhaseService.updatePhase("ph1", req);

        assertEquals(HttpStatus.OK, res.getStatus());
        assertEquals("Cập nhật thành công giai đoạn", res.getMessage());
    }

    @Test
    void testUpdatePhase_SuccessWithoutDescription() {
        MACreatePhaseRequest req = new MACreatePhaseRequest();
        req.setIdProject("p1");
        req.setName("Phase X");
        req.setCode("PX");
        req.setStartTime(1000L);
        req.setEndTime(2000L);
        req.setDescriptions(null);

        Project project = new Project(); project.setId("p1");
        PhaseProject phase = new PhaseProject(); phase.setId("ph1");

        when(maPhaseRepository.findById("ph1")).thenReturn(Optional.of(phase));
        when(projectRepository.findById("p1")).thenReturn(Optional.of(project));
        when(maPhaseRepository.save(any())).thenReturn(phase);

        ResponseObject<?> res = maPhaseService.updatePhase("ph1", req);

        assertEquals(HttpStatus.OK, res.getStatus());
        assertEquals("Cập nhật thành công giai đoạn", res.getMessage());
    }

    @Test
    void testGetAllPhase_WithRequest() {
        MAPhaseRequest req = new MAPhaseRequest();
        req.setIdProject("p1");
        Pageable pageable = Pageable.unpaged();

        Page<MAPhaseResponse> page = new PageImpl<>(List.of(mock(MAPhaseResponse.class)));
        when(maPhaseRepository.getAllPhaseProject(any(), eq("p1"))).thenReturn(page);

        ResponseObject<?> res = maPhaseService.getAllPhase(req);

        assertEquals(HttpStatus.OK, res.getStatus());
        assertEquals("Lấy dự liệu giai đoạn thành công", res.getMessage());
        assertEquals(page, res.getData());
    }

    @Test
    void testGetAllPhase_WithProjectId() {
        List<MAPhaseResponse> list = List.of(mock(MAPhaseResponse.class));
        when(maPhaseRepository.getAllPhaseProjectStatistics("p1")).thenReturn(list);

        ResponseObject<?> res = maPhaseService.getAllPhase("p1");

        assertEquals(HttpStatus.OK, res.getStatus());
        assertEquals("Lấy dữ liệu thành công", res.getMessage());
        assertEquals(list, res.getData());
    }

    @Test
    void testGetAllSprint_WithStatusAndTime() {
        MAPhaseRequest req = new MAPhaseRequest();
        req.setIdProject("p1");
        req.setStatus(StatusPhase.DANG_LAM.ordinal());
        req.setTime(System.currentTimeMillis());
        req.setSearch("search");

        Page<MAPhaseResponse> page = new PageImpl<>(List.of(mock(MAPhaseResponse.class)));

        when(maPhaseRepository.getAllSprintProject(
                any(Pageable.class),
                eq("p1"),
                eq(StatusPhase.DANG_LAM),
                anyLong(),
                anyLong(),
                eq("search")
        )).thenReturn(page);

        ResponseObject<?> res = maPhaseService.getAllSprint(req);

        assertEquals(HttpStatus.OK, res.getStatus());
        assertEquals("Lấy dữ liệu giai đoạn thành công", res.getMessage());
        assertEquals(page, res.getData());
    }

    @Test
    void testGetAllSprint_WithNullStatusAndNullTime() {
        MAPhaseRequest req = new MAPhaseRequest();
        req.setIdProject("p1");
        req.setStatus(null);
        req.setTime(null);
        req.setSearch(null);

        Page<MAPhaseResponse> page = new PageImpl<>(Collections.emptyList());

        when(maPhaseRepository.getAllSprintProject(
                any(Pageable.class),
                eq("p1"),
                isNull(),
                isNull(),
                isNull(),
                isNull()
        )).thenReturn(page);

        ResponseObject<?> res = maPhaseService.getAllSprint(req);

        assertEquals(HttpStatus.OK, res.getStatus());
        assertEquals("Lấy dữ liệu giai đoạn thành công", res.getMessage());
        assertEquals(page, res.getData());
    }


}
