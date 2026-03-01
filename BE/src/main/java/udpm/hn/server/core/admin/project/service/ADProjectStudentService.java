package udpm.hn.server.core.admin.project.service;

import udpm.hn.server.core.common.base.ResponseObject;

public interface ADProjectStudentService {

    ResponseObject<?> getAll(String idMajorFacility);

    ResponseObject<?> getAllStaff(String idMajorFacility);

    ResponseObject<?> getAllStudentByProject(String idProject);

    ResponseObject<?> getAllStaffByProject(String idProject);
}
