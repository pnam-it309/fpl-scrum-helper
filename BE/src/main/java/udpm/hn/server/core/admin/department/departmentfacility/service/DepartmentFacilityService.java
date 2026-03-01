package udpm.hn.server.core.admin.department.departmentfacility.service;

import jakarta.validation.Valid;
import udpm.hn.server.core.admin.department.departmentfacility.model.request.CreateOrUpdateDepartmentFacilityRequest;
import udpm.hn.server.core.admin.department.departmentfacility.model.request.FindFacilityDetailRequest;
import udpm.hn.server.core.common.base.ResponseObject;

public interface DepartmentFacilityService {

    ResponseObject<?> getAllDepartmentFacility(String id, FindFacilityDetailRequest request);

    ResponseObject<?> addDepartmentFacility(@Valid CreateOrUpdateDepartmentFacilityRequest request);

    ResponseObject<?> updateDepartmentFacility(@Valid CreateOrUpdateDepartmentFacilityRequest request, String id);

    ResponseObject<?> getListFacility();

    ResponseObject<?> getDepartmentName(String departmentId);

    ResponseObject<?> detailDepartmentFacility(String id);

    ResponseObject<?> changeStatus(String id);

}
