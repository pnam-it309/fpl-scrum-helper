package udpm.hn.server.core.admin.activitylog.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.batch.BatchAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import udpm.hn.server.core.admin.activitylog.modal.request.ADActivityLogRequest;
import udpm.hn.server.core.admin.activitylog.service.ADActivityLogService;
import udpm.hn.server.core.common.base.ResponseObject;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:mysql://localhost:3306/scrum",
        "spring.datasource.username=root",
        "spring.datasource.password=123456",
        "spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver"
})
public class ADActivityLogControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ADActivityLogService adActivityLogService;

    @Autowired
    private ObjectMapper objectMapper;

    private ResponseObject<?> mockResponse;

    @BeforeEach
    void setUp() {
        mockResponse = new ResponseObject<>();
        mockResponse.setStatus(HttpStatus.OK);
        mockResponse.setMessage("Success");
        mockResponse.setData(null);
    }

    @Test
    void testGetAllActivityLog() throws Exception {
        when(adActivityLogService.getAllActivityLog(any(ADActivityLogRequest.class)))
                .thenReturn((ResponseObject) mockResponse);

        mockMvc.perform(get("/api/v1/admin/activity-log"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Success"));
    }

}
