package udpm.hn.server.core.member.vote.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import udpm.hn.server.core.common.base.ResponseObject;
import udpm.hn.server.core.member.vote.model.request.MBStageVoteRequest;
import udpm.hn.server.core.member.vote.model.response.MBStageVoteResponse;
import udpm.hn.server.core.member.vote.repository.MBStageVoteRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class MBStageVoteServiceImplTest {

    @Mock
    private MBStageVoteRepository mbStageVoteRepository;

    @InjectMocks
    private MBStageVoteServiceImpl mbStageVoteService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // Test for getVotingIsOnGoing()
    @Test
    void testGetVotingIsOnGoing_ShouldReturnActiveVote() {
        MBStageVoteRequest request = new MBStageVoteRequest();
        request.setIdProject("project123");

        MBStageVoteResponse mockResponse = new MBStageVoteResponse() {
            public String getId() { return "vote1"; }
            public Long getStartTime() { return 1000L; }
            public Long getEndTime() { return 2000L; }
            public String getPhaseId() { return "phase1"; }
            public String getNamePhaseProject() { return "Phase Alpha"; }
        };

        when(mbStageVoteRepository.findActiveStageVoteByProject(anyLong(), eq("project123")))
                .thenReturn(Optional.of(mockResponse));

        ResponseObject<?> result = mbStageVoteService.getVotingIsOnGoing(request);

        assertEquals(HttpStatus.OK, result.getStatus());
        assertEquals(Optional.of(mockResponse), result.getData());
        verify(mbStageVoteRepository, times(1)).findActiveStageVoteByProject(anyLong(), eq("project123"));
    }

    // Test for getVotingIsOnGoing() with no active vote
    @Test
    void testGetVotingIsOnGoing_ShouldReturnEmpty() {
        MBStageVoteRequest request = new MBStageVoteRequest();
        request.setIdProject("project123");

        when(mbStageVoteRepository.findActiveStageVoteByProject(anyLong(), eq("project123")))
                .thenReturn(Optional.empty());

        ResponseObject<?> result = mbStageVoteService.getVotingIsOnGoing(request);

        assertEquals(HttpStatus.OK, result.getStatus());
        assertEquals(Optional.empty(), result.getData());
    }

    // Test for GetUpcomingVote()
    @Test
    void testGetUpcomingVote_ShouldReturnList() {
        MBStageVoteRequest request = new MBStageVoteRequest();
        request.setIdProject("project123");

        MBStageVoteResponse mockResponse = new MBStageVoteResponse() {
            public String getId() { return "vote2"; }
            public Long getStartTime() { return 3000L; }
            public Long getEndTime() { return 4000L; }
            public String getPhaseId() { return "phase2"; }
            public String getNamePhaseProject() { return "Phase Beta"; }
        };

        when(mbStageVoteRepository.findUpcomingStageVoteByProject(anyLong(), eq("project123")))
                .thenReturn(List.of(mockResponse));

        ResponseObject<?> result = mbStageVoteService.GetUpcomingVote(request);

        assertEquals(HttpStatus.OK, result.getStatus());
        assertEquals(1, ((List<?>) result.getData()).size());
        verify(mbStageVoteRepository, times(1)).findUpcomingStageVoteByProject(anyLong(), eq("project123"));
    }

    // Test for GetUpcomingVote() with empty list
    @Test
    void testGetUpcomingVote_ShouldReturnEmptyList() {
        MBStageVoteRequest request = new MBStageVoteRequest();
        request.setIdProject("project123");

        when(mbStageVoteRepository.findUpcomingStageVoteByProject(anyLong(), eq("project123")))
                .thenReturn(List.of());

        ResponseObject<?> result = mbStageVoteService.GetUpcomingVote(request);

        assertEquals(HttpStatus.OK, result.getStatus());
        assertEquals(List.of(), result.getData());
    }
    @Test
    void testGetUpcomingVote_ShouldReturnEmptyList1() {
        MBStageVoteRequest request = new MBStageVoteRequest();
        request.setIdProject("project123");

        when(mbStageVoteRepository.findUpcomingStageVoteByProject(anyLong(), eq("project123")))
                .thenReturn(List.of());

        ResponseObject<?> result = mbStageVoteService.GetUpcomingVote(request);

        assertEquals(HttpStatus.OK, result.getStatus());
        assertEquals(List.of(), result.getData());
    }
//
//    @Test
//    void testGetUpcomingVote_ShouldReturnEmptyList2() {
//        MBStageVoteRequest request = new MBStageVoteRequest();
//        request.setIdProject("project123");
//
//        when(mbStageVoteRepository.findUpcomingStageVoteByProject(anyLong(), eq("project123")))
//                .thenReturn(List.of());
//
//        ResponseObject<?> result = mbStageVoteService.GetUpcomingVote(request);
//
//        assertEquals(HttpStatus.OK, result.getStatus());
//        assertEquals(List.of(), result.getData());
//    }
//
//
//    @Test
//    void testGetUpcomingVote_ShouldReturnEmptyList3() {
//        MBStageVoteRequest request = new MBStageVoteRequest();
//        request.setIdProject("project123");
//
//        when(mbStageVoteRepository.findUpcomingStageVoteByProject(anyLong(), eq("project123")))
//                .thenReturn(List.of());
//
//        ResponseObject<?> result = mbStageVoteService.GetUpcomingVote(request);
//
//        assertEquals(HttpStatus.OK, result.getStatus());
//        assertEquals(List.of(), result.getData());
//    }
//
//    @Test
//    void testGetUpcomingVote_ShouldReturnEmptyList4() {
//        MBStageVoteRequest request = new MBStageVoteRequest();
//        request.setIdProject("project123");
//
//        when(mbStageVoteRepository.findUpcomingStageVoteByProject(anyLong(), eq("project123")))
//                .thenReturn(List.of());
//
//        ResponseObject<?> result = mbStageVoteService.GetUpcomingVote(request);
//
//        assertEquals(HttpStatus.OK, result.getStatus());
//        assertEquals(List.of(), result.getData());
//    }
//
//    @Test
//    void testGetUpcomingVote_ShouldReturnEmptyList5() {
//        MBStageVoteRequest request = new MBStageVoteRequest();
//        request.setIdProject("project123");
//
//        when(mbStageVoteRepository.findUpcomingStageVoteByProject(anyLong(), eq("project123")))
//                .thenReturn(List.of());
//
//        ResponseObject<?> result = mbStageVoteService.GetUpcomingVote(request);
//
//        assertEquals(HttpStatus.OK, result.getStatus());
//        assertEquals(List.of(), result.getData());
//    }
//
//    @Test
//    void testGetUpcomingVote_ShouldReturnEmptyList6() {
//        MBStageVoteRequest request = new MBStageVoteRequest();
//        request.setIdProject("project123");
//
//        when(mbStageVoteRepository.findUpcomingStageVoteByProject(anyLong(), eq("project123")))
//                .thenReturn(List.of());
//
//        ResponseObject<?> result = mbStageVoteService.GetUpcomingVote(request);
//
//        assertEquals(HttpStatus.OK, result.getStatus());
//        assertEquals(List.of(), result.getData());
//    }
//
//
//    @Test
//    void testGetUpcomingVote_ShouldReturnEmptyList7() {
//        MBStageVoteRequest request = new MBStageVoteRequest();
//        request.setIdProject("project123");
//
//        when(mbStageVoteRepository.findUpcomingStageVoteByProject(anyLong(), eq("project123")))
//                .thenReturn(List.of());
//
//        ResponseObject<?> result = mbStageVoteService.GetUpcomingVote(request);
//
//        assertEquals(HttpStatus.OK, result.getStatus());
//        assertEquals(List.of(), result.getData());
//    }
//
//
//    @Test
//    void testGetUpcomingVote_ShouldReturnEmptyList8() {
//        MBStageVoteRequest request = new MBStageVoteRequest();
//        request.setIdProject("project123");
//
//        when(mbStageVoteRepository.findUpcomingStageVoteByProject(anyLong(), eq("project123")))
//                .thenReturn(List.of());
//
//        ResponseObject<?> result = mbStageVoteService.GetUpcomingVote(request);
//
//        assertEquals(HttpStatus.OK, result.getStatus());
//        assertEquals(List.of(), result.getData());
//    }
//
//    @Test
//    void testGetUpcomingVote_ShouldReturnEmptyList9() {
//        MBStageVoteRequest request = new MBStageVoteRequest();
//        request.setIdProject("project123");
//
//        when(mbStageVoteRepository.findUpcomingStageVoteByProject(anyLong(), eq("project123")))
//                .thenReturn(List.of());
//
//        ResponseObject<?> result = mbStageVoteService.GetUpcomingVote(request);
//
//        assertEquals(HttpStatus.OK, result.getStatus());
//        assertEquals(List.of(), result.getData());
//    }
//
//    @Test
//    void testGetUpcomingVote_ShouldReturnEmptyList10() {
//        MBStageVoteRequest request = new MBStageVoteRequest();
//        request.setIdProject("project123");
//
//        when(mbStageVoteRepository.findUpcomingStageVoteByProject(anyLong(), eq("project123")))
//                .thenReturn(List.of());
//
//        ResponseObject<?> result = mbStageVoteService.GetUpcomingVote(request);
//
//        assertEquals(HttpStatus.OK, result.getStatus());
//        assertEquals(List.of(), result.getData());
//    }
//
//    @Test
//    void testGetUpcomingVote_ShouldReturnEmptyList11() {
//        MBStageVoteRequest request = new MBStageVoteRequest();
//        request.setIdProject("project123");
//
//        when(mbStageVoteRepository.findUpcomingStageVoteByProject(anyLong(), eq("project123")))
//                .thenReturn(List.of());
//
//        ResponseObject<?> result = mbStageVoteService.GetUpcomingVote(request);
//
//        assertEquals(HttpStatus.OK, result.getStatus());
//        assertEquals(List.of(), result.getData());
//    }
//
//    @Test
//    void testGetUpcomingVote_ShouldReturnEmptyList12() {
//        MBStageVoteRequest request = new MBStageVoteRequest();
//        request.setIdProject("project123");
//
//        when(mbStageVoteRepository.findUpcomingStageVoteByProject(anyLong(), eq("project123")))
//                .thenReturn(List.of());
//
//        ResponseObject<?> result = mbStageVoteService.GetUpcomingVote(request);
//
//        assertEquals(HttpStatus.OK, result.getStatus());
//        assertEquals(List.of(), result.getData());
//    }
//
//    @Test
//    void testGetUpcomingVote_ShouldReturnEmptyList13() {
//        MBStageVoteRequest request = new MBStageVoteRequest();
//        request.setIdProject("project123");
//
//        when(mbStageVoteRepository.findUpcomingStageVoteByProject(anyLong(), eq("project123")))
//                .thenReturn(List.of());
//
//        ResponseObject<?> result = mbStageVoteService.GetUpcomingVote(request);
//
//        assertEquals(HttpStatus.OK, result.getStatus());
//        assertEquals(List.of(), result.getData());
//    }
//
//    @Test
//    void testGetUpcomingVote_ShouldReturnEmptyList14() {
//        MBStageVoteRequest request = new MBStageVoteRequest();
//        request.setIdProject("project123");
//
//        when(mbStageVoteRepository.findUpcomingStageVoteByProject(anyLong(), eq("project123")))
//                .thenReturn(List.of());
//
//        ResponseObject<?> result = mbStageVoteService.GetUpcomingVote(request);
//
//        assertEquals(HttpStatus.OK, result.getStatus());
//        assertEquals(List.of(), result.getData());
//    }
//
//    @Test
//    void testGetUpcomingVote_ShouldReturnEmptyList15() {
//        MBStageVoteRequest request = new MBStageVoteRequest();
//        request.setIdProject("project123");
//
//        when(mbStageVoteRepository.findUpcomingStageVoteByProject(anyLong(), eq("project123")))
//                .thenReturn(List.of());
//
//        ResponseObject<?> result = mbStageVoteService.GetUpcomingVote(request);
//
//        assertEquals(HttpStatus.OK, result.getStatus());
//        assertEquals(List.of(), result.getData());
//    }
//
//    @Test
//    void testGetUpcomingVote_ShouldReturnEmptyList16() {
//        MBStageVoteRequest request = new MBStageVoteRequest();
//        request.setIdProject("project123");
//
//        when(mbStageVoteRepository.findUpcomingStageVoteByProject(anyLong(), eq("project123")))
//                .thenReturn(List.of());
//
//        ResponseObject<?> result = mbStageVoteService.GetUpcomingVote(request);
//
//        assertEquals(HttpStatus.OK, result.getStatus());
//        assertEquals(List.of(), result.getData());
//    }
//
//    @Test
//    void testGetUpcomingVote_ShouldReturnEmptyList17() {
//        MBStageVoteRequest request = new MBStageVoteRequest();
//        request.setIdProject("project123");
//
//        when(mbStageVoteRepository.findUpcomingStageVoteByProject(anyLong(), eq("project123")))
//                .thenReturn(List.of());
//
//        ResponseObject<?> result = mbStageVoteService.GetUpcomingVote(request);
//
//        assertEquals(HttpStatus.OK, result.getStatus());
//        assertEquals(List.of(), result.getData());
//    }
//
//    @Test
//    void testGetUpcomingVote_ShouldReturnEmptyList18() {
//        MBStageVoteRequest request = new MBStageVoteRequest();
//        request.setIdProject("project123");
//
//        when(mbStageVoteRepository.findUpcomingStageVoteByProject(anyLong(), eq("project123")))
//                .thenReturn(List.of());
//
//        ResponseObject<?> result = mbStageVoteService.GetUpcomingVote(request);
//
//        assertEquals(HttpStatus.OK, result.getStatus());
//        assertEquals(List.of(), result.getData());
//    }
//
//    @Test
//    void testGetUpcomingVote_ShouldReturnEmptyList19() {
//        MBStageVoteRequest request = new MBStageVoteRequest();
//        request.setIdProject("project123");
//
//        when(mbStageVoteRepository.findUpcomingStageVoteByProject(anyLong(), eq("project123")))
//                .thenReturn(List.of());
//
//        ResponseObject<?> result = mbStageVoteService.GetUpcomingVote(request);
//
//        assertEquals(HttpStatus.OK, result.getStatus());
//        assertEquals(List.of(), result.getData());
//    }
//
//    @Test
//    void testGetUpcomingVote_ShouldReturnEmptyList20() {
//        MBStageVoteRequest request = new MBStageVoteRequest();
//        request.setIdProject("project123");
//
//        when(mbStageVoteRepository.findUpcomingStageVoteByProject(anyLong(), eq("project123")))
//                .thenReturn(List.of());
//
//        ResponseObject<?> result = mbStageVoteService.GetUpcomingVote(request);
//
//        assertEquals(HttpStatus.OK, result.getStatus());
//        assertEquals(List.of(), result.getData());
//    }
//
//    @Test
//    void testGetUpcomingVote_ShouldReturnEmptyList21() {
//        MBStageVoteRequest request = new MBStageVoteRequest();
//        request.setIdProject("project123");
//
//        when(mbStageVoteRepository.findUpcomingStageVoteByProject(anyLong(), eq("project123")))
//                .thenReturn(List.of());
//
//        ResponseObject<?> result = mbStageVoteService.GetUpcomingVote(request);
//
//        assertEquals(HttpStatus.OK, result.getStatus());
//        assertEquals(List.of(), result.getData());
//    }
//    @Test
//    void testGetUpcomingVote_ShouldReturnEmptyList22() {
//        MBStageVoteRequest request = new MBStageVoteRequest();
//        request.setIdProject("project123");
//
//        when(mbStageVoteRepository.findUpcomingStageVoteByProject(anyLong(), eq("project123")))
//                .thenReturn(List.of());
//
//        ResponseObject<?> result = mbStageVoteService.GetUpcomingVote(request);
//
//        assertEquals(HttpStatus.OK, result.getStatus());
//        assertEquals(List.of(), result.getData());
//    }
//
//    @Test
//    void testGetUpcomingVote_ShouldReturnEmptyList23() {
//        MBStageVoteRequest request = new MBStageVoteRequest();
//        request.setIdProject("project123");
//
//        when(mbStageVoteRepository.findUpcomingStageVoteByProject(anyLong(), eq("project123")))
//                .thenReturn(List.of());
//
//        ResponseObject<?> result = mbStageVoteService.GetUpcomingVote(request);
//
//        assertEquals(HttpStatus.OK, result.getStatus());
//        assertEquals(List.of(), result.getData());
//    }
//    @Test
//    void testGetUpcomingVote_ShouldReturnEmptyList24() {
//        MBStageVoteRequest request = new MBStageVoteRequest();
//        request.setIdProject("project123");
//
//        when(mbStageVoteRepository.findUpcomingStageVoteByProject(anyLong(), eq("project123")))
//                .thenReturn(List.of());
//
//        ResponseObject<?> result = mbStageVoteService.GetUpcomingVote(request);
//
//        assertEquals(HttpStatus.OK, result.getStatus());
//        assertEquals(List.of(), result.getData());
//    }
//
//    @Test
//    void testGetUpcomingVote_ShouldReturnEmptyList25() {
//        MBStageVoteRequest request = new MBStageVoteRequest();
//        request.setIdProject("project123");
//
//        when(mbStageVoteRepository.findUpcomingStageVoteByProject(anyLong(), eq("project123")))
//                .thenReturn(List.of());
//
//        ResponseObject<?> result = mbStageVoteService.GetUpcomingVote(request);
//
//        assertEquals(HttpStatus.OK, result.getStatus());
//        assertEquals(List.of(), result.getData());
//    }
//
//    @Test
//    void testGetUpcomingVote_ShouldReturnEmptyList26() {
//        MBStageVoteRequest request = new MBStageVoteRequest();
//        request.setIdProject("project123");
//
//        when(mbStageVoteRepository.findUpcomingStageVoteByProject(anyLong(), eq("project123")))
//                .thenReturn(List.of());
//
//        ResponseObject<?> result = mbStageVoteService.GetUpcomingVote(request);
//
//        assertEquals(HttpStatus.OK, result.getStatus());
//        assertEquals(List.of(), result.getData());
//    }
//
//    @Test
//    void testGetUpcomingVote_ShouldReturnEmptyList27() {
//        MBStageVoteRequest request = new MBStageVoteRequest();
//        request.setIdProject("project123");
//
//        when(mbStageVoteRepository.findUpcomingStageVoteByProject(anyLong(), eq("project123")))
//                .thenReturn(List.of());
//
//        ResponseObject<?> result = mbStageVoteService.GetUpcomingVote(request);
//
//        assertEquals(HttpStatus.OK, result.getStatus());
//        assertEquals(List.of(), result.getData());
//    }
//
//    @Test
//    void testGetUpcomingVote_ShouldReturnEmptyList28() {
//        MBStageVoteRequest request = new MBStageVoteRequest();
//        request.setIdProject("project123");
//
//        when(mbStageVoteRepository.findUpcomingStageVoteByProject(anyLong(), eq("project123")))
//                .thenReturn(List.of());
//
//        ResponseObject<?> result = mbStageVoteService.GetUpcomingVote(request);
//
//        assertEquals(HttpStatus.OK, result.getStatus());
//        assertEquals(List.of(), result.getData());
//    }
//
//    @Test
//    void testGetUpcomingVote_ShouldReturnEmptyList29() {
//        MBStageVoteRequest request = new MBStageVoteRequest();
//        request.setIdProject("project123");
//
//        when(mbStageVoteRepository.findUpcomingStageVoteByProject(anyLong(), eq("project123")))
//                .thenReturn(List.of());
//
//        ResponseObject<?> result = mbStageVoteService.GetUpcomingVote(request);
//
//        assertEquals(HttpStatus.OK, result.getStatus());
//        assertEquals(List.of(), result.getData());
//    }
//
//    @Test
//    void testGetUpcomingVote_ShouldReturnEmptyList30() {
//        MBStageVoteRequest request = new MBStageVoteRequest();
//        request.setIdProject("project123");
//
//        when(mbStageVoteRepository.findUpcomingStageVoteByProject(anyLong(), eq("project123")))
//                .thenReturn(List.of());
//
//        ResponseObject<?> result = mbStageVoteService.GetUpcomingVote(request);
//
//        assertEquals(HttpStatus.OK, result.getStatus());
//        assertEquals(List.of(), result.getData());
//    }
//
//    @Test
//    void testGetUpcomingVote_ShouldReturnEmptyList31() {
//        MBStageVoteRequest request = new MBStageVoteRequest();
//        request.setIdProject("project123");
//
//        when(mbStageVoteRepository.findUpcomingStageVoteByProject(anyLong(), eq("project123")))
//                .thenReturn(List.of());
//
//        ResponseObject<?> result = mbStageVoteService.GetUpcomingVote(request);
//
//        assertEquals(HttpStatus.OK, result.getStatus());
//        assertEquals(List.of(), result.getData());
//    }
//
//    @Test
//    void testGetUpcomingVote_ShouldReturnEmptyList32() {
//        MBStageVoteRequest request = new MBStageVoteRequest();
//        request.setIdProject("project123");
//
//        when(mbStageVoteRepository.findUpcomingStageVoteByProject(anyLong(), eq("project123")))
//                .thenReturn(List.of());
//
//        ResponseObject<?> result = mbStageVoteService.GetUpcomingVote(request);
//
//        assertEquals(HttpStatus.OK, result.getStatus());
//        assertEquals(List.of(), result.getData());
//    }
//
//    @Test
//    void testGetUpcomingVote_ShouldReturnEmptyList33() {
//        MBStageVoteRequest request = new MBStageVoteRequest();
//        request.setIdProject("project123");
//
//        when(mbStageVoteRepository.findUpcomingStageVoteByProject(anyLong(), eq("project123")))
//                .thenReturn(List.of());
//
//        ResponseObject<?> result = mbStageVoteService.GetUpcomingVote(request);
//
//        assertEquals(HttpStatus.OK, result.getStatus());
//        assertEquals(List.of(), result.getData());
//    }
//
//    @Test
//    void testGetUpcomingVote_ShouldReturnEmptyList34() {
//        MBStageVoteRequest request = new MBStageVoteRequest();
//        request.setIdProject("project123");
//
//        when(mbStageVoteRepository.findUpcomingStageVoteByProject(anyLong(), eq("project123")))
//                .thenReturn(List.of());
//
//        ResponseObject<?> result = mbStageVoteService.GetUpcomingVote(request);
//
//        assertEquals(HttpStatus.OK, result.getStatus());
//        assertEquals(List.of(), result.getData());
//    }
//
//    @Test
//    void testGetUpcomingVote_ShouldReturnEmptyList35() {
//        MBStageVoteRequest request = new MBStageVoteRequest();
//        request.setIdProject("project123");
//
//        when(mbStageVoteRepository.findUpcomingStageVoteByProject(anyLong(), eq("project123")))
//                .thenReturn(List.of());
//
//        ResponseObject<?> result = mbStageVoteService.GetUpcomingVote(request);
//
//        assertEquals(HttpStatus.OK, result.getStatus());
//        assertEquals(List.of(), result.getData());
//    }
//
//    @Test
//    void testGetUpcomingVote_ShouldReturnEmptyList36() {
//        MBStageVoteRequest request = new MBStageVoteRequest();
//        request.setIdProject("project123");
//
//        when(mbStageVoteRepository.findUpcomingStageVoteByProject(anyLong(), eq("project123")))
//                .thenReturn(List.of());
//
//        ResponseObject<?> result = mbStageVoteService.GetUpcomingVote(request);
//
//        assertEquals(HttpStatus.OK, result.getStatus());
//        assertEquals(List.of(), result.getData());
//    }
//
//    @Test
//    void testGetUpcomingVote_ShouldReturnEmptyList37() {
//        MBStageVoteRequest request = new MBStageVoteRequest();
//        request.setIdProject("project123");
//
//        when(mbStageVoteRepository.findUpcomingStageVoteByProject(anyLong(), eq("project123")))
//                .thenReturn(List.of());
//
//        ResponseObject<?> result = mbStageVoteService.GetUpcomingVote(request);
//
//        assertEquals(HttpStatus.OK, result.getStatus());
//        assertEquals(List.of(), result.getData());
//    }
//
//    @Test
//    void testGetUpcomingVote_ShouldReturnEmptyList38() {
//        MBStageVoteRequest request = new MBStageVoteRequest();
//        request.setIdProject("project123");
//
//        when(mbStageVoteRepository.findUpcomingStageVoteByProject(anyLong(), eq("project123")))
//                .thenReturn(List.of());
//
//        ResponseObject<?> result = mbStageVoteService.GetUpcomingVote(request);
//
//        assertEquals(HttpStatus.OK, result.getStatus());
//        assertEquals(List.of(), result.getData());
//    }
//
//    @Test
//    void testGetUpcomingVote_ShouldReturnEmptyList39() {
//        MBStageVoteRequest request = new MBStageVoteRequest();
//        request.setIdProject("project123");
//
//        when(mbStageVoteRepository.findUpcomingStageVoteByProject(anyLong(), eq("project123")))
//                .thenReturn(List.of());
//
//        ResponseObject<?> result = mbStageVoteService.GetUpcomingVote(request);
//
//        assertEquals(HttpStatus.OK, result.getStatus());
//        assertEquals(List.of(), result.getData());
//    }
//
//    @Test
//    void testGetUpcomingVote_ShouldReturnEmptyList40() {
//        MBStageVoteRequest request = new MBStageVoteRequest();
//        request.setIdProject("project123");
//
//        when(mbStageVoteRepository.findUpcomingStageVoteByProject(anyLong(), eq("project123")))
//                .thenReturn(List.of());
//
//        ResponseObject<?> result = mbStageVoteService.GetUpcomingVote(request);
//
//        assertEquals(HttpStatus.OK, result.getStatus());
//        assertEquals(List.of(), result.getData());
//    }
//
//    @Test
//    void testGetUpcomingVote_ShouldReturnEmptyList41() {
//        MBStageVoteRequest request = new MBStageVoteRequest();
//        request.setIdProject("project123");
//
//        when(mbStageVoteRepository.findUpcomingStageVoteByProject(anyLong(), eq("project123")))
//                .thenReturn(List.of());
//
//        ResponseObject<?> result = mbStageVoteService.GetUpcomingVote(request);
//
//        assertEquals(HttpStatus.OK, result.getStatus());
//        assertEquals(List.of(), result.getData());
//    }
//    @Test
//    void testGetUpcomingVote_ShouldReturnEmptyList42() {
//        MBStageVoteRequest request = new MBStageVoteRequest();
//        request.setIdProject("project123");
//
//        when(mbStageVoteRepository.findUpcomingStageVoteByProject(anyLong(), eq("project123")))
//                .thenReturn(List.of());
//
//        ResponseObject<?> result = mbStageVoteService.GetUpcomingVote(request);
//
//        assertEquals(HttpStatus.OK, result.getStatus());
//        assertEquals(List.of(), result.getData());
//    }
//
//    @Test
//    void testGetUpcomingVote_ShouldReturnEmptyList43() {
//        MBStageVoteRequest request = new MBStageVoteRequest();
//        request.setIdProject("project123");
//
//        when(mbStageVoteRepository.findUpcomingStageVoteByProject(anyLong(), eq("project123")))
//                .thenReturn(List.of());
//
//        ResponseObject<?> result = mbStageVoteService.GetUpcomingVote(request);
//
//        assertEquals(HttpStatus.OK, result.getStatus());
//        assertEquals(List.of(), result.getData());
//    }
//
//    @Test
//    void testGetUpcomingVote_ShouldReturnEmptyList44() {
//        MBStageVoteRequest request = new MBStageVoteRequest();
//        request.setIdProject("project123");
//
//        when(mbStageVoteRepository.findUpcomingStageVoteByProject(anyLong(), eq("project123")))
//                .thenReturn(List.of());
//
//        ResponseObject<?> result = mbStageVoteService.GetUpcomingVote(request);
//
//        assertEquals(HttpStatus.OK, result.getStatus());
//        assertEquals(List.of(), result.getData());
//    }
//    @Test
//    void testGetUpcomingVote_ShouldReturnEmptyList45() {
//        MBStageVoteRequest request = new MBStageVoteRequest();
//        request.setIdProject("project123");
//
//        when(mbStageVoteRepository.findUpcomingStageVoteByProject(anyLong(), eq("project123")))
//                .thenReturn(List.of());
//
//        ResponseObject<?> result = mbStageVoteService.GetUpcomingVote(request);
//
//        assertEquals(HttpStatus.OK, result.getStatus());
//        assertEquals(List.of(), result.getData());
//    }
//
//    @Test
//    void testGetUpcomingVote_ShouldReturnEmptyList46() {
//        MBStageVoteRequest request = new MBStageVoteRequest();
//        request.setIdProject("project123");
//
//        when(mbStageVoteRepository.findUpcomingStageVoteByProject(anyLong(), eq("project123")))
//                .thenReturn(List.of());
//
//        ResponseObject<?> result = mbStageVoteService.GetUpcomingVote(request);
//
//        assertEquals(HttpStatus.OK, result.getStatus());
//        assertEquals(List.of(), result.getData());
//    }
//
//    @Test
//    void testGetUpcomingVote_ShouldReturnEmptyList47() {
//        MBStageVoteRequest request = new MBStageVoteRequest();
//        request.setIdProject("project123");
//
//        when(mbStageVoteRepository.findUpcomingStageVoteByProject(anyLong(), eq("project123")))
//                .thenReturn(List.of());
//
//        ResponseObject<?> result = mbStageVoteService.GetUpcomingVote(request);
//
//        assertEquals(HttpStatus.OK, result.getStatus());
//        assertEquals(List.of(), result.getData());
//    }
//
//    @Test
//    void testGetUpcomingVote_ShouldReturnEmptyList48() {
//        MBStageVoteRequest request = new MBStageVoteRequest();
//        request.setIdProject("project123");
//
//        when(mbStageVoteRepository.findUpcomingStageVoteByProject(anyLong(), eq("project123")))
//                .thenReturn(List.of());
//
//        ResponseObject<?> result = mbStageVoteService.GetUpcomingVote(request);
//
//        assertEquals(HttpStatus.OK, result.getStatus());
//        assertEquals(List.of(), result.getData());
//    }
//
//    @Test
//    void testGetUpcomingVote_ShouldReturnEmptyList49() {
//        MBStageVoteRequest request = new MBStageVoteRequest();
//        request.setIdProject("project123");
//
//        when(mbStageVoteRepository.findUpcomingStageVoteByProject(anyLong(), eq("project123")))
//                .thenReturn(List.of());
//
//        ResponseObject<?> result = mbStageVoteService.GetUpcomingVote(request);
//
//        assertEquals(HttpStatus.OK, result.getStatus());
//        assertEquals(List.of(), result.getData());
//    }
//
//    @Test
//    void testGetUpcomingVote_ShouldReturnEmptyList50() {
//        MBStageVoteRequest request = new MBStageVoteRequest();
//        request.setIdProject("project123");
//
//        when(mbStageVoteRepository.findUpcomingStageVoteByProject(anyLong(), eq("project123")))
//                .thenReturn(List.of());
//
//        ResponseObject<?> result = mbStageVoteService.GetUpcomingVote(request);
//
//        assertEquals(HttpStatus.OK, result.getStatus());
//        assertEquals(List.of(), result.getData());
//    }
}
