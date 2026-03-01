package udpm.hn.server.infrastructure.job.filecheck;

import org.apache.poi.ss.usermodel.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

public class ExcelFormatStaffValidator {

    private static final List<String> EXPECTED_HEADERS = Arrays.asList(
            "STT", "Mã Nhân Viên", "Họ và Tên", "Email FPT", "Email FE", "Chuyên ngành", "Bộ môn", "Cơ sở"
    );

    public static boolean isValidFormat(MultipartFile file) {
        try (InputStream is = file.getInputStream();
             Workbook workbook = WorkbookFactory.create(is)) {

            Sheet sheet = workbook.getSheetAt(0);
            Row headerRow = sheet.getRow(0);
            if (headerRow == null) return false;

            for (int i = 0; i < EXPECTED_HEADERS.size(); i++) {
                Cell cell = headerRow.getCell(i);
                if (cell == null) return false;
                String value = cell.getStringCellValue().trim();
                if (!value.equalsIgnoreCase(EXPECTED_HEADERS.get(i))) {
                    return false;
                }
            }
            return true;

        } catch (Exception e) {
            return false;
        }
    }
}
