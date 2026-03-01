package udpm.hn.server.infrastructure.job.report;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xwpf.usermodel.*;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTcPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STMerge;
import udpm.hn.server.core.manage.report.model.response.MAReportResponse;
import udpm.hn.server.core.manage.report.repository.MAReportRepository;
import udpm.hn.server.core.manage.user.model.response.MAUserProjectResponse;
import udpm.hn.server.core.manage.user.repository.MAUserProject;
import udpm.hn.server.infrastructure.doc.ADReportProject.repository.DocADProjectRepository;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class WordGenerator {

    public static byte[] createWordDocument(String title,
                                            String idProject,
                                            MAUserProjectResponse response,
                                            DocADProjectRepository docADProjectRepository,
                                            MAReportRepository maReportRepository,
                                            MAUserProject maUserProject) throws IOException {

        try (XWPFDocument document = new XWPFDocument(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            // Tiêu đề
            XWPFParagraph titleParagraph = document.createParagraph();
            titleParagraph.setAlignment(ParagraphAlignment.CENTER);
            XWPFRun titleRun = titleParagraph.createRun();
            titleRun.setText(title);
            titleRun.setBold(true);
            titleRun.setFontSize(20);
            titleRun.setFontFamily("Times New Roman");

            // Lấy báo cáo từ DB
            List<MAReportResponse> listReportUser = maReportRepository.findReportByProjectAndMember(idProject, response.getIdStaffProject());

            // Map thời gian -> báo cáo
            Map<String, MAReportResponse> reportMap = new HashMap<>();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            for (MAReportResponse reportResponse : listReportUser) {
                LocalDate date = Instant.ofEpochMilli(Long.parseLong(reportResponse.getReportTime()))
                        .atZone(ZoneId.systemDefault()).toLocalDate();
                reportMap.put(date.format(formatter), reportResponse);
            }

            // Lấy danh sách tuần
            List<List<LocalDate>> weeks = timeDay(idProject, docADProjectRepository);

            for (int weekIndex = 0; weekIndex < weeks.size(); weekIndex++) {
                List<LocalDate> weekDays = weeks.get(weekIndex)
                        .stream()
                        .filter(d -> d.getDayOfWeek().getValue() <= 6) // CẢ THỨ 7
                        .collect(Collectors.toList());

                if (weekDays.isEmpty()) continue;

                LocalDate start = weekDays.get(0);
                LocalDate end = weekDays.get(weekDays.size() - 1);

                // Tạo bảng với 6 cột (thứ 2 đến thứ 7)
                XWPFTable table = document.createTable(3, 6);
                table.getCTTbl().getTblPr().addNewTblW().setW(BigInteger.valueOf(9000));

                // Gộp dòng đầu tiên (6 ô)
                mergeCellsHorizontal(table, 0, 0, 5);
                setCellText(table.getRow(0).getCell(0),
                        "BÁO CÁO CÔNG TÁC TUẦN " + (weekIndex + 1) + " (Từ ngày " +
                                start.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + " đến " +
                                end.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + ")",
                        true, ParagraphAlignment.CENTER);

                // Header
                String[] headers = {"THỨ HAI", "THỨ BA", "THỨ TƯ", "THỨ NĂM", "THỨ SÁU", "THỨ BẢY"};
                XWPFTableRow headerRow = table.getRow(1);
                for (int i = 0; i < 6; i++) {
                    setCellText(headerRow.getCell(i), headers[i], true, ParagraphAlignment.CENTER);
                }

                // Nội dung
                XWPFTableRow contentRow = table.getRow(2);
                for (int i = 0; i < 6; i++) {
                    XWPFTableCell cell = contentRow.getCell(i);
                    CTTcPr tcPr = cell.getCTTc().isSetTcPr() ? cell.getCTTc().getTcPr() : cell.getCTTc().addNewTcPr();
                    tcPr.addNewTcW().setW(BigInteger.valueOf(1500)); // Điều chỉnh chiều rộng mỗi ô
                    contentRow.setHeight(1200);

                    if (i >= weekDays.size()) {
                        setCellText(cell, "NGHỈ", false, ParagraphAlignment.LEFT);
                        continue;
                    }

                    LocalDate day = weekDays.get(i);
                    String dayKey = day.format(formatter);
                    MAReportResponse report = reportMap.get(dayKey);

                    if (report != null) {
                        cell.removeParagraph(0);
                        XWPFParagraph p = cell.addParagraph();
                        p.setAlignment(ParagraphAlignment.LEFT);

                        XWPFRun run = p.createRun();
                        run.setFontFamily("Times New Roman");
                        run.setFontSize(12);

                        run.setText("1. Kết quả hôm nay làm được:");
                        run.addBreak();
                        run.setText(report.getWorkDoneToday());
                        run.addBreak();

                        run.setText("2. Những cái chưa làm được cần hỗ trợ:");
                        run.addBreak();
                        run.setText(report.getObstacles());
                        run.addBreak();

                        run.setText("3. Ngày mai làm gì:");
                        run.addBreak();
                        run.setText(report.getWorkPlanTomorrow());
                    } else {
                        cell.removeParagraph(0);
                        XWPFParagraph para = cell.addParagraph();
                        para.setAlignment(ParagraphAlignment.CENTER);
                        XWPFRun run = para.createRun();
                        run.setText("CHƯA BÁO CÁO");
                        run.setColor("FF0000");
                        run.setFontFamily("Times New Roman");
                        run.setFontSize(12);
                    }
                }

                document.createParagraph().createRun().addBreak();
            }

            document.write(out);
            return out.toByteArray();
        }
    }

    private static void setCellText(XWPFTableCell cell, String text, boolean bold, ParagraphAlignment alignment) {
        cell.removeParagraph(0);
        XWPFParagraph p = cell.addParagraph();
        p.setAlignment(alignment);
        XWPFRun run = p.createRun();
        run.setText(text);
        run.setFontSize(12);
        run.setBold(bold);
        run.setFontFamily("Times New Roman");
    }

    public static List<List<LocalDate>> timeDay(String idProject, DocADProjectRepository docADProjectRepository) {
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
                    weeks.add(currentWeek);
                    currentWeek = new ArrayList<>();
                }
                currentWeek.add(date);
            }
        }

        if (!currentWeek.isEmpty()) {
            weeks.add(currentWeek);
        }
        log.info("Số tuần import: {}", weeks);
        return weeks;
    }

    private static void mergeCellsHorizontal(XWPFTable table, int row, int fromCell, int toCell) {
        for (int cellIndex = fromCell; cellIndex <= toCell; cellIndex++) {
            XWPFTableCell cell = table.getRow(row).getCell(cellIndex);
            if (cell == null) continue;

            CTTcPr tcPr = cell.getCTTc().addNewTcPr();
            if (cellIndex == fromCell) {
                tcPr.addNewHMerge().setVal(STMerge.RESTART);
            } else {
                tcPr.addNewHMerge().setVal(STMerge.CONTINUE);
            }
        }
    }
}
