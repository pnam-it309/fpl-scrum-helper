package udpm.hn.server.core.admin.role.service;

import udpm.hn.server.core.admin.role.model.request.ADRoleRequest;
import udpm.hn.server.core.common.base.ResponseObject;

public interface ADRoleService {

    ResponseObject<?> getAllRole(ADRoleRequest roleRequest);


}