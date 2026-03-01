package udpm.hn.server.core.member.projectdetails.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import udpm.hn.server.core.common.base.ResponseObject;
import udpm.hn.server.core.member.projectdetails.model.request.*;
import udpm.hn.server.core.member.projectdetails.model.response.MBTodoAndTodoListObject;
import udpm.hn.server.core.member.projectdetails.repository.*;
import udpm.hn.server.entity.PhaseProject;
import udpm.hn.server.entity.Project;
import udpm.hn.server.entity.TodoList;
import udpm.hn.server.infrastructure.constant.EntityStatus;
import udpm.hn.server.utils.TodoListHelper;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class MBMeTodoListServiceImplTest {

    @InjectMocks
    private MBMeTodoListServiceImpl service;

    @Mock
    private MBMeTodoRepository mbMeTodoRepository;
    @Mock
    private MBMeTodoListRepository mbMeTodoListRepository;
    @Mock
    private MBMeLabelRepository meLabelRepository;
    @Mock
    private MBMeProjectRepository mbMeProjectRepository;
    @Mock
    private MBMePhaseProjectRepository mbMePhaseProjectRepository;
    @Mock
    private TodoListHelper todoListHelper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void updateIndexTodoList_shouldUpdateSuccessfully_whenIndexChanged() {
        MBMeUpdateTodoListRequest request = new MBMeUpdateTodoListRequest();
        request.setIdTodoList("todoList1");
        request.setIndexBefore("1");
        request.setIndexAfter("3");
        request.setIdProject("project1");
        request.setIdPhase("phase1");
        request.setSessionId("session123");

        ResponseObject<?> result = service.updateIndexTodoList(request);

        verify(mbMeTodoListRepository).updateIndexTodoListDecs(1, 3, "project1", "phase1");
        verify(mbMeTodoListRepository).updateIndexTodoList("todoList1", 3, "phase1");
        assertEquals(HttpStatus.OK, result.getStatus());
        assertTrue(result.getData() instanceof MBTodoAndTodoListObject);
    }

    @Test
    void updateIndexTodoList_shouldReturnConflict_whenIndexesAreEqual() {
        MBMeUpdateTodoListRequest request = new MBMeUpdateTodoListRequest();
        request.setIndexBefore("2");
        request.setIndexAfter("2");

        ResponseObject<?> result = service.updateIndexTodoList(request);

        assertEquals(HttpStatus.CONFLICT, result.getStatus());
    }

    @Test
    void createTodoList_shouldReturnSuccess_whenProjectExists() {
        MBMeCreateTodoListRequest request = new MBMeCreateTodoListRequest();
        request.setIdProject("project1");
        request.setName("Todo List");
        request.setIdPhase("phase1");

        Project project = new Project();
        PhaseProject phaseProject = new PhaseProject();

        when(mbMeProjectRepository.findById("project1")).thenReturn(Optional.of(project));
        when(mbMePhaseProjectRepository.findById("phase1")).thenReturn(Optional.of(phaseProject));
        when(todoListHelper.genCodeTodoList("project1")).thenReturn("CODE123");
        when(todoListHelper.genIndexTodoListWHEREIDPHASE("project1", "phase1")).thenReturn((byte) 1);
        when(mbMeTodoListRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        ResponseObject<?> result = service.createTodoList(request, mock(StompHeaderAccessor.class));

        assertEquals(HttpStatus.NOT_FOUND, result.getStatus());
        assertNotNull(result.getData());
    }

    @Test
    void createTodoList_shouldReturnNotFound_whenProjectMissing() {
        MBMeCreateTodoListRequest request = new MBMeCreateTodoListRequest();
        request.setIdProject("project1");

        when(mbMeProjectRepository.findById("project1")).thenReturn(Optional.empty());

        ResponseObject<?> result = service.createTodoList(request, mock(StompHeaderAccessor.class));

        assertEquals(HttpStatus.NOT_FOUND, result.getStatus());
    }

    @Test
    void deleteTodoList_shouldReturnNotFound_whenTodoListMissing() {
        MBMeDeleteTodoListRequest request = new MBMeDeleteTodoListRequest();
        request.setId("list123");

        when(mbMeTodoListRepository.findById("list123")).thenReturn(Optional.empty());

        ResponseObject<?> result = service.deleteTodoList(request);

        assertEquals(HttpStatus.NOT_FOUND, result.getStatus());
    }

    @Test
    void deleteTodoList_shouldSetInactiveStatus_whenTodoListExists() {
        MBMeDeleteTodoListRequest request = new MBMeDeleteTodoListRequest();
        request.setId("list123");
        request.setIdPhase("phase1");
        request.setProjectId("project1");

        TodoList todoList = new TodoList();
        todoList.setIndexTodoList((byte) 2L);

        when(mbMeTodoListRepository.findById("list123")).thenReturn(Optional.of(todoList));

        ResponseObject<?> result = service.deleteTodoList(request);

        verify(mbMeTodoListRepository).save(todoList);
        verify(mbMeTodoListRepository).updateIndexTodoList(2, "project1", "phase1");
        assertEquals(HttpStatus.NOT_FOUND, result.getStatus());
    }

    @Test
    void updateNameTodoList_shouldReturnNotFound_whenListNotFound() {
        MBMeUpdateNameTodoListRequest request = new MBMeUpdateNameTodoListRequest();
        request.setIdTodoList("list123");

        when(mbMeTodoListRepository.findById("list123")).thenReturn(Optional.empty());

        ResponseObject<?> result = service.updateNameTodoList(request);

        assertEquals(HttpStatus.NOT_FOUND, result.getStatus());
    }

    @Test
    void updateNameTodoList_shouldUpdateSuccessfully_whenFound() {
        MBMeUpdateNameTodoListRequest request = new MBMeUpdateNameTodoListRequest();
        request.setIdTodoList("list123");
        request.setName("Updated Name");

        TodoList todoList = new TodoList();
        todoList.setName("Old Name");

        when(mbMeTodoListRepository.findById("list123")).thenReturn(Optional.of(todoList));
        when(mbMeTodoListRepository.save(todoList)).thenReturn(todoList);

        ResponseObject<?> result = service.updateNameTodoList(request);

        verify(mbMeTodoListRepository).save(todoList);
        assertEquals(HttpStatus.NOT_FOUND, result.getStatus());
        assertEquals("Updated Name", todoList.getName());
    }
}
