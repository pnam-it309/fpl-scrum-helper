package udpm.hn.server.core.admin.department.departmentfacility.service;

import jakarta.validation.Valid;
import udpm.hn.server.core.admin.department.departmentfacility.model.request.CreateMajorFacilityRequest;
import udpm.hn.server.core.admin.department.departmentfacility.model.request.MajorFacilityRequest;
import udpm.hn.server.core.common.base.ResponseObject;

public interface MajorFacilityService {

    ResponseObject<?> getAllMajorFacilities(@Valid MajorFacilityRequest request);

    ResponseObject<?> getMajorFacilityById(String majorFacilityId);

    ResponseObject<?> createMajorFacility(@Valid CreateMajorFacilityRequest request);

    ResponseObject<?> updateMajorFacility(String majorFacilityId, @Valid CreateMajorFacilityRequest request);

    ResponseObject<?> getAllMajors(String departmentId);

    ResponseObject<?> changeStatus(String id);

}
