//package udpm.hn.server.core.manage.project.service.impl;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.*;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.http.HttpStatus;
//import udpm.hn.server.core.common.base.ResponseObject;
//import udpm.hn.server.core.manage.project.model.response.MaTodoSummaryResponse;
//import udpm.hn.server.core.manage.project.model.response.MeDataDashboardLabelResponse;
//import udpm.hn.server.core.manage.project.model.response.MeDataDashboardTodoListResponse;
//import udpm.hn.server.core.manage.project.repository.MaTodoProjectRepository;
//import udpm.hn.server.core.manage.todo.repository.MATodoRepository;
//import udpm.hn.server.infrastructure.constant.StatusTodo;
//
//import java.util.Arrays;
//import java.util.Collections;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//public class MaTodoServiceImplTest {
//
//    @Mock
//    private MATodoRepository maTodoRepository;
//
//    @Mock
//    private MaTodoProjectRepository maTodoProjectRepository;
//
//    @InjectMocks
//    private MaTodoServiceImpl maTodoService;
//
//    @Test
//    void countTodoByTodoListAllProject_shouldReturnList() {
//        // given
//        String projectId = "project-1";
//
//        MeDataDashboardTodoListResponse response1 = mock(MeDataDashboardTodoListResponse.class);
//        when(response1.getName()).thenReturn("Todo List 1");
//        when(response1.getList()).thenReturn(5);
//
//        MeDataDashboardTodoListResponse response2 = mock(MeDataDashboardTodoListResponse.class);
//        when(response2.getName()).thenReturn("Todo List 2");
//        when(response2.getList()).thenReturn(3);
//
//        List<MeDataDashboardTodoListResponse> expected = List.of(response1, response2);
//
//        // when
//        when(maTodoRepository.countTodoByTodoListAllProject(projectId)).thenReturn(expected);
//
//        // then
//        List<MeDataDashboardTodoListResponse> result = maTodoService.countTodoByTodoListAllProject(projectId);
//
//        assertEquals(2, result.size());
//        assertEquals("Todo List 1", result.get(0).getName());
//        assertEquals(5, result.get(0).getList());
//        assertEquals("Todo List 2", result.get(1).getName());
//        assertEquals(3, result.get(1).getList());
//
//        verify(maTodoRepository).countTodoByTodoListAllProject(projectId);
//    }
//
//    @Test
//    void countTodoByDueDateAllProject_shouldReturnCount() {
//        String projectId = "project-1";
//        int status = 1;
//        int expectedCount = 5;
//
//        when(maTodoRepository.countTodoByDueDateAllProject(projectId, status)).thenReturn(expectedCount);
//
//        int result = maTodoService.countTodoByDueDateAllProject(projectId, status);
//
//        assertEquals(expectedCount, result);
//        verify(maTodoRepository).countTodoByDueDateAllProject(projectId, status);
//    }
//
//    @Test
//    void countTodoByNoDueDateAllProject_shouldReturnCount() {
//        String projectId = "project-1";
//        int expectedCount = 3;
//
//        when(maTodoRepository.countTodoByNoDueDateAllProject(projectId)).thenReturn(expectedCount);
//
//        int result = maTodoService.countTodoByNoDueDateAllProject(projectId);
//
//        assertEquals(expectedCount, result);
//    }
//
//    @Test
//    void countTodoByLabelAllProject_shouldReturnList() {
//        String projectId = "project-1";
//
//        MeDataDashboardLabelResponse labelResponse = mock(MeDataDashboardLabelResponse.class);
//        when(labelResponse.getName()).thenReturn("Label A");
//        when(labelResponse.getLabel()).thenReturn(3);
//
//        List<MeDataDashboardLabelResponse> expected = List.of(labelResponse);
//
//        when(maTodoRepository.countTodoByLabelAllProject(projectId)).thenReturn(expected);
//
//        List<MeDataDashboardLabelResponse> result = maTodoService.countTodoByLabelAllProject(projectId);
//
//        assertEquals(1, result.size());
//        assertEquals("Label A", result.get(0).getName());
//        assertEquals(3, result.get(0).getLabel());
//        verify(maTodoRepository).countTodoByLabelAllProject(projectId);
//    }
//
//    @Test
//    void countTodoByNoLabelAllProject_shouldReturnCount() {
//        String projectId = "project-1";
//        int expectedCount = 2;
//
//        when(maTodoRepository.countTodoByNoLabelAllProject(projectId)).thenReturn(expectedCount);
//
//        int result = maTodoService.countTodoByNoLabelAllProject(projectId);
//
//        assertEquals(expectedCount, result);
//    }
//
//    @Test
//    void countTodoByTodoListPhase_shouldReturnList() {
//        // Given
//        String projectId = "p1";
//        String phaseId = "ph1";
//
//        MeDataDashboardTodoListResponse response = mock(MeDataDashboardTodoListResponse.class);
//        when(response.getName()).thenReturn("Todo List Phase 1");
//        when(response.getList()).thenReturn(7);
//
//        List<MeDataDashboardTodoListResponse> expected = Collections.singletonList(response);
//
//        // When
//        when(maTodoRepository.countTodoByTodoListPhase(projectId, phaseId)).thenReturn(expected);
//
//        List<MeDataDashboardTodoListResponse> result = maTodoService.countTodoByTodoListPhase(projectId, phaseId);
//
//        // Then
//        assertEquals(1, result.size());
//        assertEquals("Todo List Phase 1", result.get(0).getName());
//        assertEquals(7, result.get(0).getList());
//
//        verify(maTodoRepository).countTodoByTodoListPhase(projectId, phaseId);
//    }
//
//
//    @Test
//    void countTodoByDueDatePhase_shouldReturnCount() {
//        String projectId = "p1";
//        String phaseId = "ph1";
//        int status = 1;
//        int expected = 4;
//
//        when(maTodoRepository.countTodoByDueDatePhase(projectId, phaseId, status)).thenReturn(expected);
//
//        int result = maTodoService.countTodoByDueDatePhase(projectId, phaseId, status);
//
//        assertEquals(expected, result);
//    }
//
//    @Test
//    void countTodoByNoDueDatePhase_shouldReturnCount() {
//        String projectId = "p1";
//        String phaseId = "ph1";
//        int expected = 2;
//
//        when(maTodoRepository.countTodoByNoDueDatePhase(projectId, phaseId)).thenReturn(expected);
//
//        int result = maTodoService.countTodoByNoDueDatePhase(projectId, phaseId);
//
//        assertEquals(expected, result);
//    }
//
//    @Test
//    void countTodoByPriorityLevel_shouldReturnCount() {
//        // Arrange
//        String projectId = "p1";
//        int level = 2;
//        int expected = 7;
//
//        when(maTodoRepository.countTodoByPriorityLevel(projectId, level)).thenReturn(expected);
//
//        // Act
//        int result = maTodoService.countTodoByPriorityLevel(projectId, level);
//
//        // Assert
//        assertEquals(expected, result);
//    }
//
//    @Test
//    void countTodoByTodoStatus_shouldReturnResponseObject() {
//        // Given
//        String projectId = "p1";
//
//        MaTodoSummaryResponse mockResponse = mock(MaTodoSummaryResponse.class);
//        when(mockResponse.getAmount()).thenReturn(5);
//        when(mockResponse.getTodoStatus()).thenReturn(StatusTodo.DA_HOAN_THANH);
//
//        List<MaTodoSummaryResponse> mockData = List.of(mockResponse);
//
//        when(maTodoProjectRepository.countTodoByTodoStatus(projectId)).thenReturn(mockData);
//
//        // When
//        ResponseObject<?> response = maTodoService.countTodoByTodoStatus(projectId);
//
//        // Then
//        assertEquals(HttpStatus.OK, response.getStatus());
//        assertEquals("Tìm thấy dữ liệu thành công", response.getMessage());
//        assertEquals(mockData, response.getData());
//
//        verify(maTodoProjectRepository).countTodoByTodoStatus(projectId);
//    }
//
//}
