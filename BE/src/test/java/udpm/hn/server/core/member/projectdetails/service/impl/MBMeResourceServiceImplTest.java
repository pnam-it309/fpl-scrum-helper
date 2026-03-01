package udpm.hn.server.core.member.projectdetails.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import udpm.hn.server.core.common.base.ResponseObject;
import udpm.hn.server.core.common.base.TodoObject;
import udpm.hn.server.core.member.projectdetails.model.request.*;
import udpm.hn.server.core.member.projectdetails.model.response.MBMeResourceResponse;
import udpm.hn.server.core.member.projectdetails.repository.*;
import udpm.hn.server.entity.Activity;
import udpm.hn.server.entity.Resource;
import udpm.hn.server.entity.Todo;
import udpm.hn.server.utils.TodoListHelper;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class MBMeResourceServiceImplTest {

    @InjectMocks
    private MBMeResourceServiceImpl service;

    @Mock private SimpMessagingTemplate messagingTemplate;
    @Mock private MBMeTodoRepository todoRepo;
    @Mock private MBMeTodoListRepository todoListRepo;
    @Mock private MBMeLabelRepository labelRepo;
    @Mock private MBMeProjectRepository projectRepo;
    @Mock private MBMePhaseProjectRepository phaseProjectRepo;
    @Mock private TodoListHelper todoListHelper;
    @Mock private MBMeResourceRepository resourceRepo;
    @Mock private MBMeActivityRepository activityRepo;

    @Mock private StompHeaderAccessor headerAccessor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAll_shouldReturnResources() {
        MBMeResourceResponse mockResponse = mock(MBMeResourceResponse.class);
        when(mockResponse.getId()).thenReturn("res1");
        when(mockResponse.getName()).thenReturn("Tài liệu");
        when(mockResponse.getUrl()).thenReturn("http://example.com/resource");
        when(mockResponse.getTodoId()).thenReturn("todo1");

        when(resourceRepo.getAll("todo1")).thenReturn(Collections.singletonList(mockResponse));

        ResponseObject<?> response = service.getAll("todo1");

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals("Lay tất cả đính kèm thành công", response.getMessage());
        assertNotNull(response.getData());
    }

    @Test
    void create_shouldReturnNotFound_whenTodoNotExists() {
        MBMeCreateResourceRequest request = new MBMeCreateResourceRequest();
        request.setIdTodo("todo1");

        when(todoRepo.findById("todo1")).thenReturn(Optional.empty());

        ResponseObject<?> response = service.create(request, headerAccessor);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatus());
        assertEquals("không tìm thấy todo ", response.getMessage());
    }

    @Test
    void create_shouldCreateSuccessfully_withHttpUrl() {
        MBMeCreateResourceRequest request = new MBMeCreateResourceRequest();
        request.setIdTodo("todo1");
        request.setIdTodoList("todoList1");
        request.setProjectId("project1");
        request.setIdUser("user1");
        request.setUrlPath("/path");
        request.setName("File A");
        request.setUrl("example.com");

        Todo todo = new Todo();
        todo.setName("Todo 1");

        when(todoRepo.findById("todo1")).thenReturn(Optional.of(todo));

        Resource savedResource = new Resource();
        when(resourceRepo.save(any())).thenReturn(savedResource);

        Activity savedActivity = new Activity();
        when(activityRepo.save(any())).thenReturn(savedActivity);

        ResponseObject<?> response = service.create(request, headerAccessor);

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals("thêm thành công đính kèm", response.getMessage());

        TodoObject todoObj = (TodoObject) response.getData();
        assertEquals("todo1", todoObj.getIdTodo());
        verify(resourceRepo, times(1)).save(any());
        verify(activityRepo, times(1)).save(any());
        verify(messagingTemplate).convertAndSend(eq("/topic/subscribe-activity"), any(Object.class));
    }

    @Test
    void update_shouldReturnNotFound_whenResourceMissing() {
        MBMeUpdateResourceRequest request = new MBMeUpdateResourceRequest();
        request.setId("res1");

        when(resourceRepo.findById("res1")).thenReturn(Optional.empty());

        ResponseObject<?> response = service.update(request, headerAccessor);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatus());
        assertEquals("đính kèm không tồn tại", response.getMessage());
    }

    @Test
    void update_shouldUpdateSuccessfully() {
        MBMeUpdateResourceRequest request = new MBMeUpdateResourceRequest();
        request.setId("res1");
        request.setName("New Name");
        request.setUrl("http://updated.com");

        Resource existing = new Resource();
        when(resourceRepo.findById("res1")).thenReturn(Optional.of(existing));

        ResponseObject<?> response = service.update(request, headerAccessor);

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals("Cập nhật đính kèm thành công", response.getMessage());
        verify(resourceRepo).save(existing);
    }

    // -----------------------------
    @Test
    void delete_shouldReturnNotFound_whenTodoMissing() {
        MBMeDeleteResourceRequest request = new MBMeDeleteResourceRequest();
        request.setIdTodo("todoX");

        when(todoRepo.findById("todoX")).thenReturn(Optional.empty());

        ResponseObject<?> response = service.delete(request, headerAccessor);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatus());
        assertEquals("không tìm thấy todo ", response.getMessage());
    }

    @Test
    void delete_shouldDeleteSuccessfully() {
        MBMeDeleteResourceRequest request = new MBMeDeleteResourceRequest();
        request.setId("res1");
        request.setIdTodo("todo1");
        request.setIdTodoList("todoList1");
        request.setProjectId("project1");
        request.setIdUser("user1");
        request.setUrlPath("/path");
        request.setUrl("http://example.com");
        request.setName("Link A");

        Todo todo = new Todo();
        todo.setName("Thẻ 1");

        when(todoRepo.findById("todo1")).thenReturn(Optional.of(todo));
        when(activityRepo.save(any())).thenReturn(new Activity());

        ResponseObject<?> response = service.delete(request, headerAccessor);

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals("Xóa thành công đính kèm ", response.getMessage());
        verify(resourceRepo).deleteById("res1");
        verify(messagingTemplate).convertAndSend(eq("/topic/subscribe-activity"), any(Object.class));
    }

}
