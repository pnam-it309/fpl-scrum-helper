package udpm.hn.server.core.manage.todo.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import udpm.hn.server.core.common.base.ResponseObject;
import udpm.hn.server.core.manage.todo.model.request.MAVoteTodoRequest;
import udpm.hn.server.core.manage.todo.service.MATodoVoteService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MATodoVoteRestControllerUnitTest {

    @Mock
    private MATodoVoteService maTodoVoteService;

    @InjectMocks
    private MATodoVoteRestController controller;

    private ResponseObject<Object> response;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        response = new ResponseObject<>();
        response.setStatus(HttpStatus.OK);
        response.setMessage("Success");
        response.setData(null);
    }

    @Test
    void testCreateTodoVote_MessageMapping() {
        MAVoteTodoRequest request = new MAVoteTodoRequest();
        when(maTodoVoteService.createVote(request)).thenReturn((ResponseObject) response);

        ResponseEntity<?> entity = controller.createTodoVote(request);

        assertEquals(HttpStatus.OK, entity.getStatusCode());
        assertTrue(entity.getBody() instanceof ResponseObject);
        assertEquals("Success", ((ResponseObject<?>) entity.getBody()).getMessage());
    }

    @Test
    void testDeleteTodoVote_MessageMapping() {
        MAVoteTodoRequest request = new MAVoteTodoRequest();
        when(maTodoVoteService.deleteTodoVote(request)).thenReturn((ResponseObject) response);

        ResponseEntity<?> entity = controller.delete(request);

        assertEquals(HttpStatus.OK, entity.getStatusCode());
        assertTrue(entity.getBody() instanceof ResponseObject);
        assertEquals("Success", ((ResponseObject<?>) entity.getBody()).getMessage());
    }
}
