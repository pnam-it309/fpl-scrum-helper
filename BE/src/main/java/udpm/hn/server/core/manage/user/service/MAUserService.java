package udpm.hn.server.core.manage.user.service;

import udpm.hn.server.core.common.base.PageableRequest;
import udpm.hn.server.core.common.base.ResponseObject;
import udpm.hn.server.core.manage.project.model.request.MaProjectCreateRequest;
import udpm.hn.server.core.manage.user.model.request.MAUserRequest;
import udpm.hn.server.core.manage.user.model.request.MaUserCreateUpdateRequest;

public interface MAUserService {

    ResponseObject<?> listUserProject(MAUserRequest request, String idProject, String idPhase);

    ResponseObject<?> deleteUser(String id);

    ResponseObject<?> updateProjectMembers(String idProject, MaUserCreateUpdateRequest request);

    ResponseObject<?> idFacility(String idProject);

}
