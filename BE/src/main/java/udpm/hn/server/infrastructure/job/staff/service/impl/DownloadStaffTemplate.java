package udpm.hn.server.infrastructure.job.staff.service.impl;

import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import udpm.hn.server.core.common.base.ResponseObject;
import udpm.hn.server.infrastructure.job.staff.repository.ConfigDepartmentFacilityCustomRepository;
import udpm.hn.server.utils.UserContextHelper;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Component
@Slf4j
public class DownloadStaffTemplate {

    private final ConfigDepartmentFacilityCustomRepository departmentFacilityRepository;

    private final HttpSession httpSession;

    private final UserContextHelper userContextHelper;

    public DownloadStaffTemplate(
            ConfigDepartmentFacilityCustomRepository departmentFacilityRepository,
            HttpSession httpSession,
            UserContextHelper userContextHelper) {
        this.departmentFacilityRepository = departmentFacilityRepository;
        this.httpSession = httpSession;
        this.userContextHelper = userContextHelper;
    }

    public ResponseObject<?> downloadTemplate() {

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Template Thông Tin Nhân Viên");

            Row row = sheet.createRow(0);

            Font font = workbook.createFont();
            font.setColor(IndexedColors.WHITE.getIndex());

            CellStyle headerCellStyle = workbook.createCellStyle();
            headerCellStyle.setLocked(true);
            headerCellStyle.setFillForegroundColor(IndexedColors.BLACK.getIndex());
            headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerCellStyle.setFont(font);
            headerCellStyle.setWrapText(true);

            Cell cell = row.createCell(0);
            cell.setCellValue("STT");
            cell.setCellStyle(headerCellStyle);

            cell = row.createCell(1);
            cell.setCellValue("Mã Nhân Viên");
            cell.setCellStyle(headerCellStyle);

            cell = row.createCell(2);
            cell.setCellValue("Họ và Tên");
            cell.setCellStyle(headerCellStyle);

            cell = row.createCell(3);
            cell.setCellValue("Email FPT");
            cell.setCellStyle(headerCellStyle);

            cell = row.createCell(4);
            cell.setCellValue("Email FE");
            cell.setCellStyle(headerCellStyle);

            cell = row.createCell(5);
            cell.setCellValue("Chuyên ngành");
            cell.setCellStyle(headerCellStyle);

            cell = row.createCell(6);
            cell.setCellValue("Bộ môn");
            cell.setCellStyle(headerCellStyle);

            cell = row.createCell(7);
            cell.setCellValue("Cơ sở");
            cell.setCellStyle(headerCellStyle);

            sheet.setColumnWidth(0, 256 * 10);
            sheet.setColumnWidth(1, 256 * 20);
            sheet.setColumnWidth(2, 256 * 30);
            sheet.setColumnWidth(3, 256 * 30);
            sheet.setColumnWidth(4, 256 * 30);
            sheet.setColumnWidth(5, 256 * 25);
            sheet.setColumnWidth(6, 256 * 25);
            sheet.setColumnWidth(7, 256 * 20);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return new ResponseObject<>(new ByteArrayInputStream(outputStream.toByteArray()), HttpStatus.OK, "Download template successfully");
        } catch (IOException ex) {
            log.error("Error during export Excel file", ex);
            return null;
        }
    }

}
