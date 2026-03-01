package udpm.hn.server.core.permitall.Register.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import udpm.hn.server.core.common.base.ResponseObject;

import udpm.hn.server.core.permitall.Register.repository.PMFacilityLoginRepository;
import udpm.hn.server.core.permitall.Register.service.PMLoginFacilityService;

@Service
@RequiredArgsConstructor
public class PMLoginFacilityServiceImpl implements PMLoginFacilityService {

    private final PMFacilityLoginRepository pmFacilityLoginRepository;

    @Override
    public ResponseObject<?> getAllFacility() {
        return new ResponseObject<>(pmFacilityLoginRepository.getAllFacility(), HttpStatus.OK,"Lấy dữ liệu cơ sở thành công");
    }
}
