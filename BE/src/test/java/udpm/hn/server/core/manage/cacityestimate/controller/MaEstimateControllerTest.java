package udpm.hn.server.core.manage.cacityestimate.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import udpm.hn.server.core.common.base.ResponseObject;
import udpm.hn.server.core.manage.capacityestimate.controller.MaEstimateController;
import udpm.hn.server.core.manage.capacityestimate.model.request.MaEstimatePageRequest;
import udpm.hn.server.core.manage.capacityestimate.service.MACapacityEstimate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MaEstimateController.class)
class MaEstimateControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MACapacityEstimate capacityEstimate;

    @Test
    void testGetAllEstimate_Success() throws Exception {
        ResponseObject<?> fakeResponse = new ResponseObject<>("data", HttpStatus.OK, "OK");
        when(capacityEstimate.getAllCapacityEstimate(any(MaEstimatePageRequest.class)))
                .thenReturn((ResponseObject) fakeResponse);

        // Act & Assert
        mockMvc.perform(get("/api/v1/manage/estimate"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }
    @Test
    void testGetAllEstimate_WithParams() throws Exception {
        ResponseObject<?> fakeResponse = new ResponseObject<>("data", HttpStatus.OK, "OK");
        when(capacityEstimate.getAllCapacityEstimate(any(MaEstimatePageRequest.class)))
                .thenReturn((ResponseObject) fakeResponse);

        mockMvc.perform(get("/api/v1/manage/estimate")
                        .param("page", "2") // truyền đúng tên field trong MaEstimatePageRequest
                        .param("size", "10")
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }
    @Test
    void testGetAllEstimate_ServiceException() throws Exception {
        when(capacityEstimate.getAllCapacityEstimate(any(MaEstimatePageRequest.class)))
                .thenThrow(new RuntimeException("Some error"));

        mockMvc.perform(get("/api/v1/manage/estimate"))
                .andExpect(status().is5xxServerError());
    }

    @Test
    void testGetAllEstimate_NullParam() throws Exception {
        ResponseObject<?> fakeResponse = new ResponseObject<>("data", HttpStatus.OK, "OK");
        when(capacityEstimate.getAllCapacityEstimate(any(MaEstimatePageRequest.class)))
                .thenReturn((ResponseObject) fakeResponse);

        mockMvc.perform(get("/api/v1/manage/estimate"))
                .andExpect(status().isOk());
    }


}
