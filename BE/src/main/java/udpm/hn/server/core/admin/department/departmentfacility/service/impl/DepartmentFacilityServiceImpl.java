package udpm.hn.server.core.admin.department.departmentfacility.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import udpm.hn.server.core.admin.department.departmentfacility.model.request.CreateOrUpdateDepartmentFacilityRequest;
import udpm.hn.server.core.admin.department.departmentfacility.model.request.FindFacilityDetailRequest;
import udpm.hn.server.core.admin.department.departmentfacility.repository.DFDepartmentExtendReposiotry;
import udpm.hn.server.core.admin.department.departmentfacility.repository.DFDepartmentFacilityExtendRepository;
import udpm.hn.server.core.admin.department.departmentfacility.repository.DFFacilityExtendRepository;
import udpm.hn.server.core.admin.department.departmentfacility.service.DepartmentFacilityService;
import udpm.hn.server.core.common.base.PageableObject;
import udpm.hn.server.core.common.base.ResponseObject;
import udpm.hn.server.entity.Department;
import udpm.hn.server.entity.DepartmentFacility;
import udpm.hn.server.entity.Facility;
import udpm.hn.server.infrastructure.constant.EntityStatus;
import udpm.hn.server.utils.Helper;

import java.util.Optional;

@Service
@Validated
@RequiredArgsConstructor
public class DepartmentFacilityServiceImpl implements DepartmentFacilityService {

    private final DFFacilityExtendRepository facilityExtendRepository;

    private final DFDepartmentExtendReposiotry dfDepartmentExtendReposiotry;

    private  final DFDepartmentFacilityExtendRepository dfDepartmentFacilityExtendRepository;

    @Override
    public ResponseObject<?> getAllDepartmentFacility(String id, FindFacilityDetailRequest request) {
        Pageable pageable = Helper.createPageable(request, "createdDate");
        return new ResponseObject<>(
                PageableObject.of(dfDepartmentFacilityExtendRepository.getDepartmentFacilitiesByValueFind(id, pageable, request)),
                HttpStatus.OK,
                "Lấy thành công danh sách bộ môn theo cơ sở"
        );
    }

    @Override
    public ResponseObject<?> addDepartmentFacility(CreateOrUpdateDepartmentFacilityRequest request) {
        Optional<Department> departmentOptional = dfDepartmentExtendReposiotry.findById(request.getDepartmentId());
        Optional<Facility> facilityOptional = facilityExtendRepository.findById(request.getFacilityId());

        if (dfDepartmentFacilityExtendRepository.existsByIdDepartmentAndIdFacilityAndIdAdd(request).isPresent()) {
            return new ResponseObject<>(null, HttpStatus.NOT_ACCEPTABLE, "Bộ môn theo cơ sở đã tồn tại!");
        }

        if (facilityOptional.isPresent() && departmentOptional.isPresent()) {
            DepartmentFacility addDepartmentFacility = new DepartmentFacility();
            addDepartmentFacility.setDepartment(departmentOptional.get());
            addDepartmentFacility.setFacility(facilityOptional.get());
            addDepartmentFacility.setStatus(EntityStatus.ACTIVE);
            dfDepartmentFacilityExtendRepository.save(addDepartmentFacility);
            return new ResponseObject<>(null, HttpStatus.CREATED, "Thêm thành công");
        } else if (facilityOptional.isEmpty()) {
            return new ResponseObject<>(null, HttpStatus.NOT_ACCEPTABLE, "Không tìm thấy cơ sở trên");
        } else {
            return new ResponseObject<>(null, HttpStatus.NOT_ACCEPTABLE, "Không tìm thấy bộ môn trên");
        }
    }

    @Override
    public ResponseObject<?> updateDepartmentFacility(CreateOrUpdateDepartmentFacilityRequest request, String id) {
        Optional<DepartmentFacility> departmentFacilityOptional = dfDepartmentFacilityExtendRepository.findById(id);
        if (departmentFacilityOptional.isPresent()) {
            Optional<Department> departmentOptional = dfDepartmentExtendReposiotry.findById(request.getDepartmentId());
            Optional<Facility> facilityOptional = facilityExtendRepository.findById(request.getFacilityId());

            if (facilityOptional.isPresent() && departmentOptional.isPresent()) {
                DepartmentFacility updateDepartmentFacility = departmentFacilityOptional.get();
                if (updateDepartmentFacility.getFacility().equals(facilityOptional.get())) {
                    updateDepartmentFacility.setDepartment(departmentOptional.get());
                    updateDepartmentFacility.setFacility(facilityOptional.get());
                    dfDepartmentFacilityExtendRepository.save(updateDepartmentFacility);
                } else {
                    if (isDuplicateRecord(request).isPresent()) {
                        return new ResponseObject<>(null, HttpStatus.NOT_ACCEPTABLE, "Cập nhật trùng cơ sở hoặc để như cũ");
                    }
                    updateDepartmentFacility.setDepartment(departmentOptional.get());
                    updateDepartmentFacility.setFacility(facilityOptional.get());
                    dfDepartmentFacilityExtendRepository.save(updateDepartmentFacility);
                }

                return new ResponseObject<>(null, HttpStatus.OK, "Sửa thành công !");
            } else if (facilityOptional.isEmpty()) {
                return new ResponseObject<>(null, HttpStatus.NOT_ACCEPTABLE, "Không tìm thấy cơ sở trên");
            }else if (departmentOptional.isEmpty()) {
                return new ResponseObject<>(null, HttpStatus.NOT_ACCEPTABLE, "Không tìm thấy bộ môn trên");
            } else {
                return new ResponseObject<>(null, HttpStatus.NOT_ACCEPTABLE, "Sửa thất bại");
            }
        } else {
            return new ResponseObject<>(null, HttpStatus.NOT_ACCEPTABLE, "Bộ môn theo cơ sở trên không tồn tại");
        }
    }

    private Optional<DepartmentFacility> isDuplicateRecord(CreateOrUpdateDepartmentFacilityRequest request) {
        return dfDepartmentFacilityExtendRepository.existsDepartmentFacilitiesByDepartmentAndFacility(request.getDepartmentId(), request.getFacilityId());
    }

    @Override
    public ResponseObject<?> getListFacility() {
        return new ResponseObject<>(dfDepartmentFacilityExtendRepository.getListFacility(), HttpStatus.OK, "Lấy thành công danh sách cơ sở");
    }

    @Override
    public ResponseObject<?> getDepartmentName(String departmentId) {
        Optional<Department> departmentOptional = dfDepartmentExtendReposiotry.findById(departmentId);
        if (departmentOptional.isEmpty()) {
            return new ResponseObject<>(null, HttpStatus.NOT_ACCEPTABLE, "Tìm thấy bộ môn này!");
        }
        return new ResponseObject<>(departmentOptional.get().getName(), HttpStatus.OK, "Tìm thấy thành công tên bộ môn");

    }

    @Override
    public ResponseObject<?> detailDepartmentFacility(String id) {
        return new ResponseObject<>(
                dfDepartmentFacilityExtendRepository.getDetailDepartmentFacilityResponse(id),
                HttpStatus.OK,
                "Lấy thành công chi tiết bộ môn theo cơ sở"
        );
    }

    @Override
    public ResponseObject<?> changeStatus(String id) {
        Optional<DepartmentFacility> departmentFacilityOptional = dfDepartmentFacilityExtendRepository.findById(id);

        if (departmentFacilityOptional.isPresent()) {
            DepartmentFacility departmentFacility = departmentFacilityOptional.get();

            departmentFacility.setStatus(
                    departmentFacility.getStatus().name().equalsIgnoreCase(EntityStatus.ACTIVE.name())
                            ? EntityStatus.INACTIVE : EntityStatus.ACTIVE
            );
            dfDepartmentFacilityExtendRepository.save(departmentFacility);
            return new ResponseObject<>(null, HttpStatus.OK, "Chuyển đổi thành công bo mon co so ");
        } else {
            return new ResponseObject<>(null, HttpStatus.OK, "Bo mon co so không tồn tại");
        }
    }
}
