package udpm.hn.server.core.member.chart.burndown.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import udpm.hn.server.core.member.chart.burndown.model.request.BurndownRequest;
import udpm.hn.server.core.member.chart.burndown.service.BurndownService;
import udpm.hn.server.infrastructure.constant.MappingConstants;
import udpm.hn.server.utils.Helper;

@Controller
@RequestMapping(MappingConstants.API_MEMBER_CHART)
@Slf4j
@RequiredArgsConstructor
public class BurnDownController {

    private final BurndownService burndownService;

    @GetMapping("/burndown/{idProject}/{phaseId}")
    public ResponseEntity<?> burnDownChar( @PathVariable String idProject, @PathVariable String phaseId) {
        return Helper.createResponseEntity(burndownService.burnDownChart(idProject, phaseId));
    }

    @GetMapping("/storyPoint/{projectId}/{phaseId}")
    public ResponseEntity<?> getBurndownChart(
            @PathVariable String projectId,
            @PathVariable String phaseId
    ) {
        return ResponseEntity.ok(burndownService.getBurndownDataStoryPoint(projectId, phaseId));
    }

    @GetMapping("/storyPoint-project/{projectId}")
    public ResponseEntity<?> getBurndownChartStoryPointByproject(
            @PathVariable String projectId
    ) {
        return ResponseEntity.ok(burndownService.getBurndownDataStoryPointByProject(projectId));
    }

}
