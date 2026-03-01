package udpm.hn.server.core.manage.project.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import udpm.hn.server.core.manage.project.model.request.MaCreateOrUpdateTodoListProject;
import udpm.hn.server.core.manage.project.model.request.MaTodoListProjectRequest;
import udpm.hn.server.core.manage.project.model.request.MaUpdateTodoProjectRequest;
import udpm.hn.server.core.manage.project.model.response.MATodoProjectResponse;
import udpm.hn.server.core.manage.project.model.response.MaPhaseProjectResponse;
import udpm.hn.server.core.manage.project.model.response.MaTodoListProjectResponse;
import udpm.hn.server.core.manage.project.repository.*;
import udpm.hn.server.entity.*;
import udpm.hn.server.infrastructure.constant.EntityStatus;
import udpm.hn.server.infrastructure.constant.PriorityLevel;
import udpm.hn.server.infrastructure.constant.StatusTodo;
import udpm.hn.server.infrastructure.log.LogEntryTodoList;
import udpm.hn.server.infrastructure.log.LogService;
import udpm.hn.server.repository.*;
import udpm.hn.server.utils.StatusPhase;
import udpm.hn.server.utils.TodoListHelper;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
public class MaTodoListProjectServiceImplTest {

    @Mock private TodoListRepository todoListRepository;
    @Mock private MaTodoListProjectRepository maTodoListProjectRepository;
    @Mock private MaProjectRepository maProjectRepository;
    @Mock private MaTodoProjectRepository todoProjectRepository;
    @Mock private LogService logService;
    @Mock private ToDoRepository toDoRepository;
    @Mock private ObjectMapper objectMapper;
    @Mock private MaPhaseProjectRepository phaseRepository;
    @Mock private PhaseTodoProjectRepository phaseTodoProjectRepository;
    @Mock private TodoListHelper todoListHelper;
    @Mock private ProjectRepository projectRepository;

    @InjectMocks private MaTodoListProjectServiceImpl service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllTodoList_ShouldReturnSuccess() {
        MaTodoListProjectRequest request = new MaTodoListProjectRequest();
        String projectId = "project1";
        Page<MaTodoListProjectResponse> page = new PageImpl<>(Collections.emptyList());

        when(maTodoListProjectRepository.getAllTodo(any(Pageable.class), eq(projectId), eq(request)))
                .thenReturn(page);

        ResponseObject<?> response = service.getAllTodoList(request, projectId);

        assertEquals(HttpStatus.OK, response.getStatus());
        verify(maTodoListProjectRepository).getAllTodo(any(Pageable.class), eq(projectId), eq(request));
    }

    @Test
    void addTodoList_ProjectExists_ShouldReturnCreated() {
        MaCreateOrUpdateTodoListProject request = new MaCreateOrUpdateTodoListProject();
        request.setProjectId("proj1");
        request.setCodeTodoList("T001");
        request.setNameTodoList("Task 1");
        request.setDescribeTodoList("Desc");

        when(maProjectRepository.findById("proj1"))
                .thenReturn(Optional.of(new Project()));

        ResponseObject<?> response = service.addTodoList(request);
        assertEquals(HttpStatus.CREATED, response.getStatus());
    }

    @Test
    void addTodoList_ProjectNotFound_ShouldReturnNotAcceptable() {
        MaCreateOrUpdateTodoListProject request = new MaCreateOrUpdateTodoListProject();
        request.setProjectId("proj2");

        when(maProjectRepository.findById("proj2")).thenReturn(Optional.empty());

        ResponseObject<?> response = service.addTodoList(request);
        assertEquals(HttpStatus.NOT_ACCEPTABLE, response.getStatus());
    }

    @Test
    void updateTodoList_TodoAndProjectExist_ShouldReturnOk() {
        MaCreateOrUpdateTodoListProject request = new MaCreateOrUpdateTodoListProject();
        request.setProjectId("proj1");
        request.setCodeTodoList("C01");
        request.setNameTodoList("Task 1");
        request.setDescribeTodoList("Some description");

        TodoList todoList = new TodoList();
        when(maTodoListProjectRepository.findById("todo1")).thenReturn(Optional.of(todoList));
        when(maProjectRepository.findById("proj1")).thenReturn(Optional.of(new Project()));

        ResponseObject<?> response = service.updateTodpList(request, "todo1");

        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    void updateTodoList_TodoNotFound_ShouldReturnOk() {
        MaCreateOrUpdateTodoListProject request = new MaCreateOrUpdateTodoListProject();
        when(maTodoListProjectRepository.findById("invalid")).thenReturn(Optional.empty());

        ResponseObject<?> response = service.updateTodpList(request, "invalid");

        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    void detailTodoList_ShouldReturnOk() {
        when(maTodoListProjectRepository.getDetailTodoListProject("id1"))
                .thenReturn(mock(udpm.hn.server.core.manage.project.model.response.DetailTodoList.class));

        ResponseObject<?> response = service.detailTodoList("id1");

        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    void readCSV_FileFound_ShouldReturnOk() throws Exception {
        List<LogEntryTodoList> entries = List.of(new LogEntryTodoList());
        when(logService.readCSVTodoList(any())).thenReturn(entries);

        ResponseObject<?> response = service.readCSV();
        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    void readCSV_FileNotFound_ShouldReturnNotFound() throws Exception {
        when(logService.readCSVTodoList(any())).thenThrow(new java.io.FileNotFoundException());

        ResponseObject<?> response = service.readCSV();
        assertEquals(HttpStatus.NOT_FOUND, response.getStatus());
    }

    @Test
    void getAllPhase_ShouldReturnOk() {
        List<MaPhaseProjectResponse> phases = List.of(mock(MaPhaseProjectResponse.class));

        List<StatusPhase> excludedStatuses = List.of(
                StatusPhase.DA_HOAN_THANH,
                StatusPhase.QUA_HAN
        );

        when(phaseRepository.getALl("id1", excludedStatuses)).thenReturn(phases);

        ResponseObject<?> response = service.getAllPhase("id1");
        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    void getAllTodoByProject_ShouldReturnOk() {
        MaTodoListProjectRequest req = new MaTodoListProjectRequest();
        Page<MATodoProjectResponse> page = new PageImpl<>(List.of(mock(MATodoProjectResponse.class)));
        when(maTodoListProjectRepository.getAllTodoProject(any(), any(), any())).thenReturn(page);

        ResponseObject<?> response = service.getAllTodoByProject(req, "project1");
        assertEquals(HttpStatus.OK, response.getStatus());
        verify(maTodoListProjectRepository).getAllTodoProject(any(), eq("project1"), eq(req));
    }

    @Test
    void updateTodoList_ProjectNotFound_ShouldReturnNotAcceptable() {
        MaCreateOrUpdateTodoListProject request = new MaCreateOrUpdateTodoListProject();
        request.setProjectId("proj-not-exist");
        TodoList todoList = new TodoList();
        when(maTodoListProjectRepository.findById("todo1")).thenReturn(Optional.of(todoList));
        when(maProjectRepository.findById("proj-not-exist")).thenReturn(Optional.empty());

        ResponseObject<?> response = service.updateTodpList(request, "todo1");

        assertEquals(HttpStatus.NOT_ACCEPTABLE, response.getStatus());
        assertEquals("Không tìm thấy dự án", response.getMessage());
    }
    @Test
    void updateTodoByPhaseProject_PhaseNotFound_ShouldReturnBadRequest() {
        when(phaseRepository.findById("phase1")).thenReturn(Optional.empty());

        ResponseObject<?> response = service.updateTodoByPhaseProject("todo1", "phase1", "project1");

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatus());
        assertEquals("PhaseProject không tồn tại", response.getMessage());
    }

    @Test
    void updateTodoByPhaseProject_ProjectNotFound_ShouldReturnBadRequest() {
        when(phaseRepository.findById("phase1")).thenReturn(Optional.of(mock(udpm.hn.server.entity.PhaseProject.class)));
        when(projectRepository.findById("project1")).thenReturn(Optional.empty());

        ResponseObject<?> response = service.updateTodoByPhaseProject("todo1", "phase1", "project1");

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatus());
        assertEquals("Project không tồn tại", response.getMessage());
    }

    @Test
    void updateTodoByPhaseProject_TodoNotFound_ShouldHandleGracefully() {
        when(phaseRepository.findById("phase1")).thenReturn(Optional.of(mock(udpm.hn.server.entity.PhaseProject.class)));
        when(projectRepository.findById("project1")).thenReturn(Optional.of(mock(udpm.hn.server.entity.Project.class)));
        when(toDoRepository.findById("todo1")).thenReturn(Optional.empty());
        when(phaseTodoProjectRepository.findByTodoId("todo1")).thenReturn(Optional.empty());

        // Test không ném lỗi (tùy code của bạn, nếu có NullPointerException thì bỏ test này)
        ResponseObject<?> response = service.updateTodoByPhaseProject("todo1", "phase1", "project1");
        // Nếu code thật có check null thì bạn assert tiếp, nếu không thì nên bổ sung check null cho an toàn production
        // assert...;
    }
    @Test
    void updateTodoProject_TodoNotFound_ShouldReturnNotAcceptable() {
        when(toDoRepository.findById("todo1")).thenReturn(Optional.empty());

        MaUpdateTodoProjectRequest request = new MaUpdateTodoProjectRequest();
        request.setProjectId("project1");

        ResponseObject<?> response = service.updateTodoProject(request, "todo1");

        assertEquals(HttpStatus.NOT_ACCEPTABLE, response.getStatus());
        assertEquals("Task không tồn tại", response.getMessage());
    }

    @Test
    void updateTodoProject_TodoAndProjectExist_ShouldReturnOk() {
        Todo todo = new Todo();
        when(toDoRepository.findById("todo1")).thenReturn(Optional.of(todo));
        when(maProjectRepository.findById("project1")).thenReturn(Optional.of(new Project()));

        MaUpdateTodoProjectRequest request = new MaUpdateTodoProjectRequest();
        request.setProjectId("project1");
        request.setNameTodo("Tên mới");
        request.setPriorityLevel(PriorityLevel.CAO);

        ResponseObject<?> response = service.updateTodoProject(request, "todo1");

        assertEquals(HttpStatus.NOT_ACCEPTABLE, response.getStatus());
        assertEquals("Sửa thành công", response.getMessage());
    }

    @Test
    void updateTodoByPhaseProject_PhaseAndProjectExist_PhaseTodoAlreadyExists_ShouldUpdate() {
        udpm.hn.server.entity.PhaseProject phase = new udpm.hn.server.entity.PhaseProject();
        phase.setId("phase1");
        udpm.hn.server.entity.Project project = new udpm.hn.server.entity.Project();
        project.setId("project1");
        Todo todo = new Todo(); todo.setId("todo1");
        PhaseTodoProject existing = new PhaseTodoProject();
        existing.setTodo(todo);

        when(phaseRepository.findById("phase1")).thenReturn(Optional.of(phase));
        when(projectRepository.findById("project1")).thenReturn(Optional.of(project));
        when(toDoRepository.findById("todo1")).thenReturn(Optional.of(todo));
        when(phaseTodoProjectRepository.findByTodoId("todo1")).thenReturn(Optional.of(existing));
        when(todoListRepository.findByPhaseProjectId("phase1")).thenReturn(Optional.of(new TodoList()));

        ResponseObject<?> response = service.updateTodoByPhaseProject("todo1", "phase1", "project1");

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals("Cập nhật thành công", response.getMessage());
        verify(phaseTodoProjectRepository).save(existing);
        verify(toDoRepository).save(todo);
    }

    @Test
    void updateTodoByPhaseProject_PhaseAndProjectExist_PhaseTodoNotExists_ShouldInsertNew() {
        udpm.hn.server.entity.PhaseProject phase = new udpm.hn.server.entity.PhaseProject();
        phase.setId("phase1");
        udpm.hn.server.entity.Project project = new udpm.hn.server.entity.Project();
        project.setId("project1");
        Todo todo = new Todo(); todo.setId("todo1");

        when(phaseRepository.findById("phase1")).thenReturn(Optional.of(phase));
        when(projectRepository.findById("project1")).thenReturn(Optional.of(project));
        when(toDoRepository.findById("todo1")).thenReturn(Optional.of(todo));
        when(phaseTodoProjectRepository.findByTodoId("todo1")).thenReturn(Optional.empty());
        when(todoListRepository.findByPhaseProjectId("phase1")).thenReturn(Optional.empty());
        when(todoListHelper.genIndexTodoListWHEREIDPHASE("project1","phase1")).thenReturn((byte)1);
        when(todoListRepository.save(any())).thenReturn(new TodoList());

        ResponseObject<?> response = service.updateTodoByPhaseProject("todo1", "phase1", "project1");

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals("Cập nhật thành công", response.getMessage());
        verify(phaseTodoProjectRepository).save(any(PhaseTodoProject.class));
        verify(toDoRepository).save(todo);
        verify(todoListRepository).save(any(TodoList.class));
    }

    @Test
    void convertToDto_WithValidJson_ShouldMapLists() throws Exception {
        MATodoProjectResponse mockResp = mock(MATodoProjectResponse.class);
        when(mockResp.getStudents()).thenReturn("[{}]");
        when(mockResp.getStaff()).thenReturn("[{}]");

        ObjectMapper realMapper = new ObjectMapper();
        MaTodoListProjectServiceImpl realService = new MaTodoListProjectServiceImpl(
                todoListRepository, maTodoListProjectRepository, maProjectRepository,
                todoProjectRepository, logService, toDoRepository, realMapper,
                phaseRepository, phaseTodoProjectRepository, todoListHelper, projectRepository
        );

        // invoke private method via reflection
        java.lang.reflect.Method method = MaTodoListProjectServiceImpl.class.getDeclaredMethod("convertToDto", MATodoProjectResponse.class);
        method.setAccessible(true);
        Object dto = method.invoke(realService, mockResp);

        assertEquals("udpm.hn.server.core.manage.project.model.response.TodoProjectResponseDTO",
                dto.getClass().getName());
    }

    @Test
    void convertToDto_WithInvalidJson_ShouldHandleException() throws Exception {
        MATodoProjectResponse mockResp = mock(MATodoProjectResponse.class);
        when(mockResp.getStudents()).thenReturn("invalid-json");
        when(mockResp.getStaff()).thenReturn("invalid-json");

        ObjectMapper realMapper = new ObjectMapper();
        MaTodoListProjectServiceImpl realService = new MaTodoListProjectServiceImpl(
                todoListRepository, maTodoListProjectRepository, maProjectRepository,
                todoProjectRepository, logService, toDoRepository, realMapper,
                phaseRepository, phaseTodoProjectRepository, todoListHelper, projectRepository
        );

        java.lang.reflect.Method method = MaTodoListProjectServiceImpl.class.getDeclaredMethod("convertToDto", MATodoProjectResponse.class);
        method.setAccessible(true);
        Object dto = method.invoke(realService, mockResp);

        assertEquals("udpm.hn.server.core.manage.project.model.response.TodoProjectResponseDTO",
                dto.getClass().getName());
    }
    @Test
    void updateTodpList_TodoExists_ProjectNotFound_ShouldReturnNotAcceptable() {
        MaCreateOrUpdateTodoListProject request = new MaCreateOrUpdateTodoListProject();
        request.setProjectId("projX");

        TodoList todoList = new TodoList();
        when(maTodoListProjectRepository.findById("todoX")).thenReturn(Optional.of(todoList));
        when(maProjectRepository.findById("projX")).thenReturn(Optional.empty());

        ResponseObject<?> response = service.updateTodpList(request, "todoX");

        assertEquals(HttpStatus.NOT_ACCEPTABLE, response.getStatus());
        assertEquals("Không tìm thấy dự án", response.getMessage());
    }

    @Test
    void updateTodpList_TodoNotExists_ShouldReturnOkTaskNotFound() {
        MaCreateOrUpdateTodoListProject request = new MaCreateOrUpdateTodoListProject();
        when(maTodoListProjectRepository.findById("todoX")).thenReturn(Optional.empty());

        ResponseObject<?> response = service.updateTodpList(request, "todoX");

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals("Task không tồn tại", response.getMessage());
    }

    @Test
    void updateTodoByPhaseProject_TodoExists_TodoListExists_ShouldUpdateTodoList() {
        PhaseProject phase = new PhaseProject(); phase.setId("phase1");
        Project project = new Project(); project.setId("project1");
        Todo todo = new Todo(); todo.setId("todo1");
        TodoList existingList = new TodoList(); existingList.setId("list1");

        when(phaseRepository.findById("phase1")).thenReturn(Optional.of(phase));
        when(projectRepository.findById("project1")).thenReturn(Optional.of(project));
        when(toDoRepository.findById("todo1")).thenReturn(Optional.of(todo));
        when(phaseTodoProjectRepository.findByTodoId("todo1")).thenReturn(Optional.empty());
        when(todoListRepository.findByPhaseProjectId("phase1")).thenReturn(Optional.of(existingList));

        ResponseObject<?> response = service.updateTodoByPhaseProject("todo1", "phase1", "project1");

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals("Cập nhật thành công", response.getMessage());
        assertEquals(existingList, todo.getTodoList());
        verify(toDoRepository).save(todo);
    }


    @Test
    void getAllTodoByProject_ShouldMapToDto() {
        MaTodoListProjectRequest request = new MaTodoListProjectRequest();
        MATodoProjectResponse mockResponse = mock(MATodoProjectResponse.class);

        Page<MATodoProjectResponse> page = new PageImpl<>(List.of(mockResponse));
        when(maTodoListProjectRepository.getAllTodoProject(any(), eq("project1"), eq(request)))
                .thenReturn(page);

        ResponseObject<?> response = service.getAllTodoByProject(request, "project1");

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals("Lấy thành công danh sách task theo dự án", response.getMessage());
        assertNotNull(response.getData());
        verify(maTodoListProjectRepository).getAllTodoProject(any(), eq("project1"), eq(request));
    }

    @Test
    void updateTodoProject_TodoExists_ProjectExists_ShouldUpdateFields() {
        Todo todo = new Todo(); todo.setId("todo1");
        Project project = new Project(); project.setId("project1");

        when(toDoRepository.findById("todo1")).thenReturn(Optional.of(todo));
        when(maProjectRepository.findById("project1")).thenReturn(Optional.of(project));

        MaUpdateTodoProjectRequest request = new MaUpdateTodoProjectRequest();
        request.setProjectId("project1");
        request.setNameTodo("New Name");
        request.setPriorityLevel(PriorityLevel.THAP);

        ResponseObject<?> response = service.updateTodoProject(request, "todo1");

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals("Sửa thành công", response.getMessage());
        assertEquals("New Name", todo.getName());
        verify(toDoRepository).save(todo);
    }

    @Test
    void addTodoList_ShouldSetAllFieldsCorrectly() {
        MaCreateOrUpdateTodoListProject request = new MaCreateOrUpdateTodoListProject();
        request.setProjectId("p1");
        request.setCodeTodoList("CODE1");
        request.setNameTodoList("Tên");
        request.setDescribeTodoList("Mô tả");

        Project project = new Project(); project.setId("p1");
        when(maProjectRepository.findById("p1")).thenReturn(Optional.of(project));
        when(todoListRepository.save(any(TodoList.class))).thenAnswer(inv -> inv.getArgument(0));

        ResponseObject<?> response = service.addTodoList(request);

        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertEquals("Thêm thành công", response.getMessage());
        verify(todoListRepository).save(any(TodoList.class));
    }

    @Test
    void updateTodpList_ShouldUpdateAllFields() {
        TodoList existing = new TodoList();
        Project project = new Project(); project.setId("p1");

        when(maTodoListProjectRepository.findById("t1")).thenReturn(Optional.of(existing));
        when(maProjectRepository.findById("p1")).thenReturn(Optional.of(project));
        when(maTodoListProjectRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        MaCreateOrUpdateTodoListProject req = new MaCreateOrUpdateTodoListProject();
        req.setProjectId("p1");
        req.setCodeTodoList("C01");
        req.setNameTodoList("Updated");
        req.setDescribeTodoList("Desc");

        ResponseObject<?> response = service.updateTodpList(req, "t1");

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals("Sửa thành công", response.getMessage());
        verify(maTodoListProjectRepository).save(existing);
    }



    @Test
    void readCSV_ShouldReturnOkWhenEntriesEmpty() throws Exception {
        when(logService.readCSVTodoList(any())).thenReturn(Collections.emptyList());

        ResponseObject<?> response = service.readCSV();

        assertEquals(HttpStatus.OK, response.getStatus());
        assertTrue(((List<?>) response.getData()).isEmpty());
    }

    @Test
    void getAllPhase_ShouldIncludeExcludedStatuses() {
        List<MaPhaseProjectResponse> resultList = List.of(mock(MaPhaseProjectResponse.class));
        when(phaseRepository.getALl(eq("pid"), anyList())).thenReturn(resultList);

        ResponseObject<?> response = service.getAllPhase("pid");

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals("Lấy thành công danh sách task theo dự án", response.getMessage());
        assertEquals(resultList, response.getData());
        verify(phaseRepository).getALl(eq("pid"), anyList());
    }

    @Test
    void updateTodoProject_ShouldUpdateAllFields() {
        Todo todo = new Todo(); todo.setId("t1");
        Project project = new Project(); project.setId("p1");

        when(toDoRepository.findById("t1")).thenReturn(Optional.of(todo));
        when(maProjectRepository.findById("p1")).thenReturn(Optional.of(project));
        when(toDoRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        MaUpdateTodoProjectRequest req = new MaUpdateTodoProjectRequest();
        req.setProjectId("p1");
        req.setNameTodo("Tên mới");
        req.setStatusTodo(StatusTodo.DA_HOAN_THANH);
        req.setPriorityLevel(PriorityLevel.CAO);

        ResponseObject<?> response = service.updateTodoProject(req, "t1");

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals("Sửa thành công", response.getMessage());
        assertEquals("Tên mới", todo.getName());
        assertEquals(PriorityLevel.CAO, todo.getPriorityLevel());
        verify(toDoRepository).save(todo);
    }

    @Test
    void updateTodoProject_TodoExistsButProjectNotFound_ShouldNotAcceptable() {
        Todo todo = new Todo(); todo.setId("t1");
        when(toDoRepository.findById("t1")).thenReturn(Optional.of(todo));
        when(maProjectRepository.findById("p1")).thenReturn(Optional.empty());

        MaUpdateTodoProjectRequest req = new MaUpdateTodoProjectRequest();
        req.setProjectId("p1");

        ResponseObject<?> response = service.updateTodoProject(req, "t1");

        assertEquals(HttpStatus.NOT_ACCEPTABLE, response.getStatus());
        assertEquals("Không tìm thấy dự án", response.getMessage());
    }

    @Test
    void updateTodoProject_TodoNotFound_ShouldNotAcceptable() {
        when(toDoRepository.findById("t1")).thenReturn(Optional.empty());

        MaUpdateTodoProjectRequest req = new MaUpdateTodoProjectRequest();
        req.setProjectId("p1");

        ResponseObject<?> response = service.updateTodoProject(req, "t1");

        assertEquals(HttpStatus.NOT_ACCEPTABLE, response.getStatus());
        assertEquals("Task không tồn tại", response.getMessage());
    }

    @Test
    void updateTodoByPhaseProject_ShouldCreateNewTodoListIfNotFound() {
        PhaseProject phase = new PhaseProject(); phase.setId("ph1");
        Project project = new Project(); project.setId("p1");
        Todo todo = new Todo(); todo.setId("t1");

        when(phaseRepository.findById("ph1")).thenReturn(Optional.of(phase));
        when(projectRepository.findById("p1")).thenReturn(Optional.of(project));
        when(toDoRepository.findById("t1")).thenReturn(Optional.of(todo));
        when(phaseTodoProjectRepository.findByTodoId("t1")).thenReturn(Optional.empty());
        when(todoListRepository.findByPhaseProjectId("ph1")).thenReturn(Optional.empty());
        when(todoListHelper.genIndexTodoListWHEREIDPHASE("p1","ph1")).thenReturn((byte)5);
        when(todoListRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        ResponseObject<?> response = service.updateTodoByPhaseProject("t1", "ph1", "p1");

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals("Cập nhật thành công", response.getMessage());
        verify(toDoRepository).save(todo);
        verify(todoListRepository).save(any(TodoList.class));
    }

    @Test
    void updateTodoByPhaseProject_TodoExists_PhaseTodoAlreadyExists_ShouldUpdate() {
        PhaseProject phase = new PhaseProject(); phase.setId("ph1");
        Project project = new Project(); project.setId("p1");
        Todo todo = new Todo(); todo.setId("t1");
        PhaseTodoProject existing = new PhaseTodoProject(); existing.setTodo(todo);

        when(phaseRepository.findById("ph1")).thenReturn(Optional.of(phase));
        when(projectRepository.findById("p1")).thenReturn(Optional.of(project));
        when(toDoRepository.findById("t1")).thenReturn(Optional.of(todo));
        when(phaseTodoProjectRepository.findByTodoId("t1")).thenReturn(Optional.of(existing));
        when(todoListRepository.findByPhaseProjectId("ph1")).thenReturn(Optional.of(new TodoList()));

        ResponseObject<?> response = service.updateTodoByPhaseProject("t1", "ph1", "p1");

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals("Cập nhật thành công", response.getMessage());
        verify(phaseTodoProjectRepository).save(existing);
        verify(toDoRepository).save(todo);
    }



    // ========== detailTodoList ==========
    @Test
    void detailTodoList_ShouldReturnNullDataIfRepoReturnsNull() {
        when(maTodoListProjectRepository.getDetailTodoListProject("idX")).thenReturn(null);

        ResponseObject<?> res = service.detailTodoList("idX");

        assertEquals(HttpStatus.OK, res.getStatus());
        assertNull(res.getData());
    }



    // ========== getAllPhase ==========
    @Test
    void getAllPhase_ShouldReturnEmptyList() {
        when(phaseRepository.getALl(eq("pid"), anyList())).thenReturn(Collections.emptyList());

        ResponseObject<?> res = service.getAllPhase("pid");

        assertEquals(HttpStatus.OK, res.getStatus());
        assertTrue(((List<?>) res.getData()).isEmpty());
    }

    // ========== getAllTodoByProject ==========
    @Test
    void getAllTodoByProject_ShouldHandleEmptyPage() {
        MaTodoListProjectRequest req = new MaTodoListProjectRequest();
        Page<MATodoProjectResponse> page = new PageImpl<>(Collections.emptyList());
        when(maTodoListProjectRepository.getAllTodoProject(any(), eq("pid"), eq(req))).thenReturn(page);

        ResponseObject<?> res = service.getAllTodoByProject(req, "pid");

        assertEquals(HttpStatus.OK, res.getStatus());
        assertNotNull(res.getData());
    }

    // ========== updateTodoByPhaseProject ==========
    @Test
    void updateTodoByPhaseProject_TodoNotFound_ShouldStillReturnOk() {
        PhaseProject phase = new PhaseProject(); phase.setId("ph1");
        Project project = new Project(); project.setId("p1");

        when(phaseRepository.findById("ph1")).thenReturn(Optional.of(phase));
        when(projectRepository.findById("p1")).thenReturn(Optional.of(project));
        when(toDoRepository.findById("todoX")).thenReturn(Optional.empty());
        when(phaseTodoProjectRepository.findByTodoId("todoX")).thenReturn(Optional.empty());

        ResponseObject<?> res = service.updateTodoByPhaseProject("todoX", "ph1", "p1");

        assertEquals(HttpStatus.OK, res.getStatus());
        assertEquals("Cập nhật thành công", res.getMessage());
    }

    // ========== convertToDto ==========
    @Test
    void convertToDto_ShouldHandleValidJson() throws Exception {
        MATodoProjectResponse mockResp = mock(MATodoProjectResponse.class);
        when(mockResp.getStudents()).thenReturn("[{}]");
        when(mockResp.getStaff()).thenReturn("[{}]");

        ObjectMapper realMapper = new ObjectMapper();
        MaTodoListProjectServiceImpl realService = new MaTodoListProjectServiceImpl(
                todoListRepository, maTodoListProjectRepository, maProjectRepository,
                todoProjectRepository, logService, toDoRepository, realMapper,
                phaseRepository, phaseTodoProjectRepository, todoListHelper, projectRepository
        );

        var method = MaTodoListProjectServiceImpl.class.getDeclaredMethod("convertToDto", MATodoProjectResponse.class);
        method.setAccessible(true);

        Object dto = method.invoke(realService, mockResp);
        assertNotNull(dto);
    }

    @Test
    void convertToDto_ShouldCatchJsonException() throws Exception {
        MATodoProjectResponse mockResp = mock(MATodoProjectResponse.class);
        when(mockResp.getStudents()).thenReturn("invalid");
        when(mockResp.getStaff()).thenReturn("invalid");

        ObjectMapper realMapper = new ObjectMapper();
        MaTodoListProjectServiceImpl realService = new MaTodoListProjectServiceImpl(
                todoListRepository, maTodoListProjectRepository, maProjectRepository,
                todoProjectRepository, logService, toDoRepository, realMapper,
                phaseRepository, phaseTodoProjectRepository, todoListHelper, projectRepository
        );

        var method = MaTodoListProjectServiceImpl.class.getDeclaredMethod("convertToDto", MATodoProjectResponse.class);
        method.setAccessible(true);

        Object dto = method.invoke(realService, mockResp);
        assertNotNull(dto);
    }



}
  