package udpm.hn.server.core.admin.project.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import udpm.hn.server.core.admin.project.model.request.ADProjectCreateRequest;
import udpm.hn.server.core.admin.project.model.request.ADProjectSTRequest;
import udpm.hn.server.core.admin.project.model.request.ADProjectSearchRequest;
import udpm.hn.server.core.admin.project.model.response.ADProjectOverview;
import udpm.hn.server.core.admin.project.model.response.ADProjectTodoCountResponse;
import udpm.hn.server.core.admin.project.model.response.DepartmentProjectCountResponse;
import udpm.hn.server.core.admin.project.model.response.FacilityProjectCountResponse;
import udpm.hn.server.core.admin.project.model.response.TodoStatusByProject;
import udpm.hn.server.core.admin.project.repository.ADProjectRepository;
import udpm.hn.server.core.admin.project.repository.ADProjectStaffRepository;
import udpm.hn.server.core.admin.project.repository.ADProjectStudentRepository;
import udpm.hn.server.core.admin.project.repository.ProjectDFRepository;
import udpm.hn.server.core.admin.project.service.ADProjectService;
import udpm.hn.server.core.common.base.ResponseObject;
import udpm.hn.server.entity.*;
import udpm.hn.server.infrastructure.constant.EntityStatus;
import udpm.hn.server.infrastructure.constant.EntityStudentStatus;
import udpm.hn.server.infrastructure.constant.StatusProject;
import udpm.hn.server.repository.*;
import udpm.hn.server.utils.Helper;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Primary
public class ADProjectServiceImpl implements ADProjectService {

    private final ADProjectRepository adProjectRepository;

    private final ProjectDFRepository projectDFRepository;

    private final MajorFacilityRepository majorFacilityRepository;

    private final CategoryRepository categoryRepository;

    private final ADProjectStudentRepository adProjectStudentRepository;

    private final ADProjectStaffRepository adProjectStaffRepository;

    private final StaffProjectRepository staffProjectRepository;

    private final ProjectRepository projectRepository;

    private final FacilityRepository facilityRepository;

    private final ActivityLogRepository activityLogRepository;

    @Override
    public ResponseObject<?> getAllProject(ADProjectSearchRequest request) {
        StatusProject status = null;
        if (request.getStatus() != null) {
            status = StatusProject.values()[request.getStatus()];
        }

        Pageable page = Helper.createPageable(request, "createdDate");
        return new ResponseObject<>(adProjectRepository.getAllProject(page, request, status), HttpStatus.OK, "Lấy thông tin project thành công");
    }

    @Override
    public ResponseObject<?> getAllDepartmentFacility(String idFacility) {


        return new ResponseObject<>(projectDFRepository.getAllDepartmentFacility(idFacility), HttpStatus.OK, "Lấy danh sách bộ môn cơ sở thành công");
    }

    public ResponseObject<?> createProject(ADProjectCreateRequest request) {
        log.info("Dữ liệu request create project:{}", request);


        Optional<Project> optionalProject = projectRepository.findByCode(request.getCodeProject());
        if(optionalProject.isPresent()){
            log.info("Đã chạy vào đây create");
            return new ResponseObject<>().error(HttpStatus.NOT_FOUND,"Đã có mã này!");
        }

        Optional<MajorFacility> optionalMajorFacility = majorFacilityRepository.findById(request.getIdMajorFacility());
        if (optionalMajorFacility.isEmpty()) {
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Không tìm thấy chuyên ngành cơ sở");
        }

        Optional<Category> optionalCategory = categoryRepository.findById(request.getIdCategory());
        if (optionalCategory.isEmpty()) {
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Không tìm thấy thể loại");
        }

        Long startTime = Long.valueOf(request.getStartTime());
        Long endTime = Long.valueOf(request.getEndTime());
        Long currentTime = new Date().getTime();

        Project project = new Project();
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
        project.setBackgroundImage("https://wallpaper-mania.com/wp-content/uploads/2018/09/High_resolution_wallpaper_background_ID_77701599633.jpg");
        project.setName(request.getNameProject());
        project.setCode(request.getCodeProject());
        project.setDescriptions(request.getDescriptions());
        project.setMajorFacility(optionalMajorFacility.get());

        Project newProject = projectRepository.save(project);
        List<StaffProject> newStaffProjects = new ArrayList<>();

        for (ADProjectSTRequest item : request.getListMembers()) {
            String email = item.getEmail();
            if (email == null || email.trim().isEmpty()) continue;

            StaffProject staffProject = new StaffProject();
            staffProject.setProject(newProject);

            // Ưu tiên tìm Staff
            Optional<Staff> staff = adProjectStaffRepository.findByEmailFe(email);
            if (staff.isEmpty()) {
                staff = adProjectStaffRepository.findByEmailFpt(email);
            }

            if (staff.isPresent()) {
                staffProject.setStaff(staff.get());
            } else {
                // Nếu không tìm thấy staff thì tìm Student
                Optional<Student> optionalStudent = adProjectStudentRepository.findByEmail(email);
                if (optionalStudent.isPresent()) {
                    staffProject.setStudent(optionalStudent.get());
                } else {
                    continue;
                }
            }

            String role = item.getRole();
            if ("QUAN_Li".equalsIgnoreCase(role)) {
                staffProject.setRole(EntityStudentStatus.QUAN_Li);
            } else if ("DEV".equalsIgnoreCase(role)) {
                staffProject.setRole(EntityStudentStatus.DEV);
            } else {
                staffProject.setRole(EntityStudentStatus.TESTER);
            }

            newStaffProjects.add(staffProject);
        }

        staffProjectRepository.saveAll(newStaffProjects);


        ActivityLog activityLog = new ActivityLog();
        activityLog.setRole("ADMIN");
        activityLog.setExecutorEmail(request.getEmailLogin());
        activityLog.setContent("Đã thêm mới dự án có tên : "+request.getNameProject());
        activityLogRepository.save(activityLog);

        return new ResponseObject<>(null, HttpStatus.OK, "Thêm dự án thành công");
    }

    @Transactional
    public ResponseObject<?> updateProject(String idProject, ADProjectCreateRequest request) {

        log.info("List user request update project :{}", request.toString());



        Optional<Project> optionalProject = projectRepository.findById(idProject);
        if (optionalProject.isEmpty()) {
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Không tìm thấy dự án này");
        }

        Project existingProject = optionalProject.get();

        if (!request.getCodeProject().equals(existingProject.getCode())) { // Sử dụng .equals() để so sánh String
            Optional<Project> optionalProjectCode = projectRepository.findByCode(request.getCodeProject());
            if (optionalProjectCode.isPresent()) {
                log.info("Mã dự án đã tồn tại: {}", request.getCodeProject());

                // 4. Trả về lỗi CONFLICT thay vì NOT_FOUND cho trường hợp trùng lặp
                //    Lỗi NOT_FOUND không phù hợp khi tài nguyên đã tồn tại.
                return new ResponseObject<>().error(HttpStatus.CONFLICT, "Mã dự án đã tồn tại!");
            }
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

            Optional<Staff> staff = adProjectStaffRepository.findByEmailFe(email);
            if (staff.isEmpty()) {
                staff = adProjectStaffRepository.findByEmailFpt(email);
            }

            if (staff.isPresent()) {
                staffProject.setStaff(staff.get());
            } else {
                Optional<Student> student = adProjectStudentRepository.findByEmail(email);
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
        activityLog.setRole("ADMIN");
        activityLog.setExecutorEmail(request.getEmailLogin());
        activityLog.setContent("Đã sửa dự án có tên : "+request.getNameProject());
        activityLogRepository.save(activityLog);

        return new ResponseObject<>(null, HttpStatus.OK, "Cập nhật dự án thành công");
    }


    @Override
    public ResponseObject<?> detailProject(String idProject) {
        return adProjectRepository.findById(idProject).map(project -> new ResponseObject<>(project, HttpStatus.OK, "Lấy thông tin dự án thành công!")).orElseGet(() -> new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Không tìm thấy dự án!"));
    }


    @Override
    public ResponseObject<?> deleteProject(String idProject) {

        Optional<Project> optionalProject = adProjectRepository.findById(idProject);
        if (optionalProject.isEmpty()) {
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Không tìm thấy dự án này");
        }

        Project project = optionalProject.get();
        project.setStatus(EntityStatus.INACTIVE);

        adProjectRepository.save(project);

        return new ResponseObject<>(null, HttpStatus.OK, "Xóa thành công dự án");
    }

    @Override
    public ResponseObject<?> getAllFacility() {
        return new ResponseObject<>(facilityRepository.findAll(), HttpStatus.OK, "Lấy dư liệu cơ sở thành công");
    }

    @Override
    public ResponseObject<?> countProjectByStatus() {
        List<Object[]> resultRaw = adProjectRepository.countProjectByStatus();
        Map<String, Long> result = new LinkedHashMap<>();

        for (Object[] row : resultRaw) {
            String status = String.valueOf(row[0]);
            Long count = ((Number) row[1]).longValue();  // Chuyển đổi đúng kiểu từ Number
            result.put(status, count);
        }

        return new ResponseObject<>(result, HttpStatus.OK, "Thống kê trạng thái dự án thành công");
    }

    @Override
    public ResponseObject<?> getProjectParticipantCounts() {
        List<Object[]> resultRaw = adProjectRepository.getProjectParticipantCounts();
        List<Map<String, Object>> result = new ArrayList<>();

        for (Object[] row : resultRaw) {
            String projectName = String.valueOf(row[0]);
            Long totalParticipants = ((Number) row[1]).longValue();

            Map<String, Object> item = new HashMap<>();
            item.put("projectName", projectName);
            item.put("totalParticipants", totalParticipants);
            result.add(item);
        }

        return new ResponseObject<>(result, HttpStatus.OK, "Thống kê số lượng người tham gia theo dự án thành công");
    }

    @Override
    public ResponseObject<?> getProjectTodoCounts() {
        try {
            List<ADProjectTodoCountResponse> result = adProjectRepository.countTodoByProjectName();
            return new ResponseObject<>(result, HttpStatus.OK, "Thống kê số lượng công việc theo dự án thành công");
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseObject<>(false, "Lỗi khi thống kê công việc", null);
        }
    }

    @Override
    public ResponseObject<?> countTotalProjects() {
        Long total = adProjectRepository.countTotalProjects();
        Long overdue = adProjectRepository.countOverdueProjects(1);

        // Initialize counts for each status
        Long inProgress = 0L;
        Long done = 0L;
        Long paused = 0L;

        List<Object[]> groupedStatus = adProjectRepository.countProjectsGroupedByStatus();
        for (Object[] row : groupedStatus) {
            Byte statusByte = (Byte) row[0]; // Assuming the database returns a Byte
            Long count = ((Number) row[1]).longValue();

            StatusProject status = StatusProject.values()[statusByte.intValue()]; // Convert Byte to enum

            switch (status) {
                case DANG_DIEN_RA: // In progress
                    inProgress = count;
                    break;
                case DA_DIEN_RA:   // Done
                    done = count;
                    break;
                case CHUA_DIEN_RA: // Paused or not yet started
                    paused = count;
                    break;
                default:
                    // Handle unexpected status values if necessary
                    break;
            }
        }

        ADProjectOverview dto = new ADProjectOverview();
        dto.setTotalProjects(total);
        dto.setInProgress(inProgress);
        dto.setDone(done);
        dto.setPaused(paused);
        dto.setOverdue(overdue);

        return new ResponseObject<>(dto, HttpStatus.OK, "Thống kê tổng số dự án thành công");
    }

    @Override
    public ResponseObject<?> countProjectsByFacility() {
        List<FacilityProjectCountResponse> result = adProjectRepository.countProjectsByFacility();
        return new ResponseObject<>(result, HttpStatus.OK, "Thống kê tổng số dự án theo cơ sở");
    }

    @Override
    public ResponseObject<?> countProjectsByDepartment() {
        List<DepartmentProjectCountResponse> result = adProjectRepository.countProjectsByDepartment();
        return new ResponseObject<>(result, HttpStatus.OK, "Thống kê tổng số dự án theo bộ môn");
    }

    @Override
    public ResponseObject<?> countTaskByStatusAndProject() {
        List<TodoStatusByProject> result = adProjectRepository.countTaskByStatusAndProject();
        return new ResponseObject<>(result, HttpStatus.OK, "Thống kê công việc theo trạng thái qua dự án");
    }
}
