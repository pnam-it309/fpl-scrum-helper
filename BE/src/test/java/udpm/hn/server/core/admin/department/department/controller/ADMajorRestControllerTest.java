package udpm.hn.server.core.admin.department.department.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import udpm.hn.server.core.admin.department.department.model.request.ADCreateOrUpdateMajorRequest;
import udpm.hn.server.core.admin.department.department.model.request.ADMajorRequest;
import udpm.hn.server.core.admin.department.department.service.ADMajorService;
import udpm.hn.server.core.common.base.ResponseObject;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class ADMajorRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ADMajorService adMajorService;

    @Autowired
    private ObjectMapper objectMapper;

    private ResponseObject<?> mockResponse;

    @BeforeEach
    void setUp() {
        mockResponse = new ResponseObject<>();
        mockResponse.setStatus(HttpStatus.OK);
        mockResponse.setMessage("Success");
        mockResponse.setData(null); // Empty response data
    }

    @Test
    void testGetAllMajor() throws Exception {
        ADMajorRequest request = new ADMajorRequest();

        // Mock the service response
        when(adMajorService.getAllMajor(any(String.class), any(ADMajorRequest.class)))
                .thenReturn((ResponseObject)mockResponse);

        // Perform GET request and verify response
        mockMvc.perform(get("/api/v1/admin/major/get-all-major/departmentId")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Success"));
    }

    @Test
    void testAddMajor() throws Exception {
        ADCreateOrUpdateMajorRequest request = new ADCreateOrUpdateMajorRequest();

        // Mock the service response
        when(adMajorService.addMajor(any(ADCreateOrUpdateMajorRequest.class)))
                .thenReturn((ResponseObject)mockResponse);

        // Perform POST request and verify response
        mockMvc.perform(post("/api/v1/admin/major/add-major")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))  // Serialize the request object to JSON
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Success"));
    }

    @Test
    void testUpdateMajor() throws Exception {
        ADCreateOrUpdateMajorRequest request = new ADCreateOrUpdateMajorRequest();

        // Mock the service response
        when(adMajorService.updateMajor(any(ADCreateOrUpdateMajorRequest.class), any(String.class)))
                .thenReturn((ResponseObject)mockResponse);

        // Perform PUT request and verify response
        mockMvc.perform(put("/api/v1/admin/major/update-major/abc")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))  // Serialize the request object to JSON
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Success"));
    }

    @Test
    void testDeleteMajor() throws Exception {
        // Mock the service response
        when(adMajorService.deleteMajor(any(String.class)))
                .thenReturn((ResponseObject)mockResponse);

        // Perform DELETE request and verify response
        mockMvc.perform(put("/api/v1/admin/major/delete-major/abc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Success"));
    }

    @Test
    void testDetailMajor() throws Exception {
        // Mock the service response
        when(adMajorService.detailMajor(any(String.class)))
                .thenReturn((ResponseObject)mockResponse);

        // Perform GET request and verify response
        mockMvc.perform(get("/api/v1/admin/major/detail-major/abc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Success"));
    }

}
