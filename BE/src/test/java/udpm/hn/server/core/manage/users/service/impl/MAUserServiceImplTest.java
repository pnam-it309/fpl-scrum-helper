package udpm.hn.server.core.manage.users.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import udpm.hn.server.core.admin.project.model.request.ADProjectSTRequest;
import udpm.hn.server.core.common.base.PageableObject;
import udpm.hn.server.core.common.base.ResponseObject;
import udpm.hn.server.core.manage.project.repository.MaProjectStaffRepository;
import udpm.hn.server.core.manage.project.repository.MaProjectStudentRepository;
import udpm.hn.server.core.manage.user.model.request.MAUserRequest;
import udpm.hn.server.core.manage.user.model.request.MaUserCreateUpdateRequest;
import udpm.hn.server.core.manage.user.repository.MAUserProject;
import udpm.hn.server.core.manage.user.service.impl.MAUserServiceImpl;
import udpm.hn.server.entity.*;
import udpm.hn.server.infrastructure.constant.EntityStatus;
import udpm.hn.server.repository.ProjectRepository;
import udpm.hn.server.repository.StaffProjectRepository;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class MAUserServiceImplTest {

    @InjectMocks
    private MAUserServiceImpl service;

    @Mock
    private StaffProjectRepository staffProjectRepository;
    @Mock
    private MAUserProject maUserProjectRepository;
    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private MaProjectStaffRepository maProjectStaffRepository;
    @Mock
    private MaProjectStudentRepository maProjectStudentRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testDeleteUser_Found() {
        StaffProject sp = new StaffProject();
        sp.setStatus(EntityStatus.ACTIVE);
        Optional<StaffProject> op = Optional.of(sp);
        when(maUserProjectRepository.findById("id1")).thenReturn(op);
        when(maUserProjectRepository.save(any(StaffProject.class))).thenReturn(sp);

        ResponseObject<?> res = service.deleteUser("id1");
        assertEquals(HttpStatus.OK, res.getStatus());
        assertEquals("Xóa thành viên thành công", res.getMessage());
    }

    @Test
    void testDeleteUser_NotFound() {
        when(maUserProjectRepository.findById("id2")).thenReturn(Optional.empty());

        ResponseObject<?> res = service.deleteUser("id2");
        assertEquals(HttpStatus.OK, res.getStatus());
        assertEquals("Thành viên không tồn tại", res.getMessage());
    }

    @Test
    void testUpdateProjectMembers_ProjectNotFound() {
        when(projectRepository.findById("pid")).thenReturn(Optional.empty());
        MaUserCreateUpdateRequest req = new MaUserCreateUpdateRequest();
        ResponseObject<?> res = service.updateProjectMembers("pid", req);
        assertEquals(HttpStatus.NOT_FOUND, res.getStatus());
        assertEquals("Không tìm thấy dự án này", res.getMessage());
    }

    @Test
    void testUpdateProjectMembers_StaffFound() {
        // Chuẩn bị Project
        Project prj = new Project();
        when(projectRepository.findById("pid")).thenReturn(Optional.of(prj));

        // Chuẩn bị List member
        ADProjectSTRequest member = new ADProjectSTRequest();
        member.setEmail("email@fpt.edu.vn");
        member.setRole("DEV");
        List<ADProjectSTRequest> members = List.of(member);

        MaUserCreateUpdateRequest req = new MaUserCreateUpdateRequest();
        req.setListMembers(members);

        // Có Staff theo Email
        Staff staff = new Staff();
        when(maProjectStaffRepository.findByEmailFe("email@fpt.edu.vn")).thenReturn(Optional.of(staff));
        when(maProjectStaffRepository.findByEmailFpt("email@fpt.edu.vn")).thenReturn(Optional.empty());

        // Không có staff/project đã tồn tại
        when(staffProjectRepository.findByStaffAndProject(any(Staff.class), any(Project.class))).thenReturn(Optional.empty());

        when(staffProjectRepository.save(any(StaffProject.class))).thenReturn(new StaffProject());

        ResponseObject<?> res = service.updateProjectMembers("pid", req);
        assertEquals(HttpStatus.OK, res.getStatus());
        assertEquals("Cập nhật thành viên dự án thành công", res.getMessage());
    }

    @Test
    void testUpdateProjectMembers_StudentFound() {
        Project prj = new Project();
        when(projectRepository.findById("pid")).thenReturn(Optional.of(prj));

        ADProjectSTRequest member = new ADProjectSTRequest();
        member.setEmail("student@fpt.edu.vn");
        member.setRole("DEV");
        List<ADProjectSTRequest> members = List.of(member);

        MaUserCreateUpdateRequest req = new MaUserCreateUpdateRequest();
        req.setListMembers(members);

        // Không tìm thấy Staff
        when(maProjectStaffRepository.findByEmailFe("student@fpt.edu.vn")).thenReturn(Optional.empty());
        when(maProjectStaffRepository.findByEmailFpt("student@fpt.edu.vn")).thenReturn(Optional.empty());

        // Có Student
        Student student = new Student();
        when(maProjectStudentRepository.findByEmail("student@fpt.edu.vn")).thenReturn(Optional.of(student));

        // Không có staff/project đã tồn tại
        when(staffProjectRepository.findByStudentAndProject(any(Student.class), any(Project.class))).thenReturn(Optional.empty());

        when(staffProjectRepository.save(any(StaffProject.class))).thenReturn(new StaffProject());

        ResponseObject<?> res = service.updateProjectMembers("pid", req);
        assertEquals(HttpStatus.OK, res.getStatus());
        assertEquals("Cập nhật thành viên dự án thành công", res.getMessage());
    }

    @Test
    void testUpdateProjectMembers_NotFoundUser() {
        Project prj = new Project();
        when(projectRepository.findById("pid")).thenReturn(Optional.of(prj));

        ADProjectSTRequest member = new ADProjectSTRequest();
        member.setEmail("nouser@fpt.edu.vn");
        member.setRole("DEV");
        List<ADProjectSTRequest> members = List.of(member);

        MaUserCreateUpdateRequest req = new MaUserCreateUpdateRequest();
        req.setListMembers(members);

        // Không tìm thấy Staff và Student
        when(maProjectStaffRepository.findByEmailFe("nouser@fpt.edu.vn")).thenReturn(Optional.empty());
        when(maProjectStaffRepository.findByEmailFpt("nouser@fpt.edu.vn")).thenReturn(Optional.empty());
        when(maProjectStudentRepository.findByEmail("nouser@fpt.edu.vn")).thenReturn(Optional.empty());

        ResponseObject<?> res = service.updateProjectMembers("pid", req);
        assertEquals(HttpStatus.BAD_REQUEST, res.getStatus());
        assertTrue(res.getMessage().contains("Không tìm thấy người dùng"));
    }

    @Test
    void testUpdateProjectMembers_UpdateExistingStaff() {
        Project prj = new Project();
        when(projectRepository.findById("pid")).thenReturn(Optional.of(prj));

        ADProjectSTRequest member = new ADProjectSTRequest();
        member.setEmail("exist@fpt.edu.vn");
        member.setRole("DEV");
        List<ADProjectSTRequest> members = List.of(member);

        MaUserCreateUpdateRequest req = new MaUserCreateUpdateRequest();
        req.setListMembers(members);

        Staff staff = new Staff();
        StaffProject sp = new StaffProject();
        when(maProjectStaffRepository.findByEmailFe("exist@fpt.edu.vn")).thenReturn(Optional.of(staff));
        when(maProjectStaffRepository.findByEmailFpt("exist@fpt.edu.vn")).thenReturn(Optional.empty());
        when(staffProjectRepository.findByStaffAndProject(any(Staff.class), any(Project.class))).thenReturn(Optional.of(sp));
        when(staffProjectRepository.save(any(StaffProject.class))).thenReturn(sp);

        ResponseObject<?> res = service.updateProjectMembers("pid", req);
        assertEquals(HttpStatus.OK, res.getStatus());
    }

    @Test
    void testUpdateProjectMembers_UpdateExistingStudent() {
        Project prj = new Project();
        when(projectRepository.findById("pid")).thenReturn(Optional.of(prj));

        ADProjectSTRequest member = new ADProjectSTRequest();
        member.setEmail("existstudent@fpt.edu.vn");
        member.setRole("DEV");
        List<ADProjectSTRequest> members = List.of(member);

        MaUserCreateUpdateRequest req = new MaUserCreateUpdateRequest();
        req.setListMembers(members);

        when(maProjectStaffRepository.findByEmailFe("existstudent@fpt.edu.vn")).thenReturn(Optional.empty());
        when(maProjectStaffRepository.findByEmailFpt("existstudent@fpt.edu.vn")).thenReturn(Optional.empty());
        Student student = new Student();
        when(maProjectStudentRepository.findByEmail("existstudent@fpt.edu.vn")).thenReturn(Optional.of(student));
        StaffProject sp = new StaffProject();
        when(staffProjectRepository.findByStudentAndProject(any(Student.class), any(Project.class))).thenReturn(Optional.of(sp));
        when(staffProjectRepository.save(any(StaffProject.class))).thenReturn(sp);

        ResponseObject<?> res = service.updateProjectMembers("pid", req);
        assertEquals(HttpStatus.OK, res.getStatus());
    }

    @Test
    void testIdFacility_ProjectNotFound() {
        when(projectRepository.findById("p1")).thenReturn(Optional.empty());
        ResponseObject<?> res = service.idFacility("p1");
        assertEquals(HttpStatus.NOT_FOUND, res.getStatus());
        assertEquals("Không tìm thấy dự án này", res.getMessage());
    }

    @Test
    void testIdFacility_FacilityNotFound() {
        Project p = new Project();
        when(projectRepository.findById("p1")).thenReturn(Optional.of(p));
        when(projectRepository.idFacility("p1")).thenReturn(null);
        ResponseObject<?> res = service.idFacility("p1");
        assertEquals(HttpStatus.NOT_FOUND, res.getStatus());
        assertEquals("Không tìm thấy cơ sở cho dự án này", res.getMessage());
    }

    @Test
    void testIdFacility_Success() {
        Project p = new Project();
        when(projectRepository.findById("p1")).thenReturn(Optional.of(p));
        when(projectRepository.idFacility("p1")).thenReturn("CS123");
        ResponseObject<?> res = service.idFacility("p1");
        assertEquals(HttpStatus.OK, res.getStatus());
        assertEquals("CS123", res.getData());
        assertEquals("Lấy cơ sở thành công", res.getMessage());
    }

    @Test
    void testUpdateProjectMembers_EmptyList() {
        Project prj = new Project();
        when(projectRepository.findById("pid")).thenReturn(Optional.of(prj));
        MaUserCreateUpdateRequest req = new MaUserCreateUpdateRequest();
        req.setListMembers(Collections.emptyList());
        ResponseObject<?> res = service.updateProjectMembers("pid", req);
        assertEquals(HttpStatus.OK, res.getStatus());
        assertEquals("Cập nhật thành viên dự án thành công", res.getMessage());
    }
    @Test
    void testUpdateProjectMembers_MemberWithNullOrEmptyEmail() {
        Project prj = new Project();
        when(projectRepository.findById("pid")).thenReturn(Optional.of(prj));
        ADProjectSTRequest mem1 = new ADProjectSTRequest();
        mem1.setEmail(null);
        ADProjectSTRequest mem2 = new ADProjectSTRequest();
        mem2.setEmail("");
        MaUserCreateUpdateRequest req = new MaUserCreateUpdateRequest();
        req.setListMembers(List.of(mem1, mem2));
        ResponseObject<?> res = service.updateProjectMembers("pid", req);
        assertEquals(HttpStatus.OK, res.getStatus());
        assertEquals("Cập nhật thành viên dự án thành công", res.getMessage());
    }
    @Test
    void testUpdateProjectMembers_MemberRoleTester() {
        Project prj = new Project();
        when(projectRepository.findById("pid")).thenReturn(Optional.of(prj));
        ADProjectSTRequest mem = new ADProjectSTRequest();
        mem.setEmail("test@fpt.edu.vn");
        mem.setRole("ANYTHING"); // không phải DEV hay QUAN_Li -> sẽ là TESTER

        MaUserCreateUpdateRequest req = new MaUserCreateUpdateRequest();
        req.setListMembers(List.of(mem));
        Staff staff = new Staff();
        when(maProjectStaffRepository.findByEmailFe("test@fpt.edu.vn")).thenReturn(Optional.of(staff));
        when(maProjectStaffRepository.findByEmailFpt("test@fpt.edu.vn")).thenReturn(Optional.empty());
        when(staffProjectRepository.findByStaffAndProject(any(Staff.class), any(Project.class))).thenReturn(Optional.empty());
        when(staffProjectRepository.save(any(StaffProject.class))).thenReturn(new StaffProject());

        ResponseObject<?> res = service.updateProjectMembers("pid", req);
        assertEquals(HttpStatus.OK, res.getStatus());
    }
    @Test
    void testDeleteUser_FromInactiveToActive() {
        StaffProject sp = new StaffProject();
        sp.setStatus(EntityStatus.INACTIVE); // trạng thái ban đầu là INACTIVE
        when(maUserProjectRepository.findById("id3")).thenReturn(Optional.of(sp));
        when(maUserProjectRepository.save(any(StaffProject.class))).thenReturn(sp);

        ResponseObject<?> res = service.deleteUser("id3");
        assertEquals(HttpStatus.OK, res.getStatus());
        assertEquals("Xóa thành viên thành công", res.getMessage());
        assertEquals(EntityStatus.ACTIVE, sp.getStatus()); // đã đổi sang ACTIVE
    }
    @Test
    void testUpdateProjectMembers_ListMembersNull() {
        Project prj = new Project();
        when(projectRepository.findById("pid")).thenReturn(Optional.of(prj));

        MaUserCreateUpdateRequest req = new MaUserCreateUpdateRequest();
        req.setListMembers(null);

        ResponseObject<?> res = service.updateProjectMembers("pid", req);
        assertEquals(HttpStatus.OK, res.getStatus());
        assertEquals("Cập nhật thành viên dự án thành công", res.getMessage());
    }
    @Test
    void testUpdateProjectMembers_MemberRoleIsTester() {
        Project prj = new Project();
        when(projectRepository.findById("pid")).thenReturn(Optional.of(prj));
        ADProjectSTRequest mem = new ADProjectSTRequest();
        mem.setEmail("tester@fpt.edu.vn");
        mem.setRole("anyrole"); // sẽ gán là TESTER

        MaUserCreateUpdateRequest req = new MaUserCreateUpdateRequest();
        req.setListMembers(List.of(mem));
        Staff staff = new Staff();
        when(maProjectStaffRepository.findByEmailFe("tester@fpt.edu.vn")).thenReturn(Optional.of(staff));
        when(maProjectStaffRepository.findByEmailFpt("tester@fpt.edu.vn")).thenReturn(Optional.empty());
        when(staffProjectRepository.findByStaffAndProject(any(Staff.class), any(Project.class))).thenReturn(Optional.empty());
        when(staffProjectRepository.save(any(StaffProject.class))).thenReturn(new StaffProject());

        ResponseObject<?> res = service.updateProjectMembers("pid", req);
        assertEquals(HttpStatus.OK, res.getStatus());
    }
    @Test
    void testUpdateProjectMembers_StaffExistedUpdate() {
        Project prj = new Project();
        when(projectRepository.findById("pid")).thenReturn(Optional.of(prj));

        ADProjectSTRequest mem = new ADProjectSTRequest();
        mem.setEmail("exist@fpt.edu.vn");
        mem.setRole("DEV");

        MaUserCreateUpdateRequest req = new MaUserCreateUpdateRequest();
        req.setListMembers(List.of(mem));
        Staff staff = new Staff();
        StaffProject existed = new StaffProject();
        existed.setStatus(EntityStatus.INACTIVE); // test đổi trạng thái

        when(maProjectStaffRepository.findByEmailFe("exist@fpt.edu.vn")).thenReturn(Optional.of(staff));
        when(maProjectStaffRepository.findByEmailFpt("exist@fpt.edu.vn")).thenReturn(Optional.empty());
        when(staffProjectRepository.findByStaffAndProject(any(Staff.class), any(Project.class))).thenReturn(Optional.of(existed));
        when(staffProjectRepository.save(any(StaffProject.class))).thenReturn(existed);

        ResponseObject<?> res = service.updateProjectMembers("pid", req);
        assertEquals(HttpStatus.OK, res.getStatus());
        assertEquals(EntityStatus.INACTIVE, existed.getStatus());
    }
    @Test
    void testUpdateProjectMembers_StudentExistedUpdate() {
        Project prj = new Project();
        when(projectRepository.findById("pid")).thenReturn(Optional.of(prj));

        ADProjectSTRequest mem = new ADProjectSTRequest();
        mem.setEmail("student@fpt.edu.vn");
        mem.setRole("DEV");

        MaUserCreateUpdateRequest req = new MaUserCreateUpdateRequest();
        req.setListMembers(List.of(mem));
        when(maProjectStaffRepository.findByEmailFe("student@fpt.edu.vn")).thenReturn(Optional.empty());
        when(maProjectStaffRepository.findByEmailFpt("student@fpt.edu.vn")).thenReturn(Optional.empty());
        Student student = new Student();
        StaffProject existed = new StaffProject();
        existed.setStatus(EntityStatus.INACTIVE);

        when(maProjectStudentRepository.findByEmail("student@fpt.edu.vn")).thenReturn(Optional.of(student));
        when(staffProjectRepository.findByStudentAndProject(any(Student.class), any(Project.class))).thenReturn(Optional.of(existed));
        when(staffProjectRepository.save(any(StaffProject.class))).thenReturn(existed);

        ResponseObject<?> res = service.updateProjectMembers("pid", req);
        assertEquals(HttpStatus.OK, res.getStatus());
        assertEquals(EntityStatus.INACTIVE, existed.getStatus());
    }
    @Test
    void testIdFacility_FacilityEmptyString() {
        Project p = new Project();
        when(projectRepository.findById("p1")).thenReturn(Optional.of(p));
        when(projectRepository.idFacility("p1")).thenReturn("");
        ResponseObject<?> res = service.idFacility("p1");
        assertEquals(HttpStatus.OK, res.getStatus());
        assertEquals("Lấy cơ sở thành công", res.getMessage());
    }

    @Test
    void testDeleteUser_ToggleActiveInactive() {
        StaffProject sp = new StaffProject();
        sp.setStatus(EntityStatus.ACTIVE);
        when(maUserProjectRepository.findById("idx")).thenReturn(Optional.of(sp));
        when(maUserProjectRepository.save(any(StaffProject.class))).thenReturn(sp);

        // Lần đầu: ACTIVE -> INACTIVE
        ResponseObject<?> res1 = service.deleteUser("idx");
        assertEquals(EntityStatus.INACTIVE, sp.getStatus());

        // Lần hai: INACTIVE -> ACTIVE
        ResponseObject<?> res2 = service.deleteUser("idx");
        assertEquals(EntityStatus.ACTIVE, sp.getStatus());
    }

    @Test
    void testUpdateProjectMembers_RequestNull() {
        Project prj = new Project();
        when(projectRepository.findById("pid")).thenReturn(Optional.of(prj));

        assertThrows(NullPointerException.class, () -> service.updateProjectMembers("pid", null));
    }
    @Test
    void testUpdateProjectMembers_RoleQuanLi() {
        Project prj = new Project();
        when(projectRepository.findById("pid")).thenReturn(Optional.of(prj));
        ADProjectSTRequest mem = new ADProjectSTRequest();
        mem.setEmail("quanli@fpt.edu.vn");
        mem.setRole("QUAN_Li");

        MaUserCreateUpdateRequest req = new MaUserCreateUpdateRequest();
        req.setListMembers(List.of(mem));
        Staff staff = new Staff();
        when(maProjectStaffRepository.findByEmailFe("quanli@fpt.edu.vn")).thenReturn(Optional.of(staff));
        when(maProjectStaffRepository.findByEmailFpt("quanli@fpt.edu.vn")).thenReturn(Optional.empty());
        when(staffProjectRepository.findByStaffAndProject(any(Staff.class), any(Project.class))).thenReturn(Optional.empty());
        when(staffProjectRepository.save(any(StaffProject.class))).thenReturn(new StaffProject());

        ResponseObject<?> res = service.updateProjectMembers("pid", req);
        assertEquals(HttpStatus.OK, res.getStatus());
    }
    @Test
    void testIdFacility_FacilityBlankString() {
        Project p = new Project();
        when(projectRepository.findById("p1")).thenReturn(Optional.of(p));
        when(projectRepository.idFacility("p1")).thenReturn("   ");
        ResponseObject<?> res = service.idFacility("p1");
        assertEquals(HttpStatus.OK, res.getStatus());
        assertEquals("Lấy cơ sở thành công", res.getMessage());
    }
    @Test
    void testUpdateProjectMembers_MultiMembers_OneNotFound() {
        Project prj = new Project();
        when(projectRepository.findById("pid")).thenReturn(Optional.of(prj));

        ADProjectSTRequest mem1 = new ADProjectSTRequest();
        mem1.setEmail("user1@fpt.edu.vn");
        mem1.setRole("DEV");
        Staff staff = new Staff();
        when(maProjectStaffRepository.findByEmailFe("user1@fpt.edu.vn")).thenReturn(Optional.of(staff));
        when(maProjectStaffRepository.findByEmailFpt("user1@fpt.edu.vn")).thenReturn(Optional.empty());
        when(staffProjectRepository.findByStaffAndProject(any(Staff.class), any(Project.class))).thenReturn(Optional.empty());
        when(staffProjectRepository.save(any(StaffProject.class))).thenReturn(new StaffProject());

        ADProjectSTRequest mem2 = new ADProjectSTRequest();
        mem2.setEmail("notfound@fpt.edu.vn");
        mem2.setRole("DEV");
        when(maProjectStaffRepository.findByEmailFe("notfound@fpt.edu.vn")).thenReturn(Optional.empty());
        when(maProjectStaffRepository.findByEmailFpt("notfound@fpt.edu.vn")).thenReturn(Optional.empty());
        when(maProjectStudentRepository.findByEmail("notfound@fpt.edu.vn")).thenReturn(Optional.empty());

        MaUserCreateUpdateRequest req = new MaUserCreateUpdateRequest();
        req.setListMembers(List.of(mem1, mem2));

        ResponseObject<?> res = service.updateProjectMembers("pid", req);
        assertEquals(HttpStatus.BAD_REQUEST, res.getStatus());
        assertTrue(res.getMessage().contains("Không tìm thấy người dùng"));
    }
    @Test
    void testListUserProject_RequestNull() {
        assertThrows(NullPointerException.class, () -> service.listUserProject(null, "pid", "phid"));
    }
    @Test
    void testDeleteUser_StatusUnknown() {
        StaffProject sp = new StaffProject();
        sp.setStatus(EntityStatus.valueOf("ACTIVE")); // Nếu bạn có thêm trạng thái custom thì thử mock ở đây
        when(maUserProjectRepository.findById("idX")).thenReturn(Optional.of(sp));
        when(maUserProjectRepository.save(any())).thenReturn(sp);

        ResponseObject<?> res = service.deleteUser("idX");
        assertEquals(HttpStatus.OK, res.getStatus());
    }
    @Test
    void testUpdateProjectMembers_MemberRoleNull() {
        Project prj = new Project();
        when(projectRepository.findById("pid")).thenReturn(Optional.of(prj));
        ADProjectSTRequest mem = new ADProjectSTRequest();
        mem.setEmail("nonerole@fpt.edu.vn");
        mem.setRole(null);

        MaUserCreateUpdateRequest req = new MaUserCreateUpdateRequest();
        req.setListMembers(List.of(mem));
        Staff staff = new Staff();
        when(maProjectStaffRepository.findByEmailFe("nonerole@fpt.edu.vn")).thenReturn(Optional.of(staff));
        when(maProjectStaffRepository.findByEmailFpt("nonerole@fpt.edu.vn")).thenReturn(Optional.empty());
        when(staffProjectRepository.findByStaffAndProject(any(), any())).thenReturn(Optional.empty());
        when(staffProjectRepository.save(any())).thenReturn(new StaffProject());

        ResponseObject<?> res = service.updateProjectMembers("pid", req);
        assertEquals(HttpStatus.OK, res.getStatus());
    }
    @Test
    void testUpdateProjectMembers_StaffAndStudentPresent() {
        Project prj = new Project();
        when(projectRepository.findById("pid")).thenReturn(Optional.of(prj));
        ADProjectSTRequest mem = new ADProjectSTRequest();
        mem.setEmail("dualmem@fpt.edu.vn");
        mem.setRole("DEV");
        MaUserCreateUpdateRequest req = new MaUserCreateUpdateRequest();
        req.setListMembers(List.of(mem));

        Staff staff = new Staff();
        Student student = new Student();
        when(maProjectStaffRepository.findByEmailFe("dualmem@fpt.edu.vn")).thenReturn(Optional.of(staff));
        when(maProjectStaffRepository.findByEmailFpt("dualmem@fpt.edu.vn")).thenReturn(Optional.empty());
        // Khi staffProject set cả staff và student
        StaffProject sp = new StaffProject();
        sp.setStaff(staff); sp.setStudent(student);

        when(staffProjectRepository.findByStaffAndProject(any(), any())).thenReturn(Optional.of(sp));
        when(staffProjectRepository.save(any())).thenReturn(sp);

        ResponseObject<?> res = service.updateProjectMembers("pid", req);
        assertEquals(HttpStatus.OK, res.getStatus());
    }

}

