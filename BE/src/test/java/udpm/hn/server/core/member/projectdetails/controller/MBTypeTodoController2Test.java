package udpm.hn.server.core.member.projectdetails.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import udpm.hn.server.core.common.base.ResponseObject;
import udpm.hn.server.core.member.projectdetails.model.request.ADCreateUpdateTypeTodoRequest;
import udpm.hn.server.core.member.projectdetails.model.request.MBJoinTypeTodoRequest;
import udpm.hn.server.core.member.projectdetails.service.MBMeTypeTodoService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@TestPropertySource("classpath:application.properties")
class MBTypeTodoController2Test {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MBMeTypeTodoService mbMeTypeTodoService;

    @Autowired
    private ObjectMapper objectMapper;

    private ResponseObject<Object> mockResponse;

    @BeforeEach
    void setUp() {
        mockResponse = new ResponseObject<>();
        mockResponse.setSuccess(true);
        mockResponse.setStatus(HttpStatus.OK);
        mockResponse.setMessage("Success");
        mockResponse.setData(null);
    }

    @Test
    void testGetAllTypeTodo() throws Exception {
        when(mbMeTypeTodoService.getAllTypeTodo())
                .thenReturn((ResponseObject)mockResponse);

        mockMvc.perform(get("/api/v1/project-details/type-todo"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Success"));
    }

    @Test
    void testUpdateTypeTodo() throws Exception {
        String typeId = "type-001";
        ADCreateUpdateTypeTodoRequest request = new ADCreateUpdateTypeTodoRequest();
        request.setType("Updated Type");

        when(mbMeTypeTodoService.updateTypeTodo(anyString(), any(ADCreateUpdateTypeTodoRequest.class)))
                .thenReturn((ResponseObject)mockResponse);

        mockMvc.perform(put("/api/v1/project-details/type-todo/update/{typeId}", typeId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Success"));
    }

    @Test
    void testGetTypeTodoById() throws Exception {
        String typeId = "type-002";

        when(mbMeTypeTodoService.getTypeTodoById(anyString()))
                .thenReturn((ResponseObject)mockResponse);

        mockMvc.perform(get("/api/v1/project-details/type-todo/detail/{typeId}", typeId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Success"));
    }

    // Các hàm MessageMapping (WebSocket) chỉ có thể test bằng unit test gọi trực tiếp controller,
    // không dùng được MockMvc vì không phải REST endpoint.
    @Test
    void testCreateTypeTodo_MessageMapping() {
        ADCreateUpdateTypeTodoRequest request = new ADCreateUpdateTypeTodoRequest();
        request.setType("Type new");

        when(mbMeTypeTodoService.createTypeTodo(any(ADCreateUpdateTypeTodoRequest.class)))
                .thenReturn((ResponseObject)mockResponse);

        MBTypeTodoController controller = new MBTypeTodoController(mbMeTypeTodoService);
        ResponseEntity<?> res = controller.createTypeTodo(request);
        ResponseObject<?> body = (ResponseObject<?>) res.getBody();
        assert body != null;
        assert body.getStatus() == HttpStatus.OK;
    }

    @Test
    void testChangeStatusTypeTodo_MessageMapping() {
        ADCreateUpdateTypeTodoRequest request = new ADCreateUpdateTypeTodoRequest();

        when(mbMeTypeTodoService.changeTypeTodoStatus(any(ADCreateUpdateTypeTodoRequest.class)))
                .thenReturn((ResponseObject)mockResponse);

        MBTypeTodoController controller = new MBTypeTodoController(mbMeTypeTodoService);
        ResponseEntity<?> res = controller.changeStatusTypeTodo(request);
        ResponseObject<?> body = (ResponseObject<?>) res.getBody();
        assert body != null;
        assert body.getStatus() == HttpStatus.OK;
    }

    @Test
    void testJoinTypeTodo_MessageMapping() {
        MBJoinTypeTodoRequest request = new MBJoinTypeTodoRequest();

        when(mbMeTypeTodoService.joinTypeTodo(any(MBJoinTypeTodoRequest.class)))
                .thenReturn((ResponseObject)mockResponse);

        MBTypeTodoController controller = new MBTypeTodoController(mbMeTypeTodoService);
        ResponseEntity<?> res = controller.JoinTypeTodo(request);
        ResponseObject<?> body = (ResponseObject<?>) res.getBody();
        assert body != null;
        assert body.getStatus() == HttpStatus.OK;
    }
    @Test
    void testGetAllTypeTodo_ReturnsNull() throws Exception {
        when(mbMeTypeTodoService.getAllTypeTodo()).thenReturn(null);
        mockMvc.perform(get("/api/v1/project-details/type-todo"))
                .andExpect(status().isOk()); // hoặc isNoContent nếu Helper xử lý đặc biệt
    }
    @Test
    void testGetTypeTodoById_ReturnsNull() throws Exception {
        when(mbMeTypeTodoService.getTypeTodoById(anyString())).thenReturn(null);
        mockMvc.perform(get("/api/v1/project-details/type-todo/detail/{typeId}", "type-x"))
                .andExpect(status().isOk());
    }
    @Test
    void testGetAllTypeTodo_ServiceThrowsException() throws Exception {
        when(mbMeTypeTodoService.getAllTypeTodo()).thenThrow(new RuntimeException("Test error"));
        mockMvc.perform(get("/api/v1/project-details/type-todo"))
                .andExpect(status().is5xxServerError());
    }
    @Test
    void testUpdateTypeTodo_TypeIdEmpty() throws Exception {
        ADCreateUpdateTypeTodoRequest request = new ADCreateUpdateTypeTodoRequest();
        request.setType("test");
        when(mbMeTypeTodoService.updateTypeTodo(anyString(), any())).thenReturn((ResponseObject) mockResponse);

        mockMvc.perform(put("/api/v1/project-details/type-todo/update/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().is4xxClientError()); // Nếu framework trả về 404/405/400
    }
    @Test
    void testJoinTypeTodo_ReturnsNull() {
        MBTypeTodoController controller = new MBTypeTodoController(mbMeTypeTodoService);
        when(mbMeTypeTodoService.joinTypeTodo(any())).thenReturn(null);
        ResponseEntity<?> res = controller.JoinTypeTodo(new MBJoinTypeTodoRequest());
        Assertions.assertNull(res.getBody());
    }
    @Test
    void testCreateTypeTodo_ThrowsException() {
        ADCreateUpdateTypeTodoRequest request = new ADCreateUpdateTypeTodoRequest();
        when(mbMeTypeTodoService.createTypeTodo(any())).thenThrow(new RuntimeException("err"));
        MBTypeTodoController ctrl = new MBTypeTodoController(mbMeTypeTodoService);
        Assertions.assertThrows(RuntimeException.class, () -> ctrl.createTypeTodo(request));
    }
    @Test
    void testChangeStatusTypeTodo_NullRequest() {
        MBTypeTodoController ctrl = new MBTypeTodoController(mbMeTypeTodoService);
        when(mbMeTypeTodoService.changeTypeTodoStatus(null)).thenReturn(null);
        ResponseEntity<?> res = ctrl.changeStatusTypeTodo(null);
        Assertions.assertNull(res.getBody());
    }
    @Test
    void testGetAllTypeTodo_ServiceReturnNull() throws Exception {
        when(mbMeTypeTodoService.getAllTypeTodo()).thenReturn(null);
        mockMvc.perform(get("/api/v1/project-details/type-todo"))
                .andExpect(status().isOk()); // hoặc isNoContent tuỳ Helper
    }
    @Test
    void testGetTypeTodoById_NullTypeId() throws Exception {
        when(mbMeTypeTodoService.getTypeTodoById(null)).thenReturn((ResponseObject) mockResponse);
        MBTypeTodoController ctrl = new MBTypeTodoController(mbMeTypeTodoService);
        ResponseEntity<?> res = ctrl.getByIdTypeTodo(null);
        Assertions.assertEquals(mockResponse, res.getBody());
    }
    @Test
    void testController_ConstructorNullService() {
        Assertions.assertThrows(NullPointerException.class, () -> new MBTypeTodoController(null));
    }

}
