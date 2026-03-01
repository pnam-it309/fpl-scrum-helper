package udpm.hn.server.core.member.report.service;

import udpm.hn.server.core.common.base.ResponseObject;
import udpm.hn.server.core.member.report.model.request.MBTodoProjectRequest;

public interface MBTodoProjectService {

    ResponseObject<?> getAllTodoByProjectAndStaffProject(MBTodoProjectRequest request, String idProject);
}
