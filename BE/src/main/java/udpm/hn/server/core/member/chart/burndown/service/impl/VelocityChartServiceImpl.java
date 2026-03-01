package udpm.hn.server.core.member.chart.burndown.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import udpm.hn.server.core.common.base.ResponseObject;
import udpm.hn.server.core.member.chart.burndown.model.reponse.VelocityChartResponse;
import udpm.hn.server.core.member.chart.burndown.service.VelocityChartService;
import udpm.hn.server.entity.VelocityRecord;
import udpm.hn.server.repository.VelocityRecordRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VelocityChartServiceImpl implements VelocityChartService {

    private final VelocityRecordRepository velocityRecordRepository;

    @Override
    public ResponseObject<?> getVelocityChart(String projectId) {
        List<VelocityRecord> records = velocityRecordRepository.findAllByProjectId(projectId);

        List<VelocityChartResponse> response = records.stream()
                .map(record -> new VelocityChartResponse(
                        record.getPhaseProject().getName(),
                        record.getEstimatedPoint(),
                        record.getActualPoint()
                ))
                .collect(Collectors.toList());

        return new ResponseObject<>(response, HttpStatus.OK, "Lấy dữ liệu velocity chart thành công");
    }
}
