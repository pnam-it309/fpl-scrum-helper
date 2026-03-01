package udpm.hn.server.infrastructure.job.report;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import udpm.hn.server.core.manage.report.model.response.MAReportResponse;
import udpm.hn.server.core.manage.report.repository.MAReportRepository;
import udpm.hn.server.core.manage.user.model.response.MAUserProjectResponse;
import udpm.hn.server.core.manage.user.repository.MAUserProject;
import udpm.hn.server.infrastructure.doc.ADReportProject.repository.DocADProjectRepository;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Field;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
public class BaseReportDay<T> {

    private XSSFWorkbook workbook;
    private XSSFSheet sheet;

    private final MAUserProject maUserProject;
    private String idProject;

    List<LocalDate> listDay = new ArrayList<>();

    private final DocADProjectRepository docADProjectRepository;

    private final MAReportRepository maReportRepository;

    public BaseReportDay(MAUserProject maUserProject, String idProject, DocADProjectRepository docADProjectRepository, MAReportRepository maReportRepository) {
        this.docADProjectRepository = docADProjectRepository;
        this.maUserProject = maUserProject;
        this.idProject = idProject;
        this.maReportRepository = maReportRepository;
        workbook = new XSSFWorkbook();
    }


    public BaseReportDay<T> writeHeaderLine(String[] headers) {
        log.debug("Đã vào trong baseReportDay");
        sheet = workbook.createSheet("data export");

        CellStyle style = getHeaderStyle();  // style cho header
        CellStyle dateStyle = getHeaderStyle(); // nếu có style cho ngày

        // Dòng tiêu đề tuần (dòng 0)
        Row weekRow = sheet.createRow(0);

        // Dòng các ngày trong tuần (dòng 1)
        Row dayRow = sheet.createRow(1);

        // Tạo tiêu đề các cột cố định
        for (int i = 0; i < headers.length; i++) {
            createCell(weekRow, i, headers[i], style);
            sheet.addMergedRegion(new CellRangeAddress(0, 1, i, i)); // gộp 2 dòng cho cột cố định
        }

        List<List<LocalDate>> weeks = time(idProject);
        listDay = weeks.stream().flatMap(List::stream)   // dàn phẳng từng List<LocalDate>
                .collect(Collectors.toList());

        log.info("List date project:{}", listDay.stream().toList());

        int colIndex = headers.length;
        int weekCount = 1;

        for (List<LocalDate> week : weeks) {
            int daysCount = week.size();

            LocalDate startDay = week.get(0);
            LocalDate endDay = week.get(daysCount - 1);

            // Ghi tiêu đề tuần
            String weekTitle = String.format("Tuần %d (%s - %s)", weekCount, startDay.format(DateTimeFormatter.ofPattern("dd/MM")), endDay.format(DateTimeFormatter.ofPattern("dd/MM")));

            Cell weekCell = weekRow.createCell(colIndex);
            weekCell.setCellValue(weekTitle);
            weekCell.setCellStyle(style);

            if (daysCount > 1) {
                sheet.addMergedRegion(new CellRangeAddress(0, 0, colIndex, colIndex + daysCount - 1));
            }


            // Ghi từng ngày của tuần
            for (int i = 0; i < week.size(); i++) {
                Cell dayCell = dayRow.createCell(colIndex + i);
                dayCell.setCellValue(String.valueOf(week.get(i).format(DateTimeFormatter.ofPattern("dd-MM"))));
                dayCell.setCellStyle(dateStyle);  // nếu có style ngày
            }


            colIndex += daysCount;
            weekCount++;
        }

        return this;
    }


    private void createCell(Row row, int columnCount, Object value, CellStyle style) {
        Cell cell = row.createCell(columnCount);
        if (value instanceof Integer) {
            cell.setCellValue((Integer) value);
        } else if (value instanceof Boolean) {
            cell.setCellValue((Boolean) value);
        } else if (value instanceof Double) {
            cell.setCellValue((Double) value);
        } else {
            cell.setCellValue(value != null ? value.toString() : "");
        }
        cell.setCellStyle(style);
    }

    private CellStyle getHeaderStyle() {
        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();

        font.setBold(true);
        font.setFontHeight(12);
        font.setFontName("Times New Roman");
        style.setFont(font);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return style;
    }

    public BaseReportDay<T> writeDataLines(List<MAUserProjectResponse> listUser) {
        int rowCount = 2; // Bắt đầu từ dòng 3
        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setFontHeight(12);
        font.setFontName("Times New Roman");
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.LEFT);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setWrapText(true); // Cho phép xuống dòng trong ô


        Map<String, Integer> projectStartRowMap = new HashMap<>();
        Map<String, Integer> projectEndRowMap = new HashMap<>();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        for (MAUserProjectResponse response : listUser) {
            Row row = sheet.createRow(rowCount);
            row.setHeightInPoints(80); // Tăng chiều cao để chứa nội dung nhiều dòng

            int colCount = 0;

            String name = response.getNameStudent() != null ? response.getNameStudent() : response.getNameStaff();
            String code = response.getCodeStudent() != null ? response.getCodeStudent() : response.getCodeStaff();

            // STT
            createCell(row, colCount++, rowCount - 1, style);
            createCell(row, colCount++, code, style);
            createCell(row, colCount++, name, style);

            // Tạm thời ghi project
            createCell(row, colCount, response.getCodeProject(), style);
            createCell(row, colCount + 1, response.getNameProject(), style);

            String projectKey = response.getCodeProject() + "-" + response.getNameProject();
            projectStartRowMap.putIfAbsent(projectKey, rowCount);
            projectEndRowMap.put(projectKey, rowCount);

            colCount += 2;

            // Lấy báo cáo
            List<MAReportResponse> listReportUser = maReportRepository.findReportByProjectAndMember(idProject, response.getIdStaffProject());
            Map<String, MAReportResponse> listReportMap = new HashMap<>();
            for (MAReportResponse reportResponse : listReportUser) {
                LocalDate date = Instant.ofEpochMilli(Long.parseLong(reportResponse.getReportTime())).atZone(ZoneId.systemDefault()).toLocalDate();

                listReportMap.put(date.format(formatter), reportResponse);
            }

            for (LocalDate date : listDay) {
                Cell cell = row.createCell(colCount++);
                String dateKey = date.format(formatter);

                if (date.getDayOfWeek() == DayOfWeek.SUNDAY) {
                    cell.setCellValue("Nghỉ");
                } else {
                    MAReportResponse report = listReportMap.get(dateKey);

                    if (report != null) {
                        String content = "Hôm nay làm: " + report.getWorkDoneToday() + "\nKhó khăn: " + report.getObstacles() + "\nNgày mai làm gì: " + report.getWorkPlanTomorrow();
                        cell.setCellValue(content);
                    } else {
                        cell.setCellValue("Chưa báo cáo");
                    }
                }

                cell.setCellStyle(style);
            }

            rowCount++;
        }

        // Gộp các ô project giống nhau
        for (String key : projectStartRowMap.keySet()) {
            int startRow = projectStartRowMap.get(key);
            int endRow = projectEndRowMap.get(key);
            if (startRow != endRow) {
                sheet.addMergedRegion(new CellRangeAddress(startRow, endRow, 3, 3)); // CodeProject
                sheet.addMergedRegion(new CellRangeAddress(startRow, endRow, 4, 4)); // NameProject
            }
        }

        // Thiết lập độ rộng cột
        int totalColumns = listDay.size() + 5; // STT, Code, Name, CodeProject, NameProject + ngày
        for (int i = 0; i < totalColumns; i++) {
            sheet.setColumnWidth(i, 25 * 256); // 25 ký tự độ rộng
        }

        return this;
    }


    public XSSFWorkbook getWorkbook() {
        return workbook;
    }

    public void export(HttpServletResponse response) throws Exception {

        ServletOutputStream out = response.getOutputStream();
        workbook.write(out);
        workbook.close();
        out.close();

    }


    private List<List<LocalDate>> time(String idProject) {
        Long startTime = docADProjectRepository.findProjectStartTimeById(idProject).getStartTime();
        log.debug("start time export:{}", startTime);
        LocalDate startDate = Instant.ofEpochMilli(startTime).atZone(ZoneId.systemDefault()).toLocalDate();

        LocalDate today = LocalDate.now();

        List<List<LocalDate>> weeks = new ArrayList<>();
        List<LocalDate> currentWeek = new ArrayList<>();

        for (LocalDate date = startDate; !date.isAfter(today); date = date.plusDays(1)) {
            if (currentWeek.isEmpty()) {
                currentWeek.add(date);
            } else {
                if (date.getDayOfWeek() == DayOfWeek.MONDAY) {
                    weeks.add(currentWeek);         // kết thúc tuần trước
                    currentWeek = new ArrayList<>(); // bắt đầu tuần mới
                }
                currentWeek.add(date);
            }
        }

        if (!currentWeek.isEmpty()) {
            weeks.add(currentWeek);
        }
        log.info("số tuần import:{}", weeks);

        return weeks;
    }

    private Map<LocalDate, String> getReportMap(T data) {
        try {
            Field reportField = data.getClass().getDeclaredField("workDoneToday");
            reportField.setAccessible(true);
            Object value = reportField.get(data);
            if (value instanceof Map<?, ?> map) {
                return (Map<LocalDate, String>) map;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new HashMap<>();
    }

    public void exprortBase64(HttpServletResponse response) throws Exception {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        workbook.write(byteArrayOutputStream);
        workbook.close();

        byte[] byteArray = byteArrayOutputStream.toByteArray();

        byteArrayOutputStream.close();
        String base64EncodedString = Base64.getEncoder().encodeToString(byteArray);
        ServletOutputStream out = response.getOutputStream();
        out.write(base64EncodedString.getBytes());
        out.close();

    }


}
