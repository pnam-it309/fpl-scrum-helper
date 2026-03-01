package udpm.hn.server.core.member.projectdetails.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import udpm.hn.server.core.common.base.ResponseObject;
import udpm.hn.server.core.member.projectdetails.service.MBTypeService;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class MBMeTypeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MBTypeService mbTypeService;

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
    @DisplayName("GET /type/list - should return 200 OK with success response")
    void testGetAllType() throws Exception {
        when(mbTypeService.getAllType()).thenReturn((ResponseObject) mockResponse);

        mockMvc.perform(get("/api/v1/project-details/type/list"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Success"));
    }
    @Test
    @DisplayName("GET /type/list - service returns error status")
    void testGetAllType_ServiceReturnError() throws Exception {
        ResponseObject<Object> errorResponse = new ResponseObject<>();
        errorResponse.setSuccess(false);
        errorResponse.setStatus(HttpStatus.BAD_REQUEST);
        errorResponse.setMessage("Bad request");
        errorResponse.setData(null);

        when(mbTypeService.getAllType()).thenReturn((ResponseObject)errorResponse);

        mockMvc.perform(get("/api/v1/project-details/type/list"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Bad request"));
    }
    @Test
    @DisplayName("GET /type/list - service returns null")
    void testGetAllType_ServiceReturnsNull() throws Exception {
        when(mbTypeService.getAllType()).thenReturn(null);

        mockMvc.perform(get("/api/v1/project-details/type/list"))
                .andExpect(status().isOk()); // hoặc .isNoContent() tuỳ Helper xử lý
    }
    @Test
    @DisplayName("GET /type/list - service throws exception")
    void testGetAllType_ServiceThrowsException() throws Exception {
        when(mbTypeService.getAllType()).thenThrow(new RuntimeException("Service Error"));
        mockMvc.perform(get("/api/v1/project-details/type/list"))
                .andExpect(status().is5xxServerError());
    }

}
