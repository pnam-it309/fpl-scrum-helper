package udpm.hn.server.core.member.capacityestimate.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import udpm.hn.server.core.common.base.ResponseObject;
import udpm.hn.server.core.member.capacityestimate.model.request.CapacityEstimatePageRequest;
import udpm.hn.server.core.member.capacityestimate.model.request.CapacityEstimateRequest;
import udpm.hn.server.core.member.capacityestimate.model.response.capacityEstimateResponse;
import udpm.hn.server.core.member.capacityestimate.service.CapacityEstimateService;
import udpm.hn.server.entity.CapacityEstimate;
import udpm.hn.server.entity.PhaseProject;
import udpm.hn.server.entity.Project;
import udpm.hn.server.entity.Staff;
import udpm.hn.server.entity.StaffProject;
import udpm.hn.server.entity.Student;
import udpm.hn.server.infrastructure.constant.EntityStatus;
import udpm.hn.server.repository.CapacityEstimateRepository;
import udpm.hn.server.repository.PhaseProjectRepository;
import udpm.hn.server.repository.ProjectRepository;
import udpm.hn.server.repository.StaffProjectRepository;
import udpm.hn.server.repository.StaffRepository;
import udpm.hn.server.repository.StudentRepository;
import udpm.hn.server.repository.VelocityRecordRepository;
import udpm.hn.server.utils.Helper;
import udpm.hn.server.utils.StatusPhase;
import udpm.hn.server.utils.UserContextHelper;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CapacityEstimateServiceImpl implements CapacityEstimateService {

    private final PhaseProjectRepository phaseProjectRepository;

    private final StaffProjectRepository staffProjectRepository;

    private final StaffRepository staffRepository;

    private final ProjectRepository projectRepository;

    private final StudentRepository studentRepository;

    private final CapacityEstimateRepository capacityEstimateRepository;

    private final UserContextHelper userContextHelper;

    private final VelocityRecordRepository velocityRecordRepository;

    @Override
    public ResponseObject<?> getAll(CapacityEstimatePageRequest request) {

        log.info("Request nhận vào trong get All Capacity estimate : ===> {}", request.toString());

        Pageable page = Helper.createPageable(request);

        Optional<Staff> staffOpt = staffRepository.findById(request.getIdUser());

        Project project = project(request.getIdProject());

        StaffProject staffProject;

        if (staffOpt.isEmpty()) {
            Optional<Student> studentOpt = studentRepository.findById(request.getIdUser());
            staffProject = staffProject(studentOpt.get(), project);
        } else {
            staffProject = staffProject(staffOpt.get(), project);
        }
        return new ResponseObject<>(capacityEstimateRepository.getAll(page, request.getIdProject(), staffProject.getId()), HttpStatus.OK, "Lấy dữ liệu thành công");
    }


    @Override
    public ResponseObject<?> getAllSprint(String idProject) {
//        Set<StatusPhase> validPhases = Set.of(StatusPhase.CHUA_HOAN_THANH, StatusPhase.DANG_LAM);

        Optional<Staff> staffOpt = staffRepository.findById(userContextHelper.getCurrentUserId());
        Project project = project(idProject);

        StaffProject staffProject;
        if (staffOpt.isEmpty()) {
            Optional<Student> studentOpt = studentRepository.findById(userContextHelper.getCurrentUserId());
            staffProject = staffProject(studentOpt.get(), project);
        } else {
            staffProject = staffProject(staffOpt.get(), project);
        }

        List<PhaseProject> list = phaseProjectRepository.findAll().stream().
                filter(a -> a.getStatus() == EntityStatus.ACTIVE)
                .filter(a -> a.getProject().getId().equals(idProject))
//                .filter(a -> validPhases.contains(a.getStatusPhase()))
                .sorted(Comparator.comparing(PhaseProject::getStartTime, Comparator.nullsLast(Comparator.naturalOrder())))
                .collect(Collectors.toList());

        Set<PhaseProject> phaseProjectsInCapacityEstimate = capacityEstimateRepository.findAll()
                .stream().filter(capacityEstimate -> capacityEstimate.getStaffProject() != null)
                .filter(capacityEstimate -> capacityEstimate.getStaffProject().equals(staffProject))
                .map(capacityEstimate -> capacityEstimate.getPhaseProject())
                .collect(Collectors.toSet());

        List<PhaseProject> filteredList = list.stream().filter(phaseProject -> !phaseProjectsInCapacityEstimate.contains(phaseProject)).collect(Collectors.toList());

        return new ResponseObject<>(list, HttpStatus.OK, "Lấy dữ liệu thành công");
    }


    @Override
    public ResponseObject<?> createCapacityEstimate(CapacityEstimateRequest estimate) {
        log.info("Request truyền vào trong capacityEstimate : =====> {}", estimate.toString());

        PhaseProject phaseProject = phaseProject(estimate.getIdSprint());

        Optional<Staff> staffOpt = staffRepository.findById(estimate.getIdUser());

        Project project = project(estimate.getIdProject());

        StaffProject staffProject;


        if (staffOpt.isEmpty()) {
            Optional<Student> studentOpt = studentRepository.findById(estimate.getIdUser());
            staffProject = staffProject(studentOpt.get(), project);
        } else {
            staffProject = staffProject(staffOpt.get(), project);
        }

        Set<PhaseProject> phaseProjectsInCapacityEstimate = capacityEstimateRepository.findAll()
                .stream().filter(capacityEstimate -> capacityEstimate.getStaffProject() != null)
                .filter(capacityEstimate -> capacityEstimate.getStaffProject().equals(staffProject))
                .map(capacityEstimate -> capacityEstimate.getPhaseProject())
                .collect(Collectors.toSet());

        if(phaseProjectsInCapacityEstimate.contains(phaseProject)){
            return new ResponseObject<>().error(HttpStatus.NO_CONTENT,"Đã ước lượng cho giai đoạn này!");
        }

        CapacityEstimate capacityEstimate = CapacityEstimate.builder()
//                .availableHours(estimate.getAvailableHours())
                .workday(estimate.getWorkday()).describe(estimate.getDescriptions()).phaseProject(phaseProject).staffProject(staffProject).build();

        capacityEstimateRepository.save(capacityEstimate);
        return new ResponseObject<>().success("Thêm thành công");
    }

    @Override
    public ResponseObject<?> updateCapacityEstimate(String id, CapacityEstimateRequest request) {

        log.info("request truyền vào update capacity ====> {}{}", id, request.toString());


        Optional<CapacityEstimate> optionalCapacityEstimate = capacityEstimateRepository.findById(id);
        if (optionalCapacityEstimate.isEmpty()) {
            return new ResponseObject<>().error(HttpStatus.NOT_FOUND, "Không tìm thấy capacity estimate");
        }

        CapacityEstimate capacityEstimate = optionalCapacityEstimate.get();

        if (request.getIdSprint() != null) {
            PhaseProject phaseProject = phaseProject(request.getIdSprint());

            capacityEstimate.setPhaseProject(phaseProject);


        }

        if (request.getDescriptions() != null) {
            capacityEstimate.setDescribe(request.getDescriptions());
        }

        if (request.getWorkday() != null) {
            capacityEstimate.setWorkday(request.getWorkday());
        }

        capacityEstimateRepository.save(capacityEstimate);

        return new ResponseObject<>().success(capacityEstimate, "Cập nhật capacity estimate thành công");
    }

    @Override
    public ResponseObject<?> detailCapacityEstimate(String id) {
        log.info("id truyền vào detail capacity =====>{}", id);
        capacityEstimateResponse estimate = capacityEstimateRepository.detailCapacityEstimate(id).orElseThrow(() -> new RuntimeException("CapacityEstimate not found with id" + id));
        log.info("Duẽ liệu detail lên : ===> {}", estimate);
        return new ResponseObject<>(estimate, HttpStatus.OK, "Lấy dữ liệu thành công");
    }

    @Override
    public ResponseObject<?> deleteCapacityEstimate(String id) {

        capacityEstimateRepository.deleteById(id);
        return new ResponseObject<>().success(HttpStatus.OK, "Xóa thành công");
    }

    @Override
    public ResponseObject<?> CapacityEstimateBySprint(String id) {

        Map<String,capacityEstimateResponse> listEstimateBySprint = new HashMap<>();

        List<capacityEstimateResponse> estimateList = capacityEstimateRepository.capacityEstimateBySprint(id);
        for (capacityEstimateResponse estimate : estimateList) {
            String IdPhase = estimate.getIdPhase(); // hoặc trường tương ứng
            listEstimateBySprint.put(IdPhase, estimate);
        }

        log.info("Dữ liệu ===> {}",listEstimateBySprint.toString());

        return new ResponseObject<>(estimateList,HttpStatus.OK,"Lấy dữ liệu thành công");
    }

    private PhaseProject phaseProject(String id) {
        return phaseProjectRepository.findById(id).orElseThrow(() -> new RuntimeException("PhaseProject not found with id: " + id));
    }

    private Project project(String id) {
        return projectRepository.findById(id).orElseThrow(() -> new RuntimeException("project not found with id : " + id));
    }

    private StaffProject staffProject(Object obj, Project project) {
        StaffProject staffProject;
        if (obj instanceof Staff) {
            staffProject = staffProjectRepository.findByStaffAndProject((Staff) obj, project).orElseThrow(() -> new RuntimeException("staff project not found with id: " + ((Staff) obj).getId()));
            ;
        } else {
            staffProject = staffProjectRepository.findByStudentAndProject((Student) obj, project).orElseThrow(() -> new RuntimeException("staff project not found with student :" + ((Student) obj).toString()));
        }

        return staffProject;
    }

    public ResponseObject<?> velocityTB(String idProject) {
        Set<StatusPhase> validPhases = Set.of(StatusPhase.CHUA_HOAN_THANH, StatusPhase.DANG_LAM);
        Float actualPoint = velocityRecordRepository.avgActualPoint(idProject);
        Float actualWorkingDay = velocityRecordRepository.avgActualWorkingDay(idProject);

        Double velocity = 0.0;
        if (actualWorkingDay != null && actualWorkingDay > 0) {
            velocity = (double) actualPoint / actualWorkingDay == 0  ? 1.0 : (double) actualPoint / actualWorkingDay ;
        } else {
            velocity = 1.0;
            log.warn("Actual working day is null or zero for project ID: " + idProject + ". Velocity will be 0.");
        }

        List<PhaseProject> list = phaseProjectRepository.findAll().stream().
                filter(a -> a.getStatus() == EntityStatus.ACTIVE)
                .filter(a -> a.getProject().getId().equals(idProject))
                .filter(a -> validPhases.contains(a.getStatusPhase()))
                .sorted(Comparator.comparing(PhaseProject::getStartTime, Comparator.nullsLast(Comparator.naturalOrder())))
                .collect(Collectors.toList());

        Map<String, Double> estimatedPointsByPhase = new HashMap<>(); // Assuming PhaseProject has an ID (e.g., Long)

        for (PhaseProject phase : list) {
            Double totalWorkdayForPhase = capacityEstimateRepository.findAll().stream()
                    .filter(v -> v.getPhaseProject() != null && v.getPhaseProject().getId().equals(phase.getId())) // Compare by ID for correctness
                    .filter(v -> v.getStatus() == EntityStatus.ACTIVE)
                    .mapToDouble(v -> v.getWorkday() != null ? v.getWorkday() : 0.0)
                    .sum();

            log.info("Tổng số giai ngày làm việc ước luognjw trong giai đoạn : ===> {}",totalWorkdayForPhase);

            Double estimatedPoints = velocity * totalWorkdayForPhase;
            estimatedPointsByPhase.put(phase.getId(), estimatedPoints); // Store with phase ID

            log.info("Dánh sáchc tổng sô: ==>{}",estimatedPointsByPhase.toString());
        }

        try {
            Map<String, Object> responseObjectContent = new HashMap<>();
            responseObjectContent.put("velocity", velocity);
            responseObjectContent.put("estimatedPointsByPhase", estimatedPointsByPhase);

            return new ResponseObject<>(estimatedPointsByPhase, HttpStatus.OK, "Lấy dữ liệu thành công");

        } catch (Exception e) {
            log.error("Lỗi khi tính toán hoặc lấy dữ liệu: " + e.getMessage(), e);
            return new ResponseObject<>(null, HttpStatus.INTERNAL_SERVER_ERROR, "Có lỗi xảy ra khi xử lý dữ liệu");
        }
    }

}
