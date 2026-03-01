package udpm.hn.server.core.admin.department.department.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import udpm.hn.server.core.admin.department.department.model.request.ADCreateOrUpdateDepartmentRequest;
import udpm.hn.server.core.admin.department.department.model.request.DepartmentSearchRequest;
import udpm.hn.server.core.admin.department.department.repository.ADDepartmentExtendRepository;
import udpm.hn.server.core.admin.department.department.service.DepartmentService;
import udpm.hn.server.core.common.base.PageableObject;
import udpm.hn.server.core.common.base.ResponseObject;
import udpm.hn.server.entity.Department;
import udpm.hn.server.entity.Major;
import udpm.hn.server.infrastructure.constant.EntityStatus;
import udpm.hn.server.utils.Helper;

import java.util.Optional;

@Service
@Validated
@RequiredArgsConstructor
public class DepartmentServiceImpl implements DepartmentService {

    private final ADDepartmentExtendRepository ADDepartmentExtendRepository;

    @Override
    public ResponseObject<?> getAllDepartment(DepartmentSearchRequest request) {
        Pageable pageable = Helper.createPageable(request, "createdDate");
        return new ResponseObject<>(
                PageableObject.of(ADDepartmentExtendRepository.getAllDepartmentByFilter(pageable, request)),
                HttpStatus.OK,
                "Lấy danh sách bộ môn thành công"
        );
    }

    @Override
    public ResponseObject<?> addDepartment(ADCreateOrUpdateDepartmentRequest request) {
        String code = Helper.generateCodeFromName((request.getDepartmentCode().trim()));
        request.setDepartmentCode(code);
        request.setDepartmentName(request.getDepartmentName().replaceAll("\\s+", " "));

        boolean checkExistName = ADDepartmentExtendRepository.existsByName(request.getDepartmentName().trim());
        boolean checkExistCode = ADDepartmentExtendRepository.existsByCode(code);

        if (checkExistName){
            return new ResponseObject<>(null,HttpStatus.NOT_ACCEPTABLE, "Tên bộ môn đã tồn tại");
        }

        if (checkExistCode){
            return new ResponseObject<>(null,HttpStatus.NOT_ACCEPTABLE, "Mã bộ môn đã tồn tại");
        }

        Department department = new Department();
        department.setStatus(EntityStatus.ACTIVE);
        department.setCode(code);
        department.setName(request.getDepartmentName().trim());
        this.ADDepartmentExtendRepository.save(department);
        return new ResponseObject<>(null, HttpStatus.OK,"Thêm bộ môn thành công");
    }

    @Override
    public ResponseObject<?> updateDepartment(ADCreateOrUpdateDepartmentRequest request, String id) {
        String code = Helper.generateCodeFromName((request.getDepartmentCode().trim()));
        request.setDepartmentName(request.getDepartmentName().replaceAll("\\s+", " "));
        request.setDepartmentCode(code);

        Optional<Department> departmentOptional = ADDepartmentExtendRepository.findById(id);

        if (departmentOptional.isEmpty()) {
            return new ResponseObject<>(null, HttpStatus.NOT_ACCEPTABLE, "Bộ môn không tồn tại");
        }

        Department updateDepartment = departmentOptional.get();
        if (!updateDepartment.getCode().trim().equalsIgnoreCase(code)) {
            if (ADDepartmentExtendRepository.existsByCode(code)) {
                return new ResponseObject<>(null, HttpStatus.NOT_ACCEPTABLE, "Mã bộ môn đã tồn tại: " +
                        code);
            }
        }
        if (!updateDepartment.getName().trim().equalsIgnoreCase(request.getDepartmentName().trim())) {
            if (ADDepartmentExtendRepository.existsByName(request.getDepartmentName().trim())) {
                return new ResponseObject<>(null, HttpStatus.NOT_ACCEPTABLE, "Tên bộ môn đã tồn tại: " +
                        request.getDepartmentName().trim());
            }
        }

        updateDepartment.setCode(code);
        updateDepartment.setName(request.getDepartmentName().trim());
        this.ADDepartmentExtendRepository.save(updateDepartment);
        return new ResponseObject<>(null, HttpStatus.OK, "Cập nhật bộ môn thành công");
    }

    @Override
    public ResponseObject<?> detailDepartment(String id) {
        return new ResponseObject<>(
                ADDepartmentExtendRepository.getDetailDepartment(id),
                HttpStatus.OK,
                "Lấy thành công chi tiết bộ môn"
        );
    }

    @Override
    public ResponseObject<?> changeStatus(String id) {
        Optional<Department> departmentOptional = ADDepartmentExtendRepository.findById(id);

        if (departmentOptional.isPresent()) {
            Department department = departmentOptional.get();

            department.setStatus(
                    department.getStatus().name().equalsIgnoreCase(EntityStatus.ACTIVE.name())
                            ? EntityStatus.INACTIVE : EntityStatus.ACTIVE
            );
            ADDepartmentExtendRepository.save(department);
            return new ResponseObject<>(null, HttpStatus.OK, "Chuyển đổi thành công chuyên ngành " +
                    department.getName());
        } else {
            return new ResponseObject<>(null, HttpStatus.OK, "chuyên ngành không tồn tại trong bộ môn");
        }
    }

}
