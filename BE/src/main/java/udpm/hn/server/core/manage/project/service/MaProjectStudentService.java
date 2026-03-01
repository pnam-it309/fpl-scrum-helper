package udpm.hn.server.core.manage.project.service;

import udpm.hn.server.core.common.base.ResponseObject;
import udpm.hn.server.core.manage.project.model.request.MaUserProjectRequest;

public interface MaProjectStudentService {

    ResponseObject<?> getAll(String idMajorFacility);

    ResponseObject<?> getAllUserProject(MaUserProjectRequest request);

    ResponseObject<?> getAllStaff(String idMajorFacility);

    ResponseObject<?> getAllStudentByProject(String idProject);

    ResponseObject<?> getAllStaffByProject(String idProject);

}
