package udpm.hn.server.infrastructure.doc.ADReportProject.utils.excel;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import udpm.hn.server.infrastructure.doc.ADReportProject.model.response.DocADProjectMemberResponse;
import udpm.hn.server.infrastructure.doc.ADReportProject.model.response.PersonalReportResponse;
import udpm.hn.server.infrastructure.doc.ADReportProject.repository.ADReportRepository;
import udpm.hn.server.infrastructure.doc.ADReportProject.repository.DocADProjectRepository;

import java.io.FileOutputStream;
import java.io.IOException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class ExcelOneFileOneSheet {

    public static void exportExcelOneFileOneSheet(String filePath,
                                             List<DocADProjectMemberResponse> allMembers,
                                             ADReportRepository adReportRepository,
                                             DocADProjectRepository docADProjectRepository) throws IOException {

        LocalDate minStartDate = allMembers.stream()
                .map(m -> docADProjectRepository.findProjectStartTimeById(m.getProjectId()).getStartTime())
                .map(t -> Instant.ofEpochMilli(t).atZone(ZoneId.systemDefault()).toLocalDate())
                .map(d -> d.with(DayOfWeek.MONDAY))
                .min(LocalDate::compareTo)
                .orElse(LocalDate.now());

        LocalDate endDate = LocalDate.now();

        List<LocalDate> allDates = new ArrayList<>();
        for (LocalDate d = minStartDate; !d.isAfter(endDate); d = d.plusDays(1)) {
            allDates.add(d);
        }

        Map<LocalDate, List<Integer>> mondayToDateCols = new LinkedHashMap<>();
        List<LocalDate> mondays = new ArrayList<>();
        for (int i = 0; i < allDates.size(); i++) {
            LocalDate date = allDates.get(i);
            LocalDate monday = date.with(DayOfWeek.MONDAY);
            mondayToDateCols.computeIfAbsent(monday, k -> new ArrayList<>()).add(i);
            if (!mondays.contains(monday)) mondays.add(monday);
        }

        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Report Tổng Hợp");

        CellStyle headerStyle = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        headerStyle.setFont(font);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        headerStyle.setWrapText(true);

        int startColOffset = 5;

        Row header0 = sheet.createRow(0);
        Row header1 = sheet.createRow(1);
        String[] fixedHeaders = {"STT", "MSSV", "Họ tên sinh viên", "Team", "Dự án"};

        for (int i = 0; i < fixedHeaders.length; i++) {
            Cell cell0 = header0.createCell(i);
            cell0.setCellValue(fixedHeaders[i]);
            cell0.setCellStyle(headerStyle);

            Cell cell1 = header1.createCell(i);
            cell1.setCellStyle(headerStyle);
        }

        DateTimeFormatter weekFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter dayFormatter = DateTimeFormatter.ofPattern("dd/MM");

        Set<Integer> lastColOfWeek = new HashSet<>();
        for (List<Integer> cols : mondayToDateCols.values()) {
            lastColOfWeek.add(cols.get(cols.size() - 1) + startColOffset);
        }

        for (Map.Entry<LocalDate, List<Integer>> entry : mondayToDateCols.entrySet()) {
            LocalDate monday = entry.getKey();
            List<Integer> cols = entry.getValue();
            int firstCol = cols.get(0) + startColOffset;
            int lastCol = cols.get(cols.size() - 1) + startColOffset;

            String weekTitle = "Tuần (Từ " + monday.format(weekFormatter) + " đến " + monday.plusDays(6).format(weekFormatter) + ")";

            CellStyle weekHeaderStyle = workbook.createCellStyle();
            weekHeaderStyle.cloneStyleFrom(headerStyle);
            if (lastColOfWeek.contains(lastCol)) {
                weekHeaderStyle.setBorderRight(BorderStyle.MEDIUM);
            }

            Cell cell = header0.createCell(firstCol);
            cell.setCellValue(weekTitle);
            cell.setCellStyle(weekHeaderStyle);

            if (firstCol != lastCol) {
                sheet.addMergedRegion(new CellRangeAddress(0, 0, firstCol, lastCol));
            }

            for (int i = 0; i < cols.size(); i++) {
                LocalDate date = allDates.get(cols.get(i));
                int colIndex = cols.get(i) + startColOffset;
                Cell cell1 = header1.createCell(colIndex);
                cell1.setCellValue(date.format(dayFormatter));
                cell1.setCellStyle(headerStyle);
            }
        }

        int rowIndex = 2;
        int stt = 1;
        int teamStartRow = rowIndex;
        int projectStartRow = rowIndex;
        String prevTeam = null;
        String prevProject = null;

        for (DocADProjectMemberResponse member : allMembers) {
            Row row = sheet.createRow(rowIndex);
            row.createCell(0).setCellValue(stt++);
            row.createCell(1).setCellValue(member.getUserCode());
            row.createCell(2).setCellValue(member.getMemberName());

            String currentTeam = member.getProjectCode();
            String currentProject = docADProjectRepository.findProjectStartTimeById(member.getProjectId()).getName();

            row.createCell(3).setCellValue(currentTeam);
            row.createCell(4).setCellValue(currentProject);

            List<PersonalReportResponse> reports = adReportRepository.findAllReportsByStaffProjectId(member.getStaffProjectId());
            Map<LocalDate, PersonalReportResponse> reportMap = new HashMap<>();
            for (PersonalReportResponse r : reports) {
                try {
                    LocalDate date = Instant.ofEpochMilli(Long.parseLong(r.getReportTime())).atZone(ZoneId.systemDefault()).toLocalDate();
                    reportMap.put(date, r);
                } catch (Exception ignored) {}
            }
            LocalDate projectStartDate = Instant.ofEpochMilli(
                    docADProjectRepository.findProjectStartTimeById(member.getProjectId()).getStartTime()
            ).atZone(ZoneId.systemDefault()).toLocalDate();

            for (int i = 0; i < allDates.size(); i++) {
                LocalDate date = allDates.get(i);
                int col = i + startColOffset;
                Cell cell = row.createCell(col);
                CellStyle wrapStyle = workbook.createCellStyle();
                wrapStyle.setWrapText(true);
                if (lastColOfWeek.contains(col)) {
                    wrapStyle.setBorderRight(BorderStyle.MEDIUM);
                }

                if (date.isBefore(projectStartDate)) {
                    cell.setCellValue("Dự án chưa bắt đầu");
                } else if (date.getDayOfWeek() == DayOfWeek.SUNDAY) {
                    cell.setCellValue("Nghỉ");
                } else if (reportMap.containsKey(date)) {
                    PersonalReportResponse r = reportMap.get(date);
                    String content = "Hôm nay làm: " + r.getWorkDoneToday()
                            + "\nKhó khăn: " + r.getObstacles()
                            + "\nNgày mai làm gì: " + r.getWorkPlanTomorrow();
                    cell.setCellValue(content);
                } else {
                    cell.setCellValue("Chưa báo cáo");
                }

                cell.setCellStyle(wrapStyle);
            }

            if (!currentTeam.equals(prevTeam) && prevTeam != null) {
                if (teamStartRow < rowIndex - 1) {
                    sheet.addMergedRegion(new CellRangeAddress(teamStartRow, rowIndex - 1, 3, 3));
                }
                teamStartRow = rowIndex;
            }
            prevTeam = currentTeam;

            if (!currentProject.equals(prevProject) && prevProject != null) {
                if (projectStartRow < rowIndex - 1) {
                    sheet.addMergedRegion(new CellRangeAddress(projectStartRow, rowIndex - 1, 4, 4));
                }
                projectStartRow = rowIndex;
            }
            prevProject = currentProject;

            rowIndex++;
        }

        int lastRow = rowIndex - 1;
        if (teamStartRow < lastRow) {
            sheet.addMergedRegion(new CellRangeAddress(teamStartRow, lastRow, 3, 3));
        }
        if (projectStartRow < lastRow) {
            sheet.addMergedRegion(new CellRangeAddress(projectStartRow, lastRow, 4, 4));
        }

        for (int i = 0; i < allDates.size() + startColOffset; i++) {
            sheet.autoSizeColumn(i);
        }

        try (FileOutputStream fos = new FileOutputStream(filePath)) {
            workbook.write(fos);
        }
        workbook.close();
    }
}
