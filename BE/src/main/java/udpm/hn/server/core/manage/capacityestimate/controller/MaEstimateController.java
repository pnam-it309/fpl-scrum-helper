package udpm.hn.server.core.manage.capacityestimate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import udpm.hn.server.core.manage.capacityestimate.model.request.MaEstimatePageRequest;
import udpm.hn.server.core.manage.capacityestimate.service.MACapacityEstimate;
import udpm.hn.server.infrastructure.constant.MappingConstants;
import udpm.hn.server.utils.Helper;

@RequestMapping(MappingConstants.API_MANAGE_CAPACITY_ESTIMATE)
@RestController
@RequiredArgsConstructor
public class MaEstimateController {

    private final MACapacityEstimate capacityEstimate;

    @GetMapping
    public ResponseEntity<?> getAllEstimate(MaEstimatePageRequest request){
        return Helper.createResponseEntity(capacityEstimate.getAllCapacityEstimate(request));
    }


}
