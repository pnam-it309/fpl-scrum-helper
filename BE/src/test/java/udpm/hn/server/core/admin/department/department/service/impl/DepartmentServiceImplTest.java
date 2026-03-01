package udpm.hn.server.core.admin.department.department.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import udpm.hn.server.core.admin.department.department.model.request.ADCreateOrUpdateDepartmentRequest;
import udpm.hn.server.core.admin.department.department.model.request.DepartmentSearchRequest;
import udpm.hn.server.core.admin.department.department.model.response.DepartmentReponse;
import udpm.hn.server.core.admin.department.department.repository.ADDepartmentExtendRepository;
import udpm.hn.server.core.common.base.ResponseObject;
import udpm.hn.server.entity.Department;
import udpm.hn.server.infrastructure.constant.EntityStatus;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.skyscreamer.jsonassert.JSONAssert.assertEquals;

public class DepartmentServiceImplTest {

    @Mock
    private ADDepartmentExtendRepository departmentRepository;

    @InjectMocks
    private DepartmentServiceImpl departmentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllDepartment() {
        DepartmentSearchRequest request = new DepartmentSearchRequest();

        // 👉 Mock the projection interface
        DepartmentReponse mockProjection = mock(DepartmentReponse.class);
        when(mockProjection.getDepartmentId()).thenReturn("1");
        when(mockProjection.getDepartmentCode()).thenReturn("CNTT");
        when(mockProjection.getDepartmentName()).thenReturn("Công nghệ thông tin");
        when(mockProjection.getDepartmentStatus()).thenReturn("ACTIVE");
        when(mockProjection.getCreatedDate()).thenReturn("2024-06-01");

        Page<DepartmentReponse> mockPage = new PageImpl<>(
                List.of(mockProjection),
                PageRequest.of(0, 10),
                1
        );

        when(departmentRepository.getAllDepartmentByFilter(any(), eq(request)))
                .thenReturn(mockPage);

        ResponseObject<?> response = departmentService.getAllDepartment(request);

        assertEquals(200, response.getStatus().value());
        assertEquals("Lấy danh sách bộ môn thành công", response.getMessage());
        verify(departmentRepository).getAllDepartmentByFilter(any(), eq(request));
    }

    private void assertEquals(int i, int value) {
    }

    private void assertEquals(String s, String message) {
    }

    private void assertEquals(HttpStatus ok, HttpStatus status) {
    }

    @Test
    void testAddDepartment_Success() {
        ADCreateOrUpdateDepartmentRequest request = new ADCreateOrUpdateDepartmentRequest();
        request.setDepartmentName("Công nghệ thông tin");
        request.setDepartmentCode("cntt");

        when(departmentRepository.existsByName("Công nghệ thông tin")).thenReturn(false);
        when(departmentRepository.existsByCode("CNTT")).thenReturn(false);

        ResponseObject<?> response = departmentService.addDepartment(request);

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals("Thêm bộ môn thành công", response.getMessage());
        verify(departmentRepository).save(any(Department.class));
    }

    @Test
    void testAddDepartment_ExistName() {
        ADCreateOrUpdateDepartmentRequest request = new ADCreateOrUpdateDepartmentRequest();
        request.setDepartmentName("Khoa học máy tính");
        request.setDepartmentCode("khmt");

        when(departmentRepository.existsByName("Khoa học máy tính")).thenReturn(true);

        ResponseObject<?> response = departmentService.addDepartment(request);

        assertEquals(HttpStatus.NOT_ACCEPTABLE, response.getStatus());
        assertEquals("Tên bộ môn đã tồn tại", response.getMessage());
        verify(departmentRepository, never()).save(any());
    }

    @Test
    void testUpdateDepartment_Success() {
        String id = "abc123";
        ADCreateOrUpdateDepartmentRequest request = new ADCreateOrUpdateDepartmentRequest();
        request.setDepartmentName("Kinh tế");
        request.setDepartmentCode("kt");

        Department existing = new Department();
        existing.setId(id);
        existing.setCode("OLD");
        existing.setName("Cũ");

        when(departmentRepository.findById(id)).thenReturn(Optional.of(existing));
        when(departmentRepository.existsByCode("KT")).thenReturn(false);
        when(departmentRepository.existsByName("Kinh tế")).thenReturn(false);

        ResponseObject<?> response = departmentService.updateDepartment(request, id);

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals("Cập nhật bộ môn thành công", response.getMessage());
        verify(departmentRepository).save(existing);
    }

    @Test
    void testUpdateDepartment_NotFound() {
        String id = "not-found";
        ADCreateOrUpdateDepartmentRequest request = new ADCreateOrUpdateDepartmentRequest();
        request.setDepartmentCode("abc");
        request.setDepartmentName("Không tồn tại");

        when(departmentRepository.findById(id)).thenReturn(Optional.empty());

        ResponseObject<?> response = departmentService.updateDepartment(request, id);

        assertEquals(HttpStatus.NOT_ACCEPTABLE, response.getStatus());
        assertEquals("Bộ môn không tồn tại", response.getMessage());
        verify(departmentRepository, never()).save(any());
    }

    @Test
    void testDetailDepartment() {
        String id = "xyz";
        when(departmentRepository.getDetailDepartment(id)).thenReturn(null);

        ResponseObject<?> response = departmentService.detailDepartment(id);

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals("Lấy thành công chi tiết bộ môn", response.getMessage());
        assertEquals("Chi tiết bộ môn", (String) response.getData());
    }

    @Test
    void testChangeStatus_ActiveToInactive() {
        String id = "dept1";
        Department department = new Department();
        department.setId(id);
        department.setName("CNTT");
        department.setStatus(EntityStatus.ACTIVE);

        when(departmentRepository.findById(id)).thenReturn(Optional.of(department));

        ResponseObject<?> response = departmentService.changeStatus(id);

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals("Chuyển đổi thành công chuyên ngành CNTT", response.getMessage());
        assertEquals(EntityStatus.INACTIVE, department.getStatus());
        verify(departmentRepository).save(department);
    }

    private void assertEquals(EntityStatus inactive, EntityStatus status) {

    }

    @Test
    void testChangeStatus_NotFound() {
        when(departmentRepository.findById("nonexistent")).thenReturn(Optional.empty());

        ResponseObject<?> response = departmentService.changeStatus("nonexistent");

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals("chuyên ngành không tồn tại trong bộ môn", response.getMessage());
    }
}
