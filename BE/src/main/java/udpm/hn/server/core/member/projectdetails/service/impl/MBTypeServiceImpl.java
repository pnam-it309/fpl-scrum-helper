package udpm.hn.server.core.member.projectdetails.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import udpm.hn.server.core.common.base.ResponseObject;
import udpm.hn.server.core.member.projectdetails.repository.MBMeTypeRepository;
import udpm.hn.server.core.member.projectdetails.service.MBTypeService;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class MBTypeServiceImpl implements MBTypeService {

    private final MBMeTypeRepository mbMeTypeRepository;

    @Override
    public ResponseObject<?> getAllType() {
        return new ResponseObject<>(
                mbMeTypeRepository.getAllTypeTodoResponse(),
                HttpStatus.OK,
                "Lấy Thành công type");
    }

}
