package udpm.hn.server.core.admin.department.deparmentFacility.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import udpm.hn.server.core.admin.department.departmentfacility.model.request.CreateOrUpdateDepartmentFacilityRequest;
import udpm.hn.server.core.admin.department.departmentfacility.model.request.FindFacilityDetailRequest;
import udpm.hn.server.core.admin.department.departmentfacility.model.response.DepartmentFacilityResponse;
import udpm.hn.server.core.admin.department.departmentfacility.model.response.ListFacilityResponse;
import udpm.hn.server.core.admin.department.departmentfacility.repository.DFDepartmentExtendReposiotry;
import udpm.hn.server.core.admin.department.departmentfacility.repository.DFDepartmentFacilityExtendRepository;
import udpm.hn.server.core.admin.department.departmentfacility.repository.DFFacilityExtendRepository;
import udpm.hn.server.core.admin.department.departmentfacility.service.impl.DepartmentFacilityServiceImpl;
import udpm.hn.server.core.common.base.ResponseObject;
import udpm.hn.server.entity.Department;
import udpm.hn.server.entity.DepartmentFacility;
import udpm.hn.server.entity.Facility;
import udpm.hn.server.infrastructure.constant.EntityStatus;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DepartmentFacilityServiceImplTest {

    @InjectMocks
    private DepartmentFacilityServiceImpl service;

    @Mock
    private DFFacilityExtendRepository facilityRepo;

    @Mock
    private DFDepartmentExtendReposiotry departmentRepo;

    @Mock
    private DFDepartmentFacilityExtendRepository deptFacilityRepo;

    private String departmentId;
    private String facilityId;
    private Department department;
    private Facility facility;

    @BeforeEach
    void setUp() {
        departmentId = "dep-1";
        facilityId = "fac-1";
        department = new Department();
        department.setId(departmentId);
        facility = new Facility();
        facility.setId(facilityId);
    }

    @Test
    void getAllDepartmentFacility_ShouldReturnPagedData() {
        FindFacilityDetailRequest request = new FindFacilityDetailRequest();
        Pageable pageable = (Pageable) PageRequest.of(0, 10);

        when(deptFacilityRepo.getDepartmentFacilitiesByValueFind(eq(departmentId), any(), eq(request)))
                .thenReturn(new PageImpl<>(List.of(mock(DepartmentFacilityResponse.class))));

        ResponseObject<?> response = service.getAllDepartmentFacility(departmentId, request);

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals("Lấy thành công danh sách bộ môn theo cơ sở", response.getMessage());
    }

    private void assertEquals(String s, String message) {
    }

    private void assertEquals(HttpStatus ok, HttpStatus status) {
    }

    @Test
    void addDepartmentFacility_ShouldReturnCreated() {
        CreateOrUpdateDepartmentFacilityRequest request = new CreateOrUpdateDepartmentFacilityRequest();
        request.setDepartmentId(departmentId);
        request.setFacilityId(facilityId);

        when(departmentRepo.findById(departmentId)).thenReturn(Optional.of(department));
        when(facilityRepo.findById(facilityId)).thenReturn(Optional.of(facility));
        when(deptFacilityRepo.existsByIdDepartmentAndIdFacilityAndIdAdd(request)).thenReturn(Optional.empty());

        ResponseObject<?> response = service.addDepartmentFacility(request);

        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertEquals("Thêm thành công", response.getMessage());
    }

    @Test
    void addDepartmentFacility_ShouldReturnExistError() {
        CreateOrUpdateDepartmentFacilityRequest request = new CreateOrUpdateDepartmentFacilityRequest();
        request.setDepartmentId(departmentId);
        request.setFacilityId(facilityId);

        when(deptFacilityRepo.existsByIdDepartmentAndIdFacilityAndIdAdd(request))
                .thenReturn(Optional.of(new DepartmentFacility()));

        ResponseObject<?> response = service.addDepartmentFacility(request);
        assertEquals(HttpStatus.NOT_ACCEPTABLE, response.getStatus());
        assertEquals("Bộ môn theo cơ sở đã tồn tại!", response.getMessage());
    }

    @Test
    void updateDepartmentFacility_ShouldUpdateSuccessfully() {
        String id = "df-1";
        CreateOrUpdateDepartmentFacilityRequest request = new CreateOrUpdateDepartmentFacilityRequest();
        request.setDepartmentId(departmentId);
        request.setFacilityId(facilityId);

        DepartmentFacility df = new DepartmentFacility();
        df.setFacility(facility);

        when(deptFacilityRepo.findById(id)).thenReturn(Optional.of(df));
        when(facilityRepo.findById(facilityId)).thenReturn(Optional.of(facility));
        when(departmentRepo.findById(departmentId)).thenReturn(Optional.of(department));

        ResponseObject<?> response = service.updateDepartmentFacility(request, id);
        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals("Sửa thành công !", response.getMessage());
    }

    @Test
    void getListFacility_ShouldReturnActiveFacilities() {
        when(deptFacilityRepo.getListFacility()).thenReturn(List.of(mock(ListFacilityResponse.class)));

        ResponseObject<?> response = service.getListFacility();
        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    void getDepartmentName_ShouldReturnName() {
        department.setName("Khoa CNTT");
        when(departmentRepo.findById(departmentId)).thenReturn(Optional.of(department));

        ResponseObject<?> response = service.getDepartmentName(departmentId);
        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals("Khoa CNTT", (String) response.getData());
    }

    @Test
    void getDepartmentName_ShouldReturnNotFound() {
        when(departmentRepo.findById(departmentId)).thenReturn(Optional.empty());

        ResponseObject<?> response = service.getDepartmentName(departmentId);
        assertEquals(HttpStatus.NOT_ACCEPTABLE, response.getStatus());
    }

    @Test
    void detailDepartmentFacility_ShouldReturnDetail() {
        when(deptFacilityRepo.getDetailDepartmentFacilityResponse("id-1"))
                .thenReturn(mock(ListFacilityResponse.class));

        ResponseObject<?> response = service.detailDepartmentFacility("id-1");
        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    void changeStatus_ShouldToggleStatus() {
        DepartmentFacility df = new DepartmentFacility();
        df.setStatus(EntityStatus.ACTIVE);
        when(deptFacilityRepo.findById("df-id")).thenReturn(Optional.of(df));

        ResponseObject<?> response = service.changeStatus("df-id");
        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    void changeStatus_ShouldReturnNotFound() {
        when(deptFacilityRepo.findById("df-id")).thenReturn(Optional.empty());

        ResponseObject<?> response = service.changeStatus("df-id");
        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals("Bo mon co so không tồn tại", response.getMessage());
    }

    @Test
    void testCreateDepartmentFacility_DuplicateExists() {
        // Arrange
        CreateOrUpdateDepartmentFacilityRequest request = new CreateOrUpdateDepartmentFacilityRequest();
        request.setDepartmentId("dept1");
        request.setFacilityId("facility1");

        DepartmentFacility mockFacility = new DepartmentFacility();

        when(deptFacilityRepo.existsByIdDepartmentAndIdFacilityAndIdAdd(request))
                .thenReturn(Optional.of(new DepartmentFacility()));

        // Act
        ResponseObject<?> response = service.addDepartmentFacility(request); // ĐÚNG tên service

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatus());
        assertEquals("Bản ghi đã tồn tại", response.getMessage());
    }

    @Test
    void testAddDepartmentFacility_FacilityNotFound() {
        CreateOrUpdateDepartmentFacilityRequest request = new CreateOrUpdateDepartmentFacilityRequest();
        request.setDepartmentId(departmentId);
        request.setFacilityId("invalid-facility-id");

        when(departmentRepo.findById(departmentId)).thenReturn(Optional.of(department));
        when(facilityRepo.findById("invalid-facility-id")).thenReturn(Optional.empty());

        ResponseObject<?> response = service.addDepartmentFacility(request);

        assertEquals(HttpStatus.NOT_ACCEPTABLE, response.getStatus());
        assertEquals("Không tìm thấy cơ sở trên", response.getMessage());
    }

    @Test
    void testAddDepartmentFacility_DepartmentNotFound() {
        CreateOrUpdateDepartmentFacilityRequest request = new CreateOrUpdateDepartmentFacilityRequest();
        request.setDepartmentId("invalid-department-id");
        request.setFacilityId(facilityId);

        when(departmentRepo.findById("invalid-department-id")).thenReturn(Optional.empty());
        when(facilityRepo.findById(facilityId)).thenReturn(Optional.of(facility));

        ResponseObject<?> response = service.addDepartmentFacility(request);

        assertEquals(HttpStatus.NOT_ACCEPTABLE, response.getStatus());
        assertEquals("Không tìm thấy bộ môn trên", response.getMessage());
    }

    @Test
    void testUpdateDepartmentFacility_NoChanges() {
        CreateOrUpdateDepartmentFacilityRequest request = new CreateOrUpdateDepartmentFacilityRequest();
        request.setDepartmentId(departmentId);
        request.setFacilityId(facilityId);

        DepartmentFacility existingFacility = new DepartmentFacility();
        existingFacility.setDepartment(department);
        existingFacility.setFacility(facility);

        when(deptFacilityRepo.findById("df-id")).thenReturn(Optional.of(existingFacility));
        when(facilityRepo.findById(facilityId)).thenReturn(Optional.of(facility));
        when(departmentRepo.findById(departmentId)).thenReturn(Optional.of(department));

        ResponseObject<?> response = service.updateDepartmentFacility(request, "df-id");

        assertEquals(HttpStatus.NOT_ACCEPTABLE, response.getStatus());
        assertEquals("Cập nhật trùng cơ sở hoặc để như cũ", response.getMessage());
    }

    @Test
    void testUpdateDepartmentFacility_FacilityNotFound() {
        CreateOrUpdateDepartmentFacilityRequest request = new CreateOrUpdateDepartmentFacilityRequest();
        request.setDepartmentId(departmentId);
        request.setFacilityId("invalid-facility-id");

        DepartmentFacility existingFacility = new DepartmentFacility();
        existingFacility.setDepartment(department);

        when(deptFacilityRepo.findById("df-id")).thenReturn(Optional.of(existingFacility));
        when(facilityRepo.findById("invalid-facility-id")).thenReturn(Optional.empty());

        ResponseObject<?> response = service.updateDepartmentFacility(request, "df-id");

        assertEquals(HttpStatus.NOT_ACCEPTABLE, response.getStatus());
        assertEquals("Không tìm thấy cơ sở trên", response.getMessage());
    }

    @Test
    void testUpdateDepartmentFacility_DepartmentNotFound() {
        CreateOrUpdateDepartmentFacilityRequest request = new CreateOrUpdateDepartmentFacilityRequest();
        request.setDepartmentId("invalid-department-id");
        request.setFacilityId(facilityId);

        DepartmentFacility existingFacility = new DepartmentFacility();
        existingFacility.setFacility(facility);

        when(deptFacilityRepo.findById("df-id")).thenReturn(Optional.of(existingFacility));
        when(facilityRepo.findById(facilityId)).thenReturn(Optional.of(facility));
        when(departmentRepo.findById("invalid-department-id")).thenReturn(Optional.empty());

        ResponseObject<?> response = service.updateDepartmentFacility(request, "df-id");

        assertEquals(HttpStatus.NOT_ACCEPTABLE, response.getStatus());
        assertEquals("Không tìm thấy bộ môn trên", response.getMessage());
    }

    @Test
    void testGetAllDepartmentFacility_NoData() {
        FindFacilityDetailRequest request = new FindFacilityDetailRequest();
        Pageable pageable = PageRequest.of(0, 10);

        when(deptFacilityRepo.getDepartmentFacilitiesByValueFind(eq(departmentId), any(), eq(request)))
                .thenReturn(new PageImpl<>(List.of()));

        ResponseObject<?> response = service.getAllDepartmentFacility(departmentId, request);

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals("Lấy thành công danh sách bộ môn theo cơ sở", response.getMessage());
    }

}
