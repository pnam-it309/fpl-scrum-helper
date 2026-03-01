package udpm.hn.server.core.member.vote.controller;

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
import udpm.hn.server.core.member.vote.service.StaffStudentVoteService;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@TestPropertySource("classpath:application.properties")
public class StaffStudentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private StaffStudentVoteService staffStudentVoteService;

    private ResponseObject<Object> mockResponse;

    @BeforeEach
    void setUp() {
        mockResponse = new ResponseObject<>();
        mockResponse.setStatus(HttpStatus.OK);
        mockResponse.setMessage("Success");
        mockResponse.setData(null);
    }

    @Test
    void testGetAllCategory_Success() throws Exception {
        String projectId = "test-project-id";
        when(staffStudentVoteService.getStuentStaffVote(anyString())).thenReturn((ResponseObject) mockResponse);
        mockMvc.perform(get("/api/v1/member/todo-vote/{idProject}", projectId))
                .andExpect(status().isOk());
    }

    @Test
    void testGetAllCategory_NotFound() throws Exception {
        String projectId = "notfound-project-id";
        ResponseObject<Object> notFoundResponse = new ResponseObject<>();
        notFoundResponse.setStatus(HttpStatus.NOT_FOUND);
        notFoundResponse.setMessage("Not Found");
        notFoundResponse.setData(null);

        when(staffStudentVoteService.getStuentStaffVote(anyString())).thenReturn((ResponseObject) notFoundResponse);
        mockMvc.perform(get("/api/v1/member/todo-vote/{idProject}", projectId))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetAllCategory_Exception() throws Exception {
        String projectId = "error-project-id";
        when(staffStudentVoteService.getStuentStaffVote(anyString())).thenThrow(new RuntimeException("Internal Error"));
        mockMvc.perform(get("/api/v1/member/todo-vote/{idProject}", projectId))
                .andExpect(status().is5xxServerError());
    }

    @Test
    void testGetAllCategory_NullResponse() throws Exception {
        String projectId = "null-response-project-id";
        when(staffStudentVoteService.getStuentStaffVote(anyString())).thenReturn(null);

        mockMvc.perform(get("/api/v1/member/todo-vote/{idProject}", projectId))
                .andExpect(status().isOk()); // hoặc isNoContent() nếu Helper trả về NO_CONTENT
    }
    @Test
    void testGetAllCategory_EmptyProjectId() throws Exception {
        when(staffStudentVoteService.getStuentStaffVote(anyString())).thenReturn((ResponseObject) mockResponse);

        mockMvc.perform(get("/api/v1/member/todo-vote/ "))
                .andExpect(status().isOk());
    }
    @Test
    void testGetAllCategory_Forbidden() throws Exception {
        String projectId = "forbidden-id";
        ResponseObject<Object> forbiddenResponse = new ResponseObject<>();
        forbiddenResponse.setStatus(HttpStatus.FORBIDDEN);
        forbiddenResponse.setMessage("Forbidden");
        forbiddenResponse.setData(null);

        when(staffStudentVoteService.getStuentStaffVote(anyString())).thenReturn((ResponseObject) forbiddenResponse);

        mockMvc.perform(get("/api/v1/member/todo-vote/{idProject}", projectId))
                .andExpect(status().isForbidden());
    }
    @Test
    void testGetAllCategory_DifferentIds() throws Exception {
        for (String id : new String[]{"id1", "id2", "id3", "id-special-😊"}) {
            when(staffStudentVoteService.getStuentStaffVote(anyString())).thenReturn((ResponseObject) mockResponse);

            mockMvc.perform(get("/api/v1/member/todo-vote/{idProject}", id))
                    .andExpect(status().isOk());
        }
    }

}
