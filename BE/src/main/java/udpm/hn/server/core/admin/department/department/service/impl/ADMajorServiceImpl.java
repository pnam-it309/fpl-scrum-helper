package udpm.hn.server.core.admin.department.department.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import udpm.hn.server.core.admin.department.department.model.request.ADCreateOrUpdateMajorRequest;
import udpm.hn.server.core.admin.department.department.model.request.ADMajorRequest;
import udpm.hn.server.core.admin.department.department.repository.ADMajorExtendRepository;
import udpm.hn.server.core.admin.department.department.repository.ADDepartmentExtendRepository;
import udpm.hn.server.core.admin.department.department.service.ADMajorService;
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
public class ADMajorServiceImpl implements ADMajorService {

    private final ADMajorExtendRepository adMajorExtendRepository;

    private final ADDepartmentExtendRepository ADDepartmentExtendRepository;

    @Override
    public ResponseObject<?> getAllMajor(String id, ADMajorRequest request) {
        Pageable pageable = Helper.createPageable(request, "createdDate");
        return new ResponseObject<>(
                PageableObject.of(adMajorExtendRepository.getAllMajorByDepartmentIdFilter(id, pageable, request)),
                HttpStatus.OK,
                "Lấy thành công danh sách chuyên ngành"
        );
    }

    @Override
    public ResponseObject<?> addMajor(ADCreateOrUpdateMajorRequest request) {
        String code = Helper.generateCodeFromName((request.getMajorCode().trim()));
        request.setMajorCode(code);
        request.setMajorName(request.getMajorName().replaceAll("\\s+", " "));

        Optional<Major> existsMajor = adMajorExtendRepository.findMajorByNameAndDepartmentId(request.getMajorName().trim(), request.getDepartmentId());
        Optional<Major> existsCode = adMajorExtendRepository.findMajorByCodeAndDepartmentId(request.getMajorCode().trim(), request.getDepartmentId());

        Optional<Department> departmentOptional = ADDepartmentExtendRepository.findById(request.getDepartmentId());
        Department currentDepartment = departmentOptional.get();


        Optional<Major> existsMajorCode = adMajorExtendRepository
                .findMajorByCodeAndDepartmentId(request.getMajorCode(), request.getDepartmentId());

        if (existsMajorCode.isPresent()) {
            return new ResponseObject<>(null, HttpStatus.CONFLICT,
                    "Mã chuyên ngành đã tồn tại");
        }

        Optional<Major> existsMajorName = adMajorExtendRepository
                .findMajorByNameAndDepartmentId(request.getMajorName(), request.getDepartmentId());

        if (existsMajorName.isPresent()) {
            return new ResponseObject<>(null, HttpStatus.CONFLICT,
                    "Tên chuyên ngành đã tồn tại ");
        }

        if (departmentOptional.isPresent()) {

            if (existsMajor.isEmpty() || existsCode.isEmpty()) {
                Major addMajor = new Major();
                addMajor.setName(request.getMajorName().trim());
                addMajor.setCode(request.getMajorCode().trim());
                addMajor.setDepartment(currentDepartment);
                addMajor.setStatus(EntityStatus.ACTIVE);
                adMajorExtendRepository.save(addMajor);

                return new ResponseObject<>(null, HttpStatus.CREATED, "Thêm chuyên ngành vào bộ môn " +
                        currentDepartment.getName() + " thành công");
            } else {
                return new ResponseObject<>(null, HttpStatus.CONFLICT, "Chuyên ngành đã tồn tại trong bộ môn " +
                        currentDepartment.getName());
            }
        } else {
            return new ResponseObject<>(null, HttpStatus.NOT_ACCEPTABLE, "Bộ môn mà bạn đang thêm chuyên " +
                    "ngành không tồn tại [ " + departmentOptional.get().getName() + " ]");
        }
    }

    @Override
    public ResponseObject<?> updateMajor(ADCreateOrUpdateMajorRequest request, String id) {
        request.setMajorName(request.getMajorName().replaceAll("\\s+", " "));
        Optional<Major> majorOptional = adMajorExtendRepository.findById(id);
        Optional<Department> departmentOptional = ADDepartmentExtendRepository.findById(request.getDepartmentId());
        Department currentDepartment = departmentOptional.get();




        if (departmentOptional.isPresent()) {

            if (majorOptional.isPresent()) {
                Major majorUpdate = majorOptional.get();

                if (!majorUpdate.getCode().trim().equalsIgnoreCase(request.getMajorCode().trim())) {
                    Optional<Major> existsCode = adMajorExtendRepository
                            .findMajorByCodeAndDepartmentId(request.getMajorCode().trim(), request.getDepartmentId());

                    if (existsCode.isPresent()) {
                        return new ResponseObject<>(null, HttpStatus.NOT_ACCEPTABLE,
                                "Mã chuyên ngành đã tồn tại: " + request.getMajorCode().trim());
                    }
                }


                if (!majorUpdate.getName().trim().equalsIgnoreCase(request.getMajorName().trim())) {
                    Optional<Major> existsMajor = adMajorExtendRepository.findMajorByNameAndDepartmentId(request.getMajorName().trim(), request.getDepartmentId());
                    if(existsMajor.isPresent()){
                        return new ResponseObject<>(null, HttpStatus.NOT_ACCEPTABLE, "Tên chuyên ngành đã tồn tại: " +
                                request.getMajorName().trim());
                    }
                }

                System.out.println("Check code: " + request.getMajorCode() + " | departmentId: " + request.getDepartmentId());

                if (!majorUpdate.getCode().trim().equalsIgnoreCase(request.getMajorCode().trim())) {
                    Optional<Major> existsMajorCode = adMajorExtendRepository
                            .findMajorByCodeAndDepartmentId(request.getMajorCode().trim(), request.getDepartmentId());

                    if (existsMajorCode.isPresent()) {
                        return new ResponseObject<>(null, HttpStatus.NOT_ACCEPTABLE,
                                "Mã chuyên ngành đã tồn tại: " + request.getMajorCode().trim());
                    }
                }

                majorUpdate.setCode(request.getMajorCode().trim());
                majorUpdate.setName(request.getMajorName().trim());
                majorUpdate.setDepartment(currentDepartment);
                adMajorExtendRepository.save(majorUpdate);

                return new ResponseObject<>(null, HttpStatus.OK, "Cập nhât chuyên ngành vào bộ môn " +
                        currentDepartment.getName() + " thành công");
            } else {
                return new ResponseObject<>(null, HttpStatus.NOT_ACCEPTABLE, "Chuyên ngành không tồn tại trong bộ môn " +
                        currentDepartment.getName());
            }
        } else {
            return new ResponseObject<>(null, HttpStatus.NOT_ACCEPTABLE, "Bộ môn mà bạn đang cập nhật chuyên ngành " +
                    "không tồn tại [ " + departmentOptional.get().getName() + " ]");
        }
    }

    @Override
    public ResponseObject<?> deleteMajor(String id) {
        Optional<Major> majorOptional = adMajorExtendRepository.findById(id);

        if (majorOptional.isPresent()) {
            Major majorDelete = majorOptional.get();

            majorDelete.setStatus(
                    majorDelete.getStatus().name().equalsIgnoreCase(EntityStatus.ACTIVE.name())
                            ? EntityStatus.INACTIVE : EntityStatus.ACTIVE
            );
            adMajorExtendRepository.save(majorDelete);
            return new ResponseObject<>(null, HttpStatus.OK, "Chuyển đổi thành công chuyên ngành " +
                    majorDelete.getName());
        } else {
            return new ResponseObject<>(null, HttpStatus.OK, "chuyên ngành không tồn tại trong bộ môn");
        }
    }

    @Override
    public ResponseObject<?> detailMajor(String id) {
        return new ResponseObject<>(
                adMajorExtendRepository.getDetailMajor(id),
                HttpStatus.OK,
                "Lấy thành công chi tiết chuyên ngành"
        );
    }
}
