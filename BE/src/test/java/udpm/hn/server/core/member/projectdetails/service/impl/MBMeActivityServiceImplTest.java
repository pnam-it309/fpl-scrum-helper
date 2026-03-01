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
import udpm.hn.server.core.common.base.PageableObject;
import udpm.hn.server.core.common.base.ResponseObject;
import udpm.hn.server.core.member.projectdetails.model.request.MBMeFindActivityRequest;
import udpm.hn.server.core.member.projectdetails.model.response.MBMeActivityConvertResponse;
import udpm.hn.server.core.member.projectdetails.model.response.MBMeActivityResponse;
import udpm.hn.server.core.member.projectdetails.repository.MBMeActivityRepository;
import udpm.hn.server.entity.Staff;
import udpm.hn.server.entity.Student;
import udpm.hn.server.repository.StaffRepository;
import udpm.hn.server.repository.StudentRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class MBMeActivityServiceImplTest {


    @Mock
    private MBMeActivityRepository mbMeActivityRepository;

    @Mock
    private StaffRepository staffRepository;

    @Mock
    private StudentRepository studentRepository;

    @InjectMocks
    private MBMeActivityServiceImpl mbMeActivityService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllActivityWhereIdTodo_shouldReturnValidData() {
        // Given
        MBMeFindActivityRequest request = new MBMeFindActivityRequest();
        Pageable pageable = PageRequest.of(0, 10);

        // Mock MBMeActivityResponse
        MBMeActivityResponse activity = mock(MBMeActivityResponse.class);
        when(activity.getMemberCreatedId()).thenReturn("staff-1");
        when(activity.getMemberId()).thenReturn("member-1");
        when(activity.getProjectId()).thenReturn("project-1");
        when(activity.getTodoId()).thenReturn("todo-1");
        when(activity.getTodoListId()).thenReturn("list-1");
        when(activity.getContentAction()).thenReturn("Did something");
        when(activity.getContentActionProject()).thenReturn(null);
        when(activity.getUrlImage()).thenReturn("image.jpg");
        when(activity.getCreatedDate()).thenReturn(System.currentTimeMillis());
        when(activity.getUrlPath()).thenReturn("/todo/1");

        Page<MBMeActivityResponse> page = new PageImpl<>(Collections.singletonList(activity));
        when(mbMeActivityRepository.getAllActivityWhereIdTodo(any(Pageable.class), eq(request))).thenReturn(page);

        // Mock staff info
        Staff staff = new Staff();
        staff.setName("Staff Name");
        staff.setEmailFpt("staff@example.com");
        staff.setPicture("image.jpg");
        when(staffRepository.findById("staff-1")).thenReturn(Optional.of(staff));

        // When
        ResponseObject<?> response = mbMeActivityService.getAllActivityWhereIdTodo(request);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatus());
        assertNotNull(response.getData());

        PageableObject<?> pageableData = (PageableObject<?>) response.getData();
        assertEquals(1, pageableData.getData().size());

        MBMeActivityConvertResponse dto = (MBMeActivityConvertResponse) pageableData.getData().get(0);
        assertEquals("staff-1", dto.getMemberCreatedId());
        assertEquals("Staff Name", dto.getMemberName());
        assertEquals("staff@example.com", dto.getMemberEmail());
        assertEquals("image.jpg", dto.getMemberImage());

        verify(mbMeActivityRepository, times(1)).getAllActivityWhereIdTodo(any(Pageable.class), eq(request));
        verify(staffRepository, times(1)).findById("staff-1");
    }


    @Test
    void getAllActivityWhereIdProject_shouldReturnValidData() {
        // Given
        MBMeFindActivityRequest request = new MBMeFindActivityRequest();
        Pageable pageable = PageRequest.of(0, 10);

        // Mock MBMeActivityResponse
        MBMeActivityResponse activity = mock(MBMeActivityResponse.class);
        when(activity.getMemberCreatedId()).thenReturn("student-1");
        when(activity.getMemberId()).thenReturn("member-1");
        when(activity.getProjectId()).thenReturn("project-1");
        when(activity.getTodoId()).thenReturn("todo-1");
        when(activity.getTodoListId()).thenReturn("list-1");
        when(activity.getContentAction()).thenReturn(null);
        when(activity.getContentActionProject()).thenReturn("Updated something");
        when(activity.getUrlImage()).thenReturn("student.jpg");
        when(activity.getCreatedDate()).thenReturn(System.currentTimeMillis());
        when(activity.getUrlPath()).thenReturn("/project/1");

        Page<MBMeActivityResponse> page = new PageImpl<>(Collections.singletonList(activity));
        when(mbMeActivityRepository.getAllActivityWhereIdProject(any(Pageable.class), eq(request))).thenReturn(page);

        // Mock student info
        Student student = new Student();
        student.setName("Student Name");
        student.setEmail("student@example.com");
        student.setImage("student.jpg");
        when(studentRepository.findById("student-1")).thenReturn(Optional.of(student));

        // When
        ResponseObject<?> response = mbMeActivityService.getAllActivityWhereIdProject(request);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatus());

        PageableObject<?> pageableData = (PageableObject<?>) response.getData();
        assertNotNull(pageableData);
        assertEquals(1, pageableData.getData().size());

        MBMeActivityConvertResponse dto = (MBMeActivityConvertResponse) pageableData.getData().get(0);
        assertEquals("student-1", dto.getMemberCreatedId());
        assertEquals("Student Name", dto.getMemberName());
        assertEquals("student@example.com", dto.getMemberEmail());
        assertEquals("student.jpg", dto.getMemberImage());
        assertEquals("Updated something", dto.getContentActionProject());

        verify(mbMeActivityRepository, times(1)).getAllActivityWhereIdProject(any(Pageable.class), eq(request));
        verify(studentRepository, times(1)).findById("student-1");
    }


    @Test
    void countTotalActivitiesWhereIdProject_shouldReturnCount() {
        MBMeFindActivityRequest request = new MBMeFindActivityRequest();

        // Giả lập giá trị trả về là Long
        when(mbMeActivityRepository.countTotalActivitiesWhereIdProject(request)).thenReturn(5);

        ResponseObject<?> response = mbMeActivityService.countTotalActivitiesWhereIdProject(request);

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(5, response.getData()); // So sánh kiểu Long
    }

}
