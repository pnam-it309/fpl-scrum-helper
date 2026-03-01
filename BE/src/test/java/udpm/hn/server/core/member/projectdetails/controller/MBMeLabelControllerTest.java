package udpm.hn.server.core.member.projectdetails.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;
import udpm.hn.server.core.common.base.ResponseObject;
import udpm.hn.server.core.member.projectdetails.model.request.*;
import udpm.hn.server.core.member.projectdetails.service.MBMeLabelService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@TestPropertySource("classpath:application.properties")

public class MBMeLabelControllerTest {


    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MBMeLabelService mbMeLabelService;

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
    void testGetAllLabelByIdTodo() throws Exception {
        when(mbMeLabelService.getAllLabelByIdTodo("todo123")).thenReturn((ResponseObject) mockResponse);

        mockMvc.perform(get("/api/v1/project-details/label")
                        .param("idTodo", "todo123"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetAllLabelByIdProject() throws Exception {
        when(mbMeLabelService.getAllByIdProject("project123", "todo123")).thenReturn((ResponseObject) mockResponse);

        mockMvc.perform(get("/api/v1/project-details/label/list")
                        .param("idProject", "project123")
                        .param("idTodo", "todo123"))
                .andExpect(status().isOk());
    }

    @Test
    void testDetail() throws Exception {
        when(mbMeLabelService.detail("label123")).thenReturn((ResponseObject) mockResponse);

        mockMvc.perform(get("/api/v1/project-details/label/detail/label123"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetAllLabelSearchByIdProject() throws Exception {
        when(mbMeLabelService.getAllLabelSearchByIdProject("project123")).thenReturn((ResponseObject) mockResponse);

        mockMvc.perform(get("/api/v1/project-details/label/list-search")
                        .param("idProject", "project123"))
                .andExpect(status().isOk());
    }

    @Test
    void testCreateLabel() {
        MBMeCreateLabelProjectRequest request = new MBMeCreateLabelProjectRequest();
        when(mbMeLabelService.create(any(), any())).thenReturn((ResponseObject) mockResponse);

        MBMeLabelController controller = new MBMeLabelController(mbMeLabelService);
        ResponseObject<?> result = controller.createLabel(request, null);
        assert result.getStatus() == HttpStatus.OK;
    }

    @Test
    void testUpdateLabel() {
        MBMeUpdateLabelProjectRequest request = new MBMeUpdateLabelProjectRequest();
        when(mbMeLabelService.update(any(), any())).thenReturn((ResponseObject) mockResponse);

        MBMeLabelController controller = new MBMeLabelController(mbMeLabelService);
        ResponseObject<?> result = controller.updateLabel(request, null);
        assert result.getStatus() == HttpStatus.OK;
    }

    @Test
    void testDeleteLabel() {
        MBMeDeleteLabelProjectRequest request = new MBMeDeleteLabelProjectRequest();
        when(mbMeLabelService.delete(any(), any())).thenReturn((ResponseObject) mockResponse);

        MBMeLabelController controller = new MBMeLabelController(mbMeLabelService);
        ResponseObject<?> result = controller.deleteLabel(request, null);
        assert result.getStatus() == HttpStatus.OK;
    }


}
