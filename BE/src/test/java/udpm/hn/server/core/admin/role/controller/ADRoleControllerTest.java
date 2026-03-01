package udpm.hn.server.core.admin.role.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.batch.BatchAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import udpm.hn.server.core.admin.role.model.request.ADRoleRequest;
import udpm.hn.server.core.admin.role.service.ADRoleService;
import udpm.hn.server.core.common.base.ResponseObject;

import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class ADRoleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ADRoleService roleService;

    private ResponseObject<?> mockResponse;

    @BeforeEach
    void setUp() {
        mockResponse = new ResponseObject<>();
        mockResponse.setStatus(HttpStatus.OK);
        mockResponse.setMessage("Success");
        mockResponse.setData(null); // hoặc có thể mock dữ liệu nếu muốn test logic sâu hơn
    }

    @Test
    void testGetAllRoles() throws Exception {
        when(roleService.getAllRole(any(ADRoleRequest.class))).thenReturn((ResponseObject) mockResponse);

        mockMvc.perform(get("/api/v1/admin/role"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Success"));
    }
}