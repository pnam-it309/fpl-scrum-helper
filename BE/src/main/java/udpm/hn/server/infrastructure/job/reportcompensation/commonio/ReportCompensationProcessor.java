package udpm.hn.server.infrastructure.job.reportcompensation.commonio;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import udpm.hn.server.entity.*;
import udpm.hn.server.infrastructure.config.global.GlobalVariables;

import udpm.hn.server.infrastructure.constant.StatusReport;
import udpm.hn.server.infrastructure.job.reportcompensation.model.request.ReportCompensationRequest;
import udpm.hn.server.infrastructure.job.reportcompensation.repository.StudentReportRepository;
import udpm.hn.server.infrastructure.job.reportcompensation.service.impl.HistoryImportReportCompensationService;
import udpm.hn.server.repository.*;
import udpm.hn.server.utils.ValidationUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Optional;


@Component
@Slf4j
@StepScope
public class ReportCompensationProcessor implements ItemProcessor<ReportCompensationRequest, Report> {

    @Setter(onMethod_ = {@Autowired})
    private GlobalVariables globalVariables;

    @Setter(onMethod_ = {@Autowired})
    private CsvHistoryWriterReport csvHistoryWriter;

    @Setter(onMethod_ = {@Autowired})
    private ValidationUtil validationUtil ;

    @Setter(onMethod_ = {@Autowired})
    private HistoryImportReportCompensationService historyImportReportCompensationService;

    @Autowired
    private StaffProjectRepository staffProjectRepository;

    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private StaffRepository staffRepository;

    @Autowired
    private StudentReportRepository studentRepository;

    @Value("#{jobParameters['idProject']}")
    private String idProject;

    private void saveMessageToDatabase(String record, String message) {
        String fullMessage = record + " - " + message;
        log.info("Saving message to database: " + fullMessage);

        try {
            historyImportReportCompensationService.saveHistory(fullMessage);
            log.info("Successfully saved message to database: " + fullMessage);
        } catch (Exception e) {
            log.error("Error saving history message: " + fullMessage, e);
        }
    }


    @Override
    public Report process(ReportCompensationRequest item) throws Exception {


        if(item.getEmail().isEmpty()){
            saveMessageToDatabase(item.getName() + " - " + item.getName() , "Tên không được để trốnh");
            csvHistoryWriter.writeHistory(item.getName() + " - " + item.getName() , "Tên không được để trốnh");
            return null;
        }

        if(item.getName().isEmpty()){
            saveMessageToDatabase(item.getEmail() + " - " + item.getName() , "Email không được để trốnh");
            csvHistoryWriter.writeHistory(item.getEmail() + " - " + item.getName() , "Email không được để trốnh");
            return null;
        }

        if(item.getNgayNay().isEmpty()){
            saveMessageToDatabase(item.getNgayNay() + " - " + item.getName()  , "Chưa báo cáo ngày hôm nay");
            csvHistoryWriter.writeHistory(item.getNgayNay() + " - " + item.getName()  , "Chưa báo cáo ngày hôm nay");
            return null;
        }

        if(item.getNgayMai().isEmpty()){
            saveMessageToDatabase(item.getNgayMai() + " - " + item.getName()  , "Chưa báo cáo ngày mai");
            csvHistoryWriter.writeHistory(item.getNgayMai() + " - " + item.getName()  , "Chưa báo cáo ngày mai");
            return null;
        }

        if(item.getKhoKhan().isEmpty()){
            saveMessageToDatabase(item.getKhoKhan() + " - " + item.getName()  , "Chưa báo cáo tình trạng ngày hôm nay (khó khăn)");
            csvHistoryWriter.writeHistory(item.getKhoKhan() + " - " + item.getName()  , "Chưa báo cáo tình trạng ngày hôm nay (khó khăn)");
            return null;
        }

        if(item.getNgayBaoCao().isEmpty()){
            saveMessageToDatabase(item.getNgayBaoCao() + " - " + item.getName()  , "Không được để trống ngày báo cáo");
            csvHistoryWriter.writeHistory(item.getNgayBaoCao() + " - " + item.getName()  , "Không được để trong ngày báo cáo");
            return null;
        }

        // 1. Parse ngày báo cáo vào lúc 20:00
        String ngayBaoCao = item.getNgayBaoCao();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        sdf.setLenient(false);
        Long reportTimestamp;
        try {
            long midnightMillis = sdf.parse(ngayBaoCao).getTime();
            reportTimestamp = midnightMillis + (20 * 60 * 60 * 1000);
        } catch (ParseException e) {
            saveMessageToDatabase(ngayBaoCao + " - " + item.getName(), "Định dạng ngày báo cáo không hợp lệ. Định dạng đúng là dd/MM/yyyy");
            csvHistoryWriter.writeHistory(ngayBaoCao + " - " + item.getName(), "Định dạng ngày báo cáo không hợp lệ. Định dạng đúng là dd/MM/yyyy");
            return null;
        }

        // 2. Check user tồn tại
        Optional<Staff> staffOpt = staffRepository.findByEmailFeOrEmailFpt(item.getEmail(), item.getEmail());
        Optional<Student> studentOpt = studentRepository.findByEmail(item.getEmail());

        if (staffOpt.isEmpty() && studentOpt.isEmpty()) {
            saveMessageToDatabase(item.getEmail() + " - " + item.getName(), "Không tìm thấy thông tin email");
            csvHistoryWriter.writeHistory(item.getEmail() + " - " + item.getName(), "Không tìm thấy thông tin email");
            return null;
        }


        // 3. Check project
        log.info("Processor - idProject: {}", idProject);
        Optional<Project> projectOpt = projectRepository.findById(idProject);
//        if (projectOpt.isEmpty()) {
//            saveMessageToDatabase(item.getMaDa() + " - " + item.getName(), "Mã dự án không tồn tại");
//            csvHistoryWriter.writeHistory(item.getMaDa() + " - " + item.getName(), "Mã dự án không tồn tại");
//            return null;
//        }
        Project project = projectOpt.get();

        // 4. Check staff/student đã báo cáo ngày đó chưa
        boolean existsReport = reportRepository.existsByReportTimeAndStaffEmail(reportTimestamp, item.getEmail(), idProject)
                || reportRepository.existsByReportTimeAndStaffProject_Student_Email(reportTimestamp, item.getEmail());

        if (existsReport) {
            saveMessageToDatabase(item.getEmail() + " - " + item.getName(), "Đã có báo cáo vào ngày này");
            csvHistoryWriter.writeHistory(item.getEmail() + " - " + item.getName(), "Đã có báo cáo vào ngày này");
            return null;
        }

        // 5. Check user có trong project không
        Optional<StaffProject> staffProjectOpt = staffOpt.isPresent()
                ? staffProjectRepository.findByStaffAndProject(staffOpt.get(), project)
                : staffProjectRepository.findByStudentAndProject(studentOpt.get(), project);

        if (staffProjectOpt.isEmpty()) {
            saveMessageToDatabase(item.getEmail() + " - " + item.getName(), "Không thuộc dự án này");
            csvHistoryWriter.writeHistory(item.getEmail() + " - " + item.getName(), "Không thuộc dự án này");
            return null;
        }

        if (projectOpt.isEmpty()) {
            String error = "Không tìm thấy dự án" ;
            saveMessageToDatabase(item.getEmail() + " - " + item.getName(), error);
            csvHistoryWriter.writeHistory(item.getEmail() + " - " + item.getName(), error);
            log.error(error);
            return null;
        }

        Report report = new Report();

        report.setWorkDoneToday(item.getNgayNay());
        report.setObstacles(item.getKhoKhan());
        report.setWorkPlanTomorrow(item.getNgayMai());
        report.setProject(project);
        report.setStaffProject(staffProjectOpt.get());
        report.setReportTime(reportTimestamp);
        report.setStatusReport(StatusReport.BAO_CAO_MUON);

        return report;

    }

}
