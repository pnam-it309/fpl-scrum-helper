package udpm.hn.server.infrastructure.doc.ADReportProject.utils.docx;

import org.apache.poi.xwpf.usermodel.*;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STMerge;
import org.springframework.stereotype.Component;
import udpm.hn.server.infrastructure.doc.ADReportProject.model.request.ProjectNameStartTimeProjection;
import udpm.hn.server.infrastructure.doc.ADReportProject.model.response.DocADProjectMemberResponse;
import udpm.hn.server.infrastructure.doc.ADReportProject.model.response.PersonalReportResponse;
import udpm.hn.server.infrastructure.doc.ADReportProject.model.response.ReportProjection;
import udpm.hn.server.infrastructure.doc.ADReportProject.repository.ADReportRepository;
import udpm.hn.server.infrastructure.doc.ADReportProject.repository.DocADProjectRepository;

import java.io.*;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Component
public class DocxReportUtil {


    public static void createSingleDocxFile(String filePath,
                                            DocADProjectMemberResponse member,
                                            ADReportRepository adReportRepository,
                                            DocADProjectRepository docADProjectRepository) {

        List<ReportProjection> reports = adReportRepository.findMembersByProjectIdAndStaffProjectId(member.getProjectId(), member.getStaffProjectId());

        // thời gian bắt đầu
        ProjectNameStartTimeProjection project = docADProjectRepository.findProjectStartTimeById(member.getProjectId());

        XWPFDocument doc = new XWPFDocument();

        // Ngày bắt đầu thực tế
        LocalDate startDate = Instant.ofEpochMilli(project.getStartTime())
                .atZone(ZoneId.systemDefault())
                .toLocalDate();

        // Điều chỉnh startDate về thứ 2 đầu tiên của tuần đó
        while (startDate.getDayOfWeek() != DayOfWeek.MONDAY) {
            startDate = startDate.minusDays(1);
        }

        LocalDate endDate = LocalDate.now();

        for (ReportProjection p : reports) {
            int weekNumber = 1;
            LocalDate currentStart = startDate;

            // Tiêu đề dự án
            XWPFParagraph title = doc.createParagraph();
            XWPFRun runTitle = title.createRun();
            runTitle.setBold(true);
            runTitle.setFontSize(14);
            runTitle.setText("Dự án: " + p.getProjectName());

            // Tên thành viên
            XWPFParagraph name = doc.createParagraph();
            XWPFRun runName = name.createRun();
            runName.setFontSize(12);
            runName.setText("Thành viên: " + p.getStaffOrStudentName()+" - "+p.getStaffOrStudentEmail());

            doc.createParagraph();

            while (!currentStart.isAfter(endDate)) {
                LocalDate currentEnd = currentStart.plusDays(5); // Thứ Hai đến Thứ Bảy

                // Tạo bảng: Tuần 1 tạo 3 hàng (có header), các tuần sau 2 hàng (không header)
                int numRows = (weekNumber == 1) ? 3 : 2;
                XWPFTable table = doc.createTable(numRows, 6);

                if (weekNumber == 1) {
                    // Hàng 0: Header
                    XWPFTableRow header = table.getRow(0);
                    String[] headers = {"THỨ HAI", "THỨ BA", "THỨ TƯ", "THỨ NĂM", "THỨ SÁU", "THỨ BẢY"};
                    for (int i = 0; i < headers.length; i++) {
                        XWPFParagraph para = header.getCell(i).getParagraphs().get(0);
                        XWPFRun run = para.createRun();
                        run.setBold(true);
                        run.setText(headers[i]);
                    }

                }

                // Hàng tiêu đề công tác tuần
                // Nếu là tuần đầu => nằm hàng 1, tuần sau => hàng 0
                XWPFTableRow weekTitleRow = table.getRow((weekNumber == 1) ? 1 : 0);


                XWPFTableCell weekCell = weekTitleRow.getCell(0);
                weekCell.removeParagraph(0);
                XWPFParagraph weekPara = weekCell.addParagraph();
                XWPFRun weekRun = weekPara.createRun();
                weekRun.setBold(true);
                weekRun.setText("A. CÔNG TÁC TUẦN " + weekNumber +
                        " (Từ ngày " + currentStart.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) +
                        " đến " + currentEnd.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + ")");

                for (int i = 1; i < 6; i++) {
                    weekTitleRow.getCell(i).getCTTc().addNewTcPr().addNewHMerge().setVal(STMerge.CONTINUE);
                }
                weekTitleRow.getCell(0).getCTTc().addNewTcPr().addNewHMerge().setVal(STMerge.RESTART);


                // Nếu tuần đầu => hàng 2, tuần sau => hàng 1
                XWPFTableRow contentRow = table.getRow((weekNumber == 1) ? 2 : 1);

                List<PersonalReportResponse> personalReports = adReportRepository.findAllReportsByStaffProjectId(member.getStaffProjectId());

                // Danh sách ngày trong tuần (Thứ Hai -> Thứ Bảy)
                String[] days = {"Thứ Hai", "Thứ Ba", "Thứ Tư", "Thứ Năm", "Thứ Sáu", "Thứ Bảy"};

                // Tạo map để gán dữ liệu theo ngày
                Map<String, List<String>> reportByDay = new HashMap<>();

                // Duyệt từ Thứ Hai -> Thứ Bảy của tuần hiện tại
                for (int i = 0; i < days.length; i++) {
                    LocalDate currentDate = currentStart.plusDays(i);
                    String dayLabel = days[i];

                    Optional<PersonalReportResponse> reportOpt = personalReports.stream()
                            .filter(r -> {
                                try {
                                    long timestamp = Long.parseLong(r.getReportTime());
                                    LocalDate reportDate = Instant.ofEpochMilli(timestamp)
                                            .atZone(ZoneId.systemDefault())
                                            .toLocalDate();
                                    return reportDate.equals(currentDate);
                                } catch (Exception e) {
                                    return false;
                                }
                            })
                            .findFirst();

                    List<String> lines = new ArrayList<>();
                    if (reportOpt.isPresent()) {
                        PersonalReportResponse r = reportOpt.get();
                        lines.add("1. Kết quả hôm nay làm được: " + r.getWorkDoneToday());
                        lines.add("2. Những cái chưa làm được cần hỗ trợ: " + r.getObstacles());
                        lines.add("3. Kế hoạch ngày mai: " + r.getWorkPlanTomorrow());
                    } else {
                        lines.add("Chưa báo cáo");
                    }

                    reportByDay.put(dayLabel, lines);
                }

                // Đưa nội dung xuống dòng vào từng ô trong hàng contentRow
                for (int i = 0; i < days.length; i++) {
                    String day = days[i];
                    XWPFTableCell cell = contentRow.getCell(i);

                    // Xóa paragraph mặc định trong cell
                    cell.removeParagraph(0);

                    List<String> lines = reportByDay.getOrDefault(day, Collections.singletonList(""));

                    XWPFParagraph para = cell.addParagraph();
                    XWPFRun run = para.createRun();

                    for (int j = 0; j < lines.size(); j++) {
                        run.setText(lines.get(j));
                        if (j < lines.size() - 1) {
                            run.addBreak();
                        }
                    }
                }

                doc.createParagraph();
                doc.createParagraph();

                // Tăng tuần và chuyển ngày
                weekNumber++;
                currentStart = currentStart.plusWeeks(1);
            }
        }

        // Ghi file
        try (FileOutputStream out = new FileOutputStream(filePath)) {
            doc.write(out);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

