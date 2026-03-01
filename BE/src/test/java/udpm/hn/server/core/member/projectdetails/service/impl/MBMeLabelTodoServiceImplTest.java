package udpm.hn.server.core.member.projectdetails.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import udpm.hn.server.core.common.base.ResponseObject;
import udpm.hn.server.core.member.projectdetails.model.request.MBMeJoinOrOutLabelTodoRequest;
import udpm.hn.server.core.member.projectdetails.repository.MBMeLabelRepository;
import udpm.hn.server.core.member.projectdetails.repository.MBMeLabelTodoRepository;
import udpm.hn.server.core.member.projectdetails.repository.MBMeProjectRepository;
import udpm.hn.server.core.member.projectdetails.repository.MBMeTodoRepository;
import udpm.hn.server.core.member.projectdetails.service.impl.MBMeLabelTodoServiceImpl;
import udpm.hn.server.entity.LabelProject;
import udpm.hn.server.entity.LabelProjectTodo;
import udpm.hn.server.entity.Todo;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class MBMeLabelTodoServiceImplTest {

    @Mock
    private MBMeLabelRepository labelRepository;

    @Mock
    private MBMeTodoRepository todoRepository;

    @Mock
    private MBMeLabelTodoRepository labelTodoRepository;

    @Mock
    private MBMeProjectRepository projectRepository;

    @InjectMocks
    private MBMeLabelTodoServiceImpl service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private MBMeJoinOrOutLabelTodoRequest createRequest(String labelId, String todoId) {
        MBMeJoinOrOutLabelTodoRequest request = new MBMeJoinOrOutLabelTodoRequest();
        request.setIdLabel(labelId);
        request.setIdTodoJoinOrOut(todoId);
        return request;
    }

    @Test
    void joinLabelTodo_shouldReturnConflictIfLabelTodoExists() {
        MBMeJoinOrOutLabelTodoRequest request = createRequest("label1", "todo1");
        LabelProjectTodo existing = new LabelProjectTodo();

        when(labelTodoRepository.findByLabelProjectIdAndTodoId("label1", "todo1")).thenReturn(existing);

        ResponseObject<?> response = service.joinLabelTodo(request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatus());
        assertNull(response.getData());
        assertEquals("Todo ko tồn tại", response.getMessage());
    }

    @Test
    void joinLabelTodo_shouldReturnNotFoundIfTodoMissing() {
        MBMeJoinOrOutLabelTodoRequest request = createRequest("label1", "todo1");

        when(labelTodoRepository.findByLabelProjectIdAndTodoId("label1", "todo1")).thenReturn(null);
        when(todoRepository.findById("todo1")).thenReturn(Optional.empty());

        ResponseObject<?> response = service.joinLabelTodo(request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatus());
        assertEquals("Todo ko tồn tại", response.getMessage());
    }

    @Test
    void joinLabelTodo_shouldReturnNotFoundIfLabelMissing() {
        MBMeJoinOrOutLabelTodoRequest request = createRequest("label1", "todo1");

        when(labelTodoRepository.findByLabelProjectIdAndTodoId("label1", "todo1")).thenReturn(null);
        when(todoRepository.findById("todo1")).thenReturn(Optional.of(new Todo()));
        when(labelRepository.findById("label1")).thenReturn(Optional.empty());

        ResponseObject<?> response = service.joinLabelTodo(request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatus());
        assertEquals("Todo ko tồn tại", response.getMessage());
    }

    @Test
    void joinLabelTodo_shouldJoinSuccessfully() {
        MBMeJoinOrOutLabelTodoRequest request = new MBMeJoinOrOutLabelTodoRequest();
        request.setIdLabel("label1");
        request.setIdTodoJoinOrOut("todo1");
        request.setIdTodo("todo1"); // Đây là dòng quan trọng cần thêm!

        Todo todo = new Todo();
        LabelProject label = new LabelProject();

        LabelProjectTodo expectedSaved = new LabelProjectTodo();
        expectedSaved.setTodo(todo);
        expectedSaved.setLabelProject(label);

        when(labelTodoRepository.findByLabelProjectIdAndTodoId("label1", "todo1")).thenReturn(null);
        when(todoRepository.findById("todo1")).thenReturn(Optional.of(todo));
        when(labelRepository.findById("label1")).thenReturn(Optional.of(label));
        when(labelTodoRepository.save(any(LabelProjectTodo.class))).thenReturn(expectedSaved);

        ResponseObject<?> response = service.joinLabelTodo(request);

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(expectedSaved, response.getData());
        assertEquals("Join label todo thành công", response.getMessage());
    }

    @Test
    void outLabelTodo_shouldReturnNotFoundIfLabelTodoMissing() {
        MBMeJoinOrOutLabelTodoRequest request = createRequest("label1", "todo1");

        when(labelTodoRepository.findByLabelProjectIdAndTodoId("label1", "todo1")).thenReturn(null);

        ResponseObject<?> response = service.outLabelTodo(request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatus());
        assertEquals("label Project todo ko tồn tại", response.getMessage());
    }

    @Test
    void outLabelTodo_shouldDeleteSuccessfully() {
        MBMeJoinOrOutLabelTodoRequest request = new MBMeJoinOrOutLabelTodoRequest();
        request.setIdLabel("label1");
        request.setIdTodoJoinOrOut("todo1");
        request.setIdTodo("todo1");

        LabelProjectTodo labelTodo = new LabelProjectTodo();

        when(labelTodoRepository.findByLabelProjectIdAndTodoId("label1", "todo1")).thenReturn(labelTodo);

        ResponseObject<?> response = service.outLabelTodo(request);

        verify(labelTodoRepository, times(1)).delete(labelTodo);
        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(labelTodo, response.getData());
        assertEquals("Đã xóa label project todo thành công", response.getMessage());
    }


}
