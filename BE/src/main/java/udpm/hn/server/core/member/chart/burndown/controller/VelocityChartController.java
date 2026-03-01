package udpm.hn.server.core.member.chart.burndown.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import udpm.hn.server.core.common.base.ResponseObject;
import udpm.hn.server.core.member.chart.burndown.service.VelocityChartService;
import udpm.hn.server.infrastructure.constant.MappingConstants;
import udpm.hn.server.utils.Helper;

@Controller
@RequiredArgsConstructor
@RequestMapping(MappingConstants.API_MEMBER_CHART)
public class VelocityChartController {

    private final VelocityChartService velocityChartService;

    @GetMapping("/velocity/{projectId}")
    public ResponseEntity<?> getVelocityChart(@PathVariable String projectId) {
        return Helper.createResponseEntity(velocityChartService.getVelocityChart(projectId));
    }

}
