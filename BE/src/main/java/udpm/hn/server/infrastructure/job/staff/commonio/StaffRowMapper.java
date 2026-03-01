package udpm.hn.server.infrastructure.job.staff.commonio;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.excel.RowMapper;
import org.springframework.batch.item.excel.support.rowset.RowSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import udpm.hn.server.entity.Facility;
import udpm.hn.server.entity.HistoryImport;
import udpm.hn.server.infrastructure.job.staff.model.request.StaffExcelRequest;
import udpm.hn.server.infrastructure.job.staff.service.impl.HistoryImportStaffService;
import udpm.hn.server.repository.HistoryImportRepository;
import udpm.hn.server.repository.StaffRepository;
import udpm.hn.server.utils.UserContextHelper;

import java.util.Arrays;
import java.util.Optional;

@Slf4j
@Component
public class StaffRowMapper implements RowMapper<StaffExcelRequest> {

    @Autowired
    private HistoryImportStaffService historyImportService;

//    public StaffRowMapper(HistoryImportStaffService historyImportService) {
//        this.historyImportService = historyImportService;
//        log.info("HistoryImportStaffService injected: {}", historyImportService != null ? "Not null" : "Null");
//    }

    void logFacility(String message, String record) {
        String fullMessage = "Lỗi tại bản ghi " + record + ": " + message;
        log.info("BẮT ĐẦU LƯU VÀO DB: {}", fullMessage);
        try {
            historyImportService.saveHistory(fullMessage);
            log.info("LƯU VÀO DB THÀNH CÔNG: {}", fullMessage);
        } catch (Exception e) {
            log.error("LỖI LƯU VÀO DB: {}", fullMessage, e);
        }
    }

    @Override
    public StaffExcelRequest mapRow(RowSet rowSet) {
        log.info("BẮT ĐẦU ÁNH XẠ DÒNG: index={}, data={}", rowSet.getCurrentRowIndex(), Arrays.toString(rowSet.getCurrentRow()));
        StaffExcelRequest importExcelRequest = new StaffExcelRequest();
        importExcelRequest.setOrderNumber(rowSet.getCurrentRowIndex());

        try {
            // Ánh xạ dữ liệu từ Excel
            importExcelRequest.setStaffCode(getColumnValueSafe(rowSet, 1));
            importExcelRequest.setName(getColumnValueSafe(rowSet, 2));
            importExcelRequest.setEmailFpt(getColumnValueSafe(rowSet, 3));
            importExcelRequest.setEmailFe(getColumnValueSafe(rowSet, 4));
            importExcelRequest.setMajor(getColumnValueSafe(rowSet, 5));
            importExcelRequest.setDepartment(getColumnValueSafe(rowSet, 6));
            importExcelRequest.setFacility(getColumnValueSafe(rowSet, 7));

            String record = (importExcelRequest.getStaffCode() != null ? importExcelRequest.getStaffCode() : "N/A") + " - " +
                    (importExcelRequest.getName() != null ? importExcelRequest.getName() : "N/A");

            // Gán giá trị mặc định cho cột Cơ sở và kiểm tra lại
            if (importExcelRequest.getFacility() == null || importExcelRequest.getFacility().trim().isEmpty()) {
                log.warn("CƠ SỞ TRỐNG HOẶC THIẾU: record={}", record);
                logFacility("Cơ sở không được để trống, gán giá trị mặc định: Unknown", record);
                importExcelRequest.setFacility("Unknown");
            }

            log.info("DỮ LIỆU ÁNH XẠ: {}", importExcelRequest);
            return importExcelRequest;
        } catch (Exception e) {
            String record = (importExcelRequest.getStaffCode() != null ? importExcelRequest.getStaffCode() : "N/A") + " - " +
                    (importExcelRequest.getName() != null ? importExcelRequest.getName() : "N/A");
            log.error("LỖI ÁNH XẠ DÒNG {}: {}", rowSet.getCurrentRowIndex(), e.getMessage());
            logFacility("Lỗi đọc dữ liệu: " + e.getMessage(), record);
            return null;
        }
    }

    private String getColumnValueSafe(RowSet rowSet, int columnIndex) {
        try {
            return rowSet.getColumnValue(columnIndex);
        } catch (ArrayIndexOutOfBoundsException e) {
            log.warn("Cột {} không tồn tại trong dòng {}", columnIndex, rowSet.getCurrentRowIndex());
            return null;
        }
    }
}