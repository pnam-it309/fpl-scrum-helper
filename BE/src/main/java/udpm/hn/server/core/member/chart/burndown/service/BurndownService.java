package udpm.hn.server.core.member.chart.burndown.service;

import udpm.hn.server.core.common.base.ResponseObject;
import udpm.hn.server.core.member.chart.burndown.model.request.BurndownRequest;

public interface BurndownService {

    ResponseObject<?> burnDownChart( String idProject, String idPhase);

    ResponseObject<?> getBurndownDataStoryPoint(String projectId, String phaseId);

    ResponseObject<?> getBurndownDataStoryPointByProject(String projectId);

}
