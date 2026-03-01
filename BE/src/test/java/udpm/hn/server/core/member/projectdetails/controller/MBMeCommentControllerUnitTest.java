package udpm.hn.server.core.member.projectdetails.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import udpm.hn.server.core.common.base.ResponseObject;
import udpm.hn.server.core.member.projectdetails.model.request.*;
import udpm.hn.server.core.member.projectdetails.service.MBMeCommentService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class MBMeCommentControllerUnitTest {

    private MBMeCommentService mbMeCommentService;
    private MBMeCommentController controller;
    private ResponseObject<Object> mockResponse;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mbMeCommentService = mock(MBMeCommentService.class);
        controller = new MBMeCommentController(mbMeCommentService);

        mockResponse = new ResponseObject<>();
        mockResponse.setStatus(HttpStatus.OK);
        mockResponse.setMessage("OK");
        mockResponse.setData(null);
    }

    @Test
    void testAddComment() {
        when(mbMeCommentService.addCommment(any(MBMeCreateCommentRequest.class)))
                .thenReturn((ResponseObject)mockResponse);

        MBMeCreateCommentRequest req = new MBMeCreateCommentRequest();
        ResponseObject<?> res = controller.addComment(req);
        assertEquals(HttpStatus.OK, res.getStatus());
        assertEquals("OK", res.getMessage());
    }

    @Test
    void testUpdateComment() {
        when(mbMeCommentService.updateComment(any(MBMeUpdateCommentRequest.class)))
                .thenReturn((ResponseObject)mockResponse);

        MBMeUpdateCommentRequest req = new MBMeUpdateCommentRequest();
        ResponseObject<?> res = controller.updateComment(req);
        assertEquals(HttpStatus.OK, res.getStatus());
        assertEquals("OK", res.getMessage());
    }

    @Test
    void testDeleteComment() {
        when(mbMeCommentService.deleteComment(any(MBMeDeleteCommentRequest.class)))
                .thenReturn((ResponseObject)mockResponse);

        MBMeDeleteCommentRequest req = new MBMeDeleteCommentRequest();
        ResponseObject<?> res = controller.deleteComment(req);
        assertEquals(HttpStatus.OK, res.getStatus());
        assertEquals("OK", res.getMessage());
    }

    @Test
    void testGetAllCommentByIdTodo_Direct() {
        when(mbMeCommentService.getAllCommentByIdTodo(any(MBMeFindCommentRequest.class)))
                .thenReturn((ResponseObject)mockResponse);

        MBMeFindCommentRequest req = new MBMeFindCommentRequest();
        ResponseObject<?> res = controller.getAllCommentByIdTodo(req);
        assertEquals(HttpStatus.OK, res.getStatus());
    }

    @Test
    void testGetAllMemberInProject_Direct() {
        when(mbMeCommentService.getAllMemberInProject(any(MBMeFindTagMemberRequest.class)))
                .thenReturn((ResponseObject)mockResponse);

        MBMeFindTagMemberRequest req = new MBMeFindTagMemberRequest();
        ResponseObject<?> res = controller.getAllMemberInProject(req);
        assertEquals(HttpStatus.OK, res.getStatus());
    }

}

