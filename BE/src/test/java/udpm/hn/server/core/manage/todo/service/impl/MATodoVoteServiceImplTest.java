package udpm.hn.server.core.manage.todo.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import udpm.hn.server.core.common.base.ResponseObject;
import udpm.hn.server.core.manage.todo.model.request.MAVoteTodoRequest;
import udpm.hn.server.core.manage.todo.repository.MATodoStageVoteRepository;
import udpm.hn.server.core.manage.todo.repository.MATodoVoteRepository;
import udpm.hn.server.core.manage.todo.service.MATodoVoteService;
import udpm.hn.server.entity.*;
import udpm.hn.server.repository.*;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
public class MATodoVoteServiceImplTest {

    @InjectMocks
    private MATodoVoteServiceImpl service;

    @Mock
    private StudentRepository studentRepository;
    @Mock
    private StaffRepository staffRepository;
    @Mock
    private ToDoRepository toDoRepository;
    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private TodoVoteRepository todoVoteRepository;
    @Mock
    private StaffProjectRepository staffProjectRepository;
    @Mock
    private MATodoVoteRepository maTodoVoteRepository;
    @Mock
    private MATodoStageVoteRepository maStageVoteRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createVote_shouldReturnNotFound_whenNoActiveStageVote() {
        MAVoteTodoRequest request = new MAVoteTodoRequest();
        request.setIdUser("user1");
        request.setIdTodo("todo1");
        request.setIdProject("project1");

        when(maStageVoteRepository.findActiveStageVoteIdByProject(anyLong(), anyString()))
                .thenReturn(Optional.empty());

        ResponseObject<?> response = service.createVote(request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatus());
        assertEquals("cuộc bình chọn chưa diễn ra", response.getMessage());
    }

    @Test
    void createVote_shouldReturnNotFound_whenProjectNotFound() {
        MAVoteTodoRequest request = new MAVoteTodoRequest();
        request.setIdUser("user1");
        request.setIdTodo("todo1");
        request.setIdProject("project1");

        when(maStageVoteRepository.findActiveStageVoteIdByProject(anyLong(), anyString()))
                .thenReturn(Optional.of("stage1"));
        when(projectRepository.findById("project1")).thenReturn(Optional.empty());

        ResponseObject<?> response = service.createVote(request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatus());
        assertEquals("không tìm thấy project", response.getMessage());
    }

    @Test
    void createVote_shouldReturnCreated_whenStudentVoteSuccessfully() {
        MAVoteTodoRequest request = new MAVoteTodoRequest();
        request.setIdUser("user1");
        request.setIdTodo("todo1");
        request.setIdProject("project1");

        Project project = new Project();
        Todo todo = new Todo();
        Student student = new Student();
        StaffProject staffProject = new StaffProject();
        StageVote stageVote = new StageVote();

        when(maStageVoteRepository.findActiveStageVoteIdByProject(anyLong(), eq("project1")))
                .thenReturn(Optional.of("stage1"));
        when(maStageVoteRepository.findById("stage1")).thenReturn(Optional.of(stageVote));
        when(projectRepository.findById("project1")).thenReturn(Optional.of(project));
        when(toDoRepository.findById("todo1")).thenReturn(Optional.of(todo));
        when(studentRepository.findById("user1")).thenReturn(Optional.of(student));
        when(staffRepository.findById("user1")).thenReturn(Optional.empty()); // 👈 Bỏ qua staff
        when(staffProjectRepository.findByStudentAndProject(student, project)).thenReturn(Optional.of(staffProject));
        when(maTodoVoteRepository.findByTodoAndStaffProjectAndStageVoteId(todo, staffProject, "stage1"))
                .thenReturn(Optional.empty());

        ResponseObject<?> response = service.createVote(request);

        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertEquals("Bình chọn thành công", response.getMessage());

        verify(todoVoteRepository, times(1)).save(any(TodoVote.class));
    }


    @Test
    void deleteVote_shouldReturnNotFound_whenVoteNotExists() {
        MAVoteTodoRequest request = new MAVoteTodoRequest();
        request.setIdUser("user1");
        request.setIdTodo("todo1");
        request.setIdProject("project1");

        Project project = new Project();
        Todo todo = new Todo();
        Staff staff = new Staff();
        StaffProject staffProject = new StaffProject();

        when(maStageVoteRepository.findActiveStageVoteIdByProject(anyLong(), anyString()))
                .thenReturn(Optional.of("stage1"));
        when(projectRepository.findById("project1")).thenReturn(Optional.of(project));
        when(toDoRepository.findById("todo1")).thenReturn(Optional.of(todo));
        when(staffRepository.findById("user1")).thenReturn(Optional.of(staff));
        when(staffProjectRepository.findByStaffAndProject(staff, project)).thenReturn(Optional.of(staffProject));
        when(maTodoVoteRepository.findByTodoAndStaffProjectAndStageVoteId(todo, staffProject, "stage1"))
                .thenReturn(Optional.empty());

        ResponseObject<?> response = service.deleteTodoVote(request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatus());
        assertEquals("Không tìm thấy vote này", response.getMessage());
    }

    @Test
    void deleteVote_shouldReturnCreated_whenVoteDeletedSuccessfully() {
        MAVoteTodoRequest request = new MAVoteTodoRequest();
        request.setIdUser("user1");
        request.setIdTodo("todo1");
        request.setIdProject("project1");

        Project project = new Project();
        Todo todo = new Todo();
        Staff staff = new Staff();
        StaffProject staffProject = new StaffProject();
        TodoVote vote = new TodoVote();

        when(maStageVoteRepository.findActiveStageVoteIdByProject(anyLong(), anyString()))
                .thenReturn(Optional.of("stage1"));
        when(projectRepository.findById("project1")).thenReturn(Optional.of(project));
        when(toDoRepository.findById("todo1")).thenReturn(Optional.of(todo));
        when(staffRepository.findById("user1")).thenReturn(Optional.of(staff));
        when(staffProjectRepository.findByStaffAndProject(staff, project)).thenReturn(Optional.of(staffProject));
        when(maTodoVoteRepository.findByTodoAndStaffProjectAndStageVoteId(todo, staffProject, "stage1"))
                .thenReturn(Optional.of(vote));

        ResponseObject<?> response = service.deleteTodoVote(request);

        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertEquals("Xóa bình chọn thành công", response.getMessage());
        verify(maTodoVoteRepository, times(1)).delete(vote);
    }

    @Test
    void getAllVote_shouldReturnData() {
        when(maTodoVoteRepository.getAllVote()).thenReturn(java.util.Collections.emptyList());

        ResponseObject<?> response = service.getAllVote();

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals("Lấy dữ liệu thành công", response.getMessage());
    }

    @Test
    void countTodoVotedByPriorityLevel_shouldReturnData() {
        when(maTodoVoteRepository.countTodoVotedByPriorityLevel(eq("project1"), anyLong()))
                .thenReturn(Collections.emptyList());

        ResponseObject<?> response = service.countTodoVotedByPriorityLevel("project1");

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals("Lấy dữ liệu thành công", response.getMessage());
    }


    @Test
    void findAllVotedTodos_shouldReturnData() {
        when(maTodoVoteRepository.findAllVotedTodos("project1")).thenReturn(java.util.Collections.emptyList());

        ResponseObject<?> response = service.findAllVotedTodos("project1");

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals("Lấy danh sách công việc thành công", response.getMessage());
    }
    @Test
    void createVote_shouldReturnNotFound_whenNoStudentOrStaff() {
        MAVoteTodoRequest request = new MAVoteTodoRequest();
        request.setIdUser("user1");
        request.setIdTodo("todo1");
        request.setIdProject("project1");

        Project project = new Project();
        Todo todo = new Todo();

        when(maStageVoteRepository.findActiveStageVoteIdByProject(anyLong(), eq("project1")))
                .thenReturn(Optional.of("stage1"));
        when(maStageVoteRepository.findById("stage1")).thenReturn(Optional.of(new StageVote()));
        when(projectRepository.findById("project1")).thenReturn(Optional.of(project));
        when(toDoRepository.findById("todo1")).thenReturn(Optional.of(todo));
        when(studentRepository.findById("user1")).thenReturn(Optional.empty());
        when(staffRepository.findById("user1")).thenReturn(Optional.empty());

        ResponseObject<?> response = service.createVote(request);

        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertEquals("Không tìm thấy user", response.getMessage());
    }
    @Test
    void createVote_shouldReturnNotFound_whenNoStaffProjectForStudent() {
        MAVoteTodoRequest request = new MAVoteTodoRequest();
        request.setIdUser("user1");
        request.setIdTodo("todo1");
        request.setIdProject("project1");

        Project project = new Project();
        Todo todo = new Todo();
        Student student = new Student();

        when(maStageVoteRepository.findActiveStageVoteIdByProject(anyLong(), eq("project1")))
                .thenReturn(Optional.of("stage1"));
        when(maStageVoteRepository.findById("stage1")).thenReturn(Optional.of(new StageVote()));
        when(projectRepository.findById("project1")).thenReturn(Optional.of(project));
        when(toDoRepository.findById("todo1")).thenReturn(Optional.of(todo));
        when(studentRepository.findById("user1")).thenReturn(Optional.of(student));
        when(staffRepository.findById("user1")).thenReturn(Optional.empty());
        when(staffProjectRepository.findByStudentAndProject(student, project)).thenReturn(Optional.empty());

        ResponseObject<?> response = service.createVote(request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatus());
        assertEquals("Không tìm thấy staffproject", response.getMessage());
    }

    @Test
    void createVote_shouldReturnNotFound_whenNoStaffProjectForStaff() {
        MAVoteTodoRequest request = new MAVoteTodoRequest();
        request.setIdUser("user1");
        request.setIdTodo("todo1");
        request.setIdProject("project1");

        Project project = new Project();
        Todo todo = new Todo();
        Staff staff = new Staff();

        when(maStageVoteRepository.findActiveStageVoteIdByProject(anyLong(), eq("project1")))
                .thenReturn(Optional.of("stage1"));
        when(maStageVoteRepository.findById("stage1")).thenReturn(Optional.of(new StageVote()));
        when(projectRepository.findById("project1")).thenReturn(Optional.of(project));
        when(toDoRepository.findById("todo1")).thenReturn(Optional.of(todo));
        when(studentRepository.findById("user1")).thenReturn(Optional.empty());
        when(staffRepository.findById("user1")).thenReturn(Optional.of(staff));
        when(staffProjectRepository.findByStaffAndProject(staff, project)).thenReturn(Optional.empty());

        ResponseObject<?> response = service.createVote(request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatus());
        assertEquals("Không tìm thấy staffproject", response.getMessage());
    }
    @Test
    void createVote_shouldReturnBadRequest_whenAlreadyVoted() {
        MAVoteTodoRequest request = new MAVoteTodoRequest();
        request.setIdUser("user1");
        request.setIdTodo("todo1");
        request.setIdProject("project1");

        Project project = new Project();
        Todo todo = new Todo();
        Student student = new Student();
        StaffProject staffProject = new StaffProject();
        StageVote stageVote = new StageVote();

        when(maStageVoteRepository.findActiveStageVoteIdByProject(anyLong(), eq("project1")))
                .thenReturn(Optional.of("stage1"));
        when(maStageVoteRepository.findById("stage1")).thenReturn(Optional.of(stageVote));
        when(projectRepository.findById("project1")).thenReturn(Optional.of(project));
        when(toDoRepository.findById("todo1")).thenReturn(Optional.of(todo));
        when(studentRepository.findById("user1")).thenReturn(Optional.of(student));
        when(staffRepository.findById("user1")).thenReturn(Optional.empty());
        when(staffProjectRepository.findByStudentAndProject(student, project)).thenReturn(Optional.of(staffProject));
        when(maTodoVoteRepository.findByTodoAndStaffProjectAndStageVoteId(todo, staffProject, "stage1"))
                .thenReturn(Optional.of(new TodoVote()));

        ResponseObject<?> response = service.createVote(request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatus());
        assertEquals("Đã bình chọn", response.getMessage());
    }
    @Test
    void deleteVote_shouldReturnNotFound_whenNoStaffProjectForStudent() {
        MAVoteTodoRequest request = new MAVoteTodoRequest();
        request.setIdUser("user1");
        request.setIdTodo("todo1");
        request.setIdProject("project1");

        Project project = new Project();
        Todo todo = new Todo();
        Student student = new Student();

        when(maStageVoteRepository.findActiveStageVoteIdByProject(anyLong(), anyString()))
                .thenReturn(Optional.of("stage1"));
        when(projectRepository.findById("project1")).thenReturn(Optional.of(project));
        when(toDoRepository.findById("todo1")).thenReturn(Optional.of(todo));
        when(studentRepository.findById("user1")).thenReturn(Optional.of(student));
        when(staffRepository.findById("user1")).thenReturn(Optional.empty());
        when(staffProjectRepository.findByStudentAndProject(student, project)).thenReturn(Optional.empty());

        ResponseObject<?> response = service.deleteTodoVote(request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatus());
        assertEquals("Không tìm thấy staffproject", response.getMessage());
    }

}
