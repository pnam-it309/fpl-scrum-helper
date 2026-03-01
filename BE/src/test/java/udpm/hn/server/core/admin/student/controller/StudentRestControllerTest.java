package udpm.hn.server.core.admin.student.controller;

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
import udpm.hn.server.core.admin.student.model.request.*;
import udpm.hn.server.core.admin.student.service.StudentService;
import udpm.hn.server.core.common.base.ResponseObject;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class StudentRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private StudentService studentService;

    private ResponseObject<Object> response;

    @BeforeEach
    void setUp() {
        response = new ResponseObject<>();
        response.setStatus(HttpStatus.OK);
        response.setMessage("Success");
        response.setData(null);
    }

    @Test
    void testGetAllStudent() throws Exception {
        when(studentService.getAllStudnet(any())).thenReturn((ResponseObject) response);
        mockMvc.perform(get("/api/v1/admin/student/get-all-student"))
                .andExpect(status().isOk());
    }

    @Test
    void testAddStudent() throws Exception {
        ADCreateOrUpdateStudentRequest request = new ADCreateOrUpdateStudentRequest();
        when(studentService.addStudent(any())).thenReturn((ResponseObject) response);
        mockMvc.perform(post("/api/v1/admin/student/add-student")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void testFillAllStudent() throws Exception {
        when(studentService.findAllStudent()).thenReturn((ResponseObject) response);
        mockMvc.perform(get("/api/v1/admin/student/fill-all-student"))
                .andExpect(status().isOk());
    }

    @Test
    void testUpdateStudent() throws Exception {
        ADCreateOrUpdateStudentRequest request = new ADCreateOrUpdateStudentRequest();
        when(studentService.updateStudent(any(), eq("1"))).thenReturn((ResponseObject) response);
        mockMvc.perform(put("/api/v1/admin/student/update-student/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void testDetailStudent() throws Exception {
        when(studentService.detailStudent("1")).thenReturn((ResponseObject) response);
        mockMvc.perform(get("/api/v1/admin/student/detail-student/1"))
                .andExpect(status().isOk());
    }

    @Test
    void testStudentByDepartmentMajor() throws Exception {
        when(studentService.studentByDepartmentFacility("1")).thenReturn((ResponseObject) response);
        mockMvc.perform(get("/api/v1/admin/student/detailStudent/department-major/1"))
                .andExpect(status().isOk());
    }

    @Test
    void testDeleteStudentByFacility() throws Exception {
        when(studentService.deleteStudentByIdStudent("1", "admin@example.com")).thenReturn((ResponseObject) response);
        mockMvc.perform(delete("/api/v1/admin/student/student-facility/1/admin@example.com"))
                .andExpect(status().isOk());
    }

    @Test
    void testCreateStudentFDM() throws Exception {
        ADCreateStudentFDMRequest request = new ADCreateStudentFDMRequest();
        when(studentService.createStudentByFDM(any())).thenReturn((ResponseObject) response);
        mockMvc.perform(post("/api/v1/admin/student/major-department-facility")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void testUpdateStudentFDM() throws Exception {
        ADUpdateStudentFDMRequest request = new ADUpdateStudentFDMRequest();
        when(studentService.updateStudentByFDM(any())).thenReturn((ResponseObject) response);
        mockMvc.perform(put("/api/v1/admin/student/major-department-facility")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void testDeleteStudent() throws Exception {
        when(studentService.deleteStudent("1", "admin@example.com")).thenReturn((ResponseObject) response);
        mockMvc.perform(delete("/api/v1/admin/student/1/admin@example.com"))
                .andExpect(status().isOk());
    }

    @Test
    void testReadCSV() throws Exception {
        when(studentService.readCSV()).thenReturn((ResponseObject) response);
        mockMvc.perform(get("/api/v1/admin/student/read/fileLog"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetLogsImportStudent() throws Exception {
        when(studentService.getLogsImportStudent(anyInt(), anyInt())).thenReturn((ResponseObject) response);
        mockMvc.perform(get("/api/v1/admin/student/history?page=0&size=50"))
                .andExpect(status().isOk());
    }
}