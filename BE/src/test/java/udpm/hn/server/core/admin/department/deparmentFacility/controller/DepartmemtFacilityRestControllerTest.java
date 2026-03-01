package udpm.hn.server.core.admin.department.deparmentFacility.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.batch.BatchAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import udpm.hn.server.core.admin.department.departmentfacility.controller.DepartmemtFacilityRestController;
import udpm.hn.server.core.admin.department.departmentfacility.model.request.CreateOrUpdateDepartmentFacilityRequest;
import udpm.hn.server.core.admin.department.departmentfacility.model.request.FindFacilityDetailRequest;
import udpm.hn.server.core.admin.department.departmentfacility.service.DepartmentFacilityService;
import udpm.hn.server.core.common.base.ResponseObject;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class DepartmemtFacilityRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DepartmentFacilityService departmentFacilityService;

    @Autowired
    private ObjectMapper objectMapper;

    private ResponseObject<?> mockResponse;

    @BeforeEach
    void setUp() {
        mockResponse = new ResponseObject<>();
        mockResponse.setStatus(HttpStatus.OK);
        mockResponse.setMessage("Success");
        mockResponse.setData(null);
    }

    @Test
    void testGetAllDepartmentFacility() throws Exception {
        when(departmentFacilityService.getAllDepartmentFacility(any(), any(FindFacilityDetailRequest.class)))
                .thenReturn(( ResponseObject) mockResponse);

        mockMvc.perform(get("/api/v1/admin/department-facility/get-all-department-facility/abc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Success"));
    }

    @Test
    void testAddDepartmentFacility() throws Exception {
        CreateOrUpdateDepartmentFacilityRequest request = new CreateOrUpdateDepartmentFacilityRequest();

        when(departmentFacilityService.addDepartmentFacility(any()))
                .thenReturn(( ResponseObject) mockResponse);

        mockMvc.perform(post("/api/v1/admin/department-facility/add-department-facility")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Success"));
    }

    @Test
    void testUpdateDepartmentFacility() throws Exception {
        CreateOrUpdateDepartmentFacilityRequest request = new CreateOrUpdateDepartmentFacilityRequest();

        when(departmentFacilityService.updateDepartmentFacility(any(), any()))
                .thenReturn(( ResponseObject) mockResponse);

        mockMvc.perform(put("/api/v1/admin/department-facility/update-department-facility/abc")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Success"));
    }

    @Test
    void testGetListFacilityName() throws Exception {
        when(departmentFacilityService.getListFacility())
                .thenReturn(( ResponseObject) mockResponse);

        mockMvc.perform(get("/api/v1/admin/department-facility/get-list-facility"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Success"));
    }

    @Test
    void testGetDepartmentName() throws Exception {
        when(departmentFacilityService.getDepartmentName("abc"))
                .thenReturn(( ResponseObject) mockResponse);

        mockMvc.perform(get("/api/v1/admin/department-facility/get-department-name/abc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Success"));
    }

    @Test
    void testDetailDepartmentFacility() throws Exception {
        when(departmentFacilityService.detailDepartmentFacility("abc"))
                .thenReturn(( ResponseObject) mockResponse);

        mockMvc.perform(get("/api/v1/admin/department-facility/detail-department-facility/abc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Success"));
    }

    @Test
    void testChangeStatus() throws Exception {
        when(departmentFacilityService.changeStatus("abc"))
                .thenReturn(( ResponseObject) mockResponse);

        mockMvc.perform(put("/api/v1/admin/department-facility/status-departmentFacility/abc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Success"));
    }
}