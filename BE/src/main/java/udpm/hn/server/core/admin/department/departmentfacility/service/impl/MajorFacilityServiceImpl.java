package udpm.hn.server.core.admin.department.departmentfacility.service.impl;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import udpm.hn.server.core.admin.department.departmentfacility.model.request.CreateMajorFacilityRequest;
import udpm.hn.server.core.admin.department.departmentfacility.model.request.MajorFacilityRequest;
import udpm.hn.server.core.admin.department.departmentfacility.repository.DFDepartmentFacilityExtendRepository;
import udpm.hn.server.core.admin.department.departmentfacility.repository.DFMajorExtendRepository;
import udpm.hn.server.core.admin.department.departmentfacility.repository.MajorFacilityExtendRepository;
import udpm.hn.server.core.admin.department.departmentfacility.repository.ModifyMajorFacilityResponse;
import udpm.hn.server.core.admin.department.departmentfacility.service.MajorFacilityService;
import udpm.hn.server.core.common.base.PageableObject;
import udpm.hn.server.core.common.base.ResponseObject;
import udpm.hn.server.entity.DepartmentFacility;
import udpm.hn.server.entity.Major;
import udpm.hn.server.entity.MajorFacility;
import udpm.hn.server.infrastructure.constant.EntityStatus;
import udpm.hn.server.utils.Helper;

import java.util.Optional;

@Service
@Validated
@RequiredArgsConstructor
public class MajorFacilityServiceImpl implements MajorFacilityService {

    private final MajorFacilityExtendRepository majorFacilityExtendRepository;

    private final DFDepartmentFacilityExtendRepository departmentFacilityRepository;

    private final DFMajorExtendRepository majorRepository;

    @Override
    public ResponseObject<?> getAllMajorFacilities(MajorFacilityRequest request) {

        Pageable pageable = Helper.createPageable(request, "createdDate");
        return new ResponseObject<>(
                PageableObject.of(majorFacilityExtendRepository.findAllMajorFacilities(request, pageable)),
                HttpStatus.OK,
                "Lấy danh sách chuyên ngành theo cơ sở thành công"
        );
    }

    @Override
    public ResponseObject<?> getMajorFacilityById(String majorFacilityId) {
        return ResponseObject.successForward(
                majorFacilityExtendRepository.findMajorFacilityById(majorFacilityId),
                "Lấy thông tin chuyên ngành theo cơ sở thành công"
        );
    }

    @Override
    public ResponseObject<?> createMajorFacility(@Valid CreateMajorFacilityRequest request) {
        Optional<DepartmentFacility> departmentFacilityOptional = departmentFacilityRepository
                .findById(request.getDepartmentFacilityId());
        if (departmentFacilityOptional.isEmpty()) {
            return ResponseObject.errorForward(
                    "Không tìm thấy bộ môn theo cơ sở",
                    HttpStatus.BAD_REQUEST
            );
        }

        Optional<Major> majorOptional = majorRepository.findById(request.getMajorId());
        if (majorOptional.isEmpty()) {
            return ResponseObject.errorForward(
                    "Không tìm thấy chuyên ngành",
                    HttpStatus.BAD_REQUEST
            );
        }

        Optional<MajorFacility> majorFacilityOptional = majorFacilityExtendRepository
                .findByMajor_IdAndDepartmentFacility_Id(
                        request.getMajorId(),
                        request.getDepartmentFacilityId()
                );
        if (majorFacilityOptional.isPresent()) {
            return ResponseObject.errorForward(
                    "Chuyên ngành theo cơ sở đã tồn tại",
                    HttpStatus.BAD_REQUEST
            );
        }

        MajorFacility majorFacility = new MajorFacility();
        majorFacility.setMajor(majorOptional.get());
        majorFacility.setDepartmentFacility(departmentFacilityOptional.get());
        majorFacility.setStatus(EntityStatus.ACTIVE);
        majorFacilityExtendRepository.save(majorFacility);

        return ResponseObject.successForward(
                new ModifyMajorFacilityResponse(
                        majorFacility.getMajor().getName(),
                        majorFacility.getDepartmentFacility().getFacility().getName(),
                        majorFacility.getDepartmentFacility().getDepartment().getName()
                ),
                "Tạo chuyên ngành theo cơ sở thành công"
        );
    }

    @Override
    public ResponseObject<?> updateMajorFacility(String majorFacilityId,@Valid CreateMajorFacilityRequest request) {
        Optional<MajorFacility> majorFacilityOptional = majorFacilityExtendRepository.findById(majorFacilityId);
        if (majorFacilityOptional.isEmpty()) {
            return ResponseObject.errorForward(
                    "Không tìm thấy chuyên ngành theo cơ sở",
                    HttpStatus.NOT_FOUND
            );
        }

        Optional<Major> majorOptional = majorRepository.findById(request.getMajorId());
        if (majorOptional.isEmpty()) {
            return ResponseObject.errorForward(
                    "Không tìm thấy chuyên ngành",
                    HttpStatus.BAD_REQUEST
            );
        }

        Optional<MajorFacility> isDuplicateMajor = majorFacilityExtendRepository.findMajorFacilitiesByMajorId(
                request.getMajorId()
        );
        if (isDuplicateMajor.isPresent()) {
            return ResponseObject.errorForward(
                    "Đã tồn tại chuyên ngành " + isDuplicateMajor.get().getMajor().getName() + " tại cơ sở này",
                    HttpStatus.BAD_REQUEST
            );
        }

        MajorFacility majorFacility = majorFacilityOptional.get();
        majorFacility.setMajor(majorOptional.get());
        majorFacilityExtendRepository.save(majorFacility);

        return ResponseObject.successForward(
                new ModifyMajorFacilityResponse(
                        majorFacility.getMajor().getName(),
                        majorFacility.getDepartmentFacility().getFacility().getName(),
                        majorFacility.getDepartmentFacility().getDepartment().getName()
                ),
                "Cập nhật chuyên ngành theo cơ sở thành công"
        );
    }

    @Override
    public ResponseObject<?> getAllMajors(String departmentId) {
        return ResponseObject.successForward(
                majorRepository.findAllByDepartmentId(departmentId),
                "Lấy danh sách chuyên ngành thành công"
        );
    }

    @Override
    public ResponseObject<?> changeStatus(String id) {
        Optional<MajorFacility> majorFacilityOptional = majorFacilityExtendRepository.findById(id);

        if (majorFacilityOptional.isPresent()) {
            MajorFacility majorFacility = majorFacilityOptional.get();

            majorFacility.setStatus(
                    majorFacility.getStatus().name().equalsIgnoreCase(EntityStatus.ACTIVE.name())
                            ? EntityStatus.INACTIVE : EntityStatus.ACTIVE
            );
            majorFacilityExtendRepository.save(majorFacility);
            return new ResponseObject<>(null, HttpStatus.OK, "Chuyển đổi thành công chuyen nganh co so ");
        } else {
            return new ResponseObject<>(null, HttpStatus.OK, "Chuyen nganh co so không tồn tại");
        }
    }
}
