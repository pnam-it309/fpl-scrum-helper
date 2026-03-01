package udpm.hn.server.core.manage.project.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import udpm.hn.server.core.admin.project.model.request.ADProjectSTRequest;
import udpm.hn.server.core.common.base.ResponseObject;
import udpm.hn.server.core.manage.project.model.request.MaProjectCreateRequest;
import udpm.hn.server.core.manage.project.model.request.MaProjectSearchRequest;
import udpm.hn.server.core.manage.project.model.request.MaRestartProjectRequest;
import udpm.hn.server.core.manage.project.model.response.MAProjectDetailSummaryResponse;
import udpm.hn.server.core.manage.project.repository.MAUserProjectRepository;
import udpm.hn.server.core.manage.project.repository.MaPhaseProjectRepository;
import udpm.hn.server.core.manage.project.repository.MaProjectDFRepository;
import udpm.hn.server.core.manage.project.repository.MaProjectRepository;
import udpm.hn.server.core.manage.project.repository.MaProjectStaffRepository;
import udpm.hn.server.core.manage.project.repository.MaProjectStudentRepository;
import udpm.hn.server.core.manage.project.repository.MaTodoProjectRepository;
import udpm.hn.server.core.manage.project.service.MaProjectService;
import udpm.hn.server.entity.*;
import udpm.hn.server.infrastructure.constant.EntityStatus;
import udpm.hn.server.infrastructure.constant.EntityStudentStatus;
import udpm.hn.server.infrastructure.constant.StatusProject;
import udpm.hn.server.repository.*;
import udpm.hn.server.utils.Helper;
import udpm.hn.server.utils.UserContextHelper;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MaProjectServiceImpl implements MaProjectService {

    private final MaProjectRepository maProjectRepository;

    private final MaProjectDFRepository projectDFRepository;

    private final MajorFacilityRepository majorFacilityRepository;

    private final CategoryRepository categoryRepository;

    private final MaProjectStudentRepository maProjectStudentRepository;

    private final MaProjectStaffRepository maProjectStaffRepository;

    private final StaffProjectRepository staffProjectRepository;

    private final ProjectRepository projectRepository;

    private final FacilityRepository facilityRepository;

    private final UserContextHelper userContextHelper;

    private final MaTodoProjectRepository maTodoProjectRepository;

    private final MaPhaseProjectRepository maPhaseProjectRepository;

    private final MAUserProjectRepository maUserProjectRepository;

    private final ActivityLogRepository activityLogRepository;

    @Override
    public ResponseObject<?> getAllProject(MaProjectSearchRequest request) {
        StatusProject status = null;

        if (request.getStatus() != null) {
            status = StatusProject.values()[request.getStatus()];
        }

        Pageable page = Helper.createPageable(request, "createdDate");
        return new ResponseObject<>(maProjectRepository.getAllProject(page, request, status, userContextHelper), HttpStatus.OK, "Lấy thông tin project thành công");
    }

    @Override
    public ResponseObject<?> getAllDepartmentFacility(String idFacility) {
        return new ResponseObject<>(projectDFRepository.getAllDepartmentFacility(idFacility), HttpStatus.OK, "Lấy danh sách bộ môn cơ sở thành công");
    }

    @Override
    public ResponseObject<?> createProject(MaProjectCreateRequest request) {
        return null;
    }

    @Override
    public ResponseObject<?> updateProject(String idProject, MaProjectCreateRequest request) {

        log.info("List user request update project :{}", request.toString());

        Optional<Project> optionalProject = projectRepository.findById(idProject);
        if (optionalProject.isEmpty()) {
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Không tìm thấy dự án này");
        }

        Optional<MajorFacility> optionalMajorFacility = majorFacilityRepository.findById(request.getIdMajorFacility());
        if (optionalMajorFacility.isEmpty()) {
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Không tìm thấy chuyên ngành cơ sở");
        }

        Optional<Category> optionalCategory = categoryRepository.findById(request.getIdCategory());
        if (optionalCategory.isEmpty()) {
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Không tìm thấy thể loại");
        }

        List<StaffProject> newStaffProjects = new ArrayList<>();
        List<ADProjectSTRequest> listMembers = request.getListMembers();

        Project project = optionalProject.get();

        // ======= BƯỚC 1: KIỂM TRA TOÀN BỘ THÀNH VIÊN TRƯỚC =======
        for (ADProjectSTRequest item : listMembers) {
            String email = item.getEmail();
            if (email == null || email.isEmpty()) {
//                return new ResponseObject<>(null, HttpStatus.BAD_REQUEST, "Email của thành viên không được để trống");
                continue;
            }

            StaffProject staffProject = new StaffProject();

            Optional<Staff> staff = maProjectStaffRepository.findByEmailFe(email);
            if (staff.isEmpty()) {
                staff = maProjectStaffRepository.findByEmailFpt(email);
            }

            if (staff.isPresent()) {
                staffProject.setStaff(staff.get());
            } else {
                Optional<Student> student = maProjectStudentRepository.findByEmail(email);
                if (student.isPresent()) {
                    staffProject.setStudent(student.get());
                } else {
                    return new ResponseObject<>(null, HttpStatus.BAD_REQUEST, "Không tìm thấy người dùng với email: " + email);
                }
            }

            // Gán vai trò
            String role = item.getRole();
            if ("QUAN_Li".equalsIgnoreCase(role)) {
                staffProject.setRole(EntityStudentStatus.QUAN_Li);
            } else if ("DEV".equalsIgnoreCase(role)) {
                staffProject.setRole(EntityStudentStatus.DEV);
            } else {
                staffProject.setRole(EntityStudentStatus.TESTER);
            }

            staffProject.setProject(project);
            staffProject.setStatus(EntityStatus.ACTIVE);
            newStaffProjects.add(staffProject);
        }

        // ======= BƯỚC 2: CẬP NHẬT PROJECT =======
        Long startTime = Long.valueOf(request.getStartTime());
        Long endTime = Long.valueOf(request.getEndTime());
        Long currentTime = new Date().getTime();

        project.setStartTime(startTime);
        project.setEndTime(endTime);

        if (currentTime < startTime) {
            project.setStatusProject(StatusProject.CHUA_DIEN_RA);
        } else if (currentTime >= startTime && currentTime <= endTime) {
            project.setStatusProject(StatusProject.DANG_DIEN_RA);
        } else {
            project.setStatusProject(StatusProject.DA_DIEN_RA);
        }

        project.setProgress(0f);
        project.setCategory(optionalCategory.get());
        project.setBackgroundColor("#59a1e3");
        project.setName(request.getNameProject());
        project.setCode(request.getCodeProject());
        project.setDescriptions(request.getDescriptions());
        project.setMajorFacility(optionalMajorFacility.get());

        projectRepository.save(project);

        // ======= BƯỚC 3: XÓA THÀNH VIÊN CŨ KHÔNG CÒN TRONG DANH SÁCH MỚI =======
        List<StaffProject> existingStaffProjects = staffProjectRepository.findByProject(project);
        Set<String> requestEmails = listMembers.stream().map(ADProjectSTRequest::getEmail).collect(Collectors.toSet());

        List<StaffProject> toDelete = existingStaffProjects.stream().filter(sp -> {
            String email = (sp.getStaff() != null) ? sp.getStaff().getEmailFe() : (sp.getStudent() != null) ? sp.getStudent().getEmail() : null;
            return !requestEmails.contains(email);
        }).toList();

        toDelete.forEach(sp -> sp.setStatus(EntityStatus.INACTIVE));
        staffProjectRepository.saveAll(toDelete);

        // ======= BƯỚC 4: CẬP NHẬT HOẶC THÊM MỚI THÀNH VIÊN =======
        for (StaffProject newSP : newStaffProjects) {
            if (newSP.getStaff() != null) {
                Optional<StaffProject> existing = staffProjectRepository.findByStaffAndProject(newSP.getStaff(), project);
                if (existing.isPresent()) {
                    StaffProject existingSP = existing.get();
                    existingSP.setRole(newSP.getRole());
                    existingSP.setStatus(EntityStatus.ACTIVE);
                    staffProjectRepository.save(existingSP);
                    continue;
                }
            } else if (newSP.getStudent() != null) {
                Optional<StaffProject> existing = staffProjectRepository.findByStudentAndProject(newSP.getStudent(), project);
                if (existing.isPresent()) {
                    StaffProject existingSP = existing.get();
                    existingSP.setRole(newSP.getRole());
                    existingSP.setStatus(EntityStatus.ACTIVE);
                    staffProjectRepository.save(existingSP);
                    continue;
                }
            }

            staffProjectRepository.save(newSP);
        }

        ActivityLog activityLog = new ActivityLog();
        activityLog.setRole("Quản Lý");
        activityLog.setExecutorEmail(request.getEmailLogin());
        activityLog.setContent("Đã cập nhật thông tin của dự án: "+optionalProject.get().getName());
        activityLogRepository.save(activityLog);

        return new ResponseObject<>(null, HttpStatus.OK, "Cập nhật dự án thành công");

    }

    @Override
    public ResponseObject<?> detailProject(String idProject) {
        return maProjectRepository.findById(idProject).map(project -> new ResponseObject<>(project, HttpStatus.OK, "Lấy thông tin dự án thành công!")).orElseGet(() -> new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Không tìm thấy dự án!"));
    }

    @Override
    public ResponseObject<?> deleteProject(String idProject) {
        return null;
    }

    @Override
    public ResponseObject<?> getAllFacility() {
        return new ResponseObject<>(facilityRepository.findAll(), HttpStatus.OK, "Lấy dư liệu cơ sở thành công");
    }

    @Override
    public ResponseObject<?> getDetailSummaryProject(String id) {
        Optional<MAProjectDetailSummaryResponse> optionalProject = maProjectRepository.getDetailSummaryProject(id);

        if(optionalProject.isEmpty()) return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Không tìm thấy dự án");

        MAProjectDetailSummaryResponse projectDetailSummary = optionalProject.get();

        int amountOfMembers = maUserProjectRepository.getAmountStaff(id) + maUserProjectRepository.getAmountStudent(id);

        return new ResponseObject<>(projectDetailSummary, HttpStatus.OK, "Lấy dữ liệu tổng kết dự án thành công");

    }

    @Override
    public ResponseObject<?> finishEarlyProject(String id,String emailLogin) {
        Optional<Project> optionalProject = maProjectRepository.findProjectById(id);

        if (optionalProject.isPresent()) {
            Project project = optionalProject.get();

            project.setActualEndDate(Calendar.getInstance().getTimeInMillis());

            project.setStatusProject(StatusProject.DA_DIEN_RA);

            maProjectRepository.save(project);

            ActivityLog activityLog = new ActivityLog();
            activityLog.setRole("Quản Lý");
            activityLog.setExecutorEmail(emailLogin);
            activityLog.setContent("Đã Kết thúc sớm dự án : "+optionalProject.get().getName());
            activityLogRepository.save(activityLog);

            return new ResponseObject<>(project, HttpStatus.OK, "Kết thúc sớm dự án thành công");
        }
        return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Không tìm thấy dự án");
    }

    @Override
    public ResponseObject<?> getAmountOfPhaseByStatus(String idProject) {
        return new ResponseObject<>(maPhaseProjectRepository.getAmountOfPhaseByStatus(idProject), HttpStatus.OK, "Lấy dữ liệu số lượng giai đoạn dự án thành công");
    }

    @Override
    public ResponseObject<?> getAmountOfTodoByPhase(String idProject) {
        return new ResponseObject<>(maTodoProjectRepository.getAmountOfTodoByPhase(idProject), HttpStatus.OK, "Lấy dữ liệu thành công");
    }

    @Override
    public ResponseObject<?> restartProject(String idProject, MaRestartProjectRequest request) {
        Optional<Project> optionalProject = maProjectRepository.findProjectById(idProject);

        if(optionalProject.isEmpty()) {
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Không tìm thấy dự án");
        }

        Project project = optionalProject.get();

        project.setActualEndDate(null);
        project.setEndTime(request.getCompletionDate());
        project.setStatusProject(StatusProject.DANG_DIEN_RA);

        maProjectRepository.save(project);

        ActivityLog activityLog = new ActivityLog();
        activityLog.setRole("Quản Lý");
        activityLog.setExecutorEmail(request.getEmailLogin());
        activityLog.setContent("Đã Khởi động lại dự án : "+optionalProject.get().getName());
        activityLogRepository.save(activityLog);

        return new ResponseObject<>(project, HttpStatus.OK, "Khởi động lại dự án thành công");
    }
}