package udpm.hn.server.core.admin.project.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import udpm.hn.server.core.admin.project.model.response.ADProjectStaffStudentResponse;
import udpm.hn.server.core.admin.project.repository.ADProjectStaffRepository;
import udpm.hn.server.core.admin.project.repository.ADProjectStudentRepository;
import udpm.hn.server.core.common.base.ResponseObject;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class ADProjectStudentServiceImplTest {

    @Mock
    private ADProjectStudentRepository adProjectStudentRepository;

    @Mock
    private ADProjectStaffRepository adProjectStaffRepository;

    @InjectMocks
    private ADProjectStudentServiceImpl service;

    private String mockMajorFacilityId;
    private String mockProjectId;

    @BeforeEach
    void setUp() {
        mockMajorFacilityId = "mf001";
        mockProjectId = "pj001";
    }

    @Test
    void testGetAllStudentsByMajorFacility() {
        // Arrange
        ADProjectStaffStudentResponse student1 = new ADProjectStaffStudentResponse() {
            public String getId() { return "1"; }
            public String getName() { return "Nguyễn Văn A"; }
            public String getCode() { return "SV001"; }
            public String getEmail() { return "a@student.com"; }
            public String getImage() { return "img1.png"; }
            public String getRole() { return null; }
        };

        ADProjectStaffStudentResponse student2 = new ADProjectStaffStudentResponse() {
            public String getId() { return "2"; }
            public String getName() { return "Trần Thị B"; }
            public String getCode() { return "SV002"; }
            public String getEmail() { return "b@student.com"; }
            public String getImage() { return "img2.png"; }
            public String getRole() { return null; }
        };

        List<ADProjectStaffStudentResponse> students = List.of(student1, student2);
        when(adProjectStudentRepository.getAllStudent(mockMajorFacilityId)).thenReturn(students);

        ResponseObject<?> response = service.getAll(mockMajorFacilityId);

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals("Lấy dữ liệu sinh viên thành công", response.getMessage());
        assertEquals(students, response.getData());

        verify(adProjectStudentRepository, times(1)).getAllStudent(mockMajorFacilityId);
    }


    @Test
    void testGetAllStaffByMajorFacility() {
        // Arrange
        ADProjectStaffStudentResponse staff1 = new ADProjectStaffStudentResponse() {
            public String getId() { return "1"; }
            public String getName() { return "Nguyễn Văn B"; }
            public String getCode() { return "STF001"; }
            public String getEmail() { return "b@staff.com"; }
            public String getImage() { return "image1.jpg"; }
            public String getRole() { return null; } // không cần thiết ở đây
        };

        List<ADProjectStaffStudentResponse> staffList = List.of(staff1);
        when(adProjectStaffRepository.getALl(mockMajorFacilityId)).thenReturn(staffList);

        ResponseObject<?> response = service.getAllStaff(mockMajorFacilityId);

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals("Lấy dữ liệu thành công", response.getMessage());
        assertEquals(staffList, response.getData());

        verify(adProjectStaffRepository, times(1)).getALl(mockMajorFacilityId);
    }


    @Test
    void testGetAllStudentsByProject() {
        ADProjectStaffStudentResponse student1 = new ADProjectStaffStudentResponse() {
            public String getId() { return "stu1"; }
            public String getName() { return "Student A"; }
            public String getCode() { return "S001"; }
            public String getEmail() { return "a@student.com"; }
            public String getImage() { return "a.jpg"; }
            public String getRole() { return null; }
        };

        ADProjectStaffStudentResponse student2 = new ADProjectStaffStudentResponse() {
            public String getId() { return "stu2"; }
            public String getName() { return "Student B"; }
            public String getCode() { return "S002"; }
            public String getEmail() { return "b@student.com"; }
            public String getImage() { return "b.jpg"; }
            public String getRole() { return null; }
        };

        List<ADProjectStaffStudentResponse> students = List.of(student1, student2);
        when(adProjectStudentRepository.getAllStudentByProject(mockProjectId)).thenReturn(students);

        ResponseObject<?> response = service.getAllStudentByProject(mockProjectId);

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals("Lấy dữ liệu sinh viên trong dự án  thành công", response.getMessage());
        assertEquals(students, response.getData());

        verify(adProjectStudentRepository, times(1)).getAllStudentByProject(mockProjectId);
    }


    @Test
    void testGetAllStaffByProject() {
        ADProjectStaffStudentResponse staff1 = new ADProjectStaffStudentResponse() {
            public String getId() { return "1"; }
            public String getName() { return "Nguyễn Văn C"; }
            public String getCode() { return "STF001"; }
            public String getEmail() { return "c@staff.com"; }
            public String getImage() { return "img1.jpg"; }
            public String getRole() { return "Supervisor"; }
        };

        ADProjectStaffStudentResponse staff2 = new ADProjectStaffStudentResponse() {
            public String getId() { return "2"; }
            public String getName() { return "Lê Thị D"; }
            public String getCode() { return "STF002"; }
            public String getEmail() { return "d@staff.com"; }
            public String getImage() { return "img2.jpg"; }
            public String getRole() { return "Mentor"; }
        };

        List<ADProjectStaffStudentResponse> staffList = List.of(staff1, staff2);
        when(adProjectStaffRepository.getAllStaffByProject(mockProjectId)).thenReturn(staffList);

        ResponseObject<?> response = service.getAllStaffByProject(mockProjectId);

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals("Lấy dữ liệu nhân viên theo dự án thành công", response.getMessage());
        assertEquals(staffList, response.getData());

        verify(adProjectStaffRepository, times(1)).getAllStaffByProject(mockProjectId);
    }

}
