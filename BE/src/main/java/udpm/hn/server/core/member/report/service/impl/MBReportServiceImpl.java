package udpm.hn.server.core.member.report.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import udpm.hn.server.core.common.base.PageableObject;
import udpm.hn.server.core.common.base.ResponseObject;
import udpm.hn.server.core.member.report.model.request.UpdateReportSettingRequest;
import udpm.hn.server.core.member.report.model.response.MBReportResponse;
import udpm.hn.server.core.member.report.model.response.MBReportSettingReponse;
import udpm.hn.server.core.member.report.model.response.ReportRateResponse;
import udpm.hn.server.core.member.report.repository.MBReportSetting;
import udpm.hn.server.entity.*;
import udpm.hn.server.core.member.report.model.request.MBReportRequest;
import udpm.hn.server.core.member.report.repository.MBReportRepository;
import udpm.hn.server.core.member.report.repository.MBTodoProjectRepository;
import udpm.hn.server.core.member.report.service.MBReportService;
import udpm.hn.server.infrastructure.constant.EntityStatus;
import udpm.hn.server.infrastructure.constant.Help;
import udpm.hn.server.infrastructure.constant.StatusReport;
import udpm.hn.server.repository.*;
import udpm.hn.server.utils.Helper;
import udpm.hn.server.utils.UserContextHelper;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class MBReportServiceImpl implements MBReportService {

    private final MBReportRepository mbReportRepository;

    private final MBTodoProjectRepository todoProjectRepository;

    private final StaffProjectRepository staffProjectRepository;

    private final ProjectRepository projectRepository;

    private final UserContextHelper userContextHelper;

    private final MBReportSetting reportSettingRepository;

    private final HolidayRepository holidayRepository;

    private final PhaseProjectRepository phaseProjectRepository;

    @Override
    public ResponseObject<?> getAll(MBReportRequest request, String idProject) {
        Pageable pageable = Helper.createPageable(request, "reportTime");

        // Lấy danh sách báo cáo
        List<MBReportResponse> reports = mbReportRepository
                .getAll(pageable, idProject, userContextHelper, request)
                .getContent();

        // Lấy danh sách ngày nghỉ (không lọc theo thời gian)
        List<MBReportResponse> holidays = holidayRepository
                .findHolidayReports(idProject);

        // Gộp dữ liệu
        List<MBReportResponse> merged = new ArrayList<>();
        merged.addAll(reports);
        merged.addAll(holidays);

        // Sắp xếp theo reportTime mới nhất trước
        merged.sort(Comparator.comparing(MBReportResponse::getReportTime, Comparator.nullsLast(Long::compareTo)).reversed());

        // Phân trang thủ công
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), merged.size());
        List<MBReportResponse> pagedResult = merged.subList(start, end);

        Page<MBReportResponse> pageResult = new PageImpl<>(pagedResult, pageable, merged.size());

        return new ResponseObject<>(
                PageableObject.of(pageResult),
                HttpStatus.OK,
                "Lấy danh sách báo cáo thành công"
        );
    }


    @Override
    public ResponseObject<?> getReportSetting(String idProject) {
        MBReportSettingReponse setting = reportSettingRepository
                .getSettingByProjectId(idProject)
                .orElse(null);

        return new ResponseObject<>(setting, HttpStatus.OK, "Lấy dữ liệu cấu hình báo cáo thành công");
    }


    @Override
    public ResponseObject<?> updateReportSetting(String idProject, UpdateReportSettingRequest request) {
        Optional<ReportSetting> optional = reportSettingRepository.findByProject_Id(idProject);

        ReportSetting setting;
        if (optional.isPresent()) {
            setting = optional.get();
        } else {
            // Nếu chưa có thì tạo mới
            setting = new ReportSetting();
            Project project = projectRepository.findById(idProject)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy dự án với ID: " + idProject));
            setting.setProject(project);
        }

        setting.setStopReportHour(request.getStopReportHour());
        reportSettingRepository.save(setting);

        return new ResponseObject<>(setting, HttpStatus.OK, "Cập nhật cấu hình báo cáo thành công");
    }


    @Override
    public ResponseObject<?> detail(String id, String idProject) {
        return new ResponseObject<>(
                mbReportRepository.detail(id, idProject, userContextHelper),
                HttpStatus.OK,
                "Lấy dữ liệu báo cáo thành công"
        );
    }

    @Override
    public ResponseObject<?> add(MBReportRequest request, String idProject) {
        Optional<StaffProject> optionalStaffProject = staffProjectRepository.getStaffProjectByStaffAndProject(userContextHelper, idProject);

        Long setting = reportSettingRepository.getStopReportHour(idProject);

        if (setting == null) {
            Optional<Project> optionalProject = projectRepository.findById(idProject);
            if (optionalProject.isEmpty()) {
                return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Không tìm thấy thông tin dự án");
            }

            Project project = optionalProject.get();

            ReportSetting newSetting = new ReportSetting();
            newSetting.setProject(project);

            LocalTime defaultStopTime = LocalTime.of(23, 0);
            long defaultStopMillis = LocalDateTime.of(LocalDate.now(), defaultStopTime)
                    .atZone(ZoneId.systemDefault())
                    .toInstant()
                    .toEpochMilli();

            newSetting.setStopReportHour(defaultStopMillis);
            reportSettingRepository.save(newSetting);
            setting = defaultStopMillis;
        }

        if (optionalStaffProject.isPresent()) {
            StaffProject staffProject = optionalStaffProject.get();
            Optional<Project> optionalProject = projectRepository.findProjectById(idProject);

            if (optionalProject.isPresent()) {
                Project project = optionalProject.get();
                Report report = new Report();
                report.setWorkDoneToday(request.getWorkDoneToday());
                report.setObstacles(request.getObstacles());
                report.setHelp(request.getHelp() ? Help.CAN_GIUP_DO : Help.KHONG_CO_VAN_DE);                report.setWorkPlanTomorrow(request.getWorkPlanTomorrow());
                report.setStatus(EntityStatus.ACTIVE);

                long nowMillis = new Date().getTime();

                Instant stopInstant = Instant.ofEpochMilli(setting);
                LocalTime stopTime = stopInstant.atZone(ZoneId.systemDefault()).toLocalTime();

                long todayStopMillis = LocalDateTime.of(LocalDate.now(), stopTime)
                        .atZone(ZoneId.systemDefault())
                        .toInstant()
                        .toEpochMilli();

                report.setReportTime(nowMillis);
                report.setStatusReport(nowMillis <= todayStopMillis ? StatusReport.DA_BAO_CAO : StatusReport.BAO_CAO_MUON);
                report.setStaffProject(staffProject);
                report.setProject(project);
                mbReportRepository.save(report);

                return new ResponseObject<>(null, HttpStatus.CREATED, "Thêm báo cáo thành công");
            } else {
                return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Không tìm thấy thông tin dự án");
            }
        } else {
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Không tìm thấy thông tin dự án của nhân viên");
        }
    }


    @Override
    public ResponseObject<?> update(String id, MBReportRequest request, String idProject) {
        Optional<Report> optionalReport = mbReportRepository.findById(id);
        Long setting = reportSettingRepository.getStopReportHour(idProject);

        if (optionalReport.isPresent()) {
            Report report = optionalReport.get();
            report.setWorkDoneToday(request.getWorkDoneToday());
            report.setObstacles(request.getObstacles());
            report.setHelp(request.getHelp() ? Help.CAN_GIUP_DO : Help.KHONG_CO_VAN_DE);
            report.setWorkPlanTomorrow(request.getWorkPlanTomorrow());
            report.setWorkPlanTomorrow(request.getWorkPlanTomorrow());
            report.setStatus(EntityStatus.ACTIVE);

            long nowMillis = new Date().getTime();

            Instant stopInstant = Instant.ofEpochMilli(setting);
            LocalTime stopTime = stopInstant.atZone(ZoneId.systemDefault()).toLocalTime();

            long todayStopMillis = LocalDateTime.of(LocalDate.now(), stopTime)
                    .atZone(ZoneId.systemDefault())
                    .toInstant()
                    .toEpochMilli();

            report.setReportTime(nowMillis);
            report.setStatusReport(nowMillis <= todayStopMillis ? StatusReport.DA_BAO_CAO : StatusReport.BAO_CAO_MUON);

            mbReportRepository.save(report);

            return new ResponseObject<>(null, HttpStatus.CREATED, "Sửa báo cáo thành công");
        } else {
            return new ResponseObject<>(null, HttpStatus.OK, "Không có báo cáo này");
        }
    }


    @Override
    public ResponseObject<?> detailByDate(Long reportTime, String idProject) {
        LocalDateTime startOfDay = Instant.ofEpochMilli(reportTime)
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
                .atStartOfDay();

        Long startTimestamp = startOfDay.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        Long endTimestamp = startTimestamp + 86400000;

        String idUser = userContextHelper.getCurrentUserId();

        String reportId = mbReportRepository.findIdByReportTime(startTimestamp, endTimestamp,idUser, idProject);

        if (reportId != null) {
            return new ResponseObject<>(reportId, HttpStatus.OK, "Lấy ID báo cáo thành công");
        } else {
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Không tìm thấy báo cáo cho ngày này");
        }
    }

    @Override
    public ResponseObject<?> getReportRateByPhase(String phaseId, String projectId) {
        PhaseProject phase = phaseProjectRepository.findById(phaseId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phase"));

        Long startTime = phase.getStartTime();
        Long endTime = normalizeEndToEndOfDay(phase.getEndTime());

        // Số ngày trong phase
        long totalDays = ChronoUnit.DAYS.between(
                Instant.ofEpochMilli(startTime).atZone(ZoneId.systemDefault()).toLocalDate(),
                Instant.ofEpochMilli(endTime).atZone(ZoneId.systemDefault()).toLocalDate()
        ) + 1;

        // Số ngày nghỉ trong phase
        Long holidayCount = holidayRepository.countByProjectIdAndDateBetween(projectId, startTime, endTime);

        // Số lượng báo cáo hợp lệ
        Long reportCount = mbReportRepository.countValidReportsInRange(projectId, startTime, endTime, userContextHelper);

        long validWorkDays = totalDays - holidayCount;
        double reportRate = (validWorkDays == 0) ? 0.0 :
                BigDecimal.valueOf((double) reportCount / validWorkDays * 100)
                        .setScale(2, RoundingMode.HALF_UP)
                        .doubleValue();

        ReportRateResponse response = new ReportRateResponse(totalDays, holidayCount, reportCount, reportRate);

        return new ResponseObject<>(response, HttpStatus.OK, "Tỷ lệ báo cáo trong phase");
    }


    private Long normalizeEndToEndOfDay(Long timestamp) {
        return Instant.ofEpochMilli(timestamp)
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
                .atTime(LocalTime.MAX)
                .atZone(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli();
    }

}
