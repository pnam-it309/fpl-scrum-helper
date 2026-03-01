package udpm.hn.server.core.admin.department.department.service;

import jakarta.validation.Valid;
import udpm.hn.server.core.admin.department.department.model.request.ADCreateOrUpdateMajorRequest;
import udpm.hn.server.core.admin.department.department.model.request.ADMajorRequest;
import udpm.hn.server.core.common.base.ResponseObject;

public interface ADMajorService {
    ResponseObject<?> getAllMajor(String id, ADMajorRequest request);

    ResponseObject<?> addMajor(@Valid ADCreateOrUpdateMajorRequest request);

    ResponseObject<?> updateMajor(@Valid ADCreateOrUpdateMajorRequest request, String id);

    ResponseObject<?> deleteMajor(String id);

    ResponseObject<?> detailMajor(String id);
}
