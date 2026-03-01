package udpm.hn.server.core.manage.project.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import udpm.hn.server.core.manage.project.service.MaProjectStudentService;
import udpm.hn.server.core.common.base.ResponseObject;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class MaProjectStudentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MaProjectStudentService maProjectStudentService;

    private ResponseObject<Object> response;

    @BeforeEach
    void setUp() {
        response = new ResponseObject<>();
        response.setStatus(HttpStatus.OK);
        response.setMessage("Success");
        response.setData(null); // hoặc thêm mock data nếu cần
    }

    @Test
    void testGetAllPStudent() throws Exception {
        when(maProjectStudentService.getAll(anyString())).thenReturn((ResponseObject) response);
        mockMvc.perform(get("/api/v1/manage/project/student/1"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetAllStaff() throws Exception {
        when(maProjectStudentService.getAllStaff(anyString())).thenReturn((ResponseObject) response);
        mockMvc.perform(get("/api/v1/manage/project/student/staff/1"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetAllStaffByProject() throws Exception {
        when(maProjectStudentService.getAllStaffByProject(anyString())).thenReturn((ResponseObject) response);
        mockMvc.perform(get("/api/v1/manage/project/student/staff-project/1"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetAllStudentByProject() throws Exception {
        when(maProjectStudentService.getAllStudentByProject(anyString())).thenReturn((ResponseObject) response);
        mockMvc.perform(get("/api/v1/manage/project/student/student-project/1"))
                .andExpect(status().isOk());
    }
}
