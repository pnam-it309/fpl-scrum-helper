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
import udpm.hn.server.core.admin.department.department.model.request.ADCreateOrUpdateDepartmentRequest;
import udpm.hn.server.core.admin.department.department.model.request.DepartmentSearchRequest;
import udpm.hn.server.core.admin.department.department.service.DepartmentService;
import udpm.hn.server.core.common.base.ResponseObject;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class ADDepartmentRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DepartmentService departmentService;

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
    void testGetAllDepartment() throws Exception {
        DepartmentSearchRequest request = new DepartmentSearchRequest();

        // Mock the service response
        when(departmentService.getAllDepartment(any(DepartmentSearchRequest.class)))
                .thenReturn((ResponseObject) mockResponse);

        // Perform GET request and verify response
        mockMvc.perform(get("/api/v1/admin/department/get-all-department")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Success"));
    }

    @Test
    void testAddDepartment() throws Exception {
        ADCreateOrUpdateDepartmentRequest request = new ADCreateOrUpdateDepartmentRequest();

        // Mock the service response
        when(departmentService.addDepartment(any(ADCreateOrUpdateDepartmentRequest.class)))
                .thenReturn((ResponseObject) mockResponse);

        // Perform POST request and verify response
        mockMvc.perform(post("/api/v1/admin/department/add-department")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))  // Serialize the request object to JSON
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Success"));
    }

    @Test
    void testUpdateDepartment() throws Exception {
        ADCreateOrUpdateDepartmentRequest request = new ADCreateOrUpdateDepartmentRequest();

        // Mock the service response
        when(departmentService.updateDepartment(any(ADCreateOrUpdateDepartmentRequest.class), any()))
                .thenReturn((ResponseObject) mockResponse);

        // Perform PUT request and verify response
        mockMvc.perform(put("/api/v1/admin/department/update-department/abc")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))  // Serialize the request object to JSON
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Success"));
    }

    @Test
    void testDetailDepartment() throws Exception {
        // Mock the service response
        when(departmentService.detailDepartment("abc"))
                .thenReturn((ResponseObject) mockResponse);

        // Perform GET request and verify response
        mockMvc.perform(get("/api/v1/admin/department/detail-department/abc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Success"));
    }

    @Test
    void testChangeStatus() throws Exception {
        // Mock the service response
        when(departmentService.changeStatus("abc"))
                .thenReturn((ResponseObject) mockResponse);

        // Perform PUT request and verify response
        mockMvc.perform(put("/api/v1/admin/department/status-department/abc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Success"));
    }
}
