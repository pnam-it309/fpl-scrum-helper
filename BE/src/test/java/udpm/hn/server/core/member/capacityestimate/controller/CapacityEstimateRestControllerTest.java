package udpm.hn.server.core.member.capacityestimate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import udpm.hn.server.core.common.base.ResponseObject;
import udpm.hn.server.core.member.capacityestimate.service.CapacityEstimateService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@TestPropertySource("classpath:application.properties")

public class CapacityEstimateRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CapacityEstimateService capacityEstimateService;

    private ResponseObject<Object> response;

    @BeforeEach
    void setUp() {
        response = new ResponseObject<>();
        response.setStatus(HttpStatus.OK);
        response.setMessage("Success");
        response.setData(null);
    }

    @Test
    void testGetAllCapacityEstimate() throws Exception {
        when(capacityEstimateService.getAll(any())).thenReturn((ResponseObject) response);

        mockMvc.perform(get("/api/v1/member/capacity-estimate"))
                .andExpect(status().isOk());
    }

    @Test
    void testCapacityEstimateByPhase() throws Exception {
        when(capacityEstimateService.CapacityEstimateBySprint(anyString())).thenReturn((ResponseObject) response);

        mockMvc.perform(get("/api/v1/member/capacity-estimate/capacity-estimate/123"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetAllSprintByProject() throws Exception {
        when(capacityEstimateService.getAllSprint(anyString())).thenReturn((ResponseObject) response);

        mockMvc.perform(get("/api/v1/member/capacity-estimate/phase-project/456"))
                .andExpect(status().isOk());
    }

    @Test
    void testDetailCapacityEstimate() throws Exception {
        when(capacityEstimateService.detailCapacityEstimate(anyString())).thenReturn((ResponseObject) response);

        mockMvc.perform(get("/api/v1/member/capacity-estimate/789"))
                .andExpect(status().isOk());
    }

    @Test
    void testVelocityTB() throws Exception {
        when(capacityEstimateService.velocityTB(anyString())).thenReturn((ResponseObject) response);

        mockMvc.perform(get("/api/v1/member/capacity-estimate/velocity/999"))
                .andExpect(status().isOk());
    }

    @Test
    void testRemoveCapacityEstimate() throws Exception {
        when(capacityEstimateService.deleteCapacityEstimate(anyString())).thenReturn((ResponseObject) response);

        mockMvc.perform(delete("/api/v1/member/capacity-estimate/123"))
                .andExpect(status().isOk());
    }

    @Test
    void testCreateCapacityEstimate() throws Exception {
        when(capacityEstimateService.createCapacityEstimate(any())).thenReturn((ResponseObject) response);

        mockMvc.perform(post("/api/v1/member/capacity-estimate"))
                .andExpect(status().isOk());
    }

    @Test
    void testUpdateCapacityEstimate() throws Exception {
        when(capacityEstimateService.updateCapacityEstimate(anyString(), any())).thenReturn((ResponseObject) response);

        mockMvc.perform(put("/api/v1/member/capacity-estimate/456"))
                .andExpect(status().isOk());
    }
}

