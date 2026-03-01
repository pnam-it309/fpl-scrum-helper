package udpm.hn.server.core.member.projectdetails.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import udpm.hn.server.core.common.base.ResponseObject;
import udpm.hn.server.core.member.projectdetails.model.request.ADCreateUpdateTypeTodoRequest;
import udpm.hn.server.core.member.projectdetails.model.request.MBJoinTypeTodoRequest;
import udpm.hn.server.core.member.projectdetails.model.response.MBTypeTodoResponse;
import udpm.hn.server.core.member.projectdetails.repository.MBMeTodoRepository;
import udpm.hn.server.core.member.projectdetails.repository.MBMeTypeTodoRepository;
import udpm.hn.server.entity.Todo;
import udpm.hn.server.entity.TypeTodo;
import udpm.hn.server.infrastructure.constant.EntityStatus;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class MBMeTypeTodoServiceImplTest {

    @InjectMocks
    private MBMeTypeTodoServiceImpl service;

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @Mock
    private MBMeTypeTodoRepository typeTodoRepository;

    @Mock
    private MBMeTodoRepository todoRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllTypeTodo() {
        when(typeTodoRepository.getAllTypeTodo()).thenReturn(java.util.Collections.emptyList());

        ResponseObject<?> response = service.getAllTypeTodo();

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals("Get all category successfully", response.getMessage());
    }

    @Test
    void testCreateTypeTodo_success() {
        ADCreateUpdateTypeTodoRequest request = new ADCreateUpdateTypeTodoRequest();
        request.setType("Công việc mới");

        when(typeTodoRepository.save(any(TypeTodo.class))).thenAnswer(i -> i.getArguments()[0]);

        ResponseObject<?> response = service.createTypeTodo(request);

        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertEquals("Thêm loại công việc thành công", response.getMessage());
    }

    @Test
    void testCreateTypeTodo_failure() {
        ADCreateUpdateTypeTodoRequest request = new ADCreateUpdateTypeTodoRequest();
        request.setType("Bị lỗi");

        when(typeTodoRepository.save(any())).thenThrow(new RuntimeException("DB error"));

        ResponseObject<?> response = service.createTypeTodo(request);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatus());
        assertEquals("Lỗi khi thêm loại công việc", response.getMessage());
    }

    @Test
    void testChangeTypeTodoStatus_foundAndToggleStatus() {
        TypeTodo type = new TypeTodo();
        type.setId("type1");
        type.setType("Kiểu 1");
        type.setStatus(EntityStatus.INACTIVE);

        ADCreateUpdateTypeTodoRequest request = new ADCreateUpdateTypeTodoRequest();
        request.setTypeId("type1");

        when(typeTodoRepository.findById("type1")).thenReturn(Optional.of(type));
        when(typeTodoRepository.save(any())).thenReturn(type);

        ResponseObject<?> response = service.changeTypeTodoStatus(request);

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals("Đổi trạng thái thể loại thành công", response.getMessage());
        assertEquals(EntityStatus.ACTIVE, type.getStatus());
    }

    @Test
    void testChangeTypeTodoStatus_notFound() {
        ADCreateUpdateTypeTodoRequest request = new ADCreateUpdateTypeTodoRequest();
        request.setTypeId("not_exist");

        when(typeTodoRepository.findById("not_exist")).thenReturn(Optional.empty());

        ResponseObject<?> response = service.changeTypeTodoStatus(request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatus());
        assertEquals("Không tìm thấy thể loại", response.getMessage());
    }

    @Test
    void testGetTypeTodoById_found() {
        MBTypeTodoResponse responseMock = mock(MBTypeTodoResponse.class);

        when(responseMock.getId()).thenReturn("123");
        when(responseMock.getType()).thenReturn("Công việc");
        when(responseMock.getStatus()).thenReturn(EntityStatus.ACTIVE);

        when(typeTodoRepository.getTypeTodoById("123")).thenReturn(Optional.of(responseMock));

        ResponseObject<?> response = service.getTypeTodoById("123");

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals("Get category successfully", response.getMessage());
        assertEquals(responseMock, response.getData());
    }




    @Test
    void testGetTypeTodoById_notFound() {
        when(typeTodoRepository.getTypeTodoById("404")).thenReturn(Optional.empty());

        ResponseObject<?> response = service.getTypeTodoById("404");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatus());
        assertEquals("Category not found", response.getMessage());
    }

    // ✅ Test joinTypeTodo() - Success
    @Test
    void testJoinTypeTodo_success() {
        MBJoinTypeTodoRequest request = new MBJoinTypeTodoRequest();
        request.setIdTodo("todo1");
        request.setIdType("type1");

        Todo todo = new Todo();
        TypeTodo type = new TypeTodo();

        when(todoRepository.findById("todo1")).thenReturn(Optional.of(todo));
        when(typeTodoRepository.findById("type1")).thenReturn(Optional.of(type));

        ResponseObject<?> response = service.joinTypeTodo(request);

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals("join thành công", response.getMessage());
        assertEquals(type, todo.getTypeTodo());
    }

    @Test
    void testJoinTypeTodo_todoNotFound() {
        MBJoinTypeTodoRequest request = new MBJoinTypeTodoRequest();
        request.setIdTodo("todo404");
        request.setIdType("type1");

        when(todoRepository.findById("todo404")).thenReturn(Optional.empty());

        ResponseObject<?> response = service.joinTypeTodo(request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatus());
        assertEquals("không thấy ", response.getMessage());
    }

    @Test
    void testJoinTypeTodo_typeNotFound() {
        MBJoinTypeTodoRequest request = new MBJoinTypeTodoRequest();
        request.setIdTodo("todo1");
        request.setIdType("type404");

        when(todoRepository.findById("todo1")).thenReturn(Optional.of(new Todo()));
        when(typeTodoRepository.findById("type404")).thenReturn(Optional.empty());

        ResponseObject<?> response = service.joinTypeTodo(request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatus());
        assertEquals("không thấy ", response.getMessage());
    }

}
