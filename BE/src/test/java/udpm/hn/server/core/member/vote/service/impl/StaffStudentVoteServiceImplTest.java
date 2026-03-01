package udpm.hn.server.core.member.vote.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import udpm.hn.server.core.common.base.PageableObject;
import udpm.hn.server.core.common.base.ResponseObject;
import udpm.hn.server.core.member.vote.model.response.StaffStudentVoteResponse;
import udpm.hn.server.core.member.vote.repository.StaffStudentVoteRepository;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
public class StaffStudentVoteServiceImplTest {

    @Mock
    private StaffStudentVoteRepository staffStudentVoteRepository;

    @Mock
    private StaffStudentVoteServiceImpl staffStudentVoteService;

    @BeforeEach
    void setUp() {
        staffStudentVoteRepository = mock(StaffStudentVoteRepository.class);
        staffStudentVoteService = new StaffStudentVoteServiceImpl(staffStudentVoteRepository);
    }

    @Test
    void testGetStuentStaffVote_ShouldReturnPaginatedResults() {
        // Given
        String projectId = "project-001";
        long fakeTime = System.currentTimeMillis();

        // Mocked response
        StaffStudentVoteResponse mockResponse = new StaffStudentVoteResponse() {
            public String getOrderNumber() { return "1"; }
            public String getStaffProjectId() { return "staff-001"; }
            public String getNameStaff() { return "John Doe"; }
            public String getRole() { return "Mentor"; }
            public String getVoteStatus() { return "true"; }
        };

        List<StaffStudentVoteResponse> content = List.of(mockResponse);
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").descending());
        Page<StaffStudentVoteResponse> page = new PageImpl<>(content, pageable, 1);

        when(staffStudentVoteRepository.getStaffStudentVote(any(Pageable.class), eq(projectId), anyLong()))
                .thenReturn(page);

        // When
        ResponseObject<?> response = staffStudentVoteService.getStuentStaffVote(projectId);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals("Lấy thành công danh sách thành viên bình chọn", response.getMessage());
        assertTrue(response.getData() instanceof PageableObject);

        PageableObject<?> pageableObject = (PageableObject<?>) response.getData();
        List<?> resultList = pageableObject.getData();

        assertEquals(1, resultList.size());

        StaffStudentVoteResponse resultItem = (StaffStudentVoteResponse) resultList.get(0);
        assertEquals("1", resultItem.getOrderNumber());
        assertEquals("staff-001", resultItem.getStaffProjectId());
        assertEquals("John Doe", resultItem.getNameStaff());
        assertEquals("Mentor", resultItem.getRole());
        assertEquals("true", resultItem.getVoteStatus());

        verify(staffStudentVoteRepository, times(1))
                .getStaffStudentVote(any(Pageable.class), eq(projectId), anyLong());
    }

    @Test
    void testGetStuentStaffVote_ShouldReturnEmptyPageIfNoData() {
        // Given
        String projectId = "project-empty";
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").descending());
        Page<StaffStudentVoteResponse> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);

        when(staffStudentVoteRepository.getStaffStudentVote(any(Pageable.class), eq(projectId), anyLong()))
                .thenReturn(emptyPage);

        // When
        ResponseObject<?> response = staffStudentVoteService.getStuentStaffVote(projectId);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals("Lấy thành công danh sách thành viên bình chọn", response.getMessage());
        assertTrue(response.getData() instanceof PageableObject);

        PageableObject<?> pageableObject = (PageableObject<?>) response.getData();
        List<?> resultList = pageableObject.getData();

        assertEquals(0, resultList.size());

        verify(staffStudentVoteRepository, times(1))
                .getStaffStudentVote(any(Pageable.class), eq(projectId), anyLong());
    }
}
