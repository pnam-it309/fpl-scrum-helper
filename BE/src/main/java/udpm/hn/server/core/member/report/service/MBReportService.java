package udpm.hn.server.core.member.report.service;

import jakarta.validation.Valid;
import udpm.hn.server.core.common.base.ResponseObject;
import udpm.hn.server.core.member.report.model.request.MBReportRequest;
import udpm.hn.server.core.member.report.model.request.UpdateReportSettingRequest;

public interface MBReportService {

    ResponseObject<?> getAll(MBReportRequest request, String idProject);

    ResponseObject<?> getReportSetting(String idProject);

    ResponseObject<?> updateReportSetting(String idProject, UpdateReportSettingRequest request);



    ResponseObject<?> detail (String id, String idProject);

    ResponseObject<?> add(@Valid MBReportRequest request, String idProject);

    ResponseObject<?> update(String id, @Valid MBReportRequest request,String idProject);

    ResponseObject<?> detailByDate(Long report, String idProject);

    ResponseObject<?> getReportRateByPhase(String phaseId, String projectId);


}
