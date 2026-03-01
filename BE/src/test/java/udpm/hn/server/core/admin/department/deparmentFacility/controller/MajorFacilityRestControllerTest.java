package udpm.hn.server.core.admin.department.deparmentFacility.controller;

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
import udpm.hn.server.core.admin.department.departmentfacility.model.request.CreateMajorFacilityRequest;
import udpm.hn.server.core.admin.department.departmentfacility.model.request.MajorFacilityRequest;
import udpm.hn.server.core.admin.department.departmentfacility.service.MajorFacilityService;
import udpm.hn.server.core.common.base.ResponseObject;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class MajorFacilityRestControllerTest {


    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MajorFacilityService majorFacilityService;

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
    void testGetAllMajorFacilities() throws Exception {
        when(majorFacilityService.getAllMajorFacilities(any(MajorFacilityRequest.class)))
                .thenReturn((ResponseObject) mockResponse);

        mockMvc.perform(get("/api/v1/admin/major-facility/get-all/abc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Success"));
    }

    @Test
    void testCreateMajorFacility() throws Exception {
        CreateMajorFacilityRequest request = new CreateMajorFacilityRequest();

        when(majorFacilityService.createMajorFacility(any()))
                .thenReturn((ResponseObject) mockResponse);

        mockMvc.perform(post("/api/v1/admin/major-facility/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Success"));
    }

    @Test
    void testUpdateMajorFacility() throws Exception {
        CreateMajorFacilityRequest request = new CreateMajorFacilityRequest();

        when(majorFacilityService.updateMajorFacility(any(), any()))
                .thenReturn((ResponseObject) mockResponse);

        mockMvc.perform(put("/api/v1/admin/major-facility/update/abc")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Success"));
    }

    @Test
    void testGetMajorFacilityById() throws Exception {
        when(majorFacilityService.getMajorFacilityById("abc"))
                .thenReturn((ResponseObject) mockResponse);

        mockMvc.perform(get("/api/v1/admin/major-facility/abc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Success"));
    }

    @Test
    void testGetAllMajors() throws Exception {
        when(majorFacilityService.getAllMajors("abc"))
                .thenReturn((ResponseObject) mockResponse);

        mockMvc.perform(get("/api/v1/admin/major-facility/major/abc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Success"));
    }

    @Test
    void testChangeStatus() throws Exception {
        when(majorFacilityService.changeStatus("abc"))
                .thenReturn((ResponseObject) mockResponse);

        mockMvc.perform(put("/api/v1/admin/major-facility/status-majorFacility/abc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Success"));
    }

}
