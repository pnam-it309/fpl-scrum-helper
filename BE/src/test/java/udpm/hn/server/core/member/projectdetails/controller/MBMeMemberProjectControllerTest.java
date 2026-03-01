package udpm.hn.server.core.member.projectdetails.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;
import udpm.hn.server.core.common.base.ResponseObject;
import udpm.hn.server.core.member.projectdetails.model.request.MBMeCreateOrDeleteTodoVoteRequest;
import udpm.hn.server.core.member.projectdetails.service.MBMeMemberProjectService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@TestPropertySource("classpath:application.properties")

public class MBMeMemberProjectControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MBMeMemberProjectService mbMeMemberProjectService;

    private ResponseObject<Object> mockResponse;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockResponse = new ResponseObject<>();
        mockResponse.setStatus(HttpStatus.OK);
        mockResponse.setMessage("Success");
        mockResponse.setData(null);
    }


    @Test
    void testGetAllMemberProject() throws Exception {
        when(mbMeMemberProjectService.getAllMemberProject("p123", "t456")).thenReturn((ResponseObject) mockResponse);

        mockMvc.perform(get("/api/v1/project-details/member-project/p123/t456"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetAllFilterMemberProject() throws Exception {
        when(mbMeMemberProjectService.getAllFilterMemberProject("p123")).thenReturn((ResponseObject) mockResponse);

        mockMvc.perform(get("/api/v1/project-details/member-project/filter/p123"))
                .andExpect(status().isOk());
    }


    @Test
    void testJoinTodoVoteMemberProject() {
        MBMeCreateOrDeleteTodoVoteRequest request = new MBMeCreateOrDeleteTodoVoteRequest();
        when(mbMeMemberProjectService.joinTodoVoteMemberProject(any())).thenReturn((ResponseObject) mockResponse);

        MBMeMemberProjectController controller = new MBMeMemberProjectController(mbMeMemberProjectService);
        ResponseObject<?> response = controller.joinTodoVoteMemberProject(request);
        assert response.getStatus() == HttpStatus.OK;
    }

    @Test
    void testOutTodoVoteMemberProject() {
        MBMeCreateOrDeleteTodoVoteRequest request = new MBMeCreateOrDeleteTodoVoteRequest();
        when(mbMeMemberProjectService.outTodoVoteMemberProject(any())).thenReturn((ResponseObject) mockResponse);

        MBMeMemberProjectController controller = new MBMeMemberProjectController(mbMeMemberProjectService);
        ResponseObject<?> response = controller.outTodoVoteMemberProject(request);
        assert response.getStatus() == HttpStatus.OK;
    }
}

