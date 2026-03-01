package udpm.hn.server.utils;

import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import udpm.hn.server.entity.HistoryImport;
import udpm.hn.server.infrastructure.constant.LogFileType;
import udpm.hn.server.infrastructure.constant.LogType;
import udpm.hn.server.repository.FacilityRepository;
import udpm.hn.server.repository.HistoryImportRepository;
import udpm.hn.server.repository.RoleRepository;
import udpm.hn.server.repository.StaffRepository;

import java.util.List;

@Component
public class HistoryLogUtils {

    @Setter(onMethod_ = {@Autowired})
    private HistoryImportRepository historyImportRepository;

    @Setter(onMethod_ = {@Autowired})
    private FacilityRepository facilityRepository;

    @Setter(onMethod_ = {@Autowired})
    private RoleRepository roleRepository;

    @Setter(onMethod_ = {@Autowired})
    private StaffRepository staffRepository;

//    @Setter(onMethod_ = {@Autowired})
//    private SessionHelper sessionHelper;

    @Setter(onMethod_ = {@Autowired})
    private UserContextHelper userContextHelper;

    public void logErrorRecord(String message, String fileName) {
        HistoryImport historyImport = new HistoryImport();
//        historyImport.setFileName(fileName);
//        historyImport.setFacility(facilityRepository.getReferenceById(userContextHelper.getCurrentIdFacility()));
//        historyImport.setMessage(message);
//        historyImport.setStaff(staffRepository.getReferenceById(userContextHelper.getCurrentUserId()));
//        historyImport.setType(LogType.ERROR);
//    }
//
//    @Transactional(propagation = Propagation.REQUIRES_NEW)
//    public void logErrorRecord(
//            String message,
//            String fileName,
//            String staffId,
//            LogFileType logFileType
//    ) {
//        HistoryImport historyImport = new HistoryImport();
//        historyImport.setFileName(fileName);
//        historyImport.setMessage(message);
//        historyImport.setStaff(staffRepository.getReferenceById(staffId));
//        historyImport.setType(LogType.ERROR);
//        historyImport.setLogFileType(logFileType);
//
////        List<Role> roles = roleRepository.findRoleByStaff(staffId);
////        if (!roles.isEmpty()) {
////            historyImport.setRole(roles.get(0)); // Lưu 1 role chính
////        }
//
//        historyImportRepository.save(historyImport);
//    }
//
//
//    public void logSuccessRecord(String message, String fileName) {
//        HistoryImport historyImport = new HistoryImport();
//        historyImport.setFileName(fileName);
//        historyImport.setFacility(facilityRepository.getReferenceById(userContextHelper.getCurrentIdFacility()));
//        historyImport.setMessage(message);
//        historyImport.setStaff(staffRepository.getReferenceById(userContextHelper.getCurrentUserId()));
//        historyImport.setType(LogType.SUCCESS);
//        historyImportRepository.save(historyImport);
//    }
//
//    public List<HistoryImport> getHistoryImport() {
//        return historyImportRepository.findAll();
//    }
//
//    public List<HistoryImport> getHistoryImportByFacilityIdAndStaffIdAndFileType(
//
//            String staffId,
//            LogFileType logFileType
//    ) {
//        List<HistoryImport> historyImportList = historyImportRepository.findAllByStaff_IdAndLogFileType(
//                staffId,
//                logFileType
//        );
//
//        return historyImportList
//                .stream()
//                .sorted((o1, o2) -> o2.getCreatedDate().compareTo(o1.getCreatedDate()))
//                .toList();
//    }


}}
