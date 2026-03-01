package udpm.hn.server.core.manage.report.service.impl;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import udpm.hn.server.core.common.base.PageableObject;
import udpm.hn.server.core.common.base.ResponseObject;
import udpm.hn.server.core.manage.report.model.request.HolidayCreateRequest;
import udpm.hn.server.core.manage.report.model.request.MAReportRequest;
import udpm.hn.server.core.manage.report.model.request.MAReportUserRequest;
import udpm.hn.server.core.manage.report.model.response.MAReportResponse;
import udpm.hn.server.core.manage.report.repository.MAReportRepository;
import udpm.hn.server.core.manage.report.service.MAReportService;
import udpm.hn.server.core.manage.user.model.response.MAUserProjectResponse;
import udpm.hn.server.core.manage.user.repository.MAUserProject;
import udpm.hn.server.entity.*;
import udpm.hn.server.infrastructure.constant.EntityStatus;
import udpm.hn.server.infrastructure.constant.StatusReport;
import udpm.hn.server.infrastructure.doc.ADReportProject.repository.DocADProjectRepository;
import udpm.hn.server.infrastructure.job.report.BaseReportDay;
import udpm.hn.server.infrastructure.job.report.WordGenerator;
import udpm.hn.server.infrastructure.job.reportcompensation.model.response.HistoryImportReportCompensationResponse;
import udpm.hn.server.infrastructure.job.todolist.model.response.HistoryImportTodoResponse;
import udpm.hn.server.repository.*;
import udpm.hn.server.utils.Helper;
import udpm.hn.server.utils.UserContextHelper;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


@Service
@RequiredArgsConstructor
@Slf4j
public class MAReportServiceImpl implements MAReportService {

    private final MAReportRepository maReportRepository;

    private final DocADProjectRepository docADProjectRepository;

    private final MAUserProject maUserProject;

    private final RoleRepository roleRepository;

    private final StaffRepository staffRepository;

    private final UserContextHelper userContextHelper;

    private final HistoryImportReportCompensationRepository historyImportReportCompensationRepository;

    private final HolidayRepository holidayRepository;
    private final ProjectRepository projectRepository;

    @Override
    public ResponseObject<?> getAllReport(MAReportRequest request) {

        Long timestamp =Long.valueOf(request.getTime());
        LocalDate date = Instant.ofEpochMilli(timestamp)
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
        Long dateStart = date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
        Long dateEnd = date.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();

        Pageable page = Helper.createPageable(request,"createDate");
        return new ResponseObject<>(maReportRepository.getAllReport(page,dateStart,dateEnd, request.getIdProject()), HttpStatus.OK,"Lấy dũ liệu báo cáo thành công");

    }

    @Override
    public ResponseObject<?> findAllReport(MAReportRequest request) {
        Optional<Project> optionalProject = docADProjectRepository.findById(request.getIdProject());
        if(optionalProject.isEmpty()){
            return new ResponseObject<>().error(HttpStatus.NO_CONTENT,"Không tìm thấy project");
        }

        Project project = optionalProject.get();
        List<MAReportResponse> list  = maReportRepository.getAllReport(project.getStartTime(),project.getEndTime(), request.getIdProject());
        log.info("Danh sách báo cáo dự án :===>{}",list.stream().toList());
        return new ResponseObject<>(list,HttpStatus.OK,"Lấy dữ liệu thành công");
    }


    @Override
    public byte[] readFileDoc(String idProject, MAReportUserRequest request) {
        List<String> listIdStaff = request.getIdUser();

        List<MAUserProjectResponse> listUser = listIdStaff.stream()
                .map(t -> maUserProject.listUser(idProject, t))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());

        try (ByteArrayOutputStream zipBaos = new ByteArrayOutputStream();
             ZipOutputStream zipOut = new ZipOutputStream(zipBaos)) {

            for (MAUserProjectResponse response : listUser) {

                String code = response.getCodeStaff() != null ? response.getCodeStaff() : response.getCodeStudent();
                String name = response.getNameStaff() != null ? response.getNameStaff() : response.getNameStudent();

                byte[] data = readFile(idProject, response);
                String fileName = "BaoCao_"  + code + " -- " + name + ".docx";

                ZipEntry entry = new ZipEntry(fileName);
                zipOut.putNextEntry(entry);
                zipOut.write(data);
                zipOut.closeEntry();
            }

            zipOut.finish();
            return zipBaos.toByteArray();

        } catch (IOException e) {
            throw new RuntimeException("Lỗi tạo file zip", e);
        }
    }





    private byte[] readFile(String idProject,MAUserProjectResponse response ){

        String TitleFile = "BÁO CÁO THỰC TẬP";
        // Tạo file word ra byte[]
        byte[] data = new byte[0];
        try {
            data = WordGenerator.createWordDocument(
                    TitleFile,idProject,response,
                    docADProjectRepository,maReportRepository,maUserProject
            );
            Files.write(Paths.get("test.docx"), data);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return data;
    }

    @Override
    public ResponseObject<?> exportReport(HttpServletResponse response, String idProject, MAReportUserRequest request) {
        log.info("list idStaffProject:{}",request.toString());
        log.info("Id project export:{}",idProject);

        List<String> listIdStaff = request.getIdUser();

        List<MAUserProjectResponse> listUser = listIdStaff.stream()
                .map(t -> maUserProject.listUser(idProject, t))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());

        try {
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "attachment; filename=report.xlsx");
    
            String[] headers = {"STT",
                "MSSV", "Họ và sinh viên",
                "Team", "Dự án"
            };

            new BaseReportDay<>(maUserProject,idProject,docADProjectRepository, maReportRepository)
                .writeHeaderLine(headers)
                .writeDataLines(listUser)
                .export(response);

            return new ResponseObject<>(null, HttpStatus.OK, "Xuất báo cáo thành công");
    
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseObject<>(null, HttpStatus.INTERNAL_SERVER_ERROR, "Xuất báo cáo thất bại: " + e.getMessage());
        }
    }

    @Override
    public ResponseObject<?> getLogsImportReport(int page, int size) throws FileNotFoundException {
        if (page < 1) {
            page = 1;
        }

        String staffId = userContextHelper.getCurrentUserId();
        List<Role> roles = roleRepository.findRoleByStaff(staffId);
        String email = staffRepository.getEmailFpt(staffId);

        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("createdDate").descending());

        Page<HistoryImportReportCompensation> historyPage = historyImportReportCompensationRepository.findByEmail(email,pageable);

        Page<HistoryImportReportCompensationResponse> responsePage = historyPage.map(history ->
                new HistoryImportReportCompensationResponse(
                        history.getId(),
                        email,
                        history.getMessage(),
                        roles,
                        history.getCreatedDate()
                )
        );

        return ResponseObject.successForward(
                PageableObject.of(responsePage),
                "Lấy lịch sử import sinh viên thành công"
        );
    }

    @Override
    public ResponseObject<?> getAllReports() {
        try {
            Long count = maReportRepository.countAllReports();

            if (count == 0) {
                return new ResponseObject<>(count, HttpStatus.NO_CONTENT, "Không có báo cáo nào.");
            }

            return new ResponseObject<>(count, HttpStatus.OK, "Tổng số báo cáo: " + count);
        } catch (Exception e) {
            return new ResponseObject<>(null, HttpStatus.INTERNAL_SERVER_ERROR, "Đã xảy ra lỗi khi đếm báo cáo.");
        }
    }

    @Override
    public ResponseObject<?> createMultipleHolidays(HolidayCreateRequest request, String idProject) {
        Optional<Project> optionalProject = projectRepository.findProjectById(idProject);

        if (optionalProject.isEmpty()) {
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Không tìm thấy project.");
        }

        Project project = optionalProject.get();
        List<Holiday> holidaysToSave = new ArrayList<>();

        for (Long date : request.getDates()) {
            long startOfDay = date;
            long endOfDay = date + 86400000 - 1;
            String code = generateHolidayCode(date, idProject);

            // Check ngày đã tồn tại trong DB (trong khung ngày)
            List<Holiday> existing = holidayRepository.findByDateInDayAndProjectId(startOfDay, endOfDay, idProject);

            if (!existing.isEmpty()) {
                Holiday old = existing.get(0);
                if (old.getStatus() == EntityStatus.INACTIVE) {
                    // Đã tồn tại nhưng bị disable → bật lại
                    old.setStatus(EntityStatus.ACTIVE);
                    old.setDescribe(request.getDescribe());
                    old.setStatusReport(StatusReport.NGHI);
                    holidaysToSave.add(old);
                    continue;
                }

                return new ResponseObject<>(null, HttpStatus.CONFLICT, "Ngày nghỉ đã tồn tại");
            }

            // Tạo holiday mới
            Holiday holiday = new Holiday();
            holiday.setDate(date);
            holiday.setDescribe(request.getDescribe());
            holiday.setStatusReport(StatusReport.NGHI);
            holiday.setStatus(EntityStatus.ACTIVE);
            holiday.setProject(project);
            holiday.setCode(code);

            holidaysToSave.add(holiday);
        }

        holidayRepository.saveAll(holidaysToSave);
        return new ResponseObject<>(null, HttpStatus.CREATED, "Đã thêm hoặc cập nhật " + holidaysToSave.size() + " ngày nghỉ.");
    }


    @Override
    public ResponseObject<?> detailHoliday(String idProject) {
        return new ResponseObject<>(
                holidayRepository.getAllHoliday(idProject),
                HttpStatus.OK,
                "Lấy dữ liệu báo cáo thành công"
        );
    }

    @Override
    public ResponseObject<?> updateHolidaysStatus(List<Long> dates, String idProject) {
        Optional<Project> optionalProject = projectRepository.findProjectById(idProject);

        if (optionalProject.isEmpty()) {
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Không tìm thấy project.");
        }

        List<Holiday> holidaysToSave = new ArrayList<>();
        int updatedCount = 0;

        for (Long date : dates) {
            long startOfDay = date;
            long endOfDay = date + 86400000 - 1;

            List<Holiday> holidays = holidayRepository.findByDateInDayAndProjectId(startOfDay, endOfDay, idProject);

            for (Holiday existingHoliday : holidays) {
                String code = generateHolidayCode(existingHoliday.getDate(), idProject);

                if (!existingHoliday.getCode().equals(code) && holidayRepository.existsByCode(code)) {
                    return new ResponseObject<>(null, HttpStatus.CONFLICT, "Mã ngày nghỉ đã tồn tại: " + code);
                }

                existingHoliday.setStatus(EntityStatus.INACTIVE);
                holidaysToSave.add(existingHoliday);
                updatedCount++;
            }
        }

        holidayRepository.saveAll(holidaysToSave);

        String message = updatedCount > 0 ? "Cập nhật ngày nghỉ thành công!" : "Không có ngày nào được cập nhật.";
        return new ResponseObject<>(null, HttpStatus.OK, message);
    }


    private String generateHolidayCode(Long timestamp, String projectId) {
        Instant instant = Instant.ofEpochMilli(timestamp);
        ZonedDateTime date = instant.atZone(ZoneId.systemDefault());

        int day = date.getDayOfMonth();
        int month = date.getMonthValue();
        int year = date.getYear();

        return String.format("N%d_%d_%d_%s", day, month, year, projectId);
    }

}
