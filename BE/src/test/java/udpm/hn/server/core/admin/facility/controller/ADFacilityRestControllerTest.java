package udpm.hn.server.core.admin.facility.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.batch.BatchAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import udpm.hn.server.core.admin.facility.model.request.ADCreateUpdateFacilityRequest;
import udpm.hn.server.core.admin.facility.model.request.ADFacilityRequest;
import udpm.hn.server.core.admin.facility.service.ADFacilityService;
import udpm.hn.server.core.common.base.ResponseObject;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class ADFacilityRestControllerTest {


    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ADFacilityService facilityService;

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
    void testGetAllFacility() throws Exception {
        when(facilityService.getAllFacility(any(ADFacilityRequest.class)))
                .thenReturn((ResponseObject) mockResponse);

        mockMvc.perform(get("/api/v1/admin/facility"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Success"));
    }

    @Test
    void testCreateFacility() throws Exception {
        ADCreateUpdateFacilityRequest request = new ADCreateUpdateFacilityRequest();

        when(facilityService.createFacility(any()))
                .thenReturn((ResponseObject) mockResponse);

        mockMvc.perform(post("/api/v1/admin/facility/add")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Success"));
    }

    @Test
    void testUpdateFacility() throws Exception {
        ADCreateUpdateFacilityRequest request = new ADCreateUpdateFacilityRequest();

        when(facilityService.updateFacility(any(), any()))
                .thenReturn((ResponseObject) mockResponse);

        mockMvc.perform(put("/api/v1/admin/facility/update/abc")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Success"));
    }

    @Test
    void testChangeStatusFacility() throws Exception {
        when(facilityService.changeFacilityStatus("abc"))
                .thenReturn((ResponseObject) mockResponse);

        mockMvc.perform(put("/api/v1/admin/facility/abc/change-status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Success"));
    }

    @Test
    void testGetFacilityById() throws Exception {
        when(facilityService.getFacilityById("abc"))
                .thenReturn((ResponseObject) mockResponse);

        mockMvc.perform(get("/api/v1/admin/facility/detail/abc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Success"));
    }

}
