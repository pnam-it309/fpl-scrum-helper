package udpm.hn.server.core.member.projectdetails.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import udpm.hn.server.core.common.base.ResponseObject;
import udpm.hn.server.core.member.projectdetails.model.response.MBMemberInfoTodoResponse;
import udpm.hn.server.core.member.projectdetails.repository.MBMeAssignRepository;
import udpm.hn.server.entity.Project;
import udpm.hn.server.entity.Staff;
import udpm.hn.server.entity.StaffProject;
import udpm.hn.server.entity.Student;
import udpm.hn.server.repository.ProjectRepository;
import udpm.hn.server.repository.StaffProjectRepository;
import udpm.hn.server.repository.StaffRepository;
import udpm.hn.server.repository.StudentRepository;
import udpm.hn.server.core.member.projectdetails.service.MBMeAssignService;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class MBMeTodoVoteServiceImplTest {

    @InjectMocks
    private MBMeAssignRepository mbMeAssignRepository;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private StaffProjectRepository staffProjectRepository;

    @Mock
    private StaffRepository staffRepository;

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private MBMeAssignService service;

    @BeforeEach
    void setUp() {
        mbMeAssignRepository = mock(MBMeAssignRepository.class);
        projectRepository = mock(ProjectRepository.class);
        staffProjectRepository = mock(StaffProjectRepository.class);
        staffRepository = mock(StaffRepository.class);
        studentRepository = mock(StudentRepository.class);

        service = new MBMeTodoVoteServiceImpl(
                mbMeAssignRepository,
                projectRepository,
                staffProjectRepository,
                staffRepository,
                studentRepository
        );
    }

    @Test
    void testGetAllMemberByIdTodo_success() {
        String todoId = "todo-123";
        MBMemberInfoTodoResponse mockMember = mock(MBMemberInfoTodoResponse.class);

        when(mbMeAssignRepository.getAllMemberByIdTodo(todoId))
                .thenReturn(List.of(mockMember));

        ResponseObject<?> response = service.getAllMemberByIdTodo(todoId);

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals("lấy id thành viên dự án thành công", response.getMessage());
        assertEquals(1, ((List<?>) response.getData()).size());
        verify(mbMeAssignRepository, times(1)).getAllMemberByIdTodo(todoId);
    }

    @Test
    void testGetTodoByIdStaffProject_withStaff_success() {
        String staffId = "staff-001";
        String projectId = "project-001";

        Staff staff = new Staff();
        Project project = new Project();
        StaffProject staffProject = new StaffProject();
        staffProject.setId("sp-001");

        when(staffRepository.findById(staffId)).thenReturn(Optional.of(staff));
        when(studentRepository.findById(staffId)).thenReturn(Optional.empty());
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(staffProjectRepository.findByStaffAndProject(staff, project)).thenReturn(Optional.of(staffProject));

        List<String> mockTodoIds = List.of("todo-1", "todo-2");
        when(mbMeAssignRepository.getIDTodoByIdStaffProject(eq("sp-001"), eq(projectId), anyLong()))
                .thenReturn(mockTodoIds);

        ResponseObject<?> response = service.getTodoByIdStaffProject(staffId, projectId);

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals("Lấy id todo nhân viên dự án thành công", response.getMessage());
        assertEquals(mockTodoIds, response.getData());
    }

    @Test
    void testGetTodoByIdStaffProject_withStudent_success() {
        String studentId = "stu-001";
        String projectId = "project-002";

        Student student = new Student();
        Project project = new Project();
        StaffProject staffProject = new StaffProject();
        staffProject.setId("sp-student");

        when(staffRepository.findById(studentId)).thenReturn(Optional.empty());
        when(studentRepository.findById(studentId)).thenReturn(Optional.of(student));
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(staffProjectRepository.findByStudentAndProject(student, project)).thenReturn(Optional.of(staffProject));

        List<String> mockTodoIds = List.of("todo-A");
        when(mbMeAssignRepository.getIDTodoByIdStaffProject(eq("sp-student"), eq(projectId), anyLong()))
                .thenReturn(mockTodoIds);

        ResponseObject<?> response = service.getTodoByIdStaffProject(studentId, projectId);

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals("Lấy id todo nhân viên dự án thành công", response.getMessage());
        assertEquals(mockTodoIds, response.getData());
    }

    @Test
    void testGetTodoByIdStaffProject_projectNotFound() {
        when(projectRepository.findById("not-exist")).thenReturn(Optional.empty());

        ResponseObject<?> response = service.getTodoByIdStaffProject("user", "not-exist");

        assertEquals(HttpStatus.NO_CONTENT, response.getStatus());
        assertEquals("Không tìm thấy project", response.getMessage());
        assertNull(response.getData());
    }

    @Test
    void testGetTodoByIdStaffProject_staffProjectNotFound() {
        String id = "user";
        Project project = new Project();

        when(staffRepository.findById(id)).thenReturn(Optional.of(new Staff()));
        when(studentRepository.findById(id)).thenReturn(Optional.empty());
        when(projectRepository.findById("proj")).thenReturn(Optional.of(project));
        when(staffProjectRepository.findByStaffAndProject(any(), any())).thenReturn(Optional.empty());

        ResponseObject<?> response = service.getTodoByIdStaffProject(id, "proj");

        assertEquals(HttpStatus.NO_CONTENT, response.getStatus());
        assertEquals("Không tìm thấy staffProject của người dùng", response.getMessage());
        assertNull(response.getData());
    }

}
