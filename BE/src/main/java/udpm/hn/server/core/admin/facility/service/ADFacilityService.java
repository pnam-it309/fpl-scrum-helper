package udpm.hn.server.core.admin.facility.service;

import jakarta.validation.Valid;
import udpm.hn.server.core.admin.facility.model.request.ADCreateUpdateFacilityRequest;
import udpm.hn.server.core.admin.facility.model.request.ADFacilityRequest;
import udpm.hn.server.core.common.base.ResponseObject;

public interface ADFacilityService {

    ResponseObject<?> getAllFacility(ADFacilityRequest request);

    ResponseObject<?> createFacility(@Valid ADCreateUpdateFacilityRequest request);

    ResponseObject<?> updateFacility(String facilityId, @Valid ADCreateUpdateFacilityRequest request);

    ResponseObject<?> changeFacilityStatus(String facilityId);

    ResponseObject<?> getFacilityById(String facilityId);
}
