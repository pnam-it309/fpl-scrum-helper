package udpm.hn.server.core.manage.project.controller;

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
import udpm.hn.server.core.admin.project.service.ADProjectService;
import udpm.hn.server.core.common.base.ResponseObject;
import udpm.hn.server.core.manage.project.model.request.MaProjectCreateRequest;
import udpm.hn.server.core.manage.project.model.request.MaProjectSearchRequest;
import udpm.hn.server.core.manage.project.model.request.MaRestartProjectRequest;
import udpm.hn.server.core.manage.project.service.MaProjectService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class MaProjectRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MaProjectService maProjectService;

    @MockitoBean
    private ADProjectService adProjectService;

    private ResponseObject<Object> response;

    @BeforeEach
    void setUp() {
        response = new ResponseObject<>();
        response.setStatus(HttpStatus.OK);
        response.setMessage("Success");
        response.setData(null);
    }

    @Test
    void testGetAllProject() throws Exception {
        when(maProjectService.getAllProject(any(MaProjectSearchRequest.class))).thenReturn((ResponseObject) response);

        mockMvc.perform(get("/api/v1/manage/project"))
                .andExpect(status().isOk());
    }


    @Test
    void testGetAllFacility() throws Exception {
        when(maProjectService.getAllFacility()).thenReturn((ResponseObject) response);
        mockMvc.perform(get("/api/v1/manage/project/facility"))
                .andExpect(status().isOk());
    }

    @Test
    void testDetailProject() throws Exception {
        when(maProjectService.detailProject("1")).thenReturn((ResponseObject) response);
        mockMvc.perform(get("/api/v1/manage/project/1"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetAllDepartmentFacility() throws Exception {
        when(maProjectService.getAllDepartmentFacility("1")).thenReturn((ResponseObject) response);
        mockMvc.perform(get("/api/v1/manage/project/department-facility/1"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetDetailSummaryProject() throws Exception {
        when(maProjectService.getDetailSummaryProject("1")).thenReturn((ResponseObject) response);
        mockMvc.perform(get("/api/v1/manage/project/summary/1"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetAmountOfPhaseByStatus() throws Exception {
        when(maProjectService.getAmountOfPhaseByStatus("1")).thenReturn((ResponseObject) response);
        mockMvc.perform(get("/api/v1/manage/project/amount-of-phase-by-status/1"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetAmountOfTodoByPhase() throws Exception {
        when(maProjectService.getAmountOfTodoByPhase("1")).thenReturn((ResponseObject) response);
        mockMvc.perform(get("/api/v1/manage/project/amount-of-todo-by-phase/1"))
                .andExpect(status().isOk());
    }

    @Test
    void testFinishEarlyProject() throws Exception {
        when(maProjectService.finishEarlyProject("1", "user@example.com")).thenReturn((ResponseObject) response);
        mockMvc.perform(get("/api/v1/manage/project/finish-early/1/user@example.com"))
                .andExpect(status().isOk());
    }

    @Test
    void testCreateProject() throws Exception {
        MaProjectCreateRequest request = new MaProjectCreateRequest();
        when(maProjectService.createProject(any())).thenReturn((ResponseObject) response);

        mockMvc.perform(post("/api/v1/manage/project")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isOk());
    }


    @Test
    void testUpdateProject() throws Exception {
        MaProjectCreateRequest request = new MaProjectCreateRequest();
        when(maProjectService.updateProject(anyString(), any())).thenReturn((ResponseObject) response);
        mockMvc.perform(put("/api/v1/manage/project/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void testRestartProject() throws Exception {
        MaRestartProjectRequest request = new MaRestartProjectRequest();
        when(maProjectService.restartProject(anyString(), any())).thenReturn((ResponseObject) response);
        mockMvc.perform(put("/api/v1/manage/project/restart-project/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void testDeleteProject() throws Exception {
        when(maProjectService.deleteProject("1")).thenReturn((ResponseObject) response);
        mockMvc.perform(delete("/api/v1/manage/project/1"))
                .andExpect(status().isOk());
    }
}