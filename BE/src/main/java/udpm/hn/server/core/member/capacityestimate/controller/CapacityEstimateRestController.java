package udpm.hn.server.core.member.capacityestimate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import udpm.hn.server.core.member.capacityestimate.model.request.CapacityEstimatePageRequest;
import udpm.hn.server.core.member.capacityestimate.model.request.CapacityEstimateRequest;
import udpm.hn.server.core.member.capacityestimate.service.CapacityEstimateService;
import udpm.hn.server.infrastructure.constant.MappingConstants;
import udpm.hn.server.utils.Helper;

@RestController
@RequiredArgsConstructor
@RequestMapping(MappingConstants.API_MEMBER_CAPACITY_ESTIMATE)
public class CapacityEstimateRestController {

    private final CapacityEstimateService capacityEstimateService;

    @GetMapping
    public ResponseEntity<?> getAllCapacityEstimate(CapacityEstimatePageRequest request){
        return Helper.createResponseEntity(capacityEstimateService.getAll(request));
    }


    @GetMapping("/capacity-estimate/{id}")
    public ResponseEntity<?> capacityEstimateByPhase(@PathVariable String id ){
        return Helper.createResponseEntity(capacityEstimateService.CapacityEstimateBySprint(id));
    }

    @GetMapping("/phase-project/{id}")
    public ResponseEntity<?> getAllSprintByProject(@PathVariable String id ){
        return Helper.createResponseEntity(capacityEstimateService.getAllSprint(id));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> detailCapacityEstimate(@PathVariable String id){
        return Helper.createResponseEntity(capacityEstimateService.detailCapacityEstimate(id));
    }

    @GetMapping("/velocity/{id}")
    public ResponseEntity<?> VelocityTB(@PathVariable String id){
        return Helper.createResponseEntity(capacityEstimateService.velocityTB(id));
    }   

    @DeleteMapping("/{id}")
    public ResponseEntity<?> removeCapacityEstimate(@PathVariable String id){
        return Helper.createResponseEntity(capacityEstimateService.deleteCapacityEstimate(id));
    }


    @PostMapping
    public ResponseEntity<?> createCapacityEstimate(CapacityEstimateRequest request){
        return Helper.createResponseEntity(capacityEstimateService.createCapacityEstimate(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> createCapacityEstimate(@PathVariable String id, CapacityEstimateRequest request){
        return Helper.createResponseEntity(capacityEstimateService.updateCapacityEstimate(id,request));
    }

}
