package udpm.hn.server.infrastructure.job.MBReport.Export.service.impl;

import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.usermodel.*;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.*;
import org.springframework.stereotype.Service;
import udpm.hn.server.entity.Project;
import udpm.hn.server.infrastructure.job.MBReport.Export.model.request.ReportData;
import udpm.hn.server.infrastructure.job.MBReport.Export.repository.ExportReportDocxRepository;
import udpm.hn.server.infrastructure.job.MBReport.Export.service.ExportReportService;
import udpm.hn.server.utils.UserContextHelper;

import java.io.OutputStream;
import java.math.BigInteger;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExportReportServiceImpl implements ExportReportService {

    private final ExportReportDocxRepository reportRepo;

    @Override
    public void exportReportDocx(UserContextHelper userContextHelper, String projectId, OutputStream out) throws Exception {
        List<ReportData> reports = reportRepo.exportDocx(userContextHelper.getCurrentUserId(), projectId);
        Project project = reportRepo.getProject(projectId);

        List<ReportData> reportDataList = reports.stream()
                .map(r -> new ReportData(
                        r.getWorkDone(),
                        r.getObstacles(),
                        r.getWorkPlan(),
                        r.getReportTime()
                ))
                .collect(Collectors.toList());

        LocalDate startDate = Instant.ofEpochMilli(project.getStartTime()).atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate now = LocalDate.now();

        Map<String, List<ReportData>> grouped = groupByWeekFromStartToNow(reportDataList, startDate, now);
        generateDocx(grouped, out);
    }

    private LocalDate toLocalDate(Long millis) {
        return Instant.ofEpochMilli(millis)
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }

    private Map<String, List<ReportData>> groupByWeekFromStartToNow(List<ReportData> list, LocalDate startDate, LocalDate endDate) {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        LocalDate currentMonday = startDate.with(DayOfWeek.MONDAY);

        Map<LocalDate, List<ReportData>> grouped = new LinkedHashMap<>();
        while (!currentMonday.isAfter(endDate)) {
            grouped.put(currentMonday, new ArrayList<>());
            currentMonday = currentMonday.plusWeeks(1);
        }

        for (ReportData data : list) {
            LocalDate reportDate = toLocalDate(data.getReportTime());
            LocalDate mondayOfWeek = reportDate.with(DayOfWeek.MONDAY);
            if (grouped.containsKey(mondayOfWeek)) {
                grouped.get(mondayOfWeek).add(data);
            }
        }

        return grouped.entrySet().stream()
                .collect(Collectors.toMap(
                        entry -> {
                            LocalDate monday = entry.getKey();
                            LocalDate saturday = monday.plusDays(5);
                            return "CÔNG TÁC TUẦN ( Từ ngày " + monday.format(fmt) + " đến " + saturday.format(fmt) + ")";
                        },
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
    }

    private void generateDocx(Map<String, List<ReportData>> weeklyData, OutputStream out) throws Exception {
        XWPFDocument doc = new XWPFDocument();

        XWPFTable table = doc.createTable();
        table.removeRow(0);

        CTTbl ctTable = table.getCTTbl();
        CTTblPr tblPr = ctTable.getTblPr();
        if (tblPr == null) {
            tblPr = ctTable.addNewTblPr();
        }
        CTTblWidth tblWidth = tblPr.getTblW();
        if (tblWidth == null) {
            tblWidth = tblPr.addNewTblW();
        }
        tblWidth.setW(BigInteger.valueOf(50000));
        tblWidth.setType(STTblWidth.PCT);

        int[] colWidths = {2000, 2000, 2000, 2000, 2000, 2000};

        XWPFTableRow headerRow = table.createRow();
        String[] headers = {"THỨ HAI", "THỨ BA", "THỨ TƯ", "THỨ NĂM", "THỨ SÁU", "THỨ BẢY"};
        for (int i = 0; i < headers.length; i++) {
            XWPFTableCell cell = headerRow.getCell(i);
            if (cell == null) cell = headerRow.addNewTableCell();
            cell.setText(headers[i]);
            cell.getCTTc().addNewTcPr().addNewTcW().setW(BigInteger.valueOf(colWidths[i]));
            cell.getCTTc().getTcPr().getTcW().setType(STTblWidth.DXA);
        }

        for (Map.Entry<String, List<ReportData>> entry : weeklyData.entrySet()) {
            String weekTitle = entry.getKey();
            List<ReportData> reports = entry.getValue();

            XWPFTableRow titleRow = table.createRow();
            XWPFTableCell titleCell = titleRow.getCell(0);
            if (titleCell == null) titleCell = titleRow.addNewTableCell();
            titleCell.setText("#. " + weekTitle);
            titleCell.getCTTc().addNewTcPr().addNewGridSpan().setVal(BigInteger.valueOf(6));

            XWPFTableRow contentRow = table.createRow();
            contentRow.setHeight(800);

            for (int i = 0; i < 6; i++) {
                XWPFTableCell cell = contentRow.getCell(i);
                if (cell == null) cell = contentRow.addNewTableCell();
                cell.getCTTc().addNewTcPr().addNewTcW().setW(BigInteger.valueOf(colWidths[i]));
                cell.getCTTc().getTcPr().getTcW().setType(STTblWidth.DXA);
                cell.setText("NGHỈ");
            }

            for (ReportData r : reports) {
                int dayOfWeek = toLocalDate(r.getReportTime()).getDayOfWeek().getValue();
                if (dayOfWeek >= 1 && dayOfWeek <= 6) {
                    int index = dayOfWeek - 1;
                    XWPFTableCell cell = contentRow.getCell(index);
                    cell.removeParagraph(0);
                    XWPFParagraph p = cell.addParagraph();
                    XWPFRun run = p.createRun();
                    run.setText("1. Kết quả hôm nay làm được: " + r.getWorkDone());
                    run.addBreak();
                    run.setText("2. Những cái chưa làm được cần hỗ trợ: " + r.getObstacles());
                    run.addBreak();
                    run.setText("3. Kế hoạch ngày mai: " + r.getWorkPlan());
                }
            }
        }

        doc.write(out);
        doc.close();
    }

    @Override
    public void exportReportExcel(UserContextHelper userContextHelper, String projectId, OutputStream out) throws Exception {
        List<ReportData> reports = reportRepo.exportDocx(userContextHelper.getCurrentUserId(), projectId);
        Project project = reportRepo.getProject(projectId);

        List<ReportData> reportDataList = reports.stream()
                .map(r -> new ReportData(
                        r.getWorkDone(),
                        r.getObstacles(),
                        r.getWorkPlan(),
                        r.getReportTime()
                ))
                .collect(Collectors.toList());

        LocalDate startDate = Instant.ofEpochMilli(project.getStartTime()).atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate now = LocalDate.now();

        Map<String, List<ReportData>> grouped = groupByWeekFromStartToNow(reportDataList, startDate, now);
        generateExcel(grouped, out);
    }

    private void generateExcel(Map<String, List<ReportData>> weeklyData, OutputStream out) throws Exception {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Báo cáo tuần");

        CellStyle boldStyle = workbook.createCellStyle();
        Font boldFont = workbook.createFont();
        boldFont.setBold(true);
        boldStyle.setFont(boldFont);
        boldStyle.setWrapText(true);

        CellStyle wrapTextStyle = workbook.createCellStyle();
        wrapTextStyle.setWrapText(true);

        int rowNum = 0;

        Row headerRow = sheet.createRow(rowNum++);
        String[] headers = {"THỨ HAI", "THỨ BA", "THỨ TƯ", "THỨ NĂM", "THỨ SÁU", "THỨ BẢY"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(boldStyle);
        }

        for (Map.Entry<String, List<ReportData>> entry : weeklyData.entrySet()) {
            String weekTitle = entry.getKey();
            List<ReportData> reports = entry.getValue();

            Row titleRow = sheet.createRow(rowNum++);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue("A. " + weekTitle);
            titleCell.setCellStyle(boldStyle);
            sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(rowNum - 1, rowNum - 1, 0, 5));

            Row dataRow = sheet.createRow(rowNum++);
            for (int i = 0; i < 6; i++) {
                Cell cell = dataRow.createCell(i);
                cell.setCellValue("NGHỈ");
                cell.setCellStyle(wrapTextStyle);
            }

            for (ReportData report : reports) {
                int dayOfWeek = toLocalDate(report.getReportTime()).getDayOfWeek().getValue();
                if (dayOfWeek >= 1 && dayOfWeek <= 6) {
                    int index = dayOfWeek - 1;
                    Cell cell = dataRow.getCell(index);
                    StringBuilder content = new StringBuilder();
                    content.append("1. Kết quả hôm nay làm được: ").append(report.getWorkDone()).append("\n");
                    content.append("2. Những cái chưa làm được cần hỗ trợ: ").append(report.getObstacles()).append("\n");
                    content.append("3. Kế hoạch ngày mai: ").append(report.getWorkPlan());
                    cell.setCellValue(content.toString());
                    cell.setCellStyle(wrapTextStyle);
                }
            }

            dataRow.setHeightInPoints((reports.size() + 3) * 15f);
            rowNum++;
        }

        for (int i = 0; i < 6; i++) {
            sheet.setColumnWidth(i, 20 * 256);
        }

        workbook.write(out);
        workbook.close();
    }
}
