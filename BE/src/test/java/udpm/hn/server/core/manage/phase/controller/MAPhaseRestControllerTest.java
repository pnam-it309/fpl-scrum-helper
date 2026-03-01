package udpm.hn.server.core.manage.phase.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import udpm.hn.server.core.common.base.ResponseObject;
import udpm.hn.server.core.manage.phase.model.request.MACreatePhaseRequest;
import udpm.hn.server.core.manage.phase.model.request.MAPhaseRequest;
import udpm.hn.server.core.manage.phase.model.request.MATodoByPhase;
import udpm.hn.server.core.manage.phase.service.MAPhaseService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class MAPhaseRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MAPhaseService maPhaseService;

    private ResponseObject<Object> response;

    @BeforeEach
    void setUp() {
        response = new ResponseObject<>();
        response.setStatus(HttpStatus.OK);
        response.setMessage("Success");
        response.setData(null);
    }

    @Test
    void testGetAllPhase() throws Exception {
        when(maPhaseService.getAllPhase(any(MAPhaseRequest.class))).thenReturn((ResponseObject) response);
        mockMvc.perform(get("/api/v1/manage/phase-project"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetAllPhaseStatistics() throws Exception {
        when(maPhaseService.getAllPhase("1")).thenReturn((ResponseObject) response);
        mockMvc.perform(get("/api/v1/manage/phase-project/statistics/1"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetPhaseSuccess() throws Exception {
        when(maPhaseService.findByPhaseStatus("1")).thenReturn((ResponseObject) response);
        mockMvc.perform(get("/api/v1/manage/phase-project/phase-handle-success/1"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetAllSprint() throws Exception {
        when(maPhaseService.getAllSprint(any(MAPhaseRequest.class))).thenReturn((ResponseObject) response);
        mockMvc.perform(get("/api/v1/manage/phase-project/sprint"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetAllPhaseByIdProject() throws Exception {
        when(maPhaseService.getAllPhaseByProjectId("1")).thenReturn((ResponseObject) response);
        mockMvc.perform(get("/api/v1/manage/phase-project/1"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetAllPhaseByIdProject_StatusNull() throws Exception {
        ResponseObject<Object> responseNullStatus = new ResponseObject<>();
        responseNullStatus.setStatus(null);
        responseNullStatus.setMessage("No status");
        responseNullStatus.setData(null);

        when(maPhaseService.getAllPhaseByProjectId("no_status")).thenReturn((ResponseObject)responseNullStatus);

        mockMvc.perform(get("/api/v1/manage/phase-project/no_status"))
                .andExpect(status().isNotFound())
                .andExpect(result -> result.getResponse().getContentAsString().contains("Không tìm thấy dữ liệu"));
    }
    @Test
    void testCreatePhase() {
        MACreatePhaseRequest request = new MACreatePhaseRequest();
        when(maPhaseService.createPhase(request)).thenReturn((ResponseObject)response);

        ResponseEntity<?> result = new MAPhaseRestController(maPhaseService).createPhase(request);

        Assertions.assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(maPhaseService, times(1)).createPhase(request);
    }

    @Test
    void testCreateTodoByPhase() {
        MATodoByPhase request = new MATodoByPhase();
        when(maPhaseService.createTodoByPhase(request)).thenReturn((ResponseObject)response);

        ResponseEntity<?> result = new MAPhaseRestController(maPhaseService).createTodoByPhase(request);

        Assertions.assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(maPhaseService, times(1)).createTodoByPhase(request);
    }

    @Test
    void testDeletePhase() {
        when(maPhaseService.deletePhase("1")).thenReturn((ResponseObject)response);

        ResponseEntity<?> result = new MAPhaseRestController(maPhaseService).deletePhase("1");

        Assertions.assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(maPhaseService, times(1)).deletePhase("1");
    }


}
