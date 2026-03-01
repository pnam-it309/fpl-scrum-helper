package udpm.hn.server.core.admin.staff.service.impl;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import udpm.hn.server.core.admin.staff.model.request.ADCreateStaffFDM;
import udpm.hn.server.core.admin.staff.model.request.ADCreateStaffRequest;
import udpm.hn.server.core.admin.staff.model.request.ADStaffProjection;
import udpm.hn.server.core.admin.staff.model.request.ADStaffRequest;
import udpm.hn.server.core.admin.staff.model.request.ADUpdateStaffFDMRequest;
import udpm.hn.server.core.admin.staff.model.request.RoleStaffRequest;
import udpm.hn.server.core.admin.staff.model.response.ADStaffByDepartmentFacility;
import udpm.hn.server.core.admin.staff.model.response.StaffResponse;
import udpm.hn.server.core.admin.staff.model.response.StaffRoleResponse;
import udpm.hn.server.core.admin.staff.repository.ADStaffByDeparmentFacilityRepository;
import udpm.hn.server.core.admin.staff.repository.ADStaffDepartmentFacilityRepository;
import udpm.hn.server.core.admin.staff.repository.ADStaffDepartmentRepository;
import udpm.hn.server.core.admin.staff.repository.ADStaffMajorFacilityRepository;
import udpm.hn.server.core.admin.staff.repository.ADStaffMajorRepository;
import udpm.hn.server.core.admin.staff.repository.ADStaffRepository;
import udpm.hn.server.core.admin.staff.repository.ADStaffRoleRepository;
import udpm.hn.server.core.admin.staff.service.ADStaffService;
import udpm.hn.server.core.common.base.PageableObject;
import udpm.hn.server.core.common.base.ResponseObject;
import udpm.hn.server.entity.*;
import udpm.hn.server.infrastructure.constant.EntityStatus;
import udpm.hn.server.infrastructure.constant.LogFileType;
import udpm.hn.server.infrastructure.job.staff.model.dto.HistoryImportResponse;
import udpm.hn.server.infrastructure.job.student.model.response.HistoryImportStudentResponse;
import udpm.hn.server.repository.*;
import udpm.hn.server.utils.Helper;
import udpm.hn.server.utils.HistoryLogUtils;
import udpm.hn.server.utils.UserContextHelper;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Slf4j
public class ADStaffServiceImpl implements ADStaffService {

    private final ActivityLogRepository activityLogRepository;

    private final ADStaffRepository adStaffRepository;

    private final StaffRepository staffRepository;

    private final ADStaffByDeparmentFacilityRepository adStaffByDeparmentFacilityRepository;

    private final ADStaffDepartmentRepository adStaffDepartmentRepository;

    private final ADStaffMajorRepository adStaffMajorRepository;

    private final ADStaffDepartmentFacilityRepository departmentFacilityRepository;

    private final ADStaffMajorFacilityRepository adStaffMajorFacilityRepository;

    private final RoleRepository roleRepository;

    private final ADStaffRoleRepository adStaffRoleRepository;

    private final StaffRoleRepository staffRoleRepository;

    private final StaffMajorFacilityRepository staffMajorFacilityRepository;

    private final HistoryLogUtils historyLogUtils;

    private final UserContextHelper userContextHelper;

    private final HistoryImportRepository historyImportRepository;

    private static final String DIRECTORY = "src/main/resources/log-accountability-index/";

    @Override
    public ResponseObject<?> getAllStaff(ADStaffRequest request) {
        Pageable page = Helper.createPageable(request, "createDate");
        EntityStatus status = null;
        if (request.getSearchStatus() != null) {
            status = EntityStatus.values()[Integer.valueOf(request.getSearchStatus())];
        }
//        Page<StaffResponse> projections = adStaffRepository.getAllStaff(page, request, status);
//        log.info("List staff:{}",projections.toString());

        Page<String> staffIdsPage = adStaffRepository.findStaffIds(page, request, status);
        List<String> staffIds = staffIdsPage.getContent();

        if (staffIds.isEmpty()) {
            return new ResponseObject<>(
                    PageableObject.of(new PageImpl<>(Collections.emptyList(), page, 0)),
                    HttpStatus.OK,
                    "Lấy dữ liệu thành công"
            );
        }

        List<StaffResponse> staffDetails = adStaffRepository.findStaffDetailsByIds(staffIds);

        Map<String, ADStaffProjection> staffMap = new LinkedHashMap<>();
        staffDetails.forEach(projection -> {
            staffMap.computeIfAbsent(projection.getId(), id -> new ADStaffProjection(projection))
                    .addRole(projection.getNameRole());
        });

        List<ADStaffProjection> staffList = new ArrayList<>(staffMap.values());

        Page<ADStaffProjection> pageResult = new PageImpl<>(staffList, page, staffIdsPage.getTotalElements());

        return new ResponseObject<>(
                PageableObject.of(pageResult),
                HttpStatus.OK,
                "Lấy dữ liệu thành công"
        );
    }



    @Override
    public ResponseObject<?> getAllRole() {
        return new ResponseObject<>(adStaffRoleRepository.getAllRole(), HttpStatus.OK, "Lâý dữ liệu thành công");
    }


    @Override
    public ResponseObject<?> getMajorByDepartment(String idDepartment) {
        return new ResponseObject<>(adStaffMajorRepository.getMajorByDepartment(idDepartment), HttpStatus.OK, "Lấy dữ liệu thành công");

    }

    @Override
    public ResponseObject<?> getAllDepartmentByFacility(String idFacility) {
        return new ResponseObject<>(adStaffDepartmentRepository.getAllDepartmentByFacility(idFacility), HttpStatus.OK, "Lấy dữ liệu bộ môn theo cơ sở thành công");

    }

    @Override
    public ResponseObject<?> createStaffByFDM(ADCreateStaffFDM request) {

        Optional<Facility> optionalFacility = adStaffByDeparmentFacilityRepository.findById(request.getIdFacility());
        if (optionalFacility.isEmpty()) {
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Không tìm thấy cơ sở này");
        }

        Optional<Department> optionalDepartment = adStaffDepartmentRepository.findById(request.getIdDepartment());
        if (optionalDepartment.isEmpty()) {
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Không tìm thấy bộ môn này");
        }

        Optional<Major> optionalMajor = adStaffMajorRepository.findById(request.getIdMajor());
        if (optionalMajor.isEmpty()) {
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Không tìm thấy chuyên ngành này");
        }

        Optional<DepartmentFacility> optionalDepartmentFacility = departmentFacilityRepository.findByDepartmentAndFacility(optionalDepartment.get(), optionalFacility.get());
        if (optionalDepartmentFacility.isEmpty()) {
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Không tìm thấy bộ môn cơ sở này ");
        }

        Optional<MajorFacility> optionalMajorFacility = adStaffMajorFacilityRepository.findByMajorAndDepartmentFacility(optionalMajor.get(), optionalDepartmentFacility.get());
        if (optionalMajorFacility.isEmpty()) {
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Không tìm thấy chuyên nghành cơ sở này ");
        }

        Optional<Staff> optionalStaff = adStaffRepository.findById(request.getIdStaffDetail());
        if (optionalStaff.isEmpty()) {
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Không tìm thấy nhân viên này ");
        }
        // update role
        Set<String> setCodeRole = new HashSet<>();
        List<StaffRole> staffRoles = adStaffRoleRepository.getStaffRoleByIdStaff(request.getIdStaffDetail());
        Map<String, Role> roles = adStaffRoleRepository.getRoleByFacility(request.getIdFacility())
                .stream().collect(Collectors.toMap(
                        key -> key.getCode(),
                        value -> value
                ));
        for (StaffRole staffRole : staffRoles) {
            if (!setCodeRole.contains(staffRole.getRole().getCode())) {
                staffRole.setRole(roles.get(staffRole.getRole().getCode()));
                staffRoleRepository.save(staffRole);
                setCodeRole.add(staffRole.getRole().getCode());
            }
        }

        StaffMajorFacility staffMajorFacility = StaffMajorFacility.builder().majorFacility(optionalMajorFacility.get()).staff(optionalStaff.get()).build();

        staffMajorFacilityRepository.save(staffMajorFacility);

        ActivityLog activityLog = new ActivityLog();
        activityLog.setRole("ADMIN");
        activityLog.setExecutorEmail(request.getEmailLogin());
        activityLog.setContent("Đã thêm Chuyên ngành - Bộ môn - Cơ sở :"+optionalMajor.get().getName()+
                " - "+optionalDepartment.get().getName()+" - "+
                optionalFacility.get().getName()+". Cho nhân viên có email FPT: "+optionalStaff.get().getEmailFpt());
        activityLogRepository.save(activityLog);

        return new ResponseObject<>(null, HttpStatus.OK, "Thêm thành công chuyên ngành bộ môn cơ sở");

    }

    @Override
    public ResponseObject<?> updateStaffByFDM(ADUpdateStaffFDMRequest request) {

        Optional<DepartmentFacility> optionalDepartmentFacility = departmentFacilityRepository.findByDepartmentAndFacilityAndStaff(request.getIdDepartment(), request.getIdFacility());
        if (optionalDepartmentFacility.isEmpty()) {
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Không tìm thấy bộ môn cơ sở  này  ");
        }

        Optional<Major> optionalFindMajor = adStaffMajorRepository.findById(request.getIdMajor());
        if (optionalFindMajor.isEmpty()) {
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Không tìm thấy chuyên ngành này");
        }

        Optional<MajorFacility> optionalMajorFacility = adStaffMajorFacilityRepository.findByMajorAndDepartmentFacility(optionalFindMajor.get(), optionalDepartmentFacility.get());
        if (optionalMajorFacility.isEmpty()) {
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Không tìm thấy chuyên nghành cơ sở này ");
        }

        Optional<Staff> optionalStaff = adStaffRepository.findById(request.getIdStaffDetail());
        if (optionalStaff.isEmpty()) {
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Không tìm thấy thành viên này");
        }

        Optional<StaffMajorFacility> optionalStaffMajorFacility = staffMajorFacilityRepository.findByStaffAndMajorFacility(optionalStaff.get(), optionalMajorFacility.get());

        Optional<DepartmentFacility> optionalDepartmentFacilityUpdate = departmentFacilityRepository.findByDepartmentAndFacilityAndStaff(request.getIdUpdateDepartment(), request.getIdUpdateFacility());
        if (optionalDepartmentFacilityUpdate.isEmpty()) {
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Không tìm thấy bộ môn cơ sở này update ");
        }

        Optional<Major> optionalMajorUpdate = adStaffMajorRepository.findById(request.getIdUpdateMajor());
        if (optionalMajorUpdate.isEmpty()) {
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Không tìm thấy chuyên ngành này update");
        }

        Optional<MajorFacility> optionalMajorFacilityUpdate = adStaffMajorFacilityRepository.findByMajorAndDepartmentFacility(optionalMajorUpdate.get(), optionalDepartmentFacilityUpdate.get());
        if (optionalMajorFacilityUpdate.isEmpty()) {
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Không tìm thấy chuyên ngành cơ sở này update");
        }

        // update role
        Set<String> setCodeRole = new HashSet<>();
        List<StaffRole> staffRoles = adStaffRoleRepository.getStaffRoleByIdStaff(request.getIdStaffDetail());
        Map<String, Role> roles = adStaffRoleRepository.getRoleByFacility(request.getIdUpdateFacility())
                .stream().collect(Collectors.toMap(
                        key -> key.getCode(),
                        value -> value
                ));

        for (StaffRole staffRole : staffRoles) {
            if (!setCodeRole.contains(staffRole.getRole().getCode())) {
                staffRole.setRole(roles.get(staffRole.getRole().getCode()));
                staffRoleRepository.save(staffRole);
                setCodeRole.add(staffRole.getRole().getCode());
            }
        }

        StaffMajorFacility staffMajorFacility = optionalStaffMajorFacility.get();
        staffMajorFacility.setMajorFacility(optionalMajorFacilityUpdate.get());

        staffMajorFacilityRepository.save(staffMajorFacility);


        ActivityLog activityLog = new ActivityLog();
        activityLog.setRole("ADMIN");
        activityLog.setExecutorEmail(request.getEmailLogin());
        activityLog.setContent("Đã sửa Chuyên ngành - Bộ môn - Cơ sở thành :"+optionalFindMajor.get().getName()+
                " - "+optionalDepartmentFacility.get().getDepartment().getName()+" - "+
                optionalMajorFacility.get().getMajor().getName()+". Cho nhân viên có email FPT: "+optionalStaff.get().getEmailFpt());
        activityLogRepository.save(activityLog);

        return new ResponseObject<>(null, HttpStatus.OK, "Update thành công");

    }

    @Override
    public ResponseObject<?> deleteStaffByFDM(String id,String emailLogin) {

        Optional<StaffMajorFacility> optionalStaffMajorFacility = staffMajorFacilityRepository.findById(id);
        if (optionalStaffMajorFacility.isEmpty()) {
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Không tìm thấy thành viên cơ sở này");
        }

        Staff exsitStaff = optionalStaffMajorFacility.get().getStaff();

        Set<String> setCodeRole = new HashSet<>();
        List<StaffRole> staffRoles = adStaffRoleRepository.getStaffRoleByIdStaff(exsitStaff.getId());
        Map<String, Role> roles = adStaffRoleRepository.getRoleDefault()
                .stream().collect(Collectors.toMap(
                        key -> key.getCode(),
                        value -> value
                ));
        for (StaffRole staffRole : staffRoles) {
            if (!setCodeRole.contains(staffRole.getRole().getCode())) {
                staffRole.setRole(roles.get(staffRole.getRole().getCode()));
                staffRoleRepository.save(staffRole);
                setCodeRole.add(staffRole.getRole().getCode());
            }
        }

        staffMajorFacilityRepository.delete(optionalStaffMajorFacility.get());

        ActivityLog activityLog = new ActivityLog();
        activityLog.setRole("ADMIN");
        activityLog.setExecutorEmail(emailLogin);
        activityLog.setContent("Đã xóa nhân viên có email: FPT"+optionalStaffMajorFacility.get().getStaff().getEmailFpt());
        activityLogRepository.save(activityLog);

        return new ResponseObject<>(null, HttpStatus.OK, "Xóa thành viên khỏi chuyên ngành cơ sở thành cồng");
    }

    @Override
    public ResponseObject<?> getRoleByStaff(String idStaff) {
        List<StaffRoleResponse> staffRoleResponses = adStaffRoleRepository.getRoleByStaff(idStaff);
        Optional<StaffRole> theLast = adStaffRoleRepository.getLastStaffRoleAdmin().stream().findFirst();

        if (theLast.isPresent()) {
            StaffRole theLastAdmin = theLast.get();
            for (StaffRoleResponse staffRoleResponse : staffRoleResponses) {
                if (theLastAdmin.getId().equals(staffRoleResponse.getId())) {
                    staffRoleResponses.remove(staffRoleResponse);
                    break;
                }
            }
        }

        return new ResponseObject<>(staffRoleResponses, HttpStatus.OK, "Lấy dữ liệu thành công");
    }

    @Override
    public ResponseObject<?> getStaffRoleByStaff(String idStaff) {
        return new ResponseObject<>(adStaffRoleRepository.getStaffRoleByStaff(idStaff), HttpStatus.OK, "Lấy dữ liệu thành công");
    }

    @Override
    public ResponseObject<?> createUpdateRoleByStaff(RoleStaffRequest request) {

        Optional<Staff> optionalStaff = adStaffRepository.findById(request.getIdStaff());
        if (optionalStaff.isEmpty()) {
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Không tìm thấy nhân viên này");
        }

        StaffRole newStaffRole = new StaffRole();
        newStaffRole.setStaff(optionalStaff.get());
        Optional<ADStaffByDepartmentFacility> optionalAdStaffByDepartmentFacility = adStaffByDeparmentFacilityRepository.staffByDepartmentFacility(request.getIdStaff());
        if (optionalAdStaffByDepartmentFacility.isPresent()) {

            List<Role> roles = adStaffRoleRepository.getRoleByFacility(optionalAdStaffByDepartmentFacility.get().getIdFacility());

            for (Role role : roles) {
                if (role.getCode().equals(request.getCodeRole())) {
                    newStaffRole.setRole(role);
                    break;
                }
            }
        } else {
            Optional<Role> optionalRole = adStaffRoleRepository.findRoleDefaultByCode(request.getCodeRole());

            newStaffRole.setStaff(optionalStaff.get());
            newStaffRole.setRole(optionalRole.get());
        }

        staffRoleRepository.save(newStaffRole);

        ActivityLog activityLog = new ActivityLog();
        activityLog.setRole("ADMIN");
        activityLog.setExecutorEmail(request.getEmailLogin());
        activityLog.setContent("Đã cấp quyền : " +request.getCodeRole()+" cho nhân viên có email: "+optionalStaff.get().getEmailFpt());
        activityLogRepository.save(activityLog);

        return new ResponseObject<>(null, HttpStatus.OK, "Thêm quyền cho nhân viên thành công");

    }

    @Override
    public ResponseObject<?> deleteRoleByStaff(RoleStaffRequest request) {

        Optional<StaffRole> optionalStaffRole = adStaffRoleRepository.findTheLastByRoleIdAndStaffId(request.getIdRole(), request.getIdStaff())
                .stream().findFirst();
        if (optionalStaffRole.isEmpty()) {
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Không tìm thấy role");
        }

        staffRoleRepository.delete(optionalStaffRole.get());

        ActivityLog activityLog = new ActivityLog();
        activityLog.setRole("ADMIN");
        activityLog.setExecutorEmail(request.getEmailLogin());
        activityLog.setContent("Đã hủy quyền : " +request.getCodeRole()+" với nhân viên có email: "+optionalStaffRole.get().getStaff().getEmailFpt());
        activityLogRepository.save(activityLog);

        return new ResponseObject<>(null, HttpStatus.OK, "Xóa thành công");
    }

    @Override
    public ResponseObject<?> getAllStaffNoProject() {
        return new ResponseObject<>(adStaffRepository.getAllStaffNoProject(), HttpStatus.OK, "Lấy dữ liệu thành viên không trong dự án thành công");
    }

    @Override
    public ResponseObject<?> createStaff(ADCreateStaffRequest request) {

        if (adStaffRepository.existsByEmailFe(request.getEmailFe().trim())) {
            return new ResponseObject<>(null, HttpStatus.NOT_ACCEPTABLE, "EmailFe đã tồn tại");
        }

        if (adStaffRepository.existsByEmailFpt(request.getEmailFpt().trim())) {
            return new ResponseObject<>(null, HttpStatus.NOT_ACCEPTABLE, "EmailFPT đã tồn tại");
        }

        if (adStaffRepository.existsByCode(request.getCode().trim())) {
            return new ResponseObject<>(null, HttpStatus.NOT_ACCEPTABLE, "Mã đã tồn tại");
        }

        Staff staff = new Staff();
        staff.setCode(request.getCode().trim());
        staff.setName(request.getName().trim());
        staff.setEmailFe(request.getEmailFe().trim());
        staff.setEmailFpt(request.getEmailFpt().trim());
        staff.setStatus(EntityStatus.ACTIVE);

        adStaffRepository.save(staff);

        ActivityLog activityLog = new ActivityLog();
        activityLog.setRole("ADMIN");
        activityLog.setExecutorEmail(request.getEmailLogin());
        activityLog.setContent("Đã thêm nhân viên có email FPT : " +staff.getEmailFpt());
        activityLogRepository.save(activityLog);

        return new ResponseObject<>(null, HttpStatus.CREATED, "Thêm nhân viên thành công");
    }

    @Override
    public ResponseObject<?> updateStaff(String id, ADCreateStaffRequest request) {

        Optional<Staff> optionalStaff = adStaffRepository.findById(id);
        if (optionalStaff.isEmpty()) {
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Không tìm thấy nhân viên này");
        }

        Staff existingStaff = optionalStaff.get();

        if (!existingStaff.getEmailFe().equalsIgnoreCase(request.getEmailFe().trim()) &&
                adStaffRepository.existsByEmailFe(request.getEmailFe().trim())) {
            return new ResponseObject<>(null, HttpStatus.NOT_ACCEPTABLE, "EmailFe đã tồn tại");
        }

        if (!existingStaff.getEmailFpt().equalsIgnoreCase(request.getEmailFpt().trim()) &&
                adStaffRepository.existsByEmailFpt(request.getEmailFpt().trim())) {
            return new ResponseObject<>(null, HttpStatus.NOT_ACCEPTABLE, "EmailFPT đã tồn tại");
        }

        if (!existingStaff.getCode().equalsIgnoreCase(request.getCode().trim()) &&
                adStaffRepository.existsByCode(request.getCode().trim())) {
            return new ResponseObject<>(null, HttpStatus.NOT_ACCEPTABLE, "Mã đã tồn tại");
        }

        existingStaff.setCode(request.getCode().trim());
        existingStaff.setName(request.getName().trim());
        existingStaff.setEmailFe(request.getEmailFe().trim());
        existingStaff.setEmailFpt(request.getEmailFpt().trim());

        adStaffRepository.save(existingStaff);

        ActivityLog activityLog = new ActivityLog();
        activityLog.setRole("ADMIN");
        activityLog.setExecutorEmail(request.getEmailLogin());
        activityLog.setContent("Đã sửa nhân viên có email FPT : " +existingStaff.getEmailFpt());
        activityLogRepository.save(activityLog);

        return new ResponseObject<>(null, HttpStatus.OK, "Cập nhật thành công");
    }

    @Override
    public ResponseObject<?> deleteStaff(String id,String emailLogin) {

        Optional<Staff> optionalStaff = adStaffRepository.findById(id);

        if (optionalStaff.isEmpty()) {
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Không tìm thấy thành nhân viên này");
        }

        Staff staff = optionalStaff.get();
        staff.setStatus(staff.getStatus() != EntityStatus.ACTIVE ? EntityStatus.ACTIVE : EntityStatus.INACTIVE);
        adStaffRepository.save(staff);

        ActivityLog activityLog = new ActivityLog();
        activityLog.setRole("ADMIN");
        activityLog.setExecutorEmail(emailLogin);
        activityLog.setContent("Đã thay đổi trạng thái nhân viên có email FPT : " + optionalStaff.get().getEmailFpt().trim());
        activityLogRepository.save(activityLog);

        return new ResponseObject<>(null, HttpStatus.OK, "Cập nhật trạng thái nhân viên thành công");
    }

    @Override
    public ResponseObject<?> detailStaff(String id) {
        return adStaffRepository.findByIdStaff(id).map(staff -> new ResponseObject<>(staff, HttpStatus.OK, "Lấy thông nhân viên thành công")).orElseGet(() -> new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Không tìm thấy nhân viên có Id này"));
    }

    @Override
    public ResponseObject<?> staffByDepartmentFacility(String id) {

        return adStaffByDeparmentFacilityRepository.staffByDepartmentFacility(id).map(staff -> new ResponseObject<>(staff, HttpStatus.OK, "Lấy thông nhân viên thành công")).orElseGet(() -> new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Không tìm thấy nhân viên có Id này"));
    }

    @Override
    public ResponseObject<?> getLogsImportStaff(int page, int size) {
        if (page < 1) {
            page = 1;
        }

        String staffId = userContextHelper.getCurrentUserId();
        List<Role> roles = roleRepository.findRoleByStaff(staffId);
        String email = staffRepository.getEmailFpt(staffId);

        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("createdDate").descending());

        Page<HistoryImport> historyPage = historyImportRepository.findByEmail(email, pageable);

        Page<HistoryImportStudentResponse> responsePage = historyPage.map(history ->
                new HistoryImportStudentResponse(
                        history.getId(),
                        history.getEmail(),
                        history.getMessage(),
                        roles,
                        history.getCreatedDate()
                )
        );

        return ResponseObject.successForward(
                PageableObject.of(responsePage),
                "Lấy lịch sử import sinh viên thành công"
        );
    }


    @Override
    public Resource getAllCsv() throws IOException {
        File directory = new File(DIRECTORY);
        File[] csvFiles = directory.listFiles((dir, name) -> name.endsWith(".csv"));

        if (csvFiles == null || csvFiles.length == 0) {
            throw new FileNotFoundException("Không có file CSV nào để tải xuống.");
        }

        // Trả về file CSV đầu tiên (hoặc bạn có thể sửa logic để chọn file cụ thể)
        return new InputStreamResource(new FileInputStream(csvFiles[0]));
    }

    @Override
    public ResponseObject<?> getAllStaffCount() {
        try {
            Long count = adStaffRepository.countAllStaff();

            if (count == 0) {
                return new ResponseObject<>(count, HttpStatus.NO_CONTENT, "Không có nhân viên nào.");
            }

            return new ResponseObject<>(count, HttpStatus.OK, "Tổng số nhân viên: " + count);
        } catch (Exception e) {
            return new ResponseObject<>(null, HttpStatus.INTERNAL_SERVER_ERROR, "Đã xảy ra lỗi khi đếm nhân viên.");
        }
    }


}




