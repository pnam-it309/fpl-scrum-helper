package udpm.hn.server.core.admin.project.service;

import udpm.hn.server.core.admin.project.model.request.ADProjectCreateRequest;
import udpm.hn.server.core.admin.project.model.request.ADProjectSearchRequest;
import udpm.hn.server.core.common.base.ResponseObject;

public interface ADProjectService {

    ResponseObject<?> getAllProject( ADProjectSearchRequest request);

    ResponseObject<?> getAllDepartmentFacility(String idFacility);

    ResponseObject<?> createProject( ADProjectCreateRequest request);

    ResponseObject<?> updateProject( String idProject,ADProjectCreateRequest request);

    ResponseObject<?> detailProject( String idProject );

    ResponseObject<?> deleteProject( String idProject );

    ResponseObject<?> getAllFacility();

    ResponseObject<?> countProjectByStatus();

    ResponseObject<?> getProjectParticipantCounts();

    ResponseObject<?> getProjectTodoCounts();

    ResponseObject<?> countTotalProjects();

    ResponseObject<?> countProjectsByFacility();

    ResponseObject<?> countProjectsByDepartment();

    ResponseObject<?> countTaskByStatusAndProject();
}
