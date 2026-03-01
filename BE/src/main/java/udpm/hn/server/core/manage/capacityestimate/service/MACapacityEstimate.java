package udpm.hn.server.core.manage.capacityestimate.service;

import udpm.hn.server.core.common.base.ResponseObject;
import udpm.hn.server.core.manage.capacityestimate.model.request.MaEstimatePageRequest;

public interface MACapacityEstimate {

    ResponseObject<?> getAllCapacityEstimate(MaEstimatePageRequest request);

}
