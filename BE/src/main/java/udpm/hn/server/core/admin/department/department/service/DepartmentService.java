package udpm.hn.server.core.admin.department.department.service;

import jakarta.validation.Valid;
import udpm.hn.server.core.admin.department.department.model.request.ADCreateOrUpdateDepartmentRequest;
import udpm.hn.server.core.admin.department.department.model.request.DepartmentSearchRequest;
import udpm.hn.server.core.common.base.ResponseObject;

public interface DepartmentService {
    ResponseObject<?> getAllDepartment(DepartmentSearchRequest request);

    ResponseObject<?> addDepartment(@Valid ADCreateOrUpdateDepartmentRequest request);

    ResponseObject<?> updateDepartment(@Valid ADCreateOrUpdateDepartmentRequest request, String id);

    ResponseObject<?> detailDepartment(String id);

    ResponseObject<?> changeStatus(String id);

}
