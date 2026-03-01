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
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import udpm.hn.server.core.common.base.ResponseObject;
import udpm.hn.server.core.member.projectdetails.model.request.MBMeFindNotificationMemberRequest;
import udpm.hn.server.core.member.projectdetails.model.request.MBMessageNotificationMemberRequest;
import udpm.hn.server.core.member.projectdetails.service.MBMeMemberNotificationService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class MBMeNotificationMemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MBMeMemberNotificationService mbMeMemberNotificationService;

    @Autowired
    private ObjectMapper objectMapper;

    private ResponseObject<Object> mockResponse;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockResponse = new ResponseObject<>();
        mockResponse.setStatus(HttpStatus.OK);
        mockResponse.setMessage("Success");
        mockResponse.setData(null);
    }

    @Test
    void testGetNotificationMember() throws Exception {
        MBMeFindNotificationMemberRequest request = new MBMeFindNotificationMemberRequest();
        when(mbMeMemberNotificationService.getAllNotificationMember(request)).thenReturn((ResponseObject) mockResponse);

        mockMvc.perform(get("/api/v1/project-details/notification-member")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void testCountNotificationMember() throws Exception {
        String memberId = "123";
        when(mbMeMemberNotificationService.countNotificationMember(memberId)).thenReturn((ResponseObject) mockResponse);

        mockMvc.perform(get("/api/v1/project-details/notification-member/count")
                        .param("memberId", memberId))
                .andExpect(status().isOk());
    }

    @Test
    void testUpdateStatus() throws Exception {
        String idNotificationMember = "noti-001";
        when(mbMeMemberNotificationService.updateStatus(idNotificationMember)).thenReturn((ResponseObject) mockResponse);

        mockMvc.perform(put("/api/v1/project-details/notification-member/update-status")
                        .param("idNotificationMember", idNotificationMember))
                .andExpect(status().isOk());
    }

    @Test
    void testUpdateAllStatus() throws Exception {
        String memberId = "123";
        when(mbMeMemberNotificationService.updateAllStatus(memberId)).thenReturn((ResponseObject) mockResponse);

        mockMvc.perform(put("/api/v1/project-details/notification-member/update-all-status")
                        .param("memberId", memberId))
                .andExpect(status().isOk());
    }

    @Test
    void testSendNotificationMember() {
        MBMeNotificationMemberController controller = new MBMeNotificationMemberController(mbMeMemberNotificationService);
        MBMessageNotificationMemberRequest request = new MBMessageNotificationMemberRequest();
        ResponseObject<?> response = controller.sendNotificationMember(request);

        assert response.getStatus() == HttpStatus.OK;
        assert response.getMessage().equals("gửi thành công");
    }


    @Test
    void testGetNotificationMember_ServiceReturnNull() throws Exception {
        when(mbMeMemberNotificationService.getAllNotificationMember(any(MBMeFindNotificationMemberRequest.class)))
                .thenReturn(null);

        mockMvc.perform(get("/api/v1/project-details/notification-member"))
                .andExpect(status().isOk()); // hoặc isNoContent() nếu helper trả về như vậy
    }

    @Test
    void testCountNotificationMember_ServiceReturnNull() throws Exception {
        when(mbMeMemberNotificationService.countNotificationMember(anyString()))
                .thenReturn(null);

        mockMvc.perform(get("/api/v1/project-details/notification-member/count")
                        .param("memberId", "123"))
                .andExpect(status().isOk()); // hoặc .isNoContent()
    }

    @Test
    void testUpdateStatus_ServiceThrowException() throws Exception {
        when(mbMeMemberNotificationService.updateStatus(anyString()))
                .thenThrow(new RuntimeException("Internal error"));

        mockMvc.perform(put("/api/v1/project-details/notification-member/update-status")
                        .param("idNotificationMember", "noti-001"))
                .andExpect(status().is5xxServerError());
    }

    @Test
    void testUpdateAllStatus_ServiceThrowException() throws Exception {
        when(mbMeMemberNotificationService.updateAllStatus(anyString()))
                .thenThrow(new RuntimeException("Internal error"));

        mockMvc.perform(put("/api/v1/project-details/notification-member/update-all-status")
                        .param("memberId", "123"))
                .andExpect(status().is5xxServerError());
    }

    @Test
    void testSendNotificationMember_MessageMapping() {
        MBMeNotificationMemberController controller = new MBMeNotificationMemberController(mbMeMemberNotificationService);
        MBMessageNotificationMemberRequest request = new MBMessageNotificationMemberRequest();
        ResponseObject<?> res = controller.sendNotificationMember(request);
        assert res.getStatus() == HttpStatus.OK;
        assert res.getMessage().equals("gửi thành công");
        assert (Boolean) res.getData();
    }

}

