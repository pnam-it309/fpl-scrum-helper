package udpm.hn.server.core.manage.todo.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import udpm.hn.server.core.common.base.ResponseObject;
import udpm.hn.server.core.manage.todo.model.request.MATodoCURequest;
import udpm.hn.server.core.manage.todo.service.MATodoService;
import udpm.hn.server.core.manage.todo.service.MATypeTodoService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

public class MATodoRestControllerMessageMappingTest {

    @InjectMocks
    private MATodoRestController controller;

    @Mock
    private MATodoService maTodoService;
    @Mock
    private MATypeTodoService maTypeTodoService;

    private ResponseObject response; // để raw type, không cần generic <Object>

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        response = new ResponseObject();
        response.setStatus(org.springframework.http.HttpStatus.OK);
        response.setMessage("Success");
        response.setData(null);
    }

    @Test
    void testCreateTodo_MessageMapping() {
        MATodoCURequest req = new MATodoCURequest();
        when(maTodoService.createTodo(any(MATodoCURequest.class))).thenReturn(response);

        ResponseEntity<?> res = controller.createTodo(req);
        assertEquals(org.springframework.http.HttpStatus.OK, ((ResponseObject)res.getBody()).getStatus());
    }

    @Test
    void testUpdateTodo_MessageMapping() {
        MATodoCURequest req = new MATodoCURequest();
        when(maTodoService.updateTodo(eq("id1"), any(MATodoCURequest.class))).thenReturn(response);

        ResponseEntity<?> res = controller.updateTodo("id1", req);
        assertEquals(org.springframework.http.HttpStatus.OK, ((ResponseObject)res.getBody()).getStatus());
    }

    @Test
    void testDeleteTodo_MessageMapping() {
        when(maTodoService.deleteTodo(eq("id1"))).thenReturn(response);

        ResponseEntity<?> res = controller.createTodo("id1");
        assertEquals(org.springframework.http.HttpStatus.OK, ((ResponseObject)res.getBody()).getStatus());
    }
}
