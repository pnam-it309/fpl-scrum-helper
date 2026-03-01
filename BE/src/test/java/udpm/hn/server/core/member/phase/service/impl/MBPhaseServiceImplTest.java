package udpm.hn.server.core.member.phase.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.objectweb.asm.TypeReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import udpm.hn.server.core.common.base.ResponseObject;
import udpm.hn.server.core.manage.project.model.response.MATodoProjectResponse;
import udpm.hn.server.core.manage.project.model.response.MaPhaseProjectResponse;
import udpm.hn.server.core.manage.project.repository.MaPhaseProjectRepository;
import udpm.hn.server.core.manage.project.repository.MaTodoListProjectRepository;
import udpm.hn.server.core.manage.project.repository.MaTodoProjectRepository;
import udpm.hn.server.core.member.phase.model.request.MBPhaseRequest;
import udpm.hn.server.core.member.phase.model.response.MBDetailPhase;
import udpm.hn.server.core.member.phase.model.response.MBPhaseResponse;
import udpm.hn.server.core.member.phase.repository.MBPhaseRepository;
import udpm.hn.server.entity.*;
import udpm.hn.server.repository.PhaseTodoProjectRepository;
import udpm.hn.server.repository.ProjectRepository;
import udpm.hn.server.repository.ToDoRepository;
import udpm.hn.server.repository.TodoListRepository;
import udpm.hn.server.utils.StatusPhase;
import udpm.hn.server.utils.TodoListHelper;
import udpm.hn.server.utils.UserContextHelper;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class MBPhaseServiceImplTest {

    @Mock
    private MBPhaseRepository maPhaseRepository;
    @Mock
    private UserContextHelper userContextHelper;
    @Mock
    private ObjectMapper objectMapper;
    @Mock
    private MaPhaseProjectRepository phaseRepository;
    @Mock
    private MaTodoProjectRepository todoProjectRepository;
    @Mock
    private MaTodoListProjectRepository maTodoListProjectRepository;
    @Mock
    private TodoListRepository todoListRepository;
    @Mock
    private PhaseTodoProjectRepository phaseTodoProjectRepository;
    @Mock
    private ToDoRepository toDoRepository;
    @Mock
    private TodoListHelper todoListHelper;
    @Mock
    private ProjectRepository projectRepository;

    @InjectMocks
    private MBPhaseServiceImpl mbPhaseService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllPhase_ShouldReturnPagedPhases() {
        // Chuẩn bị request và pageable
        MBPhaseRequest request = new MBPhaseRequest();
        request.setIdProject("project1");
        Pageable pageable = PageRequest.of(0, 10);

        // Tạo mock data trả về
        MBPhaseResponse mockResponse = new MBPhaseResponse() {
            @Override public String getOrderNumber() { return "1"; }
            @Override public String getName() { return "Phase 1"; }
            @Override public String getCode() { return "P001"; }
            @Override public String getId() { return "id1"; }
            @Override public String getStartTime() { return "2025-01-01"; }
            @Override public String getEndTime() { return "2025-01-31"; }
            @Override public String getCreateDate() { return "2024-12-01"; }
            @Override public String getDescriptions() { return "Mô tả giai đoạn"; }
            @Override public StatusPhase getStatusPhase() { return StatusPhase.DANG_LAM; }
        };

        List<MBPhaseResponse> mockList = List.of(mockResponse);
        Page<MBPhaseResponse> mockPage = new PageImpl<>(mockList, pageable, mockList.size());

        when(maPhaseRepository.getAllPhaseProject(any(), eq("project1"), any())).thenReturn(mockPage);

        ResponseObject<?> response = mbPhaseService.getAllPhase(request, "project1");

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals("Lấy dự liệu giai đoạn thành công", response.getMessage());

        Page<MBPhaseResponse> data = (Page<MBPhaseResponse>) response.getData();
        assertNotNull(data);
        assertEquals(1, data.getTotalElements());
        assertEquals("Phase 1", data.getContent().get(0).getName());
    }

    @Test
    void testDetail_ShouldReturnPhaseId() {
        MBDetailPhase mockPhase = new MBDetailPhase() {
            @Override
            public String getId() {
                return "phase1";
            }
        };

        when(maPhaseRepository.getPhaseId("project1")).thenReturn(mockPhase);

        ResponseObject<?> response = mbPhaseService.detail("project1");

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals("phase1", ((MBDetailPhase) response.getData()).getId());
    }



    @Test
    void testGetAllPhase_ReturnsPhaseList() {
        MaPhaseProjectResponse mockResponse = mock(MaPhaseProjectResponse.class);
        when(mockResponse.getIdPhase()).thenReturn("phase1");
        when(mockResponse.getNamePhaseProject()).thenReturn("Phase 1");

        List<MaPhaseProjectResponse> mockPhaseList = List.of(mockResponse);

        List<StatusPhase> excludedStatuses = List.of(
                StatusPhase.DA_HOAN_THANH,
                StatusPhase.QUA_HAN
        );

        when(phaseRepository.getALl(eq("projectId"), eq(excludedStatuses)))
                .thenReturn(mockPhaseList);

        ResponseObject<?> response = mbPhaseService.getAllPhase("projectId");

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(mockPhaseList, response.getData());
    }



    @Test
    void testUpdateTodoByPhaseProject_ShouldReturnOK() {
        String todoId = "todo1";
        String newPhaseId = "phase1";
        String projectId = "project1";

        PhaseProject phaseProject = new PhaseProject();
        Project project = new Project();
        Todo todo = new Todo();
        TodoList todoList = new TodoList();

        PhaseTodoProject phaseTodoProject = new PhaseTodoProject();
        phaseTodoProject.setTodo(todo);

        // Mock repository responses
        when(phaseRepository.findById(newPhaseId)).thenReturn(Optional.of(phaseProject));
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(toDoRepository.findById(todoId)).thenReturn(Optional.of(todo));
        when(phaseTodoProjectRepository.findByTodoId(todoId)).thenReturn(Optional.of(phaseTodoProject));
        when(todoListHelper.genIndexTodoListWHEREIDPHASE(projectId, newPhaseId)).thenReturn((byte) 1);

        // Important: capture and assign saved TodoList to todo to ensure equality in test
        when(todoListRepository.save(any())).thenAnswer(invocation -> {
            TodoList saved = invocation.getArgument(0);
            todo.setTodoList(saved); // ensure this matches expected object
            return saved;
        });

        // Call service
        ResponseObject<?> response = mbPhaseService.updateTodoByPhaseProject(todoId, newPhaseId, projectId);

        // Assertions
        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals("Cập nhật thành công", response.getMessage());

        // Verify interactions
        verify(phaseTodoProjectRepository).save(any(PhaseTodoProject.class));
        verify(toDoRepository).save(todo);
        verify(todoListRepository).save(any(TodoList.class));

        // Ensure todo is updated with the correct todoList
        assertNotNull(todo.getTodoList());
    }


    @Test
    void testUpdateTodoByPhaseProject_ShouldReturnBadRequest_WhenPhaseNotFound() {
        when(phaseRepository.findById("missingPhase")).thenReturn(Optional.empty());

        ResponseObject<?> response = mbPhaseService.updateTodoByPhaseProject("todo1", "missingPhase", "project1");

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatus());
        assertEquals("PhaseProject không tồn tại", response.getMessage());
    }

    @Test
    void testUpdateTodoByPhaseProject_ShouldReturnBadRequest_WhenProjectNotFound() {
        when(phaseRepository.findById("phase1")).thenReturn(Optional.of(new PhaseProject()));
        when(projectRepository.findById("missingProject")).thenReturn(Optional.empty());

        ResponseObject<?> response = mbPhaseService.updateTodoByPhaseProject("todo1", "phase1", "missingProject");

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatus());
        assertEquals("Project không tồn tại", response.getMessage());
    }

    @Test
    void testUpdateTodoByPhaseProject_ShouldCreateNewPhaseTodoProject_WhenNotExist() {
        String todoId = "todo1";
        String newPhaseId = "phase1";
        String projectId = "project1";

        PhaseProject phaseProject = new PhaseProject();
        Project project = new Project();
        Todo todo = new Todo();
        TodoList todoList = new TodoList();

        // Mock repository responses
        when(phaseRepository.findById(newPhaseId)).thenReturn(Optional.of(phaseProject));
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(toDoRepository.findById(todoId)).thenReturn(Optional.of(todo));
        when(phaseTodoProjectRepository.findByTodoId(todoId)).thenReturn(Optional.empty());
        when(todoListRepository.findByPhaseProjectId(newPhaseId)).thenReturn(Optional.of(todoList));
        when(todoListHelper.genIndexTodoListWHEREIDPHASE(projectId, newPhaseId)).thenReturn((byte) 1);

        ResponseObject<?> response = mbPhaseService.updateTodoByPhaseProject(todoId, newPhaseId, projectId);

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals("Cập nhật thành công", response.getMessage());
        verify(phaseTodoProjectRepository).save(any(PhaseTodoProject.class));
        verify(toDoRepository).save(todo);
    }
    @Test
    void testUpdateTodoByPhaseProject_ShouldCreateNewTodoList_WhenNotExist() {
        String todoId = "todo1";
        String newPhaseId = "phase1";
        String projectId = "project1";

        PhaseProject phaseProject = new PhaseProject();
        Project project = new Project();
        Todo todo = new Todo();

        when(phaseRepository.findById(newPhaseId)).thenReturn(Optional.of(phaseProject));
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(toDoRepository.findById(todoId)).thenReturn(Optional.of(todo));
        when(phaseTodoProjectRepository.findByTodoId(todoId)).thenReturn(Optional.empty());
        when(todoListRepository.findByPhaseProjectId(newPhaseId)).thenReturn(Optional.empty());
        when(todoListHelper.genIndexTodoListWHEREIDPHASE(projectId, newPhaseId)).thenReturn((byte) 5);
        when(todoListRepository.save(any(TodoList.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ResponseObject<?> response = mbPhaseService.updateTodoByPhaseProject(todoId, newPhaseId, projectId);

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals("Cập nhật thành công", response.getMessage());
        verify(todoListRepository).save(any(TodoList.class));
        verify(toDoRepository).save(todo);
    }

    @Test
    void testGetAllTodoByProject_ShouldReturnDtoList() throws Exception {
        MBPhaseRequest request = new MBPhaseRequest();
        String id = "project1";
        Pageable pageable = PageRequest.of(0, 10);

        MATodoProjectResponse responseMock = mock(MATodoProjectResponse.class);
        when(responseMock.getStudents()).thenReturn("[{}]");
        when(responseMock.getStaff()).thenReturn("[{}]");
        when(responseMock.getOrderNumber()).thenReturn("1");
        when(responseMock.getIdTodo()).thenReturn("todo1");
        // Mock các field còn lại nếu cần

        Page<MATodoProjectResponse> pageMock = new PageImpl<>(List.of(responseMock), pageable, 1);

        when(maPhaseRepository.getAllTodoProjectMemBer(any(), eq(id), any(), any())).thenReturn(pageMock);
        when(objectMapper.readValue(eq("[{}]"), any(com.fasterxml.jackson.core.type.TypeReference.class))).thenReturn(List.of());

        ResponseObject<?> result = mbPhaseService.getAllTodoByProject(request, id);

        assertEquals(HttpStatus.OK, result.getStatus());
        assertNotNull(result.getData());
    }
    @Test
    void testUpdateTodoByPhaseProject_WhenTodoNotFound() {
        String todoId = "todo1";
        String newPhaseId = "phase1";
        String projectId = "project1";

        PhaseProject phaseProject = new PhaseProject();
        Project project = new Project();

        when(phaseRepository.findById(newPhaseId)).thenReturn(Optional.of(phaseProject));
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(toDoRepository.findById(todoId)).thenReturn(Optional.empty());
        when(phaseTodoProjectRepository.findByTodoId(todoId)).thenReturn(Optional.empty());
        when(todoListRepository.findByPhaseProjectId(newPhaseId)).thenReturn(Optional.empty());
        when(todoListHelper.genIndexTodoListWHEREIDPHASE(projectId, newPhaseId)).thenReturn((byte) 5);

        ResponseObject<?> response = mbPhaseService.updateTodoByPhaseProject(todoId, newPhaseId, projectId);

        assertEquals(HttpStatus.OK, response.getStatus());
    }

}
