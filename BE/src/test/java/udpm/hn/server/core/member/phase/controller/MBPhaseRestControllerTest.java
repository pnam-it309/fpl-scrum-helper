package udpm.hn.server.core.member.phase.controller;

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
import udpm.hn.server.core.member.phase.model.request.MBPhaseRequest;
import udpm.hn.server.core.member.phase.service.MBPhaseService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@TestPropertySource("classpath:application.properties")

public class MBPhaseRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MBPhaseService phaseService;

    private ResponseObject<Object> mockResponse;

    @BeforeEach
    void setUp() {
        mockResponse = new ResponseObject<>();
        mockResponse.setStatus(HttpStatus.OK);
        mockResponse.setMessage("Success");
        mockResponse.setData(null);
    }

    @Test
    void testGetAllPhase_shouldReturnOk() throws Exception {
        // Giả lập dữ liệu trả về từ service
        when(phaseService.getAllPhase(any(MBPhaseRequest.class), any(String.class))).thenReturn((ResponseObject) mockResponse);

        String testProjectId = "123";
        mockMvc.perform(get("/api/v1/member/phase-project/{idProject}", testProjectId))
                .andExpect(status().isOk());
    }

    @Test
    void testDetail_shouldReturnOk() throws Exception {
        when(phaseService.detail("123")).thenReturn((ResponseObject) mockResponse);

        mockMvc.perform(get("/api/v1/member/phase-project/detail/123"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetAllTodoByProject_shouldReturnOk() throws Exception {
        when(phaseService.getAllTodoByProject(any(MBPhaseRequest.class), eq("123"))).thenReturn((ResponseObject) mockResponse);

        mockMvc.perform(get("/api/v1/member/phase-project/get-all-todo/123"))
                .andExpect(status().isOk());
    }

    @Test
    void testUpdateTodoByPhase_shouldReturnOk() throws Exception {
        when(phaseService.updateTodoByPhaseProject("todo1", "phase2", "project3")).thenReturn((ResponseObject) mockResponse);

        mockMvc.perform(put("/api/v1/member/phase-project/update-phase-todo/todo1/phase2/project3"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetAllPhasesByProjectId_shouldReturnOk() throws Exception {
        when(phaseService.getAllPhase("projectId")).thenReturn((ResponseObject) mockResponse);

        mockMvc.perform(get("/api/v1/member/phase-project/get-all-phase/projectId"))
                .andExpect(status().isOk());
    }
}

