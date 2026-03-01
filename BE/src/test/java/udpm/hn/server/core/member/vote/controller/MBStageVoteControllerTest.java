package udpm.hn.server.core.member.vote.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import udpm.hn.server.core.member.vote.model.request.MBStageVoteRequest;
import udpm.hn.server.core.member.vote.service.MBStageVoteService;

import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@TestPropertySource("classpath:application.properties")
public class MBStageVoteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MBStageVoteService mbStageVoteService;

    private ResponseObject<Object> mockResponse;

    @BeforeEach
    void setUp() {
        mockResponse = new ResponseObject<>();
        mockResponse.setStatus(HttpStatus.OK);
        mockResponse.setMessage("Success");
        mockResponse.setData(null);
    }

    @Test
    void testGetVotingIsOnGoing_NoParam() throws Exception {
        when(mbStageVoteService.getVotingIsOnGoing(any(MBStageVoteRequest.class))).thenReturn((ResponseObject) mockResponse);

        mockMvc.perform(get("/api/v1/member/todo-vote/get-voting-is-on-going"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetVotingIsOnGoing_WithParams() throws Exception {
        when(mbStageVoteService.getVotingIsOnGoing(any(MBStageVoteRequest.class))).thenReturn((ResponseObject) mockResponse);

        mockMvc.perform(get("/api/v1/member/todo-vote/get-voting-is-on-going")
                        .param("projectId", "123")
                        .param("phaseId", "456"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetUpcomingVote_NoParam() throws Exception {
        when(mbStageVoteService.GetUpcomingVote(any(MBStageVoteRequest.class))).thenReturn((ResponseObject) mockResponse);

        mockMvc.perform(get("/api/v1/member/todo-vote/get-up-coming-vote"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetUpcomingVote_WithParams() throws Exception {
        when(mbStageVoteService.GetUpcomingVote(any(MBStageVoteRequest.class))).thenReturn((ResponseObject) mockResponse);

        mockMvc.perform(get("/api/v1/member/todo-vote/get-up-coming-vote")
                        .param("projectId", "321")
                        .param("phaseId", "654"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetVotingIsOnGoing_ServiceError() throws Exception {
        when(mbStageVoteService.getVotingIsOnGoing(any(MBStageVoteRequest.class))).thenThrow(new RuntimeException("Service Error"));

        mockMvc.perform(get("/api/v1/member/todo-vote/get-voting-is-on-going"))
                .andExpect(status().is5xxServerError());
    }

    @Test
    void testGetUpcomingVote_ServiceError() throws Exception {
        when(mbStageVoteService.GetUpcomingVote(any(MBStageVoteRequest.class))).thenThrow(new RuntimeException("Service Error"));

        mockMvc.perform(get("/api/v1/member/todo-vote/get-up-coming-vote"))
                .andExpect(status().is5xxServerError());
    }

    @Test
    void testGetVotingIsOnGoing_ServiceReturnNull() throws Exception {
        when(mbStageVoteService.getVotingIsOnGoing(any(MBStageVoteRequest.class))).thenReturn(null);
        mockMvc.perform(get("/api/v1/member/todo-vote/get-voting-is-on-going"))
                .andExpect(status().isOk()); // hoặc .isNoContent() tùy Helper
    }

    @Test
    void testGetUpcomingVote_ServiceReturnNull() throws Exception {
        when(mbStageVoteService.GetUpcomingVote(any(MBStageVoteRequest.class))).thenReturn(null);
        mockMvc.perform(get("/api/v1/member/todo-vote/get-up-coming-vote"))
                .andExpect(status().isOk()); // hoặc .isNoContent() tùy Helper
    }
    @Test
    void testGetVotingIsOnGoing_OnlyProjectId() throws Exception {
        when(mbStageVoteService.getVotingIsOnGoing(any(MBStageVoteRequest.class))).thenReturn((ResponseObject) mockResponse);
        mockMvc.perform(get("/api/v1/member/todo-vote/get-voting-is-on-going")
                        .param("projectId", "123"))
                .andExpect(status().isOk());
    }
    @Test
    void testGetUpcomingVote_OnlyPhaseId() throws Exception {
        when(mbStageVoteService.GetUpcomingVote(any(MBStageVoteRequest.class))).thenReturn((ResponseObject) mockResponse);

        mockMvc.perform(get("/api/v1/member/todo-vote/get-up-coming-vote")
                        .param("phaseId", "654"))
                .andExpect(status().isOk());
    }

    // Test gọi với cả projectId lẫn phaseId rỗng (giá trị rỗng)
    @Test
    void testGetVotingIsOnGoing_WithEmptyParams() throws Exception {
        when(mbStageVoteService.getVotingIsOnGoing(any(MBStageVoteRequest.class))).thenReturn((ResponseObject) mockResponse);

        mockMvc.perform(get("/api/v1/member/todo-vote/get-voting-is-on-going")
                        .param("projectId", "")
                        .param("phaseId", ""))
                .andExpect(status().isOk());
    }

    @Test
    void testGetUpcomingVote_WithEmptyParams() throws Exception {
        when(mbStageVoteService.GetUpcomingVote(any(MBStageVoteRequest.class))).thenReturn((ResponseObject) mockResponse);

        mockMvc.perform(get("/api/v1/member/todo-vote/get-up-coming-vote")
                        .param("projectId", "")
                        .param("phaseId", ""))
                .andExpect(status().isOk());
    }

    // Nếu MBStageVoteRequest có thêm field mới (ví dụ startTime), test cả param đó luôn:
    @Test
    void testGetVotingIsOnGoing_WithExtraParams() throws Exception {
        when(mbStageVoteService.getVotingIsOnGoing(any(MBStageVoteRequest.class))).thenReturn((ResponseObject) mockResponse);

        mockMvc.perform(get("/api/v1/member/todo-vote/get-voting-is-on-going")
                        .param("projectId", "999")
                        .param("phaseId", "888")
                        .param("someOtherParam", "test"))
                .andExpect(status().isOk());
    }

    // Test lại các case lỗi cho endpoint còn lại (nếu cần)
    @Test
    void testGetUpcomingVote_OnlyProjectId() throws Exception {
        when(mbStageVoteService.GetUpcomingVote(any(MBStageVoteRequest.class))).thenReturn((ResponseObject) mockResponse);

        mockMvc.perform(get("/api/v1/member/todo-vote/get-up-coming-vote")
                        .param("projectId", "321"))
                .andExpect(status().isOk());
    }

}
