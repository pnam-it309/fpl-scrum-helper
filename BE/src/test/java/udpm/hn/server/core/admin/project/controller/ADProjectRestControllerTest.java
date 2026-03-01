package udpm.hn.server.core.admin.project.controller;

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
import udpm.hn.server.core.admin.project.model.request.ADProjectCreateRequest;
import udpm.hn.server.core.admin.project.model.request.ADProjectSearchRequest;
import udpm.hn.server.core.admin.project.model.response.ADProjectOverview;
import udpm.hn.server.core.admin.project.service.ADProjectService;
import udpm.hn.server.core.common.base.ResponseObject;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class ADProjectRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ADProjectService adProjectService;

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
    void testGetAllProject() throws Exception {
        when(adProjectService.getAllProject(any(ADProjectSearchRequest.class))).thenReturn((ResponseObject) mockResponse);

        mockMvc.perform(get("/api/v1/admin/project"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Success"));
    }

    @Test
    void testGetAllFacility() throws Exception {
        when(adProjectService.getAllFacility()).thenReturn((ResponseObject) mockResponse);

        mockMvc.perform(get("/api/v1/admin/project/facility"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Success"));
    }

    @Test
    void testDetailProject() throws Exception {
        when(adProjectService.detailProject("abc")).thenReturn((ResponseObject) mockResponse);

        mockMvc.perform(get("/api/v1/admin/project/abc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Success"));
    }

    @Test
    void testGetAllDepartmentFacility() throws Exception {
        when(adProjectService.getAllDepartmentFacility("fac123")).thenReturn((ResponseObject) mockResponse);

        mockMvc.perform(get("/api/v1/admin/project/department-facility/fac123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Success"));
    }

    @Test
    void testCreateProject() throws Exception {
        ADProjectCreateRequest request = new ADProjectCreateRequest();
        when(adProjectService.createProject(any())).thenReturn((ResponseObject) mockResponse);

        mockMvc.perform(post("/api/v1/admin/project")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Success"));
    }

    @Test
    void testUpdateProject() throws Exception {
    }

    @Test
    void testDeleteProject() throws Exception {
        when(adProjectService.deleteProject("proj123")).thenReturn((ResponseObject) mockResponse);

        mockMvc.perform(delete("/api/v1/admin/project/proj123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Success"));
    }

    @Test
    void testCountProjectByStatus() throws Exception {
        when(adProjectService.countProjectByStatus()).thenReturn((ResponseObject) mockResponse);

        mockMvc.perform(get("/api/v1/admin/project/project-statistics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Success"));
    }

    @Test
    void testGetProjectParticipantCounts() throws Exception {
        when(adProjectService.getProjectParticipantCounts()).thenReturn((ResponseObject) mockResponse);

        mockMvc.perform(get("/api/v1/admin/project/project-statistics/participants"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Success"));
    }

    @Test
    void testGetProjectTodoCounts() throws Exception {
        Map<String, Integer> mockMap = new HashMap<>();
        mockMap.put("Dept1", 10);

        when(adProjectService.countProjectsByDepartment()).thenReturn((ResponseObject) mockResponse);
        mockMvc.perform(get("/api/v1/admin/project/project-statistics/project-todo-counts"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetCountTotalProjects() throws Exception {
        ADProjectOverview overview = new ADProjectOverview();
        overview.setTotalProjects(10L);
        overview.setInProgress(4L);
        overview.setDone(3L);
        overview.setPaused(2L);
        overview.setOverdue(1L);

        ResponseObject<ADProjectOverview> response = new ResponseObject<>();
        response.setStatus(HttpStatus.OK);
        response.setMessage("Thống kê tổng số dự án thành công");
        response.setData(overview);

        when(adProjectService.countTotalProjects()).thenReturn((ResponseObject) response);

        mockMvc.perform(get("/api/v1/admin/project/project-statistics/count-total-projects"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.totalProjects").value(10))
                .andExpect(jsonPath("$.data.inProgress").value(4))
                .andExpect(jsonPath("$.data.done").value(3))
                .andExpect(jsonPath("$.data.paused").value(2))
                .andExpect(jsonPath("$.data.overdue").value(1));
    }

    @Test
    void testGetProjectCountByFacility() throws Exception {
        Map<String, Integer> mockMap = new HashMap<>();
        mockMap.put("Dept1", 10);

        when(adProjectService.countProjectsByDepartment()).thenReturn((ResponseObject) mockResponse);
        mockMvc.perform(get("/api/v1/admin/project/project-statistics/project-by-facility"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetProjectCountByDepartment() throws Exception {
        Map<String, Integer> mockMap = new HashMap<>();
        mockMap.put("Dept1", 10);

        when(adProjectService.countProjectsByDepartment()).thenReturn((ResponseObject) mockResponse);

        mockMvc.perform(get("/api/v1/admin/project/project-statistics/project-by-department"))
                .andExpect(status().isOk());
    }


    @Test
    void testGetTaskByStatusAndProject() throws Exception {
        Map<String, Integer> mockMap = new HashMap<>();
        mockMap.put("Dept1", 10);

        when(adProjectService.countProjectsByDepartment()).thenReturn((ResponseObject) mockResponse);
        mockMvc.perform(get("/api/v1/admin/project/project-statistics/task-by-status-project"))
                .andExpect(status().isOk());
    }
}