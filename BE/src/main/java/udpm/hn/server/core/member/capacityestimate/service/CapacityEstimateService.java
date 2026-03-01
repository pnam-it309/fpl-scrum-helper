package udpm.hn.server.core.member.capacityestimate.service;

import udpm.hn.server.core.common.base.ResponseObject;
import udpm.hn.server.core.member.capacityestimate.model.request.CapacityEstimatePageRequest;
import udpm.hn.server.core.member.capacityestimate.model.request.CapacityEstimateRequest;

public interface CapacityEstimateService {

    ResponseObject<?> getAll(CapacityEstimatePageRequest request);

//    ResponseObject<?> getAllCapacityEstimate(CapacityEstimatePageRequest request);

    ResponseObject<?> getAllSprint(String idProject);

    ResponseObject<?> createCapacityEstimate(CapacityEstimateRequest request);

    ResponseObject<?> updateCapacityEstimate(String id,CapacityEstimateRequest request);

    ResponseObject<?> detailCapacityEstimate(String id);

    ResponseObject<?> deleteCapacityEstimate(String id);

    ResponseObject<?> CapacityEstimateBySprint(String id);

    ResponseObject<?> velocityTB(String id);


}
