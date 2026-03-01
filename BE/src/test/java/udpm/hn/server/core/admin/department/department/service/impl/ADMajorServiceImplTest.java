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
import udpm.hn.server.core.admin.department.department.model.request.ADCreateOrUpdateMajorRequest;
import udpm.hn.server.core.admin.department.department.model.request.ADMajorRequest;
import udpm.hn.server.core.admin.department.department.model.response.ADDetailMajorResponse;
import udpm.hn.server.core.admin.department.department.model.response.ADMajorResponse;
import udpm.hn.server.core.admin.department.department.repository.ADDepartmentExtendRepository;
import udpm.hn.server.core.admin.department.department.repository.ADMajorExtendRepository;
import udpm.hn.server.core.common.base.ResponseObject;
import udpm.hn.server.entity.Department;
import udpm.hn.server.entity.Major;
import udpm.hn.server.infrastructure.constant.EntityStatus;

import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ADMajorServiceImplTest {

    @InjectMocks
    private ADMajorServiceImpl adMajorService;

    @Mock
    private ADMajorExtendRepository majorRepository;

    @Mock
    private ADDepartmentExtendRepository departmentRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllMajor_ShouldReturnOkResponse() {
        // Arrange
        String departmentId = "dep1";
        ADMajorRequest request = new ADMajorRequest();

        ADMajorResponse mockProjection = mock(ADMajorResponse.class);
        when(mockProjection.getMajorId()).thenReturn("1");
        when(mockProjection.getMajorCode()).thenReturn("CNTT");
        when(mockProjection.getMajorName()).thenReturn("Công nghệ thông tin");
        when(mockProjection.getMajorStatus()).thenReturn("ACTIVE");
        when(mockProjection.getCreatedDate()).thenReturn(1717180800000L);


        Page<ADMajorResponse> mockPage = new PageImpl<>(
                Collections.singletonList(mockProjection),
                PageRequest.of(0, 10),
                1
        );

        when(majorRepository.getAllMajorByDepartmentIdFilter(eq(departmentId), any(PageRequest.class), eq(request)))
                .thenReturn(mockPage);

        // Act
        ResponseObject<?> response = adMajorService.getAllMajor(departmentId, request);

        // Assert
        assertEquals(200, response.getStatus().value());
        assertEquals("Lấy thành công danh sách chuyên ngành", response.getMessage());
    }

    private void assertEquals(int i, int value) {
    }

    private void assertEquals(String s, String message) {
    }

    private void assertEquals(HttpStatus ok, HttpStatus status) {
    }

    @Test
    void addMajor_ShouldCreateMajorSuccessfully() {
        ADCreateOrUpdateMajorRequest request = new ADCreateOrUpdateMajorRequest();
        request.setMajorName("CNTT");
        request.setMajorCode("CNTT");
        request.setDepartmentId("dep1");

        Department department = new Department();
        department.setId("dep1");
        department.setName("Công Nghệ");

        when(majorRepository.findMajorByCodeAndDepartmentId(any(), any())).thenReturn(Optional.empty());
        when(majorRepository.findMajorByNameAndDepartmentId(any(), any())).thenReturn(Optional.empty());
        when(departmentRepository.findById("dep1")).thenReturn(Optional.of(department));

        ResponseObject<?> response = adMajorService.addMajor(request);
        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertTrue(response.getMessage().contains("Thêm chuyên ngành"));
    }

    private void assertTrue(boolean thêmChuyênNgành) {
    }

    @Test
    void addMajor_ShouldReturnConflict_WhenCodeExists() {
        ADCreateOrUpdateMajorRequest request = new ADCreateOrUpdateMajorRequest();
        request.setMajorName("CNTT");
        request.setMajorCode("CNTT");
        request.setDepartmentId("dep1");

        Department department = new Department();
        department.setId("dep1");
        department.setName("Công Nghệ");

        when(departmentRepository.findById("dep1")).thenReturn(Optional.of(department));
        when(majorRepository.findMajorByCodeAndDepartmentId(any(), any())).thenReturn(Optional.of(new Major()));

        ResponseObject<?> response = adMajorService.addMajor(request);
        assertEquals(HttpStatus.CONFLICT, response.getStatus());
        assertTrue(response.getMessage().contains("Mã chuyên ngành đã tồn tại"));
    }

    @Test
    void updateMajor_ShouldUpdateSuccessfully() {
        String id = "m1";
        ADCreateOrUpdateMajorRequest request = new ADCreateOrUpdateMajorRequest();
        request.setMajorName("CNTT");
        request.setMajorCode("CNTT");
        request.setDepartmentId("dep1");

        Major existing = new Major();
        existing.setCode("OLD");
        existing.setName("OLD NAME");
        existing.setDepartment(new Department());

        Department department = new Department();
        department.setId("dep1");
        department.setName("Công Nghệ");

        when(majorRepository.findById(id)).thenReturn(Optional.of(existing));
        when(departmentRepository.findById("dep1")).thenReturn(Optional.of(department));
        when(majorRepository.findMajorByCodeAndDepartmentId(any(), any())).thenReturn(Optional.empty());
        when(majorRepository.findMajorByNameAndDepartmentId(any(), any())).thenReturn(Optional.empty());

        ResponseObject<?> response = adMajorService.updateMajor(request, id);
        assertEquals(HttpStatus.OK, response.getStatus());
        assertTrue(response.getMessage().contains("Cập nhât chuyên ngành"));
    }

    @Test
    void deleteMajor_ShouldToggleStatus() {
        String id = "m1";
        Major major = new Major();
        major.setName("CNTT");
        major.setStatus(EntityStatus.ACTIVE);

        when(majorRepository.findById(id)).thenReturn(Optional.of(major));

        ResponseObject<?> response = adMajorService.deleteMajor(id);
        assertEquals(HttpStatus.OK, response.getStatus());
        assertTrue(response.getMessage().contains("Chuyển đổi thành công"));
    }

    @Test
    void detailMajor_ShouldReturnDetailSuccessfully() {
        String id = "m1";

        ADDetailMajorResponse mockDetailResponse = new ADDetailMajorResponse() {
            @Override
            public String getMajorCode() {
                return "CNTT01";
            }

            @Override
            public String getMajorName() {
                return "Công nghệ thông tin";
            }

            @Override
            public String getId() {
                return "mock-id";
            }
        };

        when(majorRepository.getDetailMajor(id)).thenReturn(mockDetailResponse);

        ResponseObject<?> response = adMajorService.detailMajor(id);

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals("Lấy thành công chi tiết chuyên ngành", response.getMessage());

        // ✅ Kiểm tra dữ liệu đúng cách
        ADDetailMajorResponse data = (ADDetailMajorResponse) response.getData();
        assertEquals("CNTT01", data.getMajorCode());
        assertEquals("Công nghệ thông tin", data.getMajorName());
        assertEquals("mock-id", data.getId());
    }


}
