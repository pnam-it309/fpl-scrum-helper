package udpm.hn.server.core.member.chart.burndown.service;

import udpm.hn.server.core.common.base.ResponseObject;
import udpm.hn.server.core.member.chart.burndown.model.reponse.VelocityChartResponse;

public interface VelocityChartService {
    ResponseObject<?> getVelocityChart(String projectId);
}
