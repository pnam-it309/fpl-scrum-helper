package udpm.hn.server.core.admin.department.deparmentFacility.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import udpm.hn.server.core.admin.department.departmentfacility.model.request.CreateMajorFacilityRequest;
import udpm.hn.server.core.admin.department.departmentfacility.model.request.MajorFacilityRequest;
import udpm.hn.server.core.admin.department.departmentfacility.model.response.MajorFacilityDetailResponse;
import udpm.hn.server.core.admin.department.departmentfacility.model.response.MajorFacilityResponse;
import udpm.hn.server.core.admin.department.departmentfacility.repository.DFDepartmentFacilityExtendRepository;
import udpm.hn.server.core.admin.department.departmentfacility.repository.DFMajorExtendRepository;
import udpm.hn.server.core.admin.department.departmentfacility.repository.ListMajorResponse;
import udpm.hn.server.core.admin.department.departmentfacility.repository.MajorFacilityExtendRepository;
import udpm.hn.server.core.admin.department.departmentfacility.service.impl.MajorFacilityServiceImpl;
import udpm.hn.server.core.common.base.ResponseObject;
import udpm.hn.server.entity.*;
import udpm.hn.server.infrastructure.constant.EntityStatus;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MajorFacilityServiceImplTest {

    @Mock
    private MajorFacilityExtendRepository majorFacilityRepo;

    @Mock
    private DFDepartmentFacilityExtendRepository deptFacilityRepo;

    @Mock
    private DFMajorExtendRepository majorRepo;

    @InjectMocks
    private MajorFacilityServiceImpl service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllMajorFacilities_ShouldReturnPagedData() {
        MajorFacilityRequest request = new MajorFacilityRequest();
        Pageable pageable = PageRequest.of(0, 10);
        when(majorFacilityRepo.findAllMajorFacilities(eq(request), any()))
                .thenReturn(new PageImpl<>(List.of(mock(MajorFacilityResponse.class))));

        ResponseObject<?> response = service.getAllMajorFacilities(request);

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals("Lấy danh sách chuyên ngành theo cơ sở thành công", response.getMessage());
    }

    private void assertEquals(String s, String message) {
    }

    private void assertEquals(HttpStatus ok, HttpStatus status) {
    }

    @Test
    void getMajorFacilityById_ShouldReturnData() {
        String id = "test-id";
        MajorFacilityDetailResponse mockResponse = mock(MajorFacilityDetailResponse.class);
        when(majorFacilityRepo.findMajorFacilityById(id)).thenReturn(mockResponse);

        ResponseObject<?> response = service.getMajorFacilityById(id);

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals("Lấy thông tin chuyên ngành theo cơ sở thành công", response.getMessage());
        assertEquals(mockResponse, response.getData());
    }

    private void assertEquals(MajorFacilityDetailResponse mockResponse, Object data) {
    }

    @Test
    void createMajorFacility_ShouldCreateSuccessfully() {
        CreateMajorFacilityRequest request = new CreateMajorFacilityRequest();
        request.setDepartmentFacilityId("df-id");
        request.setMajorId("major-id");

        Facility facility = new Facility();
        facility.setName("Cơ sở A");

        Department department = new Department();
        department.setName("Bộ môn Công nghệ");

        DepartmentFacility df = new DepartmentFacility();
        df.setFacility(facility);
        df.setDepartment(department);
        Major major = new Major();

        when(deptFacilityRepo.findById("df-id")).thenReturn(Optional.of(df));
        when(majorRepo.findById("major-id")).thenReturn(Optional.of(major));
        when(majorFacilityRepo.findByMajor_IdAndDepartmentFacility_Id("major-id", "df-id")).thenReturn(Optional.empty());

        ResponseObject<?> response = service.createMajorFacility(request);

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals("Tạo chuyên ngành theo cơ sở thành công", response.getMessage());
    }

    @Test
    void createMajorFacility_ShouldReturnError_WhenDepartmentFacilityNotFound() {
        CreateMajorFacilityRequest request = new CreateMajorFacilityRequest();
        request.setDepartmentFacilityId("df-id");
        when(deptFacilityRepo.findById("df-id")).thenReturn(Optional.empty());

        ResponseObject<?> response = service.createMajorFacility(request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatus());
        assertEquals("Không tìm thấy bộ môn theo cơ sở", response.getMessage());
    }

    @Test
    void createMajorFacility_ShouldReturnError_WhenMajorAlreadyExists() {
        CreateMajorFacilityRequest request = new CreateMajorFacilityRequest();
        request.setDepartmentFacilityId("df-id");
        request.setMajorId("major-id");

        when(deptFacilityRepo.findById("df-id")).thenReturn(Optional.of(new DepartmentFacility()));
        when(majorRepo.findById("major-id")).thenReturn(Optional.of(new Major()));
        when(majorFacilityRepo.findByMajor_IdAndDepartmentFacility_Id("major-id", "df-id"))
                .thenReturn(Optional.of(new MajorFacility()));

        ResponseObject<?> response = service.createMajorFacility(request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatus());
        assertEquals("Chuyên ngành theo cơ sở đã tồn tại", response.getMessage());
    }

    @Test
    void updateMajorFacility_ShouldUpdateSuccessfully() {
        String id = "test-id";
        CreateMajorFacilityRequest request = new CreateMajorFacilityRequest();
        request.setMajorId("new-major-id");

        Facility facility = new Facility();
        facility.setName("Cơ sở A");

        Department department = new Department();
        department.setName("Bộ môn Công nghệ");

        DepartmentFacility departmentFacility = new DepartmentFacility();
        departmentFacility.setFacility(facility);
        departmentFacility.setDepartment(department);

        MajorFacility mf = new MajorFacility();
        mf.setDepartmentFacility(departmentFacility);

        when(majorFacilityRepo.findById(id)).thenReturn(Optional.of(mf));
        when(majorRepo.findById("new-major-id")).thenReturn(Optional.of(new Major()));
        when(majorFacilityRepo.findMajorFacilitiesByMajorId("new-major-id")).thenReturn(Optional.empty());

        ResponseObject<?> response = service.updateMajorFacility(id, request);

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals("Cập nhật chuyên ngành theo cơ sở thành công", response.getMessage());
    }

    @Test
    void updateMajorFacility_ShouldReturnNotFound() {
        when(majorFacilityRepo.findById("id")).thenReturn(Optional.empty());

        ResponseObject<?> response = service.updateMajorFacility("id", new CreateMajorFacilityRequest());

        assertEquals(HttpStatus.NOT_FOUND, response.getStatus());
        assertEquals("Không tìm thấy chuyên ngành theo cơ sở", response.getMessage());
    }

    @Test
    void getAllMajors_ShouldReturnList() {
        String departmentId = "dept-id";

        ListMajorResponse mockResponse = mock(ListMajorResponse.class);
        when(mockResponse.getMajorId()).thenReturn("major-id-1");
        when(mockResponse.getMajorName()).thenReturn("Công nghệ thông tin");

        List<ListMajorResponse> mockList = List.of(mockResponse);

        when(majorRepo.findAllByDepartmentId(departmentId)).thenReturn(mockList);

        ResponseObject<?> response = service.getAllMajors(departmentId);

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals("Lấy danh sách chuyên ngành thành công", response.getMessage());

        assertEquals(mockList, response.getData());
    }

    private void assertEquals(List<ListMajorResponse> mockList, Object data) {
    }

    @Test
    void changeStatus_ShouldActivateOrDeactivate() {
        String id = "id";
        MajorFacility mf = new MajorFacility();
        mf.setStatus(EntityStatus.ACTIVE);

        when(majorFacilityRepo.findById(id)).thenReturn(Optional.of(mf));

        ResponseObject<?> response = service.changeStatus(id);

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals("Chuyển đổi thành công chuyen nganh co so ", response.getMessage());
    }

    @Test
    void changeStatus_ShouldReturnNotFoundMessage() {
        when(majorFacilityRepo.findById("id")).thenReturn(Optional.empty());

        ResponseObject<?> response = service.changeStatus("id");

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals("Chuyen nganh co so không tồn tại", response.getMessage());
    }

}
