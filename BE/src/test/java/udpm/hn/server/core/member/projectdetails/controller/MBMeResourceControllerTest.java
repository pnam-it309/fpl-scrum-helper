package udpm.hn.server.core.member.projectdetails.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import udpm.hn.server.core.common.base.ResponseObject;
import udpm.hn.server.core.member.projectdetails.model.request.MBMeCreateResourceRequest;
import udpm.hn.server.core.member.projectdetails.model.request.MBMeDeleteResourceRequest;
import udpm.hn.server.core.member.projectdetails.model.request.MBMeUpdateResourceRequest;
import udpm.hn.server.core.member.projectdetails.service.MBMeResourceService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class MBMeResourceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MBMeResourceService mbMeResourceService;

    @Autowired
    private ObjectMapper objectMapper;

    private ResponseObject<Object> mockResponse;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockResponse = new ResponseObject<>();
        mockResponse.setSuccess(true);
        mockResponse.setStatus(HttpStatus.OK);
        mockResponse.setMessage("Success");
        mockResponse.setData(null);
    }

    @Test
    void testGetAllResourceTodo() throws Exception {
        String idTodo = "todo-123";
        when(mbMeResourceService.getAll(idTodo)).thenReturn((ResponseObject) mockResponse);

        mockMvc.perform(get("/api/v1/project-details/resource")
                        .param("idTodo", idTodo))
                .andExpect(status().isOk());
    }

    @Test
    void testCreateResourceTodo() {
        MBMeResourceController controller = new MBMeResourceController(mbMeResourceService);
        MBMeCreateResourceRequest request = new MBMeCreateResourceRequest();
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.create(StompCommand.SEND);

        ResponseObject<?> mockResponse = new ResponseObject<>();
        mockResponse.setStatus(HttpStatus.OK);

        when(mbMeResourceService.create(request, headerAccessor)).thenReturn((ResponseObject) mockResponse);

        ResponseObject<?> response = controller.createResourceTodo(request, headerAccessor);
        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    void testUpdateResourceTodo() {
        MBMeResourceController controller = new MBMeResourceController(mbMeResourceService);
        MBMeUpdateResourceRequest request = new MBMeUpdateResourceRequest();
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.create(StompCommand.SEND);

        ResponseObject<?> mockResponse = new ResponseObject<>();
        mockResponse.setStatus(HttpStatus.OK);

        when(mbMeResourceService.update(request, headerAccessor)).thenReturn((ResponseObject) mockResponse);

        ResponseObject<?> response = controller.updateResourceTodo(request, headerAccessor);
        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    void testDeleteResourceTodo() {
        MBMeResourceController controller = new MBMeResourceController(mbMeResourceService);
        MBMeDeleteResourceRequest request = new MBMeDeleteResourceRequest();
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.create(StompCommand.SEND);

        ResponseObject<?> mockResponse = new ResponseObject<>();
        mockResponse.setStatus(HttpStatus.OK);

        when(mbMeResourceService.delete(request, headerAccessor)).thenReturn((ResponseObject) mockResponse);

        ResponseObject<?> response = controller.deleteResourceTodo(request, headerAccessor);
        assertEquals(HttpStatus.OK, response.getStatus());
    }
    @Test
    void testGetAllResourceTodo_ServiceReturnNull() throws Exception {
        String idTodo = "todo-123";
        when(mbMeResourceService.getAll(idTodo)).thenReturn(null);

        mockMvc.perform(get("/api/v1/project-details/resource")
                        .param("idTodo", idTodo))
                .andExpect(status().isOk()); // hoặc .isNoContent() tùy Helper
    }
    @Test
    void testGetAllResourceTodo_ServiceReturnError() throws Exception {
        String idTodo = "todo-123";
        ResponseObject<Object> errorRes = new ResponseObject<>();
        errorRes.setStatus(HttpStatus.NOT_FOUND);
        errorRes.setMessage("Not found");
        errorRes.setData(null);

        when(mbMeResourceService.getAll(idTodo)).thenReturn((ResponseObject)errorRes);

        mockMvc.perform(get("/api/v1/project-details/resource")
                        .param("idTodo", idTodo))
                .andExpect(status().isNotFound());
    }
    @Test
    void testGetAllResourceTodo_ServiceThrowException() throws Exception {
        String idTodo = "todo-123";
        when(mbMeResourceService.getAll(idTodo)).thenThrow(new RuntimeException("Error"));

        mockMvc.perform(get("/api/v1/project-details/resource")
                        .param("idTodo", idTodo))
                .andExpect(status().is5xxServerError());
    }
    @Test
    void testCreateResourceTodo_ServiceReturnNull() {
        MBMeResourceController controller = new MBMeResourceController(mbMeResourceService);
        MBMeCreateResourceRequest request = new MBMeCreateResourceRequest();
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.create(StompCommand.SEND);

        when(mbMeResourceService.create(request, headerAccessor)).thenReturn(null);

        ResponseObject<?> res = controller.createResourceTodo(request, headerAccessor);
        assertEquals(null, res);
    }

    @Test
    void testCreateResourceTodo_ServiceReturnError() {
        MBMeResourceController controller = new MBMeResourceController(mbMeResourceService);
        MBMeCreateResourceRequest request = new MBMeCreateResourceRequest();
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.create(StompCommand.SEND);

        ResponseObject<Object> errorRes = new ResponseObject<>();
        errorRes.setStatus(HttpStatus.BAD_REQUEST);

        when(mbMeResourceService.create(request, headerAccessor)).thenReturn((ResponseObject)errorRes);

        ResponseObject<?> res = controller.createResourceTodo(request, headerAccessor);
        assertEquals(HttpStatus.BAD_REQUEST, res.getStatus());
    }

}
