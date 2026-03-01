package udpm.hn.server.core.member.chart.burndown.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import udpm.hn.server.core.common.base.ResponseObject;
import udpm.hn.server.core.member.chart.burndown.service.VelocityChartService;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@TestPropertySource("classpath:application.properties")

public class VelocityChartControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private VelocityChartService velocityChartService;

    private ResponseObject<Object> mockResponse;

    @BeforeEach
    void setUp() {
        mockResponse = new ResponseObject<>();
        mockResponse.setStatus(HttpStatus.OK);
        mockResponse.setMessage("Success");
        mockResponse.setData(null);  // Hoặc bất kỳ object mock nào tùy vào test case
    }

    @Test
    void testGetVelocityChart() throws Exception {
        when(velocityChartService.getVelocityChart(anyString())).thenReturn((ResponseObject) mockResponse);

        mockMvc.perform(get("/api/v1/member/chart/velocity/123"))
                .andExpect(status().isOk());
    }
}
