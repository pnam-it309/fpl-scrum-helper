package udpm.hn.server.core.member.chart.burndown.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import udpm.hn.server.core.common.base.ResponseObject;
import udpm.hn.server.core.member.chart.burndown.model.request.BurndownPoint;
import udpm.hn.server.core.member.chart.burndown.repository.PhaseBurndownRepository;
import udpm.hn.server.core.member.chart.burndown.repository.TodoCountRepository;
import udpm.hn.server.entity.PhaseProject;
import udpm.hn.server.infrastructure.constant.StatusTodo;
import udpm.hn.server.repository.ToDoRepository;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class BurndownServiceImplTest {

    @Mock
    private PhaseBurndownRepository phaseBurndownRepository;
    @Mock
    private TodoCountRepository todoCountRepository;
    @Mock
    private ToDoRepository toDoRepository;

    @InjectMocks
    private BurndownServiceImpl burndownService;

    private final String projectId = "project123";
    private final String phaseId = "123e4567-e89b-12d3-a456-426614174000";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void burnDownChart_invalidDuration() {
        when(phaseBurndownRepository.calculatePhaseDurationDays(phaseId, projectId)).thenReturn(0L);

        ResponseObject<?> response = burndownService.burnDownChart(projectId, phaseId);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatus());
    }

    @Test
    void burnDownChart_valid() {
        PhaseProject phase = new PhaseProject();
        phase.setStartTime(LocalDate.now().minusDays(3).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli());

        when(phaseBurndownRepository.calculatePhaseDurationDays(phaseId, projectId)).thenReturn(3L);
        when(phaseBurndownRepository.findByIdAndProjectId(phaseId, projectId)).thenReturn(Optional.of(phase));
        when(todoCountRepository.countTodosByPhaseProject(phaseId)).thenReturn(9L);
        when(todoCountRepository.countCompletedTasksByPhaseAndProject(eq(projectId), eq(phaseId), anyList())).thenReturn(3L);
        when(todoCountRepository.countRemainingTasksByPhaseAndProject(eq(projectId), eq(phaseId), anyList())).thenReturn(6L);
        when(todoCountRepository.countCompletedTasksPerDay(eq(projectId), eq(phaseId), anyList())).thenReturn(List.of(1L, 1L, 1L));

        ResponseObject<?> response = burndownService.burnDownChart(projectId, phaseId);
        assertEquals(HttpStatus.OK, response.getStatus());
        assertNotNull(response.getData());
    }

    @Test
    void getBurndownDataStoryPoint_valid() {
        PhaseProject phase = new PhaseProject();
        LocalDate start = LocalDate.now().minusDays(3);
        LocalDate end = LocalDate.now();

        phase.setStartTime(start.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli());
        phase.setEndTime(end.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli());

        // ✅ phaseId đã hợp lệ
        when(phaseBurndownRepository.findById(String.valueOf(UUID.fromString(phaseId))))
                .thenReturn(Optional.of(phase));

        when(toDoRepository.getTotalStoryPointByPhaseAndProject(projectId, phaseId)).thenReturn(9);

        List<Object[]> resultList = new ArrayList<>();
        resultList.add(new Object[]{start.plusDays(1).toString(), 3});

        when(toDoRepository.getDonePointsByDateAndProject(eq(projectId), eq(phaseId), anyLong(), anyLong(), anyList()))
                .thenReturn(resultList);

        ResponseObject<?> response = burndownService.getBurndownDataStoryPoint(projectId, phaseId);

        assertEquals(HttpStatus.OK, response.getStatus());

        List<BurndownPoint> chart = (List<BurndownPoint>) response.getData();
        assertNotNull(chart);
        assertFalse(chart.isEmpty());
    }


    @Test
    void getBurndownDataStoryPoint_phaseNotFound() {
        when(phaseBurndownRepository.findById(phaseId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> burndownService.getBurndownDataStoryPoint(projectId, phaseId));
    }


}
