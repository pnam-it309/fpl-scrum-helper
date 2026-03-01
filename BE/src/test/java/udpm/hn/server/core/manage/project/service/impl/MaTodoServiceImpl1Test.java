package udpm.hn.server.core.manage.project.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import udpm.hn.server.core.common.base.ResponseObject;
import udpm.hn.server.core.manage.project.model.response.MaTodoSummaryResponse;
import udpm.hn.server.core.manage.project.model.response.MeDataDashboardLabelResponse;
import udpm.hn.server.core.manage.project.model.response.MeDataDashboardTodoListResponse;
import udpm.hn.server.core.manage.project.repository.MaTodoProjectRepository;
import udpm.hn.server.core.manage.todo.repository.MATodoRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MaTodoServiceImpl1Test {

    @Mock
    private MATodoRepository maTodoRepository;
    @Mock
    private MaTodoProjectRepository maTodoProjectRepository;

    @InjectMocks
    private MaTodoServiceImpl service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCountTodoByTodoStatus() {
        MaTodoSummaryResponse todoSummaryMock = mock(MaTodoSummaryResponse.class);
        List<MaTodoSummaryResponse> data = List.of(todoSummaryMock);
        when(maTodoProjectRepository.countTodoByTodoStatus("p1")).thenReturn(data);

        ResponseObject<?> result = service.countTodoByTodoStatus("p1");

        assertEquals(HttpStatus.OK, result.getStatus());
        assertEquals("Tìm thấy dữ liệu thành công", result.getMessage());
        assertEquals(data, result.getData());
        verify(maTodoProjectRepository, times(1)).countTodoByTodoStatus("p1");
    }

    @Test
    void testCountTodoByTodoListAllProject_EmptyResult() {
        when(maTodoRepository.countTodoByTodoListAllProject("p1")).thenReturn(List.of());

        List<MeDataDashboardTodoListResponse> actual = service.countTodoByTodoListAllProject("p1");

        assertNotNull(actual);
        assertTrue(actual.isEmpty());
        verify(maTodoRepository).countTodoByTodoListAllProject("p1");
    }

    @Test
    void testCountTodoByLabelAllProject_EmptyResult() {
        when(maTodoRepository.countTodoByLabelAllProject("p1")).thenReturn(List.of());

        List<MeDataDashboardLabelResponse> actual = service.countTodoByLabelAllProject("p1");

        assertNotNull(actual);
        assertTrue(actual.isEmpty());
        verify(maTodoRepository).countTodoByLabelAllProject("p1");
    }

    @Test
    void testCountTodoByTodoListPhase_EmptyResult() {
        when(maTodoRepository.countTodoByTodoListPhase("p1","ph1")).thenReturn(List.of());

        List<MeDataDashboardTodoListResponse> actual = service.countTodoByTodoListPhase("p1","ph1");

        assertNotNull(actual);
        assertTrue(actual.isEmpty());
        verify(maTodoRepository).countTodoByTodoListPhase("p1","ph1");
    }

    @Test
    void testCountTodoByTodoStatus_ReturnsEmptyList() {
        when(maTodoProjectRepository.countTodoByTodoStatus("p1")).thenReturn(List.of());

        ResponseObject<?> result = service.countTodoByTodoStatus("p1");

        assertEquals(HttpStatus.OK, result.getStatus());
        assertEquals("Tìm thấy dữ liệu thành công", result.getMessage());
        assertTrue(((List<?>) result.getData()).isEmpty());
        verify(maTodoProjectRepository).countTodoByTodoStatus("p1");
    }
    @Test
    void testCountTodoByDueDateAllProject_ReturnsZero() {
        when(maTodoRepository.countTodoByDueDateAllProject("p1", 0)).thenReturn(0);

        Integer actual = service.countTodoByDueDateAllProject("p1", 0);

        assertEquals(0, actual);
        verify(maTodoRepository).countTodoByDueDateAllProject("p1", 0);
    }

    @Test
    void testCountTodoByNoDueDateAllProject_ReturnsZero() {
        when(maTodoRepository.countTodoByNoDueDateAllProject("p1")).thenReturn(0);

        Integer actual = service.countTodoByNoDueDateAllProject("p1");

        assertEquals(0, actual);
        verify(maTodoRepository).countTodoByNoDueDateAllProject("p1");
    }

    @Test
    void testCountTodoByNoLabelAllProject_ReturnsZero() {
        when(maTodoRepository.countTodoByNoLabelAllProject("p1")).thenReturn(0);

        Integer actual = service.countTodoByNoLabelAllProject("p1");

        assertEquals(0, actual);
        verify(maTodoRepository).countTodoByNoLabelAllProject("p1");
    }

    @Test
    void testCountTodoByDueDatePhase_ReturnsZero() {
        when(maTodoRepository.countTodoByDueDatePhase("p1", "ph1", 0)).thenReturn(0);

        Integer actual = service.countTodoByDueDatePhase("p1", "ph1", 0);

        assertEquals(0, actual);
        verify(maTodoRepository).countTodoByDueDatePhase("p1", "ph1", 0);
    }

    @Test
    void testCountTodoByNoDueDatePhase_ReturnsZero() {
        when(maTodoRepository.countTodoByNoDueDatePhase("p1", "ph1")).thenReturn(0);

        Integer actual = service.countTodoByNoDueDatePhase("p1", "ph1");

        assertEquals(0, actual);
        verify(maTodoRepository).countTodoByNoDueDatePhase("p1", "ph1");
    }

    @Test
    void testCountTodoByPriorityLevel_ReturnsZero() {
        when(maTodoRepository.countTodoByPriorityLevel("p1", 5)).thenReturn(0);

        Integer actual = service.countTodoByPriorityLevel("p1", 5);

        assertEquals(0, actual);
        verify(maTodoRepository).countTodoByPriorityLevel("p1", 5);
    }
    @Test
    void testCountTodoByTodoStatus_WithMultipleResults() {
        MaTodoSummaryResponse r1 = mock(MaTodoSummaryResponse.class);
        MaTodoSummaryResponse r2 = mock(MaTodoSummaryResponse.class);
        List<MaTodoSummaryResponse> data = List.of(r1, r2);

        when(maTodoProjectRepository.countTodoByTodoStatus("p1")).thenReturn(data);

        ResponseObject<?> result = service.countTodoByTodoStatus("p1");

        assertEquals(HttpStatus.OK, result.getStatus());
        assertEquals("Tìm thấy dữ liệu thành công", result.getMessage());
        assertEquals(data, result.getData());
        assertEquals(2, ((List<?>) result.getData()).size());
        verify(maTodoProjectRepository).countTodoByTodoStatus("p1");
    }

    @Test
    void testCountTodoByTodoListAllProject_WithMultipleResults() {
        MeDataDashboardTodoListResponse t1 = mock(MeDataDashboardTodoListResponse.class);
        MeDataDashboardTodoListResponse t2 = mock(MeDataDashboardTodoListResponse.class);
        List<MeDataDashboardTodoListResponse> expected = List.of(t1, t2);

        when(maTodoRepository.countTodoByTodoListAllProject("p1")).thenReturn(expected);

        List<MeDataDashboardTodoListResponse> actual = service.countTodoByTodoListAllProject("p1");

        assertEquals(2, actual.size());
        assertEquals(expected, actual);
        verify(maTodoRepository).countTodoByTodoListAllProject("p1");
    }

    @Test
    void testCountTodoByLabelAllProject_WithMultipleResults() {
        MeDataDashboardLabelResponse l1 = mock(MeDataDashboardLabelResponse.class);
        MeDataDashboardLabelResponse l2 = mock(MeDataDashboardLabelResponse.class);
        List<MeDataDashboardLabelResponse> expected = List.of(l1, l2);

        when(maTodoRepository.countTodoByLabelAllProject("p1")).thenReturn(expected);

        List<MeDataDashboardLabelResponse> actual = service.countTodoByLabelAllProject("p1");

        assertEquals(2, actual.size());
        assertEquals(expected, actual);
        verify(maTodoRepository).countTodoByLabelAllProject("p1");
    }

    @Test
    void testCountTodoByTodoListPhase_WithMultipleResults() {
        MeDataDashboardTodoListResponse r1 = mock(MeDataDashboardTodoListResponse.class);
        MeDataDashboardTodoListResponse r2 = mock(MeDataDashboardTodoListResponse.class);
        List<MeDataDashboardTodoListResponse> expected = List.of(r1, r2);

        when(maTodoRepository.countTodoByTodoListPhase("p1", "ph1")).thenReturn(expected);

        List<MeDataDashboardTodoListResponse> actual = service.countTodoByTodoListPhase("p1", "ph1");

        assertEquals(2, actual.size());
        assertEquals(expected, actual);
        verify(maTodoRepository).countTodoByTodoListPhase("p1", "ph1");
    }
    @Test
    void testCountTodoByDueDateAllProject_WithDifferentStatusValues() {
        when(maTodoRepository.countTodoByDueDateAllProject("p1", 1)).thenReturn(10);
        when(maTodoRepository.countTodoByDueDateAllProject("p1", 2)).thenReturn(20);

        Integer result1 = service.countTodoByDueDateAllProject("p1", 1);
        Integer result2 = service.countTodoByDueDateAllProject("p1", 2);

        assertEquals(10, result1);
        assertEquals(20, result2);
        verify(maTodoRepository).countTodoByDueDateAllProject("p1", 1);
        verify(maTodoRepository).countTodoByDueDateAllProject("p1", 2);
    }

    @Test
    void testCountTodoByDueDatePhase_WithDifferentStatusValues() {
        when(maTodoRepository.countTodoByDueDatePhase("p1", "ph1", 1)).thenReturn(15);
        when(maTodoRepository.countTodoByDueDatePhase("p1", "ph1", 2)).thenReturn(25);

        Integer result1 = service.countTodoByDueDatePhase("p1", "ph1", 1);
        Integer result2 = service.countTodoByDueDatePhase("p1", "ph1", 2);

        assertEquals(15, result1);
        assertEquals(25, result2);
        verify(maTodoRepository).countTodoByDueDatePhase("p1", "ph1", 1);
        verify(maTodoRepository).countTodoByDueDatePhase("p1", "ph1", 2);
    }

    @Test
    void testCountTodoByPriorityLevel_WithDifferentValues() {
        when(maTodoRepository.countTodoByPriorityLevel("p1", 1)).thenReturn(5);
        when(maTodoRepository.countTodoByPriorityLevel("p1", 3)).thenReturn(7);

        Integer result1 = service.countTodoByPriorityLevel("p1", 1);
        Integer result3 = service.countTodoByPriorityLevel("p1", 3);

        assertEquals(5, result1);
        assertEquals(7, result3);
        verify(maTodoRepository).countTodoByPriorityLevel("p1", 1);
        verify(maTodoRepository).countTodoByPriorityLevel("p1", 3);
    }

    @Test
    void testCountTodoByNoDueDatePhase_WithNonZero() {
        when(maTodoRepository.countTodoByNoDueDatePhase("p1", "ph1")).thenReturn(12);

        Integer result = service.countTodoByNoDueDatePhase("p1", "ph1");

        assertEquals(12, result);
        verify(maTodoRepository).countTodoByNoDueDatePhase("p1", "ph1");
    }

    @Test
    void testCountTodoByNoLabelAllProject_WithNonZero() {
        when(maTodoRepository.countTodoByNoLabelAllProject("p1")).thenReturn(4);

        Integer result = service.countTodoByNoLabelAllProject("p1");

        assertEquals(4, result);
        verify(maTodoRepository).countTodoByNoLabelAllProject("p1");
    }
    @Test
    void testCountTodoByNoDueDateAllProject_WithNonZero() {
        when(maTodoRepository.countTodoByNoDueDateAllProject("p1")).thenReturn(6);

        Integer result = service.countTodoByNoDueDateAllProject("p1");

        assertEquals(6, result);
        verify(maTodoRepository).countTodoByNoDueDateAllProject("p1");
    }

    @Test
    void testCountTodoByTodoListPhase_WithSingleResult() {
        MeDataDashboardTodoListResponse resp = mock(MeDataDashboardTodoListResponse.class);
        when(maTodoRepository.countTodoByTodoListPhase("p1", "ph1"))
                .thenReturn(List.of(resp));

        List<MeDataDashboardTodoListResponse> result = service.countTodoByTodoListPhase("p1", "ph1");

        assertEquals(1, result.size());
        assertEquals(resp, result.get(0));
        verify(maTodoRepository).countTodoByTodoListPhase("p1", "ph1");
    }

    @Test
    void testCountTodoByLabelAllProject_WithSingleResult() {
        MeDataDashboardLabelResponse resp = mock(MeDataDashboardLabelResponse.class);
        when(maTodoRepository.countTodoByLabelAllProject("p1"))
                .thenReturn(List.of(resp));

        List<MeDataDashboardLabelResponse> result = service.countTodoByLabelAllProject("p1");

        assertEquals(1, result.size());
        assertEquals(resp, result.get(0));
        verify(maTodoRepository).countTodoByLabelAllProject("p1");
    }

    @Test
    void testCountTodoByTodoStatus_WithSingleResult() {
        MaTodoSummaryResponse resp = mock(MaTodoSummaryResponse.class);
        when(maTodoProjectRepository.countTodoByTodoStatus("p1"))
                .thenReturn(List.of(resp));

        ResponseObject<?> result = service.countTodoByTodoStatus("p1");

        assertEquals(HttpStatus.OK, result.getStatus());
        assertEquals("Tìm thấy dữ liệu thành công", result.getMessage());
        assertEquals(1, ((List<?>) result.getData()).size());
        verify(maTodoProjectRepository).countTodoByTodoStatus("p1");
    }
    @Test
    void testCountTodoByDueDateAllProject_WithNegativeStatus() {
        when(maTodoRepository.countTodoByDueDateAllProject("p1", -1)).thenReturn(0);

        Integer result = service.countTodoByDueDateAllProject("p1", -1);

        assertEquals(0, result);
        verify(maTodoRepository).countTodoByDueDateAllProject("p1", -1);
    }

    @Test
    void testCountTodoByDueDatePhase_WithNegativeStatus() {
        when(maTodoRepository.countTodoByDueDatePhase("p1", "ph1", -1)).thenReturn(0);

        Integer result = service.countTodoByDueDatePhase("p1", "ph1", -1);

        assertEquals(0, result);
        verify(maTodoRepository).countTodoByDueDatePhase("p1", "ph1", -1);
    }

    @Test
    void testCountTodoByPriorityLevel_WithHighValue() {
        when(maTodoRepository.countTodoByPriorityLevel("p1", 99)).thenReturn(0);

        Integer result = service.countTodoByPriorityLevel("p1", 99);

        assertEquals(0, result);
        verify(maTodoRepository).countTodoByPriorityLevel("p1", 99);
    }

    @Test
    void testCountTodoByNoDueDatePhase_WithZero() {
        when(maTodoRepository.countTodoByNoDueDatePhase("p1", "ph1")).thenReturn(0);

        Integer result = service.countTodoByNoDueDatePhase("p1", "ph1");

        assertEquals(0, result);
        verify(maTodoRepository).countTodoByNoDueDatePhase("p1", "ph1");
    }

    @Test
    void testCountTodoByNoLabelAllProject_WithZero() {
        when(maTodoRepository.countTodoByNoLabelAllProject("p1")).thenReturn(0);

        Integer result = service.countTodoByNoLabelAllProject("p1");

        assertEquals(0, result);
        verify(maTodoRepository).countTodoByNoLabelAllProject("p1");
    }
    @Test
    void testCountTodoByTodoListAllProject_WithEmptyProjectId() {
        when(maTodoRepository.countTodoByTodoListAllProject("")).thenReturn(List.of());

        List<MeDataDashboardTodoListResponse> result = service.countTodoByTodoListAllProject("");

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(maTodoRepository).countTodoByTodoListAllProject("");
    }

    @Test
    void testCountTodoByDueDateAllProject_WithNullStatus() {
        when(maTodoRepository.countTodoByDueDateAllProject("p1", null)).thenReturn(0);

        Integer result = service.countTodoByDueDateAllProject("p1", null);

        assertEquals(0, result);
        verify(maTodoRepository).countTodoByDueDateAllProject("p1", null);
    }

    @Test
    void testCountTodoByDueDatePhase_WithNullStatus() {
        when(maTodoRepository.countTodoByDueDatePhase("p1", "ph1", null)).thenReturn(0);

        Integer result = service.countTodoByDueDatePhase("p1", "ph1", null);

        assertEquals(0, result);
        verify(maTodoRepository).countTodoByDueDatePhase("p1", "ph1", null);
    }

    @Test
    void testCountTodoByPriorityLevel_WithNullPriority() {
        when(maTodoRepository.countTodoByPriorityLevel("p1", null)).thenReturn(0);

        Integer result = service.countTodoByPriorityLevel("p1", null);

        assertEquals(0, result);
        verify(maTodoRepository).countTodoByPriorityLevel("p1", null);
    }

    @Test
    void testCountTodoByTodoStatus_WithEmptyId() {
        when(maTodoProjectRepository.countTodoByTodoStatus("")).thenReturn(List.of());

        ResponseObject<?> result = service.countTodoByTodoStatus("");

        assertEquals(HttpStatus.OK, result.getStatus());
        assertEquals("Tìm thấy dữ liệu thành công", result.getMessage());
        assertTrue(((List<?>) result.getData()).isEmpty());
        verify(maTodoProjectRepository).countTodoByTodoStatus("");
    }
    @Test
    void testCountTodoByNoDueDateAllProject_WithEmptyProjectId() {
        when(maTodoRepository.countTodoByNoDueDateAllProject("")).thenReturn(0);

        Integer result = service.countTodoByNoDueDateAllProject("");

        assertEquals(0, result);
        verify(maTodoRepository).countTodoByNoDueDateAllProject("");
    }

    @Test
    void testCountTodoByNoDueDatePhase_WithEmptyIds() {
        when(maTodoRepository.countTodoByNoDueDatePhase("", "")).thenReturn(0);

        Integer result = service.countTodoByNoDueDatePhase("", "");

        assertEquals(0, result);
        verify(maTodoRepository).countTodoByNoDueDatePhase("", "");
    }

    @Test
    void testCountTodoByLabelAllProject_WithEmptyProjectId() {
        when(maTodoRepository.countTodoByLabelAllProject("")).thenReturn(List.of());

        List<MeDataDashboardLabelResponse> result = service.countTodoByLabelAllProject("");

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(maTodoRepository).countTodoByLabelAllProject("");
    }

    @Test
    void testCountTodoByTodoListPhase_WithEmptyIds() {
        when(maTodoRepository.countTodoByTodoListPhase("", "")).thenReturn(List.of());

        List<MeDataDashboardTodoListResponse> result = service.countTodoByTodoListPhase("", "");

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(maTodoRepository).countTodoByTodoListPhase("", "");
    }

    @Test
    void testCountTodoByNoLabelAllProject_WithEmptyProjectId() {
        when(maTodoRepository.countTodoByNoLabelAllProject("")).thenReturn(0);

        Integer result = service.countTodoByNoLabelAllProject("");

        assertEquals(0, result);
        verify(maTodoRepository).countTodoByNoLabelAllProject("");
    }
    @Test
    void testCountTodoByDueDateAllProject_WithDifferentProjects() {
        when(maTodoRepository.countTodoByDueDateAllProject("p1", 1)).thenReturn(11);
        when(maTodoRepository.countTodoByDueDateAllProject("p2", 1)).thenReturn(22);

        Integer result1 = service.countTodoByDueDateAllProject("p1", 1);
        Integer result2 = service.countTodoByDueDateAllProject("p2", 1);

        assertEquals(11, result1);
        assertEquals(22, result2);
        verify(maTodoRepository).countTodoByDueDateAllProject("p1", 1);
        verify(maTodoRepository).countTodoByDueDateAllProject("p2", 1);
    }

    @Test
    void testCountTodoByDueDatePhase_WithDifferentProjectsAndPhases() {
        when(maTodoRepository.countTodoByDueDatePhase("p1", "ph1", 1)).thenReturn(13);
        when(maTodoRepository.countTodoByDueDatePhase("p2", "ph2", 1)).thenReturn(23);

        Integer result1 = service.countTodoByDueDatePhase("p1", "ph1", 1);
        Integer result2 = service.countTodoByDueDatePhase("p2", "ph2", 1);

        assertEquals(13, result1);
        assertEquals(23, result2);
        verify(maTodoRepository).countTodoByDueDatePhase("p1", "ph1", 1);
        verify(maTodoRepository).countTodoByDueDatePhase("p2", "ph2", 1);
    }

    @Test
    void testCountTodoByPriorityLevel_WithDifferentProjects() {
        when(maTodoRepository.countTodoByPriorityLevel("p1", 1)).thenReturn(33);
        when(maTodoRepository.countTodoByPriorityLevel("p2", 1)).thenReturn(44);

        Integer result1 = service.countTodoByPriorityLevel("p1", 1);
        Integer result2 = service.countTodoByPriorityLevel("p2", 1);

        assertEquals(33, result1);
        assertEquals(44, result2);
        verify(maTodoRepository).countTodoByPriorityLevel("p1", 1);
        verify(maTodoRepository).countTodoByPriorityLevel("p2", 1);
    }

    @Test
    void testCountTodoByTodoStatus_WithDifferentProjects() {
        MaTodoSummaryResponse r1 = mock(MaTodoSummaryResponse.class);
        MaTodoSummaryResponse r2 = mock(MaTodoSummaryResponse.class);

        when(maTodoProjectRepository.countTodoByTodoStatus("p1")).thenReturn(List.of(r1));
        when(maTodoProjectRepository.countTodoByTodoStatus("p2")).thenReturn(List.of(r2));

        ResponseObject<?> res1 = service.countTodoByTodoStatus("p1");
        ResponseObject<?> res2 = service.countTodoByTodoStatus("p2");

        assertEquals(List.of(r1), res1.getData());
        assertEquals(List.of(r2), res2.getData());
        verify(maTodoProjectRepository).countTodoByTodoStatus("p1");
        verify(maTodoProjectRepository).countTodoByTodoStatus("p2");
    }

    @Test
    void testCountTodoByTodoListAllProject_WithDifferentProjects() {
        MeDataDashboardTodoListResponse t1 = mock(MeDataDashboardTodoListResponse.class);
        MeDataDashboardTodoListResponse t2 = mock(MeDataDashboardTodoListResponse.class);

        when(maTodoRepository.countTodoByTodoListAllProject("p1")).thenReturn(List.of(t1));
        when(maTodoRepository.countTodoByTodoListAllProject("p2")).thenReturn(List.of(t2));

        List<MeDataDashboardTodoListResponse> res1 = service.countTodoByTodoListAllProject("p1");
        List<MeDataDashboardTodoListResponse> res2 = service.countTodoByTodoListAllProject("p2");

        assertEquals(List.of(t1), res1);
        assertEquals(List.of(t2), res2);
        verify(maTodoRepository).countTodoByTodoListAllProject("p1");
        verify(maTodoRepository).countTodoByTodoListAllProject("p2");
    }
    @Test
    void testCountTodoByLabelAllProject_WithDifferentProjects() {
        MeDataDashboardLabelResponse l1 = mock(MeDataDashboardLabelResponse.class);
        MeDataDashboardLabelResponse l2 = mock(MeDataDashboardLabelResponse.class);

        when(maTodoRepository.countTodoByLabelAllProject("p1")).thenReturn(List.of(l1));
        when(maTodoRepository.countTodoByLabelAllProject("p2")).thenReturn(List.of(l2));

        List<MeDataDashboardLabelResponse> res1 = service.countTodoByLabelAllProject("p1");
        List<MeDataDashboardLabelResponse> res2 = service.countTodoByLabelAllProject("p2");

        assertEquals(List.of(l1), res1);
        assertEquals(List.of(l2), res2);
        verify(maTodoRepository).countTodoByLabelAllProject("p1");
        verify(maTodoRepository).countTodoByLabelAllProject("p2");
    }

    @Test
    void testCountTodoByTodoListPhase_WithDifferentProjectsAndPhases() {
        MeDataDashboardTodoListResponse r1 = mock(MeDataDashboardTodoListResponse.class);
        MeDataDashboardTodoListResponse r2 = mock(MeDataDashboardTodoListResponse.class);

        when(maTodoRepository.countTodoByTodoListPhase("p1", "ph1")).thenReturn(List.of(r1));
        when(maTodoRepository.countTodoByTodoListPhase("p2", "ph2")).thenReturn(List.of(r2));

        List<MeDataDashboardTodoListResponse> res1 = service.countTodoByTodoListPhase("p1", "ph1");
        List<MeDataDashboardTodoListResponse> res2 = service.countTodoByTodoListPhase("p2", "ph2");

        assertEquals(List.of(r1), res1);
        assertEquals(List.of(r2), res2);
        verify(maTodoRepository).countTodoByTodoListPhase("p1", "ph1");
        verify(maTodoRepository).countTodoByTodoListPhase("p2", "ph2");
    }

    @Test
    void testCountTodoByNoDueDatePhase_WithDifferentProjectsAndPhases() {
        when(maTodoRepository.countTodoByNoDueDatePhase("p1", "ph1")).thenReturn(5);
        when(maTodoRepository.countTodoByNoDueDatePhase("p2", "ph2")).thenReturn(15);

        Integer res1 = service.countTodoByNoDueDatePhase("p1", "ph1");
        Integer res2 = service.countTodoByNoDueDatePhase("p2", "ph2");

        assertEquals(5, res1);
        assertEquals(15, res2);
        verify(maTodoRepository).countTodoByNoDueDatePhase("p1", "ph1");
        verify(maTodoRepository).countTodoByNoDueDatePhase("p2", "ph2");
    }

    @Test
    void testCountTodoByNoLabelAllProject_WithDifferentProjects() {
        when(maTodoRepository.countTodoByNoLabelAllProject("p1")).thenReturn(7);
        when(maTodoRepository.countTodoByNoLabelAllProject("p2")).thenReturn(17);

        Integer res1 = service.countTodoByNoLabelAllProject("p1");
        Integer res2 = service.countTodoByNoLabelAllProject("p2");

        assertEquals(7, res1);
        assertEquals(17, res2);
        verify(maTodoRepository).countTodoByNoLabelAllProject("p1");
        verify(maTodoRepository).countTodoByNoLabelAllProject("p2");
    }

    @Test
    void testCountTodoByNoDueDateAllProject_WithDifferentProjects() {
        when(maTodoRepository.countTodoByNoDueDateAllProject("p1")).thenReturn(4);
        when(maTodoRepository.countTodoByNoDueDateAllProject("p2")).thenReturn(14);

        Integer res1 = service.countTodoByNoDueDateAllProject("p1");
        Integer res2 = service.countTodoByNoDueDateAllProject("p2");

        assertEquals(4, res1);
        assertEquals(14, res2);
        verify(maTodoRepository).countTodoByNoDueDateAllProject("p1");
        verify(maTodoRepository).countTodoByNoDueDateAllProject("p2");
    }
    @Test
    void testCountTodoByDueDateAllProject_WithNullProjectId() {
        when(maTodoRepository.countTodoByDueDateAllProject(null, 1)).thenReturn(0);

        Integer result = service.countTodoByDueDateAllProject(null, 1);

        assertEquals(0, result);
        verify(maTodoRepository).countTodoByDueDateAllProject(null, 1);
    }

    @Test
    void testCountTodoByNoDueDateAllProject_WithNullProjectId() {
        when(maTodoRepository.countTodoByNoDueDateAllProject(null)).thenReturn(0);

        Integer result = service.countTodoByNoDueDateAllProject(null);

        assertEquals(0, result);
        verify(maTodoRepository).countTodoByNoDueDateAllProject(null);
    }

    @Test
    void testCountTodoByLabelAllProject_WithNullProjectId() {
        when(maTodoRepository.countTodoByLabelAllProject(null)).thenReturn(List.of());

        List<MeDataDashboardLabelResponse> result = service.countTodoByLabelAllProject(null);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(maTodoRepository).countTodoByLabelAllProject(null);
    }

    @Test
    void testCountTodoByNoLabelAllProject_WithNullProjectId() {
        when(maTodoRepository.countTodoByNoLabelAllProject(null)).thenReturn(0);

        Integer result = service.countTodoByNoLabelAllProject(null);

        assertEquals(0, result);
        verify(maTodoRepository).countTodoByNoLabelAllProject(null);
    }

    @Test
    void testCountTodoByTodoListPhase_WithNullIds() {
        when(maTodoRepository.countTodoByTodoListPhase(null, null)).thenReturn(List.of());

        List<MeDataDashboardTodoListResponse> result = service.countTodoByTodoListPhase(null, null);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(maTodoRepository).countTodoByTodoListPhase(null, null);
    }

    @Test
    void testCountTodoByDueDatePhase_WithNullIdsAndStatus() {
        when(maTodoRepository.countTodoByDueDatePhase(null, null, null)).thenReturn(0);

        Integer result = service.countTodoByDueDatePhase(null, null, null);

        assertEquals(0, result);
        verify(maTodoRepository).countTodoByDueDatePhase(null, null, null);
    }

    @Test
    void testCountTodoByNoDueDatePhase_WithNullIds() {
        when(maTodoRepository.countTodoByNoDueDatePhase(null, null)).thenReturn(0);

        Integer result = service.countTodoByNoDueDatePhase(null, null);

        assertEquals(0, result);
        verify(maTodoRepository).countTodoByNoDueDatePhase(null, null);
    }

    @Test
    void testCountTodoByPriorityLevel_WithNullProjectIdAndPriority() {
        when(maTodoRepository.countTodoByPriorityLevel(null, null)).thenReturn(0);

        Integer result = service.countTodoByPriorityLevel(null, null);

        assertEquals(0, result);
        verify(maTodoRepository).countTodoByPriorityLevel(null, null);
    }

    @Test
    void testCountTodoByTodoStatus_WithNullId() {
        when(maTodoProjectRepository.countTodoByTodoStatus(null)).thenReturn(List.of());

        ResponseObject<?> result = service.countTodoByTodoStatus(null);

        assertEquals(HttpStatus.OK, result.getStatus());
        assertEquals("Tìm thấy dữ liệu thành công", result.getMessage());
        assertTrue(((List<?>) result.getData()).isEmpty());
        verify(maTodoProjectRepository).countTodoByTodoStatus(null);
    }
    @Test
    void testCountTodoByTodoListAllProject_WithNullProjectId() {
        when(maTodoRepository.countTodoByTodoListAllProject(null)).thenReturn(List.of());

        List<MeDataDashboardTodoListResponse> result = service.countTodoByTodoListAllProject(null);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(maTodoRepository).countTodoByTodoListAllProject(null);
    }

    @Test
    void testCountTodoByTodoListPhase_WithProjectIdOnly() {
        when(maTodoRepository.countTodoByTodoListPhase("p1", null)).thenReturn(List.of());

        List<MeDataDashboardTodoListResponse> result = service.countTodoByTodoListPhase("p1", null);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(maTodoRepository).countTodoByTodoListPhase("p1", null);
    }

    @Test
    void testCountTodoByTodoListPhase_WithPhaseIdOnly() {
        when(maTodoRepository.countTodoByTodoListPhase(null, "ph1")).thenReturn(List.of());

        List<MeDataDashboardTodoListResponse> result = service.countTodoByTodoListPhase(null, "ph1");

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(maTodoRepository).countTodoByTodoListPhase(null, "ph1");
    }

    @Test
    void testCountTodoByDueDatePhase_WithProjectIdOnly() {
        when(maTodoRepository.countTodoByDueDatePhase("p1", null, 1)).thenReturn(0);

        Integer result = service.countTodoByDueDatePhase("p1", null, 1);

        assertEquals(0, result);
        verify(maTodoRepository).countTodoByDueDatePhase("p1", null, 1);
    }

    @Test
    void testCountTodoByDueDatePhase_WithPhaseIdOnly() {
        when(maTodoRepository.countTodoByDueDatePhase(null, "ph1", 1)).thenReturn(0);

        Integer result = service.countTodoByDueDatePhase(null, "ph1", 1);

        assertEquals(0, result);
        verify(maTodoRepository).countTodoByDueDatePhase(null, "ph1", 1);
    }

    @Test
    void testCountTodoByNoDueDatePhase_WithProjectIdOnly() {
        when(maTodoRepository.countTodoByNoDueDatePhase("p1", null)).thenReturn(0);

        Integer result = service.countTodoByNoDueDatePhase("p1", null);

        assertEquals(0, result);
        verify(maTodoRepository).countTodoByNoDueDatePhase("p1", null);
    }

    @Test
    void testCountTodoByNoDueDatePhase_WithPhaseIdOnly() {
        when(maTodoRepository.countTodoByNoDueDatePhase(null, "ph1")).thenReturn(0);

        Integer result = service.countTodoByNoDueDatePhase(null, "ph1");

        assertEquals(0, result);
        verify(maTodoRepository).countTodoByNoDueDatePhase(null, "ph1");
    }

    @Test
    void testCountTodoByPriorityLevel_WithOnlyPriority() {
        when(maTodoRepository.countTodoByPriorityLevel(null, 1)).thenReturn(0);

        Integer result = service.countTodoByPriorityLevel(null, 1);

        assertEquals(0, result);
        verify(maTodoRepository).countTodoByPriorityLevel(null, 1);
    }

    @Test
    void testCountTodoByPriorityLevel_WithOnlyProject() {
        when(maTodoRepository.countTodoByPriorityLevel("p1", null)).thenReturn(0);

        Integer result = service.countTodoByPriorityLevel("p1", null);

        assertEquals(0, result);
        verify(maTodoRepository).countTodoByPriorityLevel("p1", null);
    }

}
