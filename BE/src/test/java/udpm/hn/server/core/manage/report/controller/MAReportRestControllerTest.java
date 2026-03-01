package udpm.hn.server.core.manage.report.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
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
import udpm.hn.server.core.manage.report.model.request.HolidayCreateRequest;
import udpm.hn.server.core.manage.report.model.request.MAReportRequest;
import udpm.hn.server.core.manage.report.model.request.MAReportUserRequest;
import udpm.hn.server.core.manage.report.service.MAReportService;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@TestPropertySource("classpath:application.properties")

public class MAReportRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MAReportService maReportService;

    private ResponseObject<Object> response;

    @BeforeEach
    void setUp() {
        response = new ResponseObject<>();
        response.setStatus(HttpStatus.OK);
        response.setMessage("Success");
        response.setData(null);
    }

    @Test
    void testGetAllReport() throws Exception {
        when(maReportService.getAllReport(any(MAReportRequest.class))).thenReturn((ResponseObject) response);

        mockMvc.perform(get("/api/v1/manage/report"))
                .andExpect(status().isOk());
    }

    @Test
    void testFindAllReport() throws Exception {
        when(maReportService.findAllReport(any(MAReportRequest.class))).thenReturn((ResponseObject) response);

        mockMvc.perform(get("/api/v1/manage/report/allReport"))
                .andExpect(status().isOk());
    }

    @Test
    void testDownloadWordFile() throws Exception {
        MAReportUserRequest request = new MAReportUserRequest();
        byte[] zipBytes = "fake-content".getBytes();

        when(maReportService.readFileDoc(eq("1"), any(MAReportUserRequest.class))).thenReturn(zipBytes);

        mockMvc.perform(post("/api/v1/manage/report/docx/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void testExportReport() throws Exception {
        MAReportUserRequest request = new MAReportUserRequest();

        when(maReportService.exportReport(any(HttpServletResponse.class), eq("1"), any(MAReportUserRequest.class)))
                .thenReturn((ResponseObject) response);

        mockMvc.perform(post("/api/v1/manage/report/export/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void testGetChangeHistory() throws Exception {
        when(maReportService.getLogsImportReport(eq(0), eq(50))).thenReturn((ResponseObject) response);

        mockMvc.perform(get("/api/v1/manage/report/history")
                        .param("page", "0")
                        .param("size", "50"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetAllReportCompensation() throws Exception {
        when(maReportService.getAllReports()).thenReturn((ResponseObject) response);

        mockMvc.perform(get("/api/v1/manage/report/report-compensation"))
                .andExpect(status().isOk());
    }

    @Test
    void testCreateMultipleHolidays() throws Exception {
        when(maReportService.createMultipleHolidays(any(), eq("1")))
                .thenReturn((ResponseObject) response);

        mockMvc.perform(post("/api/v1/manage/report/create-holiday/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(new HolidayCreateRequest())))
                .andExpect(status().isOk());
    }

    @Test
    void testDetailHoliday() throws Exception {
        when(maReportService.detailHoliday(eq("1"))).thenReturn((ResponseObject) response);

        mockMvc.perform(get("/api/v1/manage/report/detail-holiday/1"))
                .andExpect(status().isOk());
    }

    @Test
    void testUpdateHolidayStatus() throws Exception {
        when(maReportService.updateHolidaysStatus(anyList(), eq("1")))
                .thenReturn((ResponseObject) response);

        mockMvc.perform(put("/api/v1/manage/report/update-holiday/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(List.of(System.currentTimeMillis()))))
                .andExpect(status().isOk());
    }

    

}
