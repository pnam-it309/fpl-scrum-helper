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
import udpm.hn.server.core.admin.project.service.ADProjectStudentService;
import udpm.hn.server.core.manage.project.model.request.MaUserProjectRequest;
import udpm.hn.server.core.manage.project.service.MaProjectStudentService;
import udpm.hn.server.core.common.base.ResponseObject;

import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class ADProjectStudentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ADProjectStudentService adProjectStudentService;

    @MockitoBean
    private MaProjectStudentService maProjectStudentService;

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
    void testFetchDataUserProject() throws Exception {
        when(maProjectStudentService.getAllUserProject(any(MaUserProjectRequest.class)))
                .thenReturn((ResponseObject) mockResponse);

        mockMvc.perform(get("/api/v1/admin/project/student/user-project"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Success"));
    }

    @Test
    void testGetAllPStudent() throws Exception {
        when(adProjectStudentService.getAll("123")).thenReturn((ResponseObject) mockResponse);

        mockMvc.perform(get("/api/v1/admin/project/student/123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Success"));
    }

    @Test
    void testGetAllStaff() throws Exception {
        when(adProjectStudentService.getAllStaff("123")).thenReturn((ResponseObject) mockResponse);

        mockMvc.perform(get("/api/v1/admin/project/student/staff/123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Success"));
    }

    @Test
    void testGetAllStaffByProject() throws Exception {
        when(adProjectStudentService.getAllStaffByProject("proj001")).thenReturn((ResponseObject) mockResponse);

        mockMvc.perform(get("/api/v1/admin/project/student/staff-project/proj001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Success"));
    }

    @Test
    void testGetAllStudentByProject() throws Exception {
        when(adProjectStudentService.getAllStudentByProject("proj001")).thenReturn((ResponseObject) mockResponse);

        mockMvc.perform(get("/api/v1/admin/project/student/student-project/proj001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Success"));
    }

}
