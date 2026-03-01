package udpm.hn.server.core.admin.staff.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import udpm.hn.server.core.admin.staff.model.response.ADStaffByDepartmentFacility;
import udpm.hn.server.core.admin.staff.repository.ADStaffFacilityRepository;
import udpm.hn.server.core.admin.staff.service.ADStaffFacilityService;
import udpm.hn.server.core.common.base.ResponseObject;

@Service
@RequiredArgsConstructor
public class ADStaffFacilityServiceImpl implements ADStaffFacilityService {

    private final ADStaffFacilityRepository adStaffFacilityRepository;

    @Override
    public ResponseObject<?> getAllFacility() {
        return new ResponseObject<>(adStaffFacilityRepository.findAll(), HttpStatus.OK,"Lấy dữ liệu cơ sở thành công");
    }

}