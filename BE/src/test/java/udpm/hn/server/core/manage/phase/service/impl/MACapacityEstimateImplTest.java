package udpm.hn.server.core.manage.phase.service.impl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import udpm.hn.server.core.common.base.ResponseObject;
import udpm.hn.server.core.manage.capacityestimate.model.request.MaEstimatePageRequest;
import udpm.hn.server.core.manage.capacityestimate.repository.MACapacityEstimateRepository;
import udpm.hn.server.core.member.capacityestimate.model.response.capacityEstimateResponse;
import udpm.hn.server.core.manage.capacityestimate.service.impl.MACapacityEstimateImpl;
import udpm.hn.server.utils.Helper;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MACapacityEstimateImplTest {

    @Mock
    private MACapacityEstimateRepository maCapacityEstimateRepository;

    @InjectMocks
    private MACapacityEstimateImpl maCapacityEstimateImpl;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllCapacityEstimate_success() {
        MaEstimatePageRequest mockRequest = mock(MaEstimatePageRequest.class);
        Pageable mockPageable = mock(Pageable.class);

        // Tạo fakeResult kiểu đúng
        Page<capacityEstimateResponse> fakeResult = new PageImpl<>(Collections.emptyList());

        when(mockRequest.getIdProject()).thenReturn("project123");

        try (MockedStatic<Helper> helperMockedStatic = mockStatic(Helper.class)) {
            helperMockedStatic.when(() -> Helper.createPageable(mockRequest)).thenReturn(mockPageable);

            when(maCapacityEstimateRepository.getAllEstimate(mockPageable, "project123"))
                    .thenReturn(fakeResult);

            ResponseObject<?> response = maCapacityEstimateImpl.getAllCapacityEstimate(mockRequest);

            assertEquals(HttpStatus.OK, response.getStatus());
            assertEquals(fakeResult, response.getData());
            assertEquals("Lấy dữ liệu thành công", response.getMessage());
        }
    }
}
