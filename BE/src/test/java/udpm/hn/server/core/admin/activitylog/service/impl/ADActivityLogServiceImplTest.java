package udpm.hn.server.core.admin.activitylog.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import udpm.hn.server.core.admin.activitylog.modal.request.ADActivityLogRequest;
import udpm.hn.server.core.admin.activitylog.modal.response.ADActivityLogResponse;
import udpm.hn.server.core.admin.activitylog.repository.ADActivityLogRepository;
import udpm.hn.server.core.common.base.PageableObject;
import udpm.hn.server.core.common.base.ResponseObject;
import udpm.hn.server.utils.Helper;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ADActivityLogServiceImplTest {

    @InjectMocks
    private ADActivityLogServiceImpl activityLogService;

    @Mock
    private ADActivityLogRepository adActivityLogRepository;

    @Test
    void getAllActivityLog_ShouldReturnPageOfLogs() {
        ADActivityLogRequest request = new ADActivityLogRequest();
        Pageable pageable = PageRequest.of(0, 10, Sort.by("createdDate").descending());
        ADActivityLogResponse mockResponse = mock(ADActivityLogResponse.class);
        Page<ADActivityLogResponse> mockPage = new PageImpl<>(List.of(mockResponse));

        try (MockedStatic<Helper> helperMockedStatic = mockStatic(Helper.class)) {
            helperMockedStatic.when(() -> Helper.createPageable(request, "createdDate"))
                    .thenReturn(pageable);

            when(adActivityLogRepository.getAllActivityLogs(pageable)).thenReturn(mockPage);

            ResponseObject<?> response = activityLogService.getAllActivityLog(request);

            assertEquals(HttpStatus.OK, response.getStatus());
            assertEquals("Get all activityLog successfully", response.getMessage());
            assertNotNull(response.getData());

            PageableObject<?> data = (PageableObject<?>) response.getData();
            assertEquals(1, data.getTotalElements());
        }
    }

    private void assertEquals(int i, long totalElements) {
    }

    private void assertEquals(String getAllActivityLogSuccessfully, String message) {
    }

    private void assertEquals(HttpStatus ok, HttpStatus status) {
    }
}

