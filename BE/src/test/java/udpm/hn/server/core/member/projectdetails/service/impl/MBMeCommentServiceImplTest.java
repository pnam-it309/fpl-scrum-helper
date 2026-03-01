package udpm.hn.server.core.member.projectdetails.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import udpm.hn.server.core.common.base.ResponseObject;
import udpm.hn.server.core.member.projectdetails.model.request.*;
import udpm.hn.server.core.member.projectdetails.model.response.MBMeCommentResponse;
import udpm.hn.server.core.member.projectdetails.model.response.MBMeUserInProjectResponse;
import udpm.hn.server.core.member.projectdetails.repository.*;
import udpm.hn.server.entity.Comment;
import udpm.hn.server.entity.StaffProject;
import udpm.hn.server.entity.Todo;
import udpm.hn.server.infrastructure.constant.EntityStatus;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class MBMeCommentServiceImplTest {

    @InjectMocks
    private MBMeCommentServiceImpl commentService;

    @Mock
    private MBMeCommentRepository commentRepository;
    @Mock
    private MBMeTodoRepository todoRepository;
    @Mock
    private MBMeMemberProjectRepository memberProjectRepository;
    @Mock
    private MBMeStaffProjectRepository staffProjectRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void addComment_shouldReturnSuccess() {
        MBMeCreateCommentRequest request = new MBMeCreateCommentRequest();
        request.setIdTodo("todo1");
        request.setIdUser("user1");
        request.setIdProject("project1");
        request.setContent("Test comment");
        request.setMentionedEmails(List.of("abc@example.com"));

        Todo todo = new Todo();
        StaffProject staffProject = new StaffProject();

        when(todoRepository.findById("todo1")).thenReturn(Optional.of(todo));
        when(staffProjectRepository.findIdStaffProjectByIdUserAndIdProject("user1", "project1"))
                .thenReturn(Optional.of("staffProject1"));
        when(memberProjectRepository.findById("staffProject1")).thenReturn(Optional.of(staffProject));

        ResponseObject<?> response = commentService.addCommment(request);
        assertEquals(HttpStatus.OK, response.getStatus());
        verify(commentRepository, times(1)).save(any(Comment.class));
    }

    @Test
    void addComment_shouldReturnNotFound_whenTodoMissing() {
        MBMeCreateCommentRequest request = new MBMeCreateCommentRequest();
        request.setIdTodo("todo1");
        when(todoRepository.findById("todo1")).thenReturn(Optional.empty());

        ResponseObject<?> response = commentService.addCommment(request);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatus());
    }

    @Test
    void getAllCommentByIdTodo_shouldReturnPage() {
        MBMeFindCommentRequest request = new MBMeFindCommentRequest();
        Pageable pageable = PageRequest.of(0, 10);

        MBMeCommentResponse mockResponse = new MBMeCommentResponse() {
            public String getContent() { return "Test comment"; }
            public String getId() { return "c1"; }
            public String getTodoId() { return "todo1"; }
            public String getProjectId() { return "project1"; }
            public String getStaffProjectId() { return "sp1"; }
            public Integer getStatusEdit() { return 0; }
            public Long getCreatedDate() { return System.currentTimeMillis(); }
            public String getUserId() { return "user1"; }
            public String getUserCode() { return "U001"; }
            public String getUserName() { return "User One"; }
            public String getUserEmail() { return "user1@example.com"; }
            public String getUserImage() { return "img1.jpg"; }
        };

        Page<MBMeCommentResponse> page = new PageImpl<>(List.of(mockResponse));

        when(commentRepository.getAllCommentByIdTodo(any(Pageable.class), eq(request)))
                .thenReturn(page);

        ResponseObject<?> response = commentService.getAllCommentByIdTodo(request);

        assertEquals(HttpStatus.OK, response.getStatus());
        assertNotNull(response.getData());
    }


    @Test
    void updateComment_shouldUpdateSuccessfully() {
        MBMeUpdateCommentRequest request = new MBMeUpdateCommentRequest();
        request.setId("c1");
        request.setIdStaffProject("s1");
        request.setContent("Updated");

        Comment comment = new Comment();
        StaffProject staffProject = new StaffProject();
        staffProject.setId("s1");
        comment.setStaffProject(staffProject);

        when(commentRepository.findById("c1")).thenReturn(Optional.of(comment));
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        ResponseObject<?> response = commentService.updateComment(request);
        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    void updateComment_shouldReturnNotFound_whenInvalidOwner() {
        MBMeUpdateCommentRequest request = new MBMeUpdateCommentRequest();
        request.setId("c1");
        request.setIdStaffProject("wrongId");

        Comment comment = new Comment();
        StaffProject staffProject = new StaffProject();
        staffProject.setId("s1");
        comment.setStaffProject(staffProject);

        when(commentRepository.findById("c1")).thenReturn(Optional.of(comment));

        ResponseObject<?> response = commentService.updateComment(request);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatus());
    }

    @Test
    void deleteComment_shouldSucceed() {
        MBMeDeleteCommentRequest request = new MBMeDeleteCommentRequest();
        request.setId("c1");
        request.setIdStaffProject("s1");

        Comment comment = new Comment();
        StaffProject staffProject = new StaffProject();
        staffProject.setId("s1");
        comment.setStaffProject(staffProject);

        when(commentRepository.findById("c1")).thenReturn(Optional.of(comment));
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        ResponseObject<?> response = commentService.deleteComment(request);
        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(EntityStatus.INACTIVE, comment.getStatus());
    }

    @Test
    void deleteComment_shouldReturnNotFound_whenWrongStaff() {
        MBMeDeleteCommentRequest request = new MBMeDeleteCommentRequest();
        request.setId("c1");
        request.setIdStaffProject("wrongId");

        Comment comment = new Comment();
        StaffProject staffProject = new StaffProject();
        staffProject.setId("s1");
        comment.setStaffProject(staffProject);

        when(commentRepository.findById("c1")).thenReturn(Optional.of(comment));

        ResponseObject<?> response = commentService.deleteComment(request);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatus());
    }

    @Test
    void getAllMemberInProject_shouldReturnList() {
        MBMeFindTagMemberRequest request = new MBMeFindTagMemberRequest();
        request.setIdProject("p1");
        request.setEmail("email@example.com");

        MBMeUserInProjectResponse user1 = mock(MBMeUserInProjectResponse.class);
        when(user1.getUserId()).thenReturn("u1");
        when(user1.getUserName()).thenReturn("User One");
        when(user1.getUserEmail()).thenReturn("user1@example.com");

        MBMeUserInProjectResponse user2 = mock(MBMeUserInProjectResponse.class);
        when(user2.getUserId()).thenReturn("u2");
        when(user2.getUserName()).thenReturn("User Two");
        when(user2.getUserEmail()).thenReturn("user2@example.com");

        when(commentRepository.getUsersByProject("p1", "email@example.com"))
                .thenReturn(List.of(user1, user2));

        ResponseObject<?> response = commentService.getAllMemberInProject(request);

        assertEquals(HttpStatus.OK, response.getStatus());
        assertNotNull(response.getData());

        List<?> dataList = (List<?>) response.getData();
        assertEquals(2, dataList.size());
    }

}
