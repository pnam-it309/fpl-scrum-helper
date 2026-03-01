//package udpm.hn.server.infrastructure.doc.ADReportProject.utils.excel;
//
//import org.apache.poi.ss.usermodel.*;
//import org.apache.poi.ss.util.CellRangeAddress;
//import org.apache.poi.xssf.usermodel.XSSFSheet;
//import org.apache.poi.xssf.usermodel.XSSFWorkbook;
//import udpm.hn.server.infrastructure.doc.ADReportProject.model.response.DocADProjectMemberResponse;
//import udpm.hn.server.infrastructure.doc.ADReportProject.model.response.PersonalReportResponse;
//import udpm.hn.server.infrastructure.doc.ADReportProject.repository.ADReportRepository;
//import udpm.hn.server.infrastructure.doc.ADReportProject.repository.DocADProjectRepository;
//
//import java.io.*;
//import java.time.*;
//import java.time.format.DateTimeFormatter;
//import java.util.*;
//import java.util.zip.ZipEntry;
//import java.util.zip.ZipOutputStream;
//
//public class ExcelMultiFileSheetZip {
//
//    public static void exportExcelMultiFileSheetZip(String zipFilePath,
//                                                    List<DocADProjectMemberResponse> allMembers,
//                                                    ADReportRepository adReportRepository,
//                                                    DocADProjectRepository docADProjectRepository) throws IOException {
//
//        // Gom nhóm member theo projectId để tạo file riêng cho từng project
//        Map<String, List<DocADProjectMemberResponse>> membersByProject = new LinkedHashMap<>();
//        for (DocADProjectMemberResponse member : allMembers) {
//            String projectId = member.getProjectId();
//            membersByProject.computeIfAbsent(projectId, k -> new ArrayList<>()).add(member);
//        }
//
//        try (FileOutputStream fos = new FileOutputStream(zipFilePath);
//             ZipOutputStream zipOut = new ZipOutputStream(fos)) {
//
//            for (Map.Entry<String, List<DocADProjectMemberResponse>> entry : membersByProject.entrySet()) {
//                String projectId = entry.getKey();
//                List<DocADProjectMemberResponse> members = entry.getValue();
//
//                // Lấy thông tin project từ repository
//                var projectInfo = docADProjectRepository.findProjectStartTimeById(projectId);
//                if (projectInfo == null) {
//                    System.err.println("Project not found: " + projectId);
//                    continue;
//                }
//                String projectName = projectInfo.getName();
//                String sanitizedName = projectName.replaceAll("[\\\\/:*?\"<>|]", "_");
//                String excelFileName = sanitizedName + ".xlsx";
//
//                try (XSSFWorkbook workbook = new XSSFWorkbook();
//                     ByteArrayOutputStream excelOutStream = new ByteArrayOutputStream()) {
//
//                    XSSFSheet sheet = workbook.createSheet("Báo cáo");
//
//                    // Tính ngày bắt đầu tuần sớm nhất (thứ 2 đầu tiên) của tất cả các member trong project
//                    LocalDate minStartDate = members.stream()
//                            .map(m -> docADProjectRepository.findProjectStartTimeById(m.getProjectId()).getStartTime())
//                            .map(timestamp -> Instant.ofEpochMilli(timestamp).atZone(ZoneId.systemDefault()).toLocalDate())
//                            .map(d -> d.with(DayOfWeek.MONDAY))
//                            .min(LocalDate::compareTo)
//                            .orElse(LocalDate.now());
//
//                    LocalDate endDate = LocalDate.now();
//                    List<LocalDate> allDates = new ArrayList<>();
//                    for (LocalDate d = minStartDate; !d.isAfter(endDate); d = d.plusDays(1)) {
//                        allDates.add(d);
//                    }
//
//                    // Map từ ngày thứ 2 trong tuần tới các cột trong excel
//                    Map<LocalDate, List<Integer>> mondayToDateCols = new LinkedHashMap<>();
//                    List<LocalDate> mondays = new ArrayList<>();
//                    for (int i = 0; i < allDates.size(); i++) {
//                        LocalDate date = allDates.get(i);
//                        LocalDate monday = date.with(DayOfWeek.MONDAY);
//                        mondayToDateCols.computeIfAbsent(monday, k -> new ArrayList<>()).add(i);
//                        if (!mondays.contains(monday)) mondays.add(monday);
//                    }
//
//                    // Style cho header
//                    CellStyle headerStyle = workbook.createCellStyle();
//                    Font font = workbook.createFont();
//                    font.setBold(true);
//                    headerStyle.setFont(font);
//                    headerStyle.setAlignment(HorizontalAlignment.CENTER);
//                    headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
//                    headerStyle.setWrapText(true);
//
//                    int startColOffset = 5;
//
//                    // Tạo header cố định
//                    Row header0 = sheet.createRow(0);
//                    Row header1 = sheet.createRow(1);
//                    String[] fixedHeaders = {"STT", "MSSV", "Họ tên sinh viên", "Team", "Dự án"};
//
//                    for (int i = 0; i < fixedHeaders.length; i++) {
//                        Cell cell0 = header0.createCell(i);
//                        cell0.setCellValue(fixedHeaders[i]);
//                        cell0.setCellStyle(headerStyle);
//
//                        Cell cell1 = header1.createCell(i);
//                        cell1.setCellStyle(headerStyle);
//                    }
//
//                    DateTimeFormatter weekFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
//                    DateTimeFormatter dayFormatter = DateTimeFormatter.ofPattern("dd/MM");
//
//                    // Tìm cột cuối của mỗi tuần để tạo border phải cho dễ nhìn
//                    Set<Integer> lastColOfWeek = new HashSet<>();
//                    for (List<Integer> cols : mondayToDateCols.values()) {
//                        lastColOfWeek.add(cols.get(cols.size() - 1) + startColOffset);
//                    }
//
//                    // Header tuần - merge cells theo tuần
//                    for (Map.Entry<LocalDate, List<Integer>> mondayEntry : mondayToDateCols.entrySet()) {
//                        LocalDate monday = mondayEntry.getKey();
//                        List<Integer> cols = mondayEntry.getValue();
//                        int firstCol = cols.get(0) + startColOffset;
//                        int lastCol = cols.get(cols.size() - 1) + startColOffset;
//
//                        String weekTitle = "Tuần (Từ " + monday.format(weekFormatter) + " đến " + monday.plusDays(6).format(weekFormatter) + ")";
//
//                        CellStyle weekHeaderStyle = workbook.createCellStyle();
//                        weekHeaderStyle.cloneStyleFrom(headerStyle);
//                        if (lastColOfWeek.contains(lastCol)) {
//                            weekHeaderStyle.setBorderRight(BorderStyle.MEDIUM);
//                        }
//
//                        Cell cell = header0.createCell(firstCol);
//                        cell.setCellValue(weekTitle);
//                        cell.setCellStyle(weekHeaderStyle);
//
//                        if (firstCol != lastCol) {
//                            sheet.addMergedRegion(new CellRangeAddress(0, 0, firstCol, lastCol));
//                        }
//
//                        // Tạo header ngày trong tuần
//                        for (int i = 0; i < cols.size(); i++) {
//                            LocalDate date = allDates.get(cols.get(i));
//                            int colIndex = cols.get(i) + startColOffset;
//                            Cell cell1 = header1.createCell(colIndex);
//                            cell1.setCellValue(date.format(dayFormatter));
//                            cell1.setCellStyle(headerStyle);
//                        }
//                    }
//
//                    // Dữ liệu chi tiết từng member
//                    int rowIndex = 2;
//                    int stt = 1;
//                    for (DocADProjectMemberResponse member : members) {
//                        Row row = sheet.createRow(rowIndex++);
//                        row.createCell(0).setCellValue(stt++);
//                        row.createCell(1).setCellValue(member.getUserCode());
//                        row.createCell(2).setCellValue(member.getMemberName());
//                        row.createCell(3).setCellValue(member.getProjectCode());
//                        row.createCell(4).setCellValue(projectName);
//
//                        List<PersonalReportResponse> reports = adReportRepository.findAllReportsByStaffProjectId(member.getStaffProjectId());
//                        Map<LocalDate, PersonalReportResponse> reportMap = new HashMap<>();
//                        for (PersonalReportResponse r : reports) {
//                            try {
//                                long reportTimeMillis = Long.parseLong(r.getReportTime());
//                                LocalDate date = Instant.ofEpochMilli(reportTimeMillis).atZone(ZoneId.systemDefault()).toLocalDate();
//                                reportMap.put(date, r);
//                            } catch (Exception ignored) {
//                            }
//                        }
//
//                        for (int i = 0; i < allDates.size(); i++) {
//                            LocalDate date = allDates.get(i);
//                            int col = i + startColOffset;
//                            Cell cell = row.createCell(col);
//
//                            CellStyle wrapStyle = workbook.createCellStyle();
//                            wrapStyle.setWrapText(true);
//                            if (lastColOfWeek.contains(col)) {
//                                wrapStyle.setBorderRight(BorderStyle.MEDIUM);
//                            }
//
//                            if (date.getDayOfWeek() == DayOfWeek.SUNDAY) {
//                                cell.setCellValue("Nghỉ");
//                            } else if (reportMap.containsKey(date)) {
//                                PersonalReportResponse r = reportMap.get(date);
//                                String content = "Hôm nay làm: " + r.getWorkDoneToday()
//                                        + "\nKhó khăn: " + r.getObstacles()
//                                        + "\nNgày mai làm gì: " + r.getWorkPlanTomorrow();
//                                cell.setCellValue(content);
//                            } else {
//                                cell.setCellValue("Chưa báo cáo");
//                            }
//                            cell.setCellStyle(wrapStyle);
//                        }
//                    }
//
//                    // Tự động điều chỉnh độ rộng cột (lưu ý nhiều cột có thể chậm)
//                    for (int i = 0; i < allDates.size() + startColOffset; i++) {
//                        sheet.autoSizeColumn(i);
//                    }
//
//                    // Ghi workbook vào ByteArrayOutputStream
//                    workbook.write(excelOutStream);
//
//                    // Thêm file Excel vào zip
//                    zipOut.putNextEntry(new ZipEntry(excelFileName));
//                    zipOut.write(excelOutStream.toByteArray());
//                    zipOut.closeEntry();
//
//                } catch (Exception e) {
//                    System.err.println("Error generating Excel for project " + projectId + ": " + e.getMessage());
//                    e.printStackTrace();
//                }
//            }
//        }
//    }
//
//
//}
