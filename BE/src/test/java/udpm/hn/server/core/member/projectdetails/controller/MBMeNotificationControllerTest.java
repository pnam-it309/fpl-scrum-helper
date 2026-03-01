package udpm.hn.server.core.member.projectdetails.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import udpm.hn.server.core.common.base.ResponseObject;
import udpm.hn.server.core.member.projectdetails.model.request.MBMeCreateNotificationCommentRequest;
import udpm.hn.server.core.member.projectdetails.service.MBMeNotificationService;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@TestPropertySource("classpath:application.properties")

public class MBMeNotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MBMeNotificationService mbMeNotificationService;

    @Autowired
    private ObjectMapper objectMapper;

    private ResponseObject<Object> mockResponse;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockResponse = new ResponseObject<>();
        mockResponse.setStatus(HttpStatus.OK);
        mockResponse.setMessage("Created");
        mockResponse.setData(null);
    }

    @Test
    void testCreateNotificationTag() throws Exception {
        MBMeCreateNotificationCommentRequest request = new MBMeCreateNotificationCommentRequest();
        // You can set request fields here if needed, e.g. request.setTodoId("abc");

        when(mbMeNotificationService.createNotification(request)).thenReturn((ResponseObject) mockResponse);

        mockMvc.perform(post("/api/v1/project-details/notification")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void testSendNotification() {
        MBMeNotificationController controller = new MBMeNotificationController();
        ResponseObject<?> response = controller.sendNotification("mockId");

        assert response.getStatus() == HttpStatus.OK;
        assert response.getMessage().equals("gửi thành công");
    }


}

