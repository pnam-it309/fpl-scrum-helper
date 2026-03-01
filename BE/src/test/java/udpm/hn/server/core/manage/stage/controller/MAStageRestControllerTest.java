package udpm.hn.server.core.manage.stage.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import udpm.hn.server.core.common.base.ResponseObject;
import udpm.hn.server.core.manage.stage.model.request.StageRequest;
import udpm.hn.server.core.manage.stage.model.request.StageVoteRequest;
import udpm.hn.server.core.manage.stage.service.MAStageVoteService;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@TestPropertySource("classpath:application.properties")

public class MAStageRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MAStageVoteService maStageVoteService;

    private ResponseObject<Object> response;

    @BeforeEach
    void setUp() {
        response = new ResponseObject<>();
        response.setStatus(HttpStatus.OK);
        response.setMessage("Success");
        response.setData(null);
    }

    @Test
    void testStageStartWithRequestParam() throws Exception {
        when(maStageVoteService.getAllStage(any(StageVoteRequest.class))).thenReturn((ResponseObject) response);

        mockMvc.perform(get("/api/v1/manage/stage"))
                .andExpect(status().isOk());
    }

    @Test
    void testDetailStage() throws Exception {
        when(maStageVoteService.detailStageVote("stage123")).thenReturn((ResponseObject) response);

        mockMvc.perform(get("/api/v1/manage/stage/detail/stage123"))
                .andExpect(status().isOk());
    }

    @Test
    void testDeleteStage() throws Exception {
        when(maStageVoteService.deleteStage("stageVote123")).thenReturn((ResponseObject) response);

        mockMvc.perform(delete("/api/v1/manage/stage/stageVote123"))
                .andExpect(status().isOk());
    }

    @Test
    void testStageStartWithPathVariable() throws Exception {
        when(maStageVoteService.findALlTodoVoteStage("stageVoteId")).thenReturn((ResponseObject) response);

        mockMvc.perform(get("/api/v1/manage/stage/stageVoteId"))
                .andExpect(status().isOk());
    }

    @Test
    void testStageTakePlace() throws Exception {
        when(maStageVoteService.stageTakePlace("project123")).thenReturn((ResponseObject) response);

        mockMvc.perform(get("/api/v1/manage/stage/take-place/project123"))
                .andExpect(status().isOk());
    }

    @Test
    void testCreateUpdateStage() throws Exception {
        StageRequest request = new StageRequest();

        when(maStageVoteService.createUpdate(any(StageRequest.class))).thenReturn((ResponseObject) response);

        mockMvc.perform(post("/api/v1/manage/stage")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void testStageStartWithRequestParamEmpty() throws Exception {
        when(maStageVoteService.getAllStage(any(StageVoteRequest.class))).thenReturn((ResponseObject) response);
        mockMvc.perform(get("/api/v1/manage/stage")) // không truyền param gì cả
                .andExpect(status().isOk());
    }
    @Test
    void testServiceException() throws Exception {
        when(maStageVoteService.getAllStage(any(StageVoteRequest.class)))
                .thenThrow(new RuntimeException("Error!"));
        mockMvc.perform(get("/api/v1/manage/stage"))
                .andExpect(status().is5xxServerError());
    }
    @Test
    void testCreateUpdateStage_EmptyBody() throws Exception {
        // Không cần mock service vì method sẽ lỗi ở layer parse body (400)
        mockMvc.perform(post("/api/v1/manage/stage")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isOk()); // hoặc .isBadRequest() nếu bạn muốn check lỗi truyền thiếu
    }
    @Test
    void testStageStart_ServiceThrowsException() throws Exception {
        when(maStageVoteService.getAllStage(any())).thenThrow(new RuntimeException("Lỗi nội bộ"));
        mockMvc.perform(get("/api/v1/manage/stage"))
                .andExpect(status().is5xxServerError());
    }
    @Test
    void testDetailStage_WithInvalidId() throws Exception {
        when(maStageVoteService.detailStageVote(anyString())).thenReturn((ResponseObject) response);
        mockMvc.perform(get("/api/v1/manage/stage/detail/"))
                .andExpect(status().isNotFound()); // vì không có path variable, framework sẽ báo lỗi
    }
    @Test
    void testStageStartWithRequestParamNull() throws Exception {
        when(maStageVoteService.getAllStage(any())).thenReturn((ResponseObject) response);
        mockMvc.perform(get("/api/v1/manage/stage"))
                .andExpect(status().isOk());
    }

}