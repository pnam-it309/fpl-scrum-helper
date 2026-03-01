package udpm.hn.server.core.member.capacityestimate.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import udpm.hn.server.core.common.base.ResponseObject;
import udpm.hn.server.core.member.capacityestimate.model.request.CapacityEstimatePageRequest;
import udpm.hn.server.core.member.capacityestimate.model.request.CapacityEstimateRequest;
import udpm.hn.server.core.member.capacityestimate.model.response.capacityEstimateResponse;
import udpm.hn.server.entity.*;
import udpm.hn.server.infrastructure.constant.EntityStatus;
import udpm.hn.server.repository.*;
import udpm.hn.server.utils.UserContextHelper;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;


public class CapacityEstimateServiceImplTest {

    @InjectMocks
    private CapacityEstimateServiceImpl service;

    @Mock
    private PhaseProjectRepository phaseProjectRepository;

    @Mock
    private StaffProjectRepository staffProjectRepository;

    @Mock
    private StaffRepository staffRepository;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private CapacityEstimateRepository capacityEstimateRepository;

    @Mock
    private UserContextHelper userContextHelper;

    @Mock
    private VelocityRecordRepository velocityRecordRepository;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetAll_AsStaff_ReturnsData() {
        CapacityEstimatePageRequest request = new CapacityEstimatePageRequest();
        request.setIdUser("staff123");
        request.setIdProject("project123");

        Staff staff = new Staff();
        staff.setId("staff123");

        Project project = new Project();
        project.setId("project123");

        StaffProject staffProject = new StaffProject();
        staffProject.setId("sp123");

        Page<capacityEstimateResponse> mockPage = new PageImpl<>(Collections.emptyList());

        when(staffRepository.findById("staff123")).thenReturn(Optional.of(staff));
        when(projectRepository.findById("project123")).thenReturn(Optional.of(project));
        when(staffProjectRepository.findByStaffAndProject(staff, project)).thenReturn(Optional.of(staffProject));
        when(capacityEstimateRepository.getAll(any(Pageable.class), eq("project123"), eq("sp123"))).thenReturn(mockPage);

        ResponseObject<?> response = service.getAll(request);

        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    public void testCreateCapacityEstimate_AlreadyEstimated_ReturnsError() {
        CapacityEstimateRequest request = new CapacityEstimateRequest();
        request.setIdSprint("phase1");
        request.setIdProject("proj1");
        request.setIdUser("user1");

        PhaseProject phase = new PhaseProject();
        phase.setId("phase1");

        Project project = new Project();
        project.setId("proj1");

        Student student = new Student();
        StaffProject sp = new StaffProject();

        CapacityEstimate ce = new CapacityEstimate();
        ce.setPhaseProject(phase);
        ce.setStaffProject(sp);

        when(phaseProjectRepository.findById("phase1")).thenReturn(Optional.of(phase));
        when(projectRepository.findById("proj1")).thenReturn(Optional.of(project));
        when(staffRepository.findById("user1")).thenReturn(Optional.empty());
        when(studentRepository.findById("user1")).thenReturn(Optional.of(student));
        when(staffProjectRepository.findByStudentAndProject(student, project)).thenReturn(Optional.of(sp));
        when(capacityEstimateRepository.findAll()).thenReturn(List.of(ce));

        ResponseObject<?> response = service.createCapacityEstimate(request);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatus());
    }

    @Test
    public void testDetailCapacityEstimate_ReturnsEstimate() {
        String id = "ce1";
        capacityEstimateResponse mockResponse = new capacityEstimateResponse() {
            @Override
            public String getOrderNumber() {
                return null;
            }

            @Override
            public String getId() {
                return null;
            }

            @Override
            public String getName() {
                return null;
            }

            @Override
            public String getWorkday() {
                return null;
            }

            @Override
            public String getSprint() {
                return null;
            }

            @Override
            public String getIdPhase() {
                return null;
            }

            @Override
            public String getIdUser() {
                return null;
            }

            @Override
            public String getStartTime() {
                return null;
            }

            @Override
            public String getDescription() {
                return null;
            }

            @Override
            public String getDescriptions() {
                return null;
            }

            @Override
            public String getEndTime() {
                return null;
            }

            @Override
            public String getAvailableHours() {
                return null;
            }

            @Override
            public String getAdjustedStoryPoints() {
                return null;
            }
        };

        when(capacityEstimateRepository.detailCapacityEstimate(id)).thenReturn(Optional.of(mockResponse));

        ResponseObject<?> response = service.detailCapacityEstimate(id);
        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    public void testUpdateCapacityEstimate_NotFound() {
        String id = "ce1";
        CapacityEstimateRequest request = new CapacityEstimateRequest();

        when(capacityEstimateRepository.findById(id)).thenReturn(Optional.empty());

        ResponseObject<?> response = service.updateCapacityEstimate(id, request);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatus());
    }

    @Test
    public void testDeleteCapacityEstimate_Success() {
        String id = "ce1";
        doNothing().when(capacityEstimateRepository).deleteById(id);

        ResponseObject<?> response = service.deleteCapacityEstimate(id);
        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    public void testGetAll_AsStudent_ReturnsData() {
        CapacityEstimatePageRequest request = new CapacityEstimatePageRequest();
        request.setIdUser("student123");
        request.setIdProject("project123");

        Student student = new Student();
        student.setId("student123");

        Project project = new Project();
        project.setId("project123");

        StaffProject staffProject = new StaffProject();
        staffProject.setId("sp123");

        Page<capacityEstimateResponse> mockPage = new PageImpl<>(Collections.emptyList());

        when(staffRepository.findById("student123")).thenReturn(Optional.empty());
        when(studentRepository.findById("student123")).thenReturn(Optional.of(student));
        when(projectRepository.findById("project123")).thenReturn(Optional.of(project));
        when(staffProjectRepository.findByStudentAndProject(student, project)).thenReturn(Optional.of(staffProject));
        when(capacityEstimateRepository.getAll(any(Pageable.class), eq("project123"), eq("sp123"))).thenReturn(mockPage);

        ResponseObject<?> response = service.getAll(request);

        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    public void testCreateCapacityEstimate_Success() {
        CapacityEstimateRequest request = new CapacityEstimateRequest();
        request.setIdSprint("phase1");
        request.setIdProject("proj1");
        request.setIdUser("user1");

        PhaseProject phase = new PhaseProject();
        phase.setId("phase1");

        Project project = new Project();
        project.setId("proj1");

        Student student = new Student();
        StaffProject sp = new StaffProject();

        // Danh sách không chứa capacityEstimate nào cùng phase & staffProject
        when(phaseProjectRepository.findById("phase1")).thenReturn(Optional.of(phase));
        when(projectRepository.findById("proj1")).thenReturn(Optional.of(project));
        when(staffRepository.findById("user1")).thenReturn(Optional.empty());
        when(studentRepository.findById("user1")).thenReturn(Optional.of(student));
        when(staffProjectRepository.findByStudentAndProject(student, project)).thenReturn(Optional.of(sp));
        when(capacityEstimateRepository.findAll()).thenReturn(Collections.emptyList());
        when(capacityEstimateRepository.save(any(CapacityEstimate.class))).thenReturn(new CapacityEstimate());

        ResponseObject<?> response = service.createCapacityEstimate(request);
        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    public void testUpdateCapacityEstimate_Success() {
        String id = "ce1";
        CapacityEstimateRequest request = new CapacityEstimateRequest();
        request.setIdSprint("phase1");
        request.setDescriptions("desc");
        request.setWorkday((short)5);
//        request.setAdjustedStoryPoints(10);

        CapacityEstimate ce = new CapacityEstimate();
        ce.setId(id);

        PhaseProject phase = new PhaseProject();
        phase.setId("phase1");

        when(capacityEstimateRepository.findById(id)).thenReturn(Optional.of(ce));
        when(phaseProjectRepository.findById("phase1")).thenReturn(Optional.of(phase));
        when(capacityEstimateRepository.save(any(CapacityEstimate.class))).thenReturn(ce);

        ResponseObject<?> response = service.updateCapacityEstimate(id, request);
        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    public void testGetAllSprint_AsStaff() {
        String idProject = "proj1";
        Staff staff = new Staff();
        staff.setId("staff1");
        Project project = new Project();
        project.setId("proj1");
        StaffProject sp = new StaffProject();
        sp.setId("sp1");

        when(userContextHelper.getCurrentUserId()).thenReturn("staff1");
        when(staffRepository.findById("staff1")).thenReturn(Optional.of(staff));
        when(projectRepository.findById("proj1")).thenReturn(Optional.of(project));
        when(staffProjectRepository.findByStaffAndProject(staff, project)).thenReturn(Optional.of(sp));
        when(phaseProjectRepository.findAll()).thenReturn(Collections.emptyList());
        when(capacityEstimateRepository.findAll()).thenReturn(Collections.emptyList());

        ResponseObject<?> response = service.getAllSprint(idProject);
        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    public void testGetAllSprint_AsStudent() {
        String idProject = "proj1";
        Student student = new Student();
        student.setId("student1");
        Project project = new Project();
        project.setId("proj1");
        StaffProject sp = new StaffProject();
        sp.setId("sp1");

        when(userContextHelper.getCurrentUserId()).thenReturn("student1");
        when(staffRepository.findById("student1")).thenReturn(Optional.empty());
        when(studentRepository.findById("student1")).thenReturn(Optional.of(student));
        when(projectRepository.findById("proj1")).thenReturn(Optional.of(project));
        when(staffProjectRepository.findByStudentAndProject(student, project)).thenReturn(Optional.of(sp));
        when(phaseProjectRepository.findAll()).thenReturn(Collections.emptyList());
        when(capacityEstimateRepository.findAll()).thenReturn(Collections.emptyList());

        ResponseObject<?> response = service.getAllSprint(idProject);
        assertEquals(HttpStatus.OK, response.getStatus());
    }
    @Test
    public void testCapacityEstimateBySprint_Success() {
        String id = "sprint1";
        capacityEstimateResponse res1 = mock(capacityEstimateResponse.class);
        when(res1.getIdPhase()).thenReturn("phase1");

        when(capacityEstimateRepository.capacityEstimateBySprint(id)).thenReturn(List.of(res1));

        ResponseObject<?> response = service.CapacityEstimateBySprint(id);
        assertEquals(HttpStatus.OK, response.getStatus());
    }
    @Test
    public void testVelocityTB_ActualWorkingDayZero() {
        String idProject = "proj1";
        when(velocityRecordRepository.avgActualPoint(idProject)).thenReturn(10f);
        when(velocityRecordRepository.avgActualWorkingDay(idProject)).thenReturn(0f);
        when(phaseProjectRepository.findAll()).thenReturn(Collections.emptyList());

        ResponseObject<?> response = service.velocityTB(idProject);
        assertEquals(HttpStatus.OK, response.getStatus());
    }

}
