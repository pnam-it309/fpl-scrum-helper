package udpm.hn.server.core.manage.user.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import udpm.hn.server.core.admin.project.model.request.ADProjectSTRequest;
import udpm.hn.server.core.common.base.PageableObject;
import udpm.hn.server.core.common.base.ResponseObject;
import udpm.hn.server.core.manage.project.model.request.MaProjectCreateRequest;
import udpm.hn.server.core.manage.project.repository.MaProjectStaffRepository;
import udpm.hn.server.core.manage.project.repository.MaProjectStudentRepository;
import udpm.hn.server.core.manage.user.model.request.MAUserRequest;
import udpm.hn.server.core.manage.user.model.request.MaUserCreateUpdateRequest;
import udpm.hn.server.core.manage.user.repository.MAUserProject;
import udpm.hn.server.core.manage.user.service.MAUserService;
import udpm.hn.server.entity.*;
import udpm.hn.server.infrastructure.constant.EntityStatus;
import udpm.hn.server.infrastructure.constant.EntityStudentStatus;
import udpm.hn.server.repository.ProjectRepository;
import udpm.hn.server.repository.StaffProjectRepository;
import udpm.hn.server.utils.Helper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class MAUserServiceImpl implements MAUserService {
    private final StaffProjectRepository staffProjectRepository;

    private final MAUserProject maUserProjectRepository;

    private final ProjectRepository projectRepository;

    private final MaProjectStaffRepository maProjectStaffRepository;

    private final MaProjectStudentRepository maProjectStudentRepository;

    @Override
    public ResponseObject<?> listUserProject(MAUserRequest request, String idProject, String idPhase) {
        Pageable pageable = Helper.createPageable(request, "createdDate");
        return new ResponseObject<>(
                PageableObject.of(maUserProjectRepository.getListUser(pageable, idProject, idPhase, request)),
                HttpStatus.OK,
                "Lấy danh sách bộ môn thành công"
        );
    }

    @Override
    public ResponseObject<?> deleteUser(String id) {
        Optional<StaffProject> optionalStaffProject = maUserProjectRepository.findById(id);

        if (optionalStaffProject.isPresent()) {
            StaffProject staffProject = optionalStaffProject.get();

            staffProject.setStatus(
                    staffProject.getStatus().name().equalsIgnoreCase(EntityStatus.ACTIVE.name())
                            ? EntityStatus.INACTIVE : EntityStatus.ACTIVE
            );
            maUserProjectRepository.save(staffProject);
            return new ResponseObject<>(null, HttpStatus.OK, "Xóa thành viên thành công");
        } else {
            return new ResponseObject<>(null, HttpStatus.OK, "Thành viên không tồn tại");
        }
    }

    @Override
    public ResponseObject<?> updateProjectMembers(String idProject, MaUserCreateUpdateRequest request) {

        log.info("List user request update project members: {}", request.toString());

        Optional<Project> optionalProject = projectRepository.findById(idProject);
        if (optionalProject.isEmpty()) {
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Không tìm thấy dự án này");
        }

        Project project = optionalProject.get();
        List<ADProjectSTRequest> listMembers = request.getListMembers();
        List<StaffProject> newStaffProjects = new ArrayList<>();

        for (ADProjectSTRequest item : listMembers) {
            String email = item.getEmail();
            if (email == null || email.isEmpty()) continue;

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

        return new ResponseObject<>(null, HttpStatus.OK, "Cập nhật thành viên dự án thành công");
    }

    @Override
    public ResponseObject<?> idFacility(String idProject) {
        Optional<Project> optionalProject = projectRepository.findById(idProject);

        if (optionalProject.isEmpty()) {
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Không tìm thấy dự án này");
        }

        String facilityId = projectRepository.idFacility(idProject);

        if (facilityId == null) {
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Không tìm thấy cơ sở cho dự án này");
        }

        return new ResponseObject<>(facilityId, HttpStatus.OK, "Lấy cơ sở thành công");
    }



}
