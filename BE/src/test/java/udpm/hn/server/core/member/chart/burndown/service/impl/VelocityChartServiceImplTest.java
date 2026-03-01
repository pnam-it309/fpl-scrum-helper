package udpm.hn.server.core.member.chart.burndown.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import udpm.hn.server.core.common.base.ResponseObject;
import udpm.hn.server.core.member.chart.burndown.model.reponse.VelocityChartResponse;
import udpm.hn.server.entity.PhaseProject;
import udpm.hn.server.entity.VelocityRecord;
import udpm.hn.server.repository.VelocityRecordRepository;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class VelocityChartServiceImplTest {

    private VelocityRecordRepository velocityRecordRepository;
    private VelocityChartServiceImpl velocityChartService;

    @BeforeEach
    void setUp() {
        velocityRecordRepository = mock(VelocityRecordRepository.class);
        velocityChartService = new VelocityChartServiceImpl(velocityRecordRepository);
    }

    @Test
    void getVelocityChart_shouldReturnDataCorrectly() {
        // Given
        String projectId = "project-123";

        PhaseProject phase1 = new PhaseProject();
        phase1.setName("Phase 1");

        PhaseProject phase2 = new PhaseProject();
        phase2.setName("Phase 2");

        VelocityRecord record1 = new VelocityRecord();
        record1.setPhaseProject(phase1);
        record1.setEstimatedPoint(20.0);
        record1.setActualPoint(18.0);

        VelocityRecord record2 = new VelocityRecord();
        record2.setPhaseProject(phase2);
        record2.setEstimatedPoint(25.0);
        record2.setActualPoint(27.0);

        List<VelocityRecord> mockData = Arrays.asList(record1, record2);
        when(velocityRecordRepository.findAllByProjectId(projectId)).thenReturn(mockData);

        // When
        ResponseObject<?> response = velocityChartService.getVelocityChart(projectId);

        // Then
        assertNotNull(response);
        assertEquals(200, response.getStatus().value());
        assertEquals("Lấy dữ liệu velocity chart thành công", response.getMessage());

        List<VelocityChartResponse> chartData = (List<VelocityChartResponse>) response.getData();
        assertEquals(2, chartData.size());

        VelocityChartResponse first = chartData.get(0);
        assertEquals("Phase 1", first.getPhaseName());
        assertEquals(20, first.getEstimated());
        assertEquals(18, first.getActual());

        VelocityChartResponse second = chartData.get(1);
        assertEquals("Phase 2", second.getPhaseName());
        assertEquals(25, second.getEstimated());
        assertEquals(27, second.getActual());

        verify(velocityRecordRepository, times(1)).findAllByProjectId(projectId);
    }

    @Test
    void getVelocityChart_shouldReturnEmptyListIfNoData() {
        // Given
        String projectId = "empty-project";
        when(velocityRecordRepository.findAllByProjectId(projectId)).thenReturn(List.of());

        // When
        ResponseObject<?> response = velocityChartService.getVelocityChart(projectId);

        // Then
        assertNotNull(response);
        assertEquals(200, response.getStatus().value());
        assertTrue(((List<?>) response.getData()).isEmpty());
        assertEquals("Lấy dữ liệu velocity chart thành công", response.getMessage());

        verify(velocityRecordRepository, times(1)).findAllByProjectId(projectId);
    }

}
