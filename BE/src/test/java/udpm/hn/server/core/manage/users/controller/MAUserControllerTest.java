package udpm.hn.server.core.manage.users.controller;


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
import udpm.hn.server.core.manage.user.model.request.MAUserRequest;
import udpm.hn.server.core.manage.user.model.request.MaUserCreateUpdateRequest;
import udpm.hn.server.core.manage.user.service.MAUserService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@TestPropertySource("classpath:application.properties")
public class MAUserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MAUserService maUserService;

    private ResponseObject<Object> response;

    @BeforeEach
    void setUp() {
        response = new ResponseObject<>();
        response.setStatus(HttpStatus.OK);
        response.setMessage("Success");
        response.setData(null);
    }

    @Test
    void testGetAllUser() throws Exception {
        when(maUserService.listUserProject(any(MAUserRequest.class), anyString(), anyString()))
                .thenReturn((ResponseObject)response);

        mockMvc.perform(get("/api/v1/manage/user/123/456"))
                .andExpect(status().isOk());
    }

    @Test
    void testDeleteUser() throws Exception {
        when(maUserService.deleteUser(anyString())).thenReturn((ResponseObject)response);

        mockMvc.perform(put("/api/v1/manage/user/delete-user/999"))
                .andExpect(status().isOk());
    }

    @Test
    void testUpdateProject() throws Exception {
        MaUserCreateUpdateRequest req = new MaUserCreateUpdateRequest();
        ObjectMapper mapper = new ObjectMapper();

        when(maUserService.updateProjectMembers(anyString(), any(MaUserCreateUpdateRequest.class)))
                .thenReturn((ResponseObject)response);

        mockMvc.perform(put("/api/v1/manage/user/111")
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(req)))
                .andExpect(status().isOk());
    }

    @Test
    void testGetAllUserFacility() throws Exception {
        when(maUserService.idFacility(anyString())).thenReturn((ResponseObject)response);

        mockMvc.perform(get("/api/v1/manage/user/facility/123"))
                .andExpect(status().isOk());
    }
}

