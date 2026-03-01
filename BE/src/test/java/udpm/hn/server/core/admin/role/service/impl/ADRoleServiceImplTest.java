package udpm.hn.server.core.admin.role.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import udpm.hn.server.core.admin.role.model.request.ADRoleRequest;
import udpm.hn.server.core.admin.role.model.response.ADRoleResponse;
import udpm.hn.server.core.admin.role.repository.ADRoleRepository;
import udpm.hn.server.core.common.base.ResponseObject;
import udpm.hn.server.utils.Helper;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ADRoleServiceImplTest {

    @InjectMocks
    private ADRoleServiceImpl roleService;

    @Mock
    private ADRoleRepository roleRepository;

    @Test
    void getAllRole_ShouldReturnRolesPage() {
        // Arrange
        ADRoleRequest request = new ADRoleRequest();
        request.setQ("admin");
        request.setDepartment("IT");

        Pageable pageable = PageRequest.of(0, 10, Sort.by("createdDate").descending());

        ADRoleResponse mockResponse = mock(ADRoleResponse.class);
        Page<ADRoleResponse> mockPage = new PageImpl<>(List.of(mockResponse));

        try (MockedStatic<Helper> helperMockedStatic = mockStatic(Helper.class)) {
            helperMockedStatic.when(() -> Helper.createPageable(request, "created_date"))
                    .thenReturn(pageable);


            when(roleRepository.getAllRole(pageable, "admin", "IT")).thenReturn(mockPage);

            // Act
            ResponseObject<?> response = roleService.getAllRole(request);

            // Assert
            assertEquals(HttpStatus.OK, response.getStatus());
            assertEquals("Get all roles successfully", response.getMessage());
            assertNotNull(response.getData());

            Page<ADRoleResponse> result = (Page<ADRoleResponse>) response.getData();
            assertEquals(1, result.getTotalElements());
            verify(roleRepository, times(1)).getAllRole(pageable, "admin", "IT");
        }
    }
}
