package udpm.hn.server.core.member.projectdetails.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import udpm.hn.server.core.common.base.ResponseObject;
import udpm.hn.server.core.member.projectdetails.model.request.*;
import udpm.hn.server.core.member.projectdetails.model.response.MBUpdateBackgroundProject;
import udpm.hn.server.core.member.projectdetails.service.MBMeImageService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class MBMeImageControllerMessageMappingTest {

    private MBMeImageService mbMeImageService;
    private MBMeImageController controller;
    private ResponseObject<Object> mockResponse;
    private StompHeaderAccessor headerAccessor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mbMeImageService = mock(MBMeImageService.class);
        controller = new MBMeImageController(mbMeImageService);

        mockResponse = new ResponseObject<>();
        mockResponse.setStatus(HttpStatus.OK);
        mockResponse.setMessage("OK");
        mockResponse.setData(null);

        headerAccessor = StompHeaderAccessor.create(StompCommand.SEND);
    }

    @Test
    void testCreateImage() {
        MBMeCreateImageRequest req = new MBMeCreateImageRequest();
        when(mbMeImageService.add(eq(req), any())).thenReturn((ResponseObject)mockResponse);
        ResponseObject<?> res = controller.createImage(req, headerAccessor);
        assertEquals(HttpStatus.OK, res.getStatus());
    }

    @Test
    void testUpdateNameImage() {
        MBMeUpdateNameImageRequest req = new MBMeUpdateNameImageRequest();
        when(mbMeImageService.updateNameImage(eq(req), any())).thenReturn((ResponseObject)mockResponse);
        ResponseObject<?> res = controller.updateNameImage(req, headerAccessor);
        assertEquals(HttpStatus.OK, res.getStatus());
    }

    @Test
    void testDeleteImage() {
        MBMeDeleteImageRequest req = new MBMeDeleteImageRequest();
        when(mbMeImageService.deleteImage(eq(req), any())).thenReturn((ResponseObject)mockResponse);
        ResponseObject<?> res = controller.deleteImage(req, headerAccessor);
        assertEquals(HttpStatus.OK, res.getStatus());
    }

    @Test
    void testChangeCoverTodo() {
        MBMeChangeCoverTodoRequest req = new MBMeChangeCoverTodoRequest();
        when(mbMeImageService.changeCoverTodo(eq(req), any())).thenReturn((ResponseObject)mockResponse);
        ResponseObject<?> res = controller.changeCoverTodo(req, headerAccessor);
        assertEquals(HttpStatus.OK, res.getStatus());
    }

    @Test
    void testUpdateBackgroundProject() {
        MBUpdateBackgroundProject req = new MBUpdateBackgroundProject();
        when(mbMeImageService.updateBackgroundProject(eq(req))).thenReturn((ResponseObject)mockResponse);
        ResponseObject<?> res = controller.updateNameImage(req);
        assertEquals(HttpStatus.OK, res.getStatus());
    }
    @Test
    void testCreateImage_ServiceReturnsNull() {
        MBMeCreateImageRequest req = new MBMeCreateImageRequest();
        when(mbMeImageService.add(eq(req), any())).thenReturn(null);
        ResponseObject<?> res = controller.createImage(req, headerAccessor);
        assertNull(res);
    }
    @Test
    void testUpdateNameImage_ServiceReturnsNull() {
        MBMeUpdateNameImageRequest req = new MBMeUpdateNameImageRequest();
        when(mbMeImageService.updateNameImage(eq(req), any())).thenReturn(null);
        ResponseObject<?> res = controller.updateNameImage(req, headerAccessor);
        assertNull(res);
    }
    @Test
    void testUpdateBackgroundProject_NullRequest() {
        when(mbMeImageService.updateBackgroundProject(null)).thenReturn((ResponseObject)mockResponse);
        ResponseObject<?> res = controller.updateNameImage((MBUpdateBackgroundProject) null);
        assertEquals(HttpStatus.OK, res.getStatus());
    }

}
