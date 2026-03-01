package udpm.hn.server.core.member.projectdetails.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import udpm.hn.server.core.common.base.ResponseObject;
import udpm.hn.server.core.member.projectdetails.model.request.MBMeJoinOrOutLabelTodoRequest;
import udpm.hn.server.core.member.projectdetails.service.MBMeLabelTodoService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class MBMeLabelTodoControllerTest {

    private MBMeLabelTodoService mbMeLabelTodoService;
    private MBMeLabelTodoController controller;
    private ResponseObject<Object> mockResponse;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mbMeLabelTodoService = mock(MBMeLabelTodoService.class);
        controller = new MBMeLabelTodoController(mbMeLabelTodoService);

        mockResponse = new ResponseObject<>();
        mockResponse.setStatus(HttpStatus.OK);
        mockResponse.setMessage("OK");
        mockResponse.setData(null);
    }

    @Test
    void testJoinLabelTodo() {
        when(mbMeLabelTodoService.joinLabelTodo(any(MBMeJoinOrOutLabelTodoRequest.class)))
                .thenReturn((ResponseObject)mockResponse);

        MBMeJoinOrOutLabelTodoRequest req = new MBMeJoinOrOutLabelTodoRequest();
        ResponseObject<?> res = controller.joinLabelTodo(req);
        assertEquals(HttpStatus.OK, res.getStatus());
        assertEquals("OK", res.getMessage());
    }

    @Test
    void testOutLabelTodo() {
        when(mbMeLabelTodoService.outLabelTodo(any(MBMeJoinOrOutLabelTodoRequest.class)))
                .thenReturn((ResponseObject)mockResponse);

        MBMeJoinOrOutLabelTodoRequest req = new MBMeJoinOrOutLabelTodoRequest();
        ResponseObject<?> res = controller.outLabelTodo(req);
        assertEquals(HttpStatus.OK, res.getStatus());
        assertEquals("OK", res.getMessage());
    }

    @Test
    void testJoinLabelTodo_ReturnNull() {
        when(mbMeLabelTodoService.joinLabelTodo(any())).thenReturn(null);

        MBMeJoinOrOutLabelTodoRequest req = new MBMeJoinOrOutLabelTodoRequest();
        ResponseObject<?> res = controller.joinLabelTodo(req);
        assertNull(res);
    }
    @Test
    void testOutLabelTodo_ReturnNull() {
        when(mbMeLabelTodoService.outLabelTodo(any())).thenReturn(null);

        MBMeJoinOrOutLabelTodoRequest req = new MBMeJoinOrOutLabelTodoRequest();
        ResponseObject<?> res = controller.outLabelTodo(req);
        assertNull(res);
    }
    @Test
    void testJoinLabelTodo_CustomStatus() {
        ResponseObject<Object> customRes = new ResponseObject<>();
        customRes.setStatus(HttpStatus.BAD_REQUEST);
        customRes.setMessage("Some error");
        when(mbMeLabelTodoService.joinLabelTodo(any())).thenReturn((ResponseObject)customRes);

        MBMeJoinOrOutLabelTodoRequest req = new MBMeJoinOrOutLabelTodoRequest();
        ResponseObject<?> res = controller.joinLabelTodo(req);
        assertEquals(HttpStatus.BAD_REQUEST, res.getStatus());
        assertEquals("Some error", res.getMessage());
    }


}

