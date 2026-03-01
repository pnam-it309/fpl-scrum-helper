package udpm.hn.server.core.manage.capacityestimate.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import udpm.hn.server.core.common.base.ResponseObject;
import udpm.hn.server.core.manage.capacityestimate.model.request.MaEstimatePageRequest;
import udpm.hn.server.core.manage.capacityestimate.repository.MACapacityEstimateRepository;
import udpm.hn.server.core.manage.capacityestimate.service.MACapacityEstimate;
import udpm.hn.server.utils.Helper;

@Service
@RequiredArgsConstructor
public class MACapacityEstimateImpl implements MACapacityEstimate {

    private final MACapacityEstimateRepository maCapacityEstimateRepository;

    @Override
    public ResponseObject<?> getAllCapacityEstimate(MaEstimatePageRequest request) {

        Pageable page = Helper.createPageable(request);

        return new ResponseObject<>(maCapacityEstimateRepository.getAllEstimate(page, request.getIdProject()), HttpStatus.OK,"Lấy dữ liệu thành công") ;
    }
}
