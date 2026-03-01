package udpm.hn.server.core.member.report.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import udpm.hn.server.core.member.report.model.request.MBReportRequest;
import udpm.hn.server.core.member.report.model.request.MBTodoProjectRequest;
import udpm.hn.server.core.member.report.model.request.UpdateReportSettingRequest;
import udpm.hn.server.core.member.report.service.MBReportService;
import udpm.hn.server.core.member.report.service.MBTodoProjectService;
import udpm.hn.server.infrastructure.constant.MappingConstants;
import udpm.hn.server.utils.Helper;

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping(MappingConstants.API_MEMBER_MYPROJECT)
public class MBReportController {

    private final MBReportService mbReportService;

    private final MBTodoProjectService mbTodoProjectService;

    @GetMapping("/report/{idProject}")
    public ResponseEntity<?> getAllReport(MBReportRequest request, @PathVariable String idProject){
        return Helper.createResponseEntity(mbReportService.getAll(request,idProject));
    }

    @GetMapping("/report-setting/{idProject}")
    public ResponseEntity<?> getReportSetting(@PathVariable String idProject){
        return Helper.createResponseEntity(mbReportService.getReportSetting(idProject));
    }

    @GetMapping("/todo/{idProject}")
    public ResponseEntity<?> getAllTodo(MBTodoProjectRequest request, @PathVariable String idProject) {
        return Helper.createResponseEntity(mbTodoProjectService.getAllTodoByProjectAndStaffProject(request, idProject));
    }

    @PostMapping("/add-report/{idProject}")
    public ResponseEntity<?> add(@RequestBody MBReportRequest request, @PathVariable String idProject){
        return Helper.createResponseEntity(mbReportService.add(request, idProject));
    }

    @PutMapping("/update-report/{id}/{idProject}")
    public ResponseEntity<?> update(@RequestBody MBReportRequest request, @PathVariable String id, @PathVariable String idProject){
        return Helper.createResponseEntity(mbReportService.update(id, request, idProject));
    }

    @PutMapping("/update-report-setting/{id}")
    public ResponseEntity<?> update(@RequestBody UpdateReportSettingRequest request, @PathVariable String id){
        return Helper.createResponseEntity(mbReportService.updateReportSetting(id, request));
    }

    @GetMapping("/detail-report/{idProject}/{id}")
    public ResponseEntity<?> detail(@PathVariable String id, @PathVariable String idProject){
        return Helper.createResponseEntity(mbReportService.detail(id, idProject));
    }

    @GetMapping("/report-by-date/{reportTime}/{idProject}")
    public ResponseEntity<?> getReportIdByDate(@PathVariable Long reportTime, @PathVariable String idProject) {
        return Helper.createResponseEntity(mbReportService.detailByDate(reportTime, idProject));
    }

    @GetMapping("/report-rate/{phaseId}/{projectId}")
    public ResponseEntity<?> getReportRate(@PathVariable String phaseId, @PathVariable String projectId) {
        return ResponseEntity.ok(mbReportService.getReportRateByPhase(phaseId, projectId));
    }

}
