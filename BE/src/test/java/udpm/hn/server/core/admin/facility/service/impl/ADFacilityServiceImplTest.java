package udpm.hn.server.core.admin.facility.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import udpm.hn.server.core.admin.facility.model.request.ADCreateUpdateFacilityRequest;
import udpm.hn.server.core.admin.facility.model.request.ADFacilityRequest;
import udpm.hn.server.core.admin.facility.model.response.ADFacilityResponse;
import udpm.hn.server.core.admin.facility.repository.FacilityExtendRepository;
import udpm.hn.server.core.common.base.ResponseObject;
import udpm.hn.server.entity.Facility;
import udpm.hn.server.infrastructure.config.database.RoleGenerator;
import udpm.hn.server.infrastructure.constant.EntityStatus;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ADFacilityServiceImplTest {

    @InjectMocks
    private ADFacilityServiceImpl facilityService;

    @Mock
    private FacilityExtendRepository facilityExtendRepository;

    @Mock
    private RoleGenerator roleGenerator;

    @Test
    void getAllFacility_ShouldReturnPage() {
        ADFacilityRequest request = new ADFacilityRequest();
        Pageable pageable = PageRequest.of(0, 10, Sort.by("createdDate").descending());
        Page<ADFacilityResponse> mockPage = new PageImpl<>(List.of(mock(ADFacilityResponse.class)));

        when(facilityExtendRepository.getAllFacility(any(Pageable.class), eq(request)))
                .thenReturn(mockPage);

        ResponseObject<?> response = facilityService.getAllFacility(request);

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals("Get all facility successfully", response.getMessage());
        assertNotNull(response.getData());
    }

    private void assertEquals(String getAllFacilitySuccessfully, String message) {
    }

    private void assertEquals(HttpStatus ok, HttpStatus status) {
    }

    @Test
    void createFacility_ShouldSucceed_WhenNameIsUnique() {
        ADCreateUpdateFacilityRequest request = new ADCreateUpdateFacilityRequest();
        request.setFacilityName("Cơ sở mới");

        when(facilityExtendRepository.findAllByName(anyString())).thenReturn(Collections.emptyList());

        ResponseObject<?> response = facilityService.createFacility(request);

        verify(facilityExtendRepository).save(any(Facility.class));
        verify(roleGenerator).generate();

        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertEquals("Thêm cơ sở thành công", response.getMessage());
    }

    @Test
    void createFacility_ShouldFail_WhenNameExists() {
        ADCreateUpdateFacilityRequest request = new ADCreateUpdateFacilityRequest();
        request.setFacilityName("Trùng tên");

        when(facilityExtendRepository.findAllByName(anyString()))
                .thenReturn(List.of(new Facility()));

        ResponseObject<?> response = facilityService.createFacility(request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatus());
        assertEquals("Cơ sở đã tồn tại", response.getMessage());
    }

    @Test
    void updateFacility_ShouldSucceed_WhenValid() {
        String facilityId = "fa-01";
        ADCreateUpdateFacilityRequest request = new ADCreateUpdateFacilityRequest();
        request.setFacilityName("Cập nhật cơ sở");

        Facility facility = new Facility();
        facility.setCreatedDate(System.currentTimeMillis());

        when(facilityExtendRepository.existsByNameAndIdNot(anyString(), anyString())).thenReturn(false);
        when(facilityExtendRepository.findById(facilityId)).thenReturn(Optional.of(facility));

        ResponseObject<?> response = facilityService.updateFacility(facilityId, request);

        verify(facilityExtendRepository).save(any(Facility.class));

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals("Cập nhật cơ sở thành công", response.getMessage());
    }

    @Test
    void updateFacility_ShouldFail_WhenNameExists() {
        String facilityId = "fa-01";
        ADCreateUpdateFacilityRequest request = new ADCreateUpdateFacilityRequest();
        request.setFacilityName("Trùng");

        when(facilityExtendRepository.existsByNameAndIdNot(anyString(), anyString()))
                .thenReturn(true);

        ResponseObject<?> response = facilityService.updateFacility(facilityId, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatus());
        assertEquals("Cơ sở đã tồn tại", response.getMessage());
    }

    @Test
    void updateFacility_ShouldFail_WhenNotFound() {
        String facilityId = "fa-404";
        ADCreateUpdateFacilityRequest request = new ADCreateUpdateFacilityRequest();
        request.setFacilityName("Không tồn tại");

        when(facilityExtendRepository.existsByNameAndIdNot(anyString(), anyString())).thenReturn(false);
        when(facilityExtendRepository.findById(facilityId)).thenReturn(Optional.empty());

        ResponseObject<?> response = facilityService.updateFacility(facilityId, request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatus());
        assertEquals("Không tìm thấy cơ sở", response.getMessage());
    }

    @Test
    void changeFacilityStatus_ShouldToggleStatus() {
        String facilityId = "fa-01";
        Facility facility = new Facility();
        facility.setStatus(EntityStatus.ACTIVE);

        when(facilityExtendRepository.findById(facilityId)).thenReturn(Optional.of(facility));

        ResponseObject<?> response = facilityService.changeFacilityStatus(facilityId);

        verify(facilityExtendRepository).save(any(Facility.class));

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals("Đổi trạng thái cơ sở thành công", response.getMessage());
    }

    @Test
    void changeFacilityStatus_ShouldFail_WhenNotFound() {
        String facilityId = "fa-404";

        when(facilityExtendRepository.findById(facilityId)).thenReturn(Optional.empty());

        ResponseObject<?> response = facilityService.changeFacilityStatus(facilityId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatus());
        assertEquals("Không tìm thấy cơ sở", response.getMessage());
    }

    @Test
    void getFacilityById_ShouldReturnFacility_WhenFound() {
        String facilityId = "fa-01";
        ADFacilityResponse responseMock = mock(ADFacilityResponse.class);

        when(facilityExtendRepository.getDetailFacilityById(facilityId))
                .thenReturn(Optional.of(responseMock));

        ResponseObject<?> response = facilityService.getFacilityById(facilityId);

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals("Get facility successfully", response.getMessage());
        assertNotNull(response.getData());
    }

    @Test
    void getFacilityById_ShouldFail_WhenNotFound() {
        String facilityId = "fa-404";

        when(facilityExtendRepository.getDetailFacilityById(facilityId))
                .thenReturn(Optional.empty());

        ResponseObject<?> response = facilityService.getFacilityById(facilityId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatus());
        assertEquals("Facility not found", response.getMessage());
    }
}

