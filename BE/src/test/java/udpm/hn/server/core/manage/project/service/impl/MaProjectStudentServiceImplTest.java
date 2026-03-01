package udpm.hn.server.core.manage.project.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import udpm.hn.server.core.common.base.ResponseObject;
import udpm.hn.server.core.manage.project.model.request.MaUserProjectRequest;
import udpm.hn.server.core.manage.project.model.response.MAUserProjectResponse;
import udpm.hn.server.core.manage.project.model.response.MaProjectStaffStudentResponse;
import udpm.hn.server.core.manage.project.repository.MAUserProjectRepository;
import udpm.hn.server.core.manage.project.repository.MaProjectStaffRepository;
import udpm.hn.server.core.manage.project.repository.MaProjectStudentRepository;
import udpm.hn.server.utils.Helper;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MaProjectStudentServiceImplTest {

    @Mock
    private MAUserProjectRepository maUserProjectRepository;

    @Mock
    private MaProjectStudentRepository maProjectStudentRepository;

    @Mock
    private MaProjectStaffRepository maProjectStaffRepository;

    @InjectMocks
    private MaProjectStudentServiceImpl service;

    @BeforeEach
    void setUp() {
        // nếu cần chuẩn bị gì thêm
    }

    @Test
    void testGetAll_ShouldReturnStudentList() {
        // Arrange
        String majorFacilityId = "mf1";

        MaProjectStaffStudentResponse student1 = mock(MaProjectStaffStudentResponse.class);
        MaProjectStaffStudentResponse student2 = mock(MaProjectStaffStudentResponse.class);
        List<MaProjectStaffStudentResponse> mockData = List.of(student1, student2);

        when(maProjectStudentRepository.getAllStudent(majorFacilityId)).thenReturn(mockData);

        // Act
        ResponseObject<?> response = service.getAll(majorFacilityId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(mockData, response.getData());
    }


    @Test
    void testGetAllUserProject_ShouldReturnPagedUsers() {
        // Arrange
        MaUserProjectRequest request = new MaUserProjectRequest();
        request.setIdProject("p1");

        Pageable pageable = Helper.createPageable(request, "createdDate");

        MAUserProjectResponse mockUser1 = mock(MAUserProjectResponse.class);
        MAUserProjectResponse mockUser2 = mock(MAUserProjectResponse.class);

        Page<MAUserProjectResponse> pageData = new PageImpl<>(List.of(mockUser1, mockUser2));

        when(maUserProjectRepository.getAllUserByProject(any(Pageable.class), eq("p1")))
                .thenReturn(pageData);

        // Act
        ResponseObject<?> response = service.getAllUserProject(request);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(pageData, response.getData());
    }


    @Test
    void testGetAllStaff_ShouldReturnStaffList() {
        // Arrange
        String majorFacilityId = "mf2";

        MaProjectStaffStudentResponse mockStaff1 = mock(MaProjectStaffStudentResponse.class);
        MaProjectStaffStudentResponse mockStaff2 = mock(MaProjectStaffStudentResponse.class);
        List<MaProjectStaffStudentResponse> staffList = List.of(mockStaff1, mockStaff2);

        when(maProjectStaffRepository.getALl(majorFacilityId)).thenReturn(staffList);

        // Act
        ResponseObject<?> response = service.getAllStaff(majorFacilityId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(staffList, response.getData());
    }


    @Test
    void testGetAllStudentByProject_ShouldReturnStudentList() {
        String projectId = "p2";

        MaProjectStaffStudentResponse student1 = mock(MaProjectStaffStudentResponse.class);
        MaProjectStaffStudentResponse student2 = mock(MaProjectStaffStudentResponse.class);
        List<MaProjectStaffStudentResponse> studentList = List.of(student1, student2);

        when(maProjectStudentRepository.getAllStudentByProject(projectId)).thenReturn(studentList);

        ResponseObject<?> response = service.getAllStudentByProject(projectId);

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(studentList, response.getData());
    }


    @Test
    void testGetAllStaffByProject_ShouldReturnStaffList() {
        String projectId = "p3";

        MaProjectStaffStudentResponse staff1 = mock(MaProjectStaffStudentResponse.class);
        MaProjectStaffStudentResponse staff2 = mock(MaProjectStaffStudentResponse.class);
        List<MaProjectStaffStudentResponse> staffList = List.of(staff1, staff2);

        when(maProjectStaffRepository.getAllStaffByProject(projectId)).thenReturn(staffList);

        ResponseObject<?> response = service.getAllStaffByProject(projectId);

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(staffList, response.getData());
    }

}
