package udpm.hn.server.core.member.projectdetails.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.server.ResponseStatusException;
import udpm.hn.server.core.common.base.ResponseObject;
import udpm.hn.server.core.member.projectdetails.model.request.*;
import udpm.hn.server.core.member.projectdetails.model.response.*;
import udpm.hn.server.core.member.projectdetails.repository.*;
import udpm.hn.server.entity.*;
import udpm.hn.server.infrastructure.constant.EntityStatus;
import udpm.hn.server.infrastructure.constant.StatusTodoChild;
import udpm.hn.server.utils.StatusPhase;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class MBMeTodoServiceImplTest {

    @InjectMocks
    private MBMeTodoServiceImpl service;

    @Mock
    private MBMeTodoRepository todoRepository;

    @Mock
    private MBMeTodoListRepository todoListRepository;

    @Mock
    private MBMeTodoChildRepository todoChildRepository;

    @Mock
    private MBMeAssignRepository assignRepository;

    @Mock
    private MBMeLabelRepository labelRepository;

    @Mock
    private MBMeImageRepository imageRepository;

    @Mock
    private MBMeActivityRepository activityRepository;

    @Mock
    private MBMePhaseProjectRepository phaseProjectRepository;

    @Mock
    private SimpMessagingTemplate messagingTemplate;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllBoard_WithEmptyPhase_ShouldReturnEmptyTasks() {
        MBMeFilterTodoRequest request = new MBMeFilterTodoRequest();
        request.setProjectId(String.valueOf(1L));
        request.setIdPhase("undefined");

        TodoList list = new TodoList();
        list.setId(String.valueOf(1L));
        list.setCode("T1");
        list.setName("List 1");
        list.setIndexTodoList((byte) 1);

        when(todoRepository.findAllByProjectIdAndPhaseIdOrderByIndexTodoList(String.valueOf(anyLong()), anyString()))
                .thenReturn(List.of(list));

        ResponseObject<?> result = service.getAllBoard(request);
        assertEquals(HttpStatus.OK, result.getStatus());
    }

    @Test
    void testCreateTodo_Success() {
        MBMeCreateTodoRequest request = new MBMeCreateTodoRequest();
        request.setIdTodoList(String.valueOf(1L));
        request.setName("New Todo");
        request.setIndexTodo((short) 1);

        TodoList todoList = new TodoList();
        todoList.setId(String.valueOf(1L));

        when(todoListRepository.findById(String.valueOf(1L))).thenReturn(Optional.of(todoList));
        when(todoRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        ResponseObject<?> response = service.createTodo(request);
        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    void testCreateTodo_ListNotFound_ShouldThrowException() {
        MBMeCreateTodoRequest request = new MBMeCreateTodoRequest();
        request.setIdTodoList(String.valueOf(999L));

        when(todoListRepository.findById(String.valueOf(999L))).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> service.createTodo(request));
    }


    @Test
    void testFindTodoById_ReturnsTodo() {
        MBFilterTodoModalRequest request = new MBFilterTodoModalRequest();
        request.setIdTodo("1");

        MBMeTodoResponse mockTodo = mock(MBMeTodoResponse.class);
        when(mockTodo.getId()).thenReturn("1");
        when(mockTodo.getName()).thenReturn("Test Todo");

        when(todoRepository.findTodoDetailById("1")).thenReturn(mockTodo);

        ResponseObject<?> result = service.findTodoById(request);

        assertEquals("lấy details Todo thành công", result.getMessage());
        assertEquals(HttpStatus.OK, result.getStatus());

        // Optional additional assertions:
        MBMeTodoResponse responseData = (MBMeTodoResponse) result.getData();
        assertEquals("1", responseData.getId());
        assertEquals("Test Todo", responseData.getName());
    }


    @Test
    void testCreateTodoChildChecklist_Success() {
        MBMeCreateDetailTodoChildRequest request = new MBMeCreateDetailTodoChildRequest();
        request.setIdTodo(String.valueOf(1L));
        request.setName("Child Task");

        Todo todo = new Todo();
        todo.setId(String.valueOf(1L));

        when(todoRepository.findById(String.valueOf(1L))).thenReturn(Optional.of(todo));
        when(todoRepository.countCompletedByTodoChildId(String.valueOf(1L))).thenReturn((short) 1);
        when(todoRepository.countByTodoChildId(String.valueOf(1L))).thenReturn((short) 2);
        when(todoRepository.save(any())).thenReturn(todo);

        ResponseObject<?> response = service.createTodoChildChecklist(request);
        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    void testDeleteTodoChild_Success() {
        MBMeDeleteDetailTodoChildRequest request = new MBMeDeleteDetailTodoChildRequest();
        request.setIdTodo(String.valueOf(1L));
        request.setIdTodoChild(String.valueOf(2L));

        TodoChild child = new TodoChild();
        child.setId(String.valueOf(2L));

        Todo parent = new Todo();
        parent.setId(String.valueOf(1L));

        when(todoChildRepository.findById(String.valueOf(2L))).thenReturn(Optional.of(child));
        when(todoRepository.findById(String.valueOf(1L))).thenReturn(Optional.of(parent));
        when(todoRepository.countCompletedByTodoChildId(String.valueOf(1L))).thenReturn((short) 1);
        when(todoRepository.countByTodoChildId(String.valueOf(1L))).thenReturn((short) 2);

        ResponseObject<?> response = service.deleteDetailTodoChild(request);
        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    void testDeleteTodoChild_NotFound() {
        MBMeDeleteDetailTodoChildRequest request = new MBMeDeleteDetailTodoChildRequest();
        request.setIdTodoChild(String.valueOf(99L));

        when(todoChildRepository.findById(String.valueOf(99L))).thenReturn(Optional.empty());
        ResponseObject<?> response = service.deleteDetailTodoChild(request);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatus());
    }


    @Test
    void testHandleStatusTodoChildChange_Success() {
        // Arrange
        MBHandleStatusTodoChildRequest request = new MBHandleStatusTodoChildRequest();
        request.setIdTodoChild("child1");
        request.setIdTodo("todo1");
        request.setStatusTodoChild(StatusTodoChild.COMPLETE);

        // Mock TodoChild entity
        TodoChild todoChild = new TodoChild();
        todoChild.setId("child1");
        todoChild.setStatusTodoChild(StatusTodoChild.UNCOMPLETE); // Initial status

        // Mock Todo entity
        Todo todo = new Todo();
        todo.setId("todo1");

        // Mock repository behavior
        when(todoChildRepository.findById("child1")).thenReturn(Optional.of(todoChild));
        when(todoRepository.findById("todo1")).thenReturn(Optional.of(todo));
        when(todoRepository.countCompletedByTodoChildId("todo1")).thenReturn((short) 2);
        when(todoRepository.countByTodoChildId("todo1")).thenReturn((short) 4);
        when(todoRepository.save(any(Todo.class))).thenReturn(todo);

        // Act
        ResponseObject<?> response = service.handleStatusTodoChildChange(request);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals("update status todo child thành công", response.getMessage());

        // Verify that the todoChild status was updated
        assertEquals(StatusTodoChild.COMPLETE, todoChild.getStatusTodoChild());

        // Verify interactions
        verify(todoChildRepository).findById("child1");
        verify(todoRepository).findById("todo1");
        verify(todoRepository).save(any(Todo.class));
    }


    @Test
    void testUpdateDescriptionsTodo_Success() {
        MBMeUpdateDescriptionsTodoRequest request = new MBMeUpdateDescriptionsTodoRequest();
        request.setIdTodo("1");
        request.setDescriptions("New Description");

        Todo todo = new Todo();
        when(todoRepository.findById("1")).thenReturn(Optional.of(todo));
        when(todoRepository.save(any())).thenReturn(todo);

        ResponseObject<?> response = service.updateDescriptionsTodo(request, null);

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals("update descriptions todo thành công", response.getMessage());
    }


    @Test
    void testUpdateNameTodo_Success() {
        MBMeUpdateNameTodoRequest request = new MBMeUpdateNameTodoRequest();
        request.setIdTodo("todo1");
        request.setNameTodo("Updated Name");

        Todo todo = new Todo();
        when(todoRepository.findById("todo1")).thenReturn(Optional.of(todo));
        when(todoRepository.save(any())).thenReturn(todo);

        ResponseObject<?> response = service.updateNameTodo(request, null);

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals("update ten todo thành công", response.getMessage());
    }

    @Test
    void testUpdateDeadlineTodo_InvalidDate() {
        MBMeUpdateDeadlineTodoRequest request = new MBMeUpdateDeadlineTodoRequest();
        request.setDeadline("invalid-date");

        ResponseObject<?> response = service.updateDeadlineTodo(request, null);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatus());
        assertEquals("Ngày không hợp lệ", response.getMessage());
    }


    @Test
    void testDeleteDeadlineTodo_Success() {
        MBMeDeleteDeadlineTodoRequest request = new MBMeDeleteDeadlineTodoRequest();
        request.setIdTodo("todo1");

        Todo todo = new Todo();
        todo.setName("TestTodo");

        when(todoRepository.findById("todo1")).thenReturn(Optional.of(todo));
        when(todoRepository.save(any())).thenReturn(todo);
        when(activityRepository.save(any())).thenReturn(new Activity());

        ResponseObject<?> response = service.deleteDeadlineTodo(request, null);

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals("Xóa thành công Ngày hết hạn", response.getMessage());
    }


    @Test
    void testUpdateCompleteTodo_MarkComplete() {
        MBMeUpdateCompleteTodoRequest request = new MBMeUpdateCompleteTodoRequest();
        request.setId("todo1");
        request.setIdTodo("todo1");
        request.setIdTodoList("list1");
        request.setProjectId("project1");
        request.setStatus((short) 0);

        Todo todo = new Todo();
        todo.setName("Test Todo");

        todo.setDeadline(System.currentTimeMillis() + 100000);

        when(todoRepository.findById("todo1")).thenReturn(Optional.of(todo));
        when(todoRepository.save(any())).thenReturn(todo);
        when(activityRepository.save(any())).thenReturn(new Activity());
        when(todoRepository.countTodoInCheckList("todo1")).thenReturn((short) 0);

        ResponseObject<?> response = service.updateCompleteTodo(request);

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals("Update công việc đã hoàn thành", response.getMessage());
    }


    @Test
    void testUpdatePriorityLevel_Success() {
        MBMeUpdateTodoRequest request = new MBMeUpdateTodoRequest();
        request.setIdTodo("todo1");
        request.setPriorityLevel(1);

        Todo todo = new Todo();
        when(todoRepository.findById("todo1")).thenReturn(Optional.of(todo));
        when(todoRepository.save(any())).thenReturn(todo);

        ResponseObject<?> response = service.updatePriorityLevel(request, null);

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals("Update mức độ ưu tiên cv", response.getMessage());
    }


    @Test
    void testUpdateProgress_Success() {
        MBMeUpdateProgressTodoRequest request = new MBMeUpdateProgressTodoRequest();
        request.setIdTodo("todo1");
        request.setIdTodoList("list1");
        request.setProjectId("project1");
        request.setProgress(90F);
        request.setIdUser("user1");
        request.setUrlPath("/test");

        Todo todo = new Todo();
        todo.setName("Test");

        when(todoRepository.findById("todo1")).thenReturn(Optional.of(todo));
        when(todoRepository.save(any())).thenReturn(todo);
        when(activityRepository.save(any())).thenReturn(new Activity());

        ResponseObject<?> response = service.updateProgress(request, null);

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals("Update progress success", response.getMessage());
    }


    @Test
    void testSortTodoPriority_ASC() {
        MBMeSortTodoRequest request = new MBMeSortTodoRequest();
        request.setType((int) 0);
        request.setIdPhase("phase1");

        TodoList list = new TodoList();
        List<Todo> todos = List.of(new Todo(), new Todo());

        when(todoListRepository.findAll()).thenReturn(List.of(list));
        when(todoRepository.findTodoSortTodoPriorityASC(list, "phase1")).thenReturn(todos);

        ResponseObject<?> response = service.sortTodoPriority(request);

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals("Sort todo priority success", response.getMessage());
    }


    @Test
    void testGetStatusByIdPhase_Success() {
        when(phaseProjectRepository.findStatusPhaseById("phase1"))
                .thenReturn(Optional.of(StatusPhase.DA_HOAN_THANH));

        ResponseObject<?> response = service.getStatusByIdPhase("phase1");

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals("Sort todo name success", response.getMessage());
        assertEquals("DA_HOAN_THANH", ((StatusPhase) response.getData()).name());
    }

    @Test
    void testConvertTodoResponses_EmptyList() {
        List<MBMeTodoResponse> emptyList = new ArrayList<>();
        List<?> result = service.convertTodoResponses(emptyList);
        assertTrue(result.isEmpty());
    }
    @Test
    void testGetAllDetailTodo_NotFound() {
        MBFilterTodoModalRequest request = new MBFilterTodoModalRequest();
        request.setIdTodo("notfound");
        when(todoRepository.findById("notfound")).thenReturn(Optional.empty());
        ResponseObject<?> result = service.getAllDetailTodo(request);
        assertEquals(HttpStatus.NOT_FOUND, result.getStatus());
    }
    @Test
    void testEditTodoChildChecklist_NotFound() {
        MBMeEditDetailTodoChildRequest request = new MBMeEditDetailTodoChildRequest();
        request.setIdTodoChild("notfound");
        when(todoChildRepository.findById("notfound")).thenReturn(Optional.empty());
        ResponseObject<?> result = service.editTodoChildChecklist(request);
        assertEquals(HttpStatus.NOT_FOUND, result.getStatus());
    }
    @Test
    void testSortTodoDeadline_ASC() {
        MBMeSortTodoRequest request = new MBMeSortTodoRequest();
        request.setType(0);
        request.setIdPhase("phase1");
        TodoList list = new TodoList();
        List<Todo> todos = List.of(new Todo(), new Todo());
        when(todoListRepository.findAll()).thenReturn(List.of(list));
        when(todoRepository.findTodoSortTodoDeadlineASC(list, "phase1")).thenReturn(todos);
        ResponseObject<?> response = service.sortTodoDeadline(request);
        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    void testSortTodoDeadline_DESC() {
        MBMeSortTodoRequest request = new MBMeSortTodoRequest();
        request.setType(1);
        request.setIdPhase("phase1");
        TodoList list = new TodoList();
        List<Todo> todos = List.of(new Todo(), new Todo());
        when(todoListRepository.findAll()).thenReturn(List.of(list));
        when(todoRepository.findTodoSortTodoDeadlineDESC(list, "phase1")).thenReturn(todos);
        ResponseObject<?> response = service.sortTodoDeadline(request);
        assertEquals(HttpStatus.OK, response.getStatus());
    }


    @Test
    void testUpdateNameTodo_NotFound() {
        MBMeUpdateNameTodoRequest request = new MBMeUpdateNameTodoRequest();
        request.setIdTodo("notfound");
        when(todoRepository.findById("notfound")).thenReturn(Optional.empty());
        ResponseObject<?> response = service.updateNameTodo(request, null);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatus());
    }
    @Test
    void testUpdateDescriptionsTodo_NotFound() {
        MBMeUpdateDescriptionsTodoRequest request = new MBMeUpdateDescriptionsTodoRequest();
        request.setIdTodo("notfound");
        when(todoRepository.findById("notfound")).thenReturn(Optional.empty());
        ResponseObject<?> response = service.updateDescriptionsTodo(request, null);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatus());
    }
    @Test
    void testUpdatePriorityLevel_NotFound() {
        MBMeUpdateTodoRequest request = new MBMeUpdateTodoRequest();
        request.setIdTodo("notfound");
        when(todoRepository.findById("notfound")).thenReturn(Optional.empty());
        ResponseObject<?> response = service.updatePriorityLevel(request, null);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatus());
    }
    @Test
    void testUpdateProgress_NotFound() {
        MBMeUpdateProgressTodoRequest request = new MBMeUpdateProgressTodoRequest();
        request.setIdTodo("notfound");
        when(todoRepository.findById("notfound")).thenReturn(Optional.empty());
        ResponseObject<?> response = service.updateProgress(request, null);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatus());
    }
    @Test
    void testUpdateDeadlineTodo_TodoNotFound() {
        MBMeUpdateDeadlineTodoRequest request = new MBMeUpdateDeadlineTodoRequest();
        request.setDeadline("01/01/2025 10:00:00");
        request.setIdTodo("notfound");
        when(todoRepository.findById("notfound")).thenReturn(Optional.empty());
        ResponseObject<?> response = service.updateDeadlineTodo(request, null);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatus());
    }
    @Test
    void testDeleteDeadlineTodo_TodoNotFound() {
        MBMeDeleteDeadlineTodoRequest request = new MBMeDeleteDeadlineTodoRequest();
        request.setIdTodo("notfound");
        when(todoRepository.findById("notfound")).thenReturn(Optional.empty());
        ResponseObject<?> response = service.deleteDeadlineTodo(request, null);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatus());
    }
    @Test
    void testSortTodoCreatedDate_ASC() {
        MBMeSortTodoRequest request = new MBMeSortTodoRequest();
        request.setType(0); // ASC
        request.setIdPhase("phase1");

        TodoList list = new TodoList();
        List<Todo> todos = List.of(new Todo(), new Todo());

        when(todoListRepository.findAll()).thenReturn(List.of(list));
        when(todoRepository.findTodoSortTodoCreateDateASC(list, "phase1")).thenReturn(todos);

        ResponseObject<?> response = service.sortTodoCreatedDate(request);

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals("Sort todo createdate success", response.getMessage());
    }

    @Test
    void testSortTodoCreatedDate_DESC() {
        MBMeSortTodoRequest request = new MBMeSortTodoRequest();
        request.setType(1); // DESC
        request.setIdPhase("phase1");

        TodoList list = new TodoList();
        List<Todo> todos = List.of(new Todo(), new Todo());

        when(todoListRepository.findAll()).thenReturn(List.of(list));
        when(todoRepository.findTodoSortTodoCreateDateDESC(list, "phase1")).thenReturn(todos);

        ResponseObject<?> response = service.sortTodoCreatedDate(request);

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals("Sort todo createdate success", response.getMessage());
    }

    @Test
    void testSortTodoName_ASC() {
        MBMeSortTodoRequest request = new MBMeSortTodoRequest();
        request.setType(0);
        request.setIdPhase("phase1");

        TodoList list = new TodoList();
        List<Todo> todos = List.of(new Todo(), new Todo());

        when(todoListRepository.findAll()).thenReturn(List.of(list));
        when(todoRepository.findTodoSortTodoNameASC(list, "phase1")).thenReturn(todos);

        ResponseObject<?> response = service.sortTodoName(request);

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals("Sort todo name success", response.getMessage());
    }

    @Test
    void testSortTodoName_DESC() {
        MBMeSortTodoRequest request = new MBMeSortTodoRequest();
        request.setType(1);
        request.setIdPhase("phase1");

        TodoList list = new TodoList();
        List<Todo> todos = List.of(new Todo(), new Todo());

        when(todoListRepository.findAll()).thenReturn(List.of(list));
        when(todoRepository.findTodoSortTodoNameDESC(list, "phase1")).thenReturn(todos);

        ResponseObject<?> response = service.sortTodoName(request);

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals("Sort todo name success", response.getMessage());
    }

    @Test
    void testSortTodoProgress_ASC() {
        MBMeSortTodoRequest request = new MBMeSortTodoRequest();
        request.setType(0);
        request.setIdPhase("phase1");

        TodoList list = new TodoList();
        List<Todo> todos = List.of(new Todo(), new Todo());

        when(todoListRepository.findAll()).thenReturn(List.of(list));
        when(todoRepository.findTodoSortTodoProgressASC(list, "phase1")).thenReturn(todos);

        ResponseObject<?> response = service.sortTodoProgress(request);

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals("Sort todo name success", response.getMessage());
    }

    @Test
    void testSortTodoProgress_DESC() {
        MBMeSortTodoRequest request = new MBMeSortTodoRequest();
        request.setType(1);
        request.setIdPhase("phase1");

        TodoList list = new TodoList();
        List<Todo> todos = List.of(new Todo(), new Todo());

        when(todoListRepository.findAll()).thenReturn(List.of(list));
        when(todoRepository.findTodoSortTodoProgressDESC(list, "phase1")).thenReturn(todos);

        ResponseObject<?> response = service.sortTodoProgress(request);

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals("Sort todo name success", response.getMessage());
    }

    @Test
    void testSortTodoPriority_TypeNot0Or1() {
        MBMeSortTodoRequest request = new MBMeSortTodoRequest();
        request.setType(2); // Type khác 0/1
        request.setIdPhase("phase1");

        ResponseObject<?> response = service.sortTodoPriority(request);

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals("Sort todo priority success", response.getMessage());
    }

    @Test
    void testSortTodoDeadline_TypeNot0Or1() {
        MBMeSortTodoRequest request = new MBMeSortTodoRequest();
        request.setType(2);
        request.setIdPhase("phase1");

        ResponseObject<?> response = service.sortTodoDeadline(request);

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals("Sort todo deadline success", response.getMessage());
    }

    @Test
    void testSortTodoCreatedDate_TypeNot0Or1() {
        MBMeSortTodoRequest request = new MBMeSortTodoRequest();
        request.setType(2);
        request.setIdPhase("phase1");

        ResponseObject<?> response = service.sortTodoCreatedDate(request);

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals("Sort todo createdate success", response.getMessage());
    }

    @Test
    void testSortTodoName_TypeNot0Or1() {
        MBMeSortTodoRequest request = new MBMeSortTodoRequest();
        request.setType(2);
        request.setIdPhase("phase1");

        ResponseObject<?> response = service.sortTodoName(request);

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals("Sort todo name success", response.getMessage());
    }

    @Test
    void testSortTodoProgress_TypeNot0Or1() {
        MBMeSortTodoRequest request = new MBMeSortTodoRequest();
        request.setType(2);
        request.setIdPhase("phase1");

        ResponseObject<?> response = service.sortTodoProgress(request);

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals("Sort todo name success", response.getMessage());
    }
    @Test
    void testSortTodoPriority_NoTodoList() {
        MBMeSortTodoRequest request = new MBMeSortTodoRequest();
        request.setType(0);
        request.setIdPhase("phase1");

        when(todoListRepository.findAll()).thenReturn(List.of());

        ResponseObject<?> response = service.sortTodoPriority(request);

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals("Sort todo priority success", response.getMessage());
    }

    @Test
    void testSortTodoDeadline_NoTodoList() {
        MBMeSortTodoRequest request = new MBMeSortTodoRequest();
        request.setType(0);
        request.setIdPhase("phase1");

        when(todoListRepository.findAll()).thenReturn(List.of());

        ResponseObject<?> response = service.sortTodoDeadline(request);

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals("Sort todo deadline success", response.getMessage());
    }
    @Test
    void testSortTodoCreatedDate_NoTodoList() {
        MBMeSortTodoRequest request = new MBMeSortTodoRequest();
        request.setType(0);
        request.setIdPhase("phase1");

        when(todoListRepository.findAll()).thenReturn(List.of());

        ResponseObject<?> response = service.sortTodoCreatedDate(request);

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals("Sort todo createdate success", response.getMessage());
    }
    @Test
    void testSortTodoName_NoTodoList() {
        MBMeSortTodoRequest request = new MBMeSortTodoRequest();
        request.setType(0);
        request.setIdPhase("phase1");

        when(todoListRepository.findAll()).thenReturn(List.of());

        ResponseObject<?> response = service.sortTodoName(request);

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals("Sort todo name success", response.getMessage());
    }
    @Test
    void testSortTodoProgress_NoTodoList() {
        MBMeSortTodoRequest request = new MBMeSortTodoRequest();
        request.setType(0);
        request.setIdPhase("phase1");

        when(todoListRepository.findAll()).thenReturn(List.of());

        ResponseObject<?> response = service.sortTodoProgress(request);

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals("Sort todo name success", response.getMessage());
    }

}
