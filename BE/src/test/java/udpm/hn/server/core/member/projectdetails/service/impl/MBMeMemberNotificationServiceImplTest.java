package udpm.hn.server.core.member.projectdetails.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import udpm.hn.server.core.common.base.ResponseObject;
import udpm.hn.server.core.member.projectdetails.model.request.MBMeFindNotificationMemberRequest;
import udpm.hn.server.core.member.projectdetails.model.request.MBMeNotificationMemberResponse;
import udpm.hn.server.core.member.projectdetails.repository.MBMeNotificationMemberRepository;
import udpm.hn.server.core.member.projectdetails.repository.MBMeNotificationRepository;
import udpm.hn.server.core.member.projectdetails.repository.MBMeStaffProjectRepository;
import udpm.hn.server.entity.MemberNotification;
import udpm.hn.server.infrastructure.constant.EntityStatus;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class MBMeMemberNotificationServiceImplTest {

    @Mock
    MBMeNotificationRepository notificationRepository;

    @Mock
    MBMeNotificationMemberRepository notificationMemberRepository;

    @Mock
    MBMeStaffProjectRepository staffProjectRepository;

    @InjectMocks
    MBMeMemberNotificationServiceImpl service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllNotificationMember_shouldReturnNotificationsSuccessfully() {
        MBMeFindNotificationMemberRequest request = new MBMeFindNotificationMemberRequest();
        request.setIdMember("member1");

        MBMeNotificationMemberResponse mockResponse = mock(MBMeNotificationMemberResponse.class);
        Page<MBMeNotificationMemberResponse> mockPage =
                new PageImpl<>(Collections.singletonList(mockResponse));

        when(notificationMemberRepository.getAllNotificationMember(any(Pageable.class), eq("member1")))
                .thenReturn(mockPage);

        ResponseObject<?> response = service.getAllNotificationMember(request);

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals("Get all notification successfully ", response.getMessage());
        assertNotNull(response.getData());
    }

    @Test
    void updateStatus_shouldReturnNotFoundIfNotificationNotExist() {
        when(notificationMemberRepository.findById("noti123")).thenReturn(Optional.empty());

        ResponseObject<?> response = service.updateStatus("noti123");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatus());
        assertEquals("Notification member not found", response.getMessage());
    }

    @Test
    void updateStatus_shouldUpdateStatusSuccessfully() {
        MemberNotification notification = new MemberNotification();
        notification.setStatus(EntityStatus.ACTIVE);

        when(notificationMemberRepository.findById("noti123")).thenReturn(Optional.of(notification));
        when(notificationMemberRepository.save(any(MemberNotification.class))).thenReturn(notification);

        ResponseObject<?> response = service.updateStatus("noti123");

        assertNull(response);  // Method hiện trả về null sau khi cập nhật
        assertEquals(EntityStatus.INACTIVE, notification.getStatus());
        verify(notificationMemberRepository, times(1)).save(notification);
    }

    @Test
    void countNotificationMember_shouldReturnNullBecauseNotImplemented() {
        ResponseObject<?> response = service.countNotificationMember("member1");
        assertNull(response);
    }

    @Test
    void updateAllStatus_shouldReturnNullBecauseNotImplemented() {
        ResponseObject<?> response = service.updateAllStatus("member1");
        assertNull(response);
    }
}
