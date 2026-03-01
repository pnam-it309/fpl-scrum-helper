package udpm.hn.server.core.member.projectdetails.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import udpm.hn.server.core.common.base.ResponseObject;
import udpm.hn.server.core.member.projectdetails.model.request.MBMeCreateNotificationCommentRequest;
import udpm.hn.server.core.member.projectdetails.repository.*;
import udpm.hn.server.entity.MemberNotification;
import udpm.hn.server.entity.Notification;
import udpm.hn.server.entity.StaffProject;
import udpm.hn.server.entity.Todo;
import udpm.hn.server.infrastructure.constant.EntityStatus;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class MBMeNotificationServiceImplTest {

    @InjectMocks
    private MBMeNotificationServiceImpl service;

    @Mock
    private MBMeNotificationMemberRepository notificationMemberRepo;

    @Mock
    private MBMeNotificationRepository notificationRepo;

    @Mock
    private MBMeTodoRepository todoRepo;

    @Mock
    private MBMeStaffProjectRepository staffProjectRepo;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private MBMeCreateNotificationCommentRequest createValidRequest() {
        MBMeCreateNotificationCommentRequest request = new MBMeCreateNotificationCommentRequest();
        request.setIdUser("user1");
        request.setIdProject("project1");
        request.setIdTodo("todo1");
        request.setUsername("Alice");
        request.setUrl("/some-url");
        request.setMentionedIds(List.of("user2", "user3"));
        return request;
    }

    @Test
    void createNotification_shouldReturnNotFound_whenTodoDoesNotExist() {
        MBMeCreateNotificationCommentRequest request = createValidRequest();
        when(todoRepo.findById("todo1")).thenReturn(Optional.empty());

        ResponseObject<?> response = service.createNotification(request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatus());
        assertEquals("khoogn tìm thấy todo", response.getMessage());
        assertNull(response.getData());
    }

    @Test
    void createNotification_shouldCreateSuccessfully() {
        MBMeCreateNotificationCommentRequest request = createValidRequest();

        Todo todo = new Todo();
        when(todoRepo.findById("todo1")).thenReturn(Optional.of(todo));

        Notification savedNotification = new Notification();
        when(notificationRepo.save(any(Notification.class))).thenReturn(savedNotification);

        when(staffProjectRepo.findIdStaffProjectByIdUserAndIdProject("user2", "project1"))
                .thenReturn(Optional.of("staff1"));
        when(staffProjectRepo.findIdStaffProjectByIdUserAndIdProject("user3", "project1"))
                .thenReturn(Optional.of("staff2"));

        when(staffProjectRepo.findById("staff1")).thenReturn(Optional.of(new StaffProject()));
        when(staffProjectRepo.findById("staff2")).thenReturn(Optional.of(new StaffProject()));

        ResponseObject<?> response = service.createNotification(request);

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals("Thêm thành công thng báo ", response.getMessage());
        assertNull(response.getData());

        ArgumentCaptor<List<MemberNotification>> captor = ArgumentCaptor.forClass(List.class);
        verify(notificationMemberRepo, times(1)).saveAll(captor.capture());

        List<MemberNotification> saved = captor.getValue();
        assertEquals(2, saved.size());

        for (MemberNotification mn : saved) {
            assertEquals(savedNotification, mn.getNotification());
            assertNotNull(mn.getStaffProject());
            assertEquals(EntityStatus.ACTIVE, mn.getStatus());
        }
    }

    @Test
    void createNotification_shouldIgnoreSelfMention() {
        MBMeCreateNotificationCommentRequest request = createValidRequest();
        request.setMentionedIds(List.of("user1", "user2")); // user1 là người tạo

        Todo todo = new Todo();
        when(todoRepo.findById("todo1")).thenReturn(Optional.of(todo));

        Notification savedNotification = new Notification();
        when(notificationRepo.save(any(Notification.class))).thenReturn(savedNotification);

        when(staffProjectRepo.findIdStaffProjectByIdUserAndIdProject("user2", "project1"))
                .thenReturn(Optional.of("staff2"));
        when(staffProjectRepo.findById("staff2")).thenReturn(Optional.of(new StaffProject()));

        ResponseObject<?> response = service.createNotification(request);

        assertEquals(HttpStatus.OK, response.getStatus());
        verify(notificationMemberRepo, times(1)).saveAll(argThat(list -> {
            if (list instanceof List<?>) {
                return ((List<?>) list).size() == 1;
            }
            int count = 0;
            for (Object ignored : list) count++;
            return count == 1;
        }));
    }

    @Test
    void createNotification_shouldSkipInvalidStaffProject() {
        MBMeCreateNotificationCommentRequest request = createValidRequest();

        Todo todo = new Todo();
        when(todoRepo.findById("todo1")).thenReturn(Optional.of(todo));

        Notification savedNotification = new Notification();
        when(notificationRepo.save(any(Notification.class))).thenReturn(savedNotification);

        // Only one valid staff project
        when(staffProjectRepo.findIdStaffProjectByIdUserAndIdProject("user2", "project1"))
                .thenReturn(Optional.of("staff1"));
        when(staffProjectRepo.findById("staff1")).thenReturn(Optional.of(new StaffProject()));

        // user3 is invalid (not found)
        when(staffProjectRepo.findIdStaffProjectByIdUserAndIdProject("user3", "project1"))
                .thenReturn(Optional.of("staffX"));
        when(staffProjectRepo.findById("staffX")).thenReturn(Optional.empty());

        ResponseObject<?> response = service.createNotification(request);

        assertEquals(HttpStatus.OK, response.getStatus());
        verify(notificationMemberRepo, times(1)).saveAll(argThat(list -> {
            int validCount = 0;
            for (Object item : list) {
                if (item instanceof MemberNotification notification) {
                    if (notification.getStaffProject() != null) {
                        validCount++;
                    }
                }
            }
            return validCount == 1;
        }));

    }

}
