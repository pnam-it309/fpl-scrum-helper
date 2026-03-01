package udpm.hn.server.infrastructure.job.staff.commonio;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import udpm.hn.server.entity.*;
import udpm.hn.server.infrastructure.config.global.GlobalVariables;
import udpm.hn.server.infrastructure.constant.EntityStatus;
import udpm.hn.server.infrastructure.job.staff.model.dto.TransferStaffRole;
import udpm.hn.server.infrastructure.job.staff.model.request.StaffExcelRequest;
import udpm.hn.server.infrastructure.job.staff.repository.ConfigMajorFacilityCustomRepository;
import udpm.hn.server.infrastructure.job.staff.repository.ConfigStaffCustomRepository;
import udpm.hn.server.infrastructure.job.staff.service.impl.HistoryImportStaffService;
import udpm.hn.server.repository.*;
import udpm.hn.server.utils.ValidationUtil;

import java.util.*;

@Component
@Slf4j
public class StaffProcessor implements ItemProcessor<StaffExcelRequest, TransferStaffRole> {

    @Setter(onMethod_ = {@Autowired})
    private ConfigStaffCustomRepository staffCustomRepository;

    @Setter(onMethod_ = {@Autowired})
    private ConfigMajorFacilityCustomRepository majorFacilityCustomRepository;

    @Setter(onMethod_ = {@Autowired})
    private StaffMajorFacilityRepository staffMajorFacilityRepository;

    @Setter(onMethod_ = {@Autowired})
    private DepartmentRepository departmentRepository;

    @Setter(onMethod_ = {@Autowired})
    private FacilityRepository facilityRepository;

    @Setter(onMethod_ = {@Autowired})
    private MajorRepository majorRepository;

    @Setter(onMethod_ = {@Autowired})
    private ValidationUtil validationUtil;

    @Setter(onMethod_ = {@Autowired})
    private GlobalVariables globalVariables;

    @Setter(onMethod_ = {@Autowired})
    private HistoryImportStaffService historyImportService;

    @Setter(onMethod_ = {@Autowired})
    private CsvHistoryWriterStaff csvHistoryWriterStaff;

    private final Set<String> seenEmails = new HashSet<>();


    private void saveMessageToDatabase(String record, String message) {
        String fullMessage = record + " - " + message;
        log.info("BẮT ĐẦU LƯU VÀO DB: {}", fullMessage);
        try {
            historyImportService.saveHistory(fullMessage);
            log.info("LƯU VÀO DB THÀNH CÔNG: {}", fullMessage);
        } catch (Exception e) {
            log.error("LỖI LƯU VÀO DB: {}", fullMessage, e);
            throw e;
        }
    }

    void logFacility(String message) {
        String fullMessage = message;
        log.info("BẮT ĐẦU LƯU VÀO DB: {}", fullMessage);
        try {
            historyImportService.saveHistory(fullMessage);
            log.info("LƯU VÀO DB THÀNH CÔNG: {}", fullMessage);
        } catch (Exception e) {
            log.error("LỖI LƯU VÀO DB: {}", fullMessage, e);
            throw e; // Tạm thời ném ngoại lệ để debug
        }
    }

    void saveError(String record, String message) {

        log.info("BẮT ĐẦU saveError: record={}, message={}", record, message);
        try {
            saveMessageToDatabase(record, message);
            log.info("LƯU LỖI VÀO DB THÀNH CÔNG: record={}", record);
        } catch (Exception e) {
            log.error("LỖI KHI LƯU VÀO DB: record={}, message={}", record, message, e);
        }
        try {
            csvHistoryWriterStaff.writeHistory(record, message);
            log.info("GHI LỖI VÀO CSV THÀNH CÔNG: record={}", record);
        } catch (Exception e) {
            log.error("LỖI KHI GHI VÀO CSV: record={}, message={}", record, message, e);
        }
    }

    @Override
    public TransferStaffRole process(StaffExcelRequest item) throws Exception {
        log.info("BẮT ĐẦU XỬ LÝ STAFF: {}", item);
        if (item == null) {
            log.error("ITEM NULL, BỎ QUA BẢN GHI");
            return null;
        }

        if (item.getFacility() == null || item.getFacility().trim().isEmpty()) {
//            String staffCode = item.getStaffCode() != null ? item.getStaffCode() : "N/A";
//            String name = item.getName() != null ? item.getName() : "N/A";
            logFacility( "Cơ sở không được để trống..");
            return null;
        }



        if (item.getEmailFpt().isEmpty() || item.getEmailFe().isEmpty()) {
            saveError(item.getStaffCode() + " - " + item.getName(), "Email không được để trống");
            return null;
        }

        if (item.getName().isEmpty()) {
            saveError(item.getStaffCode() + " - " + item.getName(), "Họ tên nhân viên không được để trống");
            return null;
        }

        if (item.getStaffCode().isEmpty()) {
            saveError(item.getStaffCode() + " - " + item.getName(), "Mã nhân viên không được để trống");
            return null;
        }

        // [ADDED] Chuẩn hóa email và kiểm tra trùng lặp trong batch hiện tại
        String emailFpt = item.getEmailFpt().toLowerCase().trim();
        String emailFe = item.getEmailFe().toLowerCase().trim();

        if (seenEmails.contains(emailFpt) || seenEmails.contains(emailFe)) {
            saveError(item.getStaffCode() + " - " + emailFpt, "Email đã trùng trong file import");
            return null;
        }

        // [MODIFIED] Kiểm tra trùng email trong database
        Optional<Staff> existingEmail = staffCustomRepository.findByEmailFeOrEmailFpt(emailFpt, emailFe);
        if (existingEmail.isPresent()) {
            saveError(item.getStaffCode() + " - " + emailFpt, "Email đã tồn tại trong hệ thống");
            return null;
        }

        Optional<Staff> existingStaff = staffCustomRepository.findByCodeAndStatus(item.getStaffCode(), EntityStatus.ACTIVE);
        if (existingStaff.isPresent()) {
            saveError(item.getStaffCode() + " - " + item.getEmailFpt(), "Đã có nhân viên với mã này");
            return null;
        }

        if (item.getEmailFpt() == null || item.getEmailFpt().isBlank() ||
                item.getEmailFe() == null || item.getEmailFe().isBlank()) {
            saveError(item.getStaffCode() + " - " + item.getName(), "Email không được để trống");
            return null;
        }

        if (!validationUtil.isValidEmailExcel(item.getEmailFpt()) || !validationUtil.isValidEmailExcel(item.getEmailFe())) {
            saveError(item.getStaffCode() + " - " + item.getEmailFpt(), "Email không đúng định dạng");
            return null;
        }

        seenEmails.add(emailFpt);
        seenEmails.add(emailFe);

        Optional<Facility> optionalFacility = facilityRepository.findByName(item.getFacility());
        if (optionalFacility.isEmpty()) {
            saveError(item.getStaffCode() + " - " + item.getFacility(), "Không tìm thấy cơ sở này");
            return null;
        }

        if (item.getDepartment() == null || item.getDepartment().isBlank()) {
            saveError(item.getStaffCode(), "Bộ môn không được để trống");
            return null;
        }
        Optional<Department> optionalDepartment = departmentRepository.findByName(item.getDepartment());
        if (optionalDepartment.isEmpty()) {
            saveError(item.getStaffCode() + " - " + item.getDepartment(), "Không tìm thấy bộ môn này");
            return null;
        }

        if (item.getMajor() == null || item.getMajor().isBlank()) {
            saveError(item.getStaffCode(), "Chuyên ngành không được để trống");
            return null;
        }
        Optional<Major> optionalMajor = majorRepository.findByName(item.getMajor());
        if (optionalMajor.isEmpty()) {
            saveError(item.getStaffCode() + " - " + item.getMajor(), "Không tìm thấy chuyên ngành này");
            return null;
        }

        Optional<MajorFacility> majorFacility = fetchMajorFacility(item.getDepartment(), item.getMajor(), item.getFacility());
        if (majorFacility.isEmpty()) {
            saveError(item.getStaffCode() + " - " + item.getEmailFpt(), "Không tìm thấy chuyên ngành cơ sở này");
            return null;
        }

        try {
            Staff staff = new Staff();
            staff.setId(UUID.randomUUID().toString());
            staff.setCode(item.getStaffCode());
            staff.setName(item.getName());
            staff.setEmailFpt(item.getEmailFpt());
            staff.setEmailFe(item.getEmailFe());
            staff.setStatus(EntityStatus.ACTIVE);

            Optional<MajorFacility> majorFacilitys = fetchMajorFacility(item.getDepartment(), item.getMajor(), item.getFacility());
            if (majorFacilitys.isEmpty()) {
                saveError(item.getStaffCode(), "Không tìm thấy chuyên ngành cơ sở này");
                return null;
            }

            StaffMajorFacility staffMajorFacility = new StaffMajorFacility();
            staffMajorFacility.setStatus(EntityStatus.ACTIVE);
            staffMajorFacility.setMajorFacility(majorFacilitys.get());
            staffMajorFacility.setStaff(staff);

            return new TransferStaffRole(staff, staffMajorFacility);

        } catch (Exception e) {
            log.error("Đã có lỗi khi xử lý nhân viên: " + item.getStaffCode(), e);
            saveError(item.getStaffCode(), "Lỗi xử lý dữ liệu nhân viên");
            return null;
        }
    }

    private Optional<MajorFacility> fetchMajorFacility(String department, String major, String facility) {
        List<MajorFacility> majorFacilities = majorFacilityCustomRepository.getMajorFacilities(department, major, facility);
        return majorFacilities.isEmpty() ? Optional.empty() : Optional.of(majorFacilities.get(0));
    }

}
