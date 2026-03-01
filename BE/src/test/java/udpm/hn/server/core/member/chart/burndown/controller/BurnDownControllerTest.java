package udpm.hn.server.core.member.chart.burndown.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import udpm.hn.server.core.common.base.ResponseObject;
import udpm.hn.server.core.member.chart.burndown.service.BurndownService;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@TestPropertySource("classpath:application.properties")
public class BurnDownControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BurndownService burndownService;

    private ResponseObject<Object> response;

    @BeforeEach
    void setUp() {
        response = new ResponseObject<>();
        response.setStatus(HttpStatus.OK);
        response.setMessage("Success");
        response.setData(null);
    }

    @Test
    void testBurnDownChart() throws Exception {
        when(burndownService.burnDownChart(anyString(), anyString())).thenReturn((ResponseObject) response);

        mockMvc.perform(get("/api/v1/member/chart/burndown/123/456"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetBurndownChartByStoryPoint() throws Exception {
        when(burndownService.getBurndownDataStoryPoint(anyString(), anyString())).thenReturn((ResponseObject) response);

        mockMvc.perform(get("/api/v1/member/chart/storyPoint/789/101"))
                .andExpect(status().isOk());
    }
}

