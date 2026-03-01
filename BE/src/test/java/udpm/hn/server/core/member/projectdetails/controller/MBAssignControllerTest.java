package udpm.hn.server.core.member.projectdetails.controller;

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
import udpm.hn.server.core.member.projectdetails.service.MBMeAssignService;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@TestPropertySource("classpath:application.properties")

class MBAssignControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MBMeAssignService mbMeAssignService;

    private ResponseObject<Object> mockResponse;

    @BeforeEach
    void setUp() {
        mockResponse = new ResponseObject<>();
        mockResponse.setStatus(HttpStatus.OK);
        mockResponse.setMessage("Success");
        mockResponse.setData(null);
    }

    @Test
    void testGetMemberByIdTodo() throws Exception {
        String idTodo = "todo-123";

        when(mbMeAssignService.getAllMemberByIdTodo(anyString())).thenReturn((ResponseObject) mockResponse);

        mockMvc.perform(get("/api/v1/project-details/todo-vote/member-by-idtodo")
                        .param("idTodo", idTodo))
                .andExpect(status().isOk());
    }

    @Test
    void testGetTodoByStaffProject() throws Exception {
        String idStaff = "staff-456";
        String idProject = "project-789";

        when(mbMeAssignService.getTodoByIdStaffProject(anyString(), anyString()))
                .thenReturn((ResponseObject) mockResponse);

        mockMvc.perform(get("/api/v1/project-details/todo-vote/todo-by-staffproject")
                        .param("idStaff", idStaff)
                        .param("idProject", idProject))
                .andExpect(status().isOk());
    }
}
