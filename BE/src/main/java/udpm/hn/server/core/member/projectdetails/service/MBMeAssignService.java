package udpm.hn.server.core.member.projectdetails.service;

import udpm.hn.server.core.common.base.ResponseObject;

public interface MBMeAssignService {

    ResponseObject<?> getAllMemberByIdTodo(String idTodo);

    ResponseObject<?> getTodoByIdStaffProject(String idStaffProject,String idProject);
}
