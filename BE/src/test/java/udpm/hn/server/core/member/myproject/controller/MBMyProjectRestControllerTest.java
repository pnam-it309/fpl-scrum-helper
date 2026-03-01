package udpm.hn.server.core.member.myproject.controller;

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
import udpm.hn.server.core.manage.project.model.request.MaProjectSearchRequest;
import udpm.hn.server.core.member.myproject.service.MBMyProjectService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@TestPropertySource("classpath:application.properties")

public class MBMyProjectRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MBMyProjectService myProjectService;

    private ResponseObject<Object> mockResponse;

    @BeforeEach
    void setUp() {
        mockResponse = new ResponseObject<>();
        mockResponse.setStatus(HttpStatus.OK);
        mockResponse.setMessage("Success");
        mockResponse.setData(null); // Mock dữ liệu trả về nếu cần
    }

    @Test
    void testGetAllMyProject_shouldReturnOk() throws Exception {
        // Giả lập service trả về dữ liệu
        when(myProjectService.getAllMyProject(any(MaProjectSearchRequest.class))).thenReturn((ResponseObject) mockResponse);

        mockMvc.perform(get("/api/v1/member/my-project"))
                .andExpect(status().isOk());
    }
}
