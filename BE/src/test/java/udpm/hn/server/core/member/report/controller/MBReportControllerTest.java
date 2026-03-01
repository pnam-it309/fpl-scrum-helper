//package udpm.hn.server.core.member.report.controller;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.MediaType;
//import org.springframework.test.context.TestPropertySource;
//import org.springframework.test.context.bean.override.mockito.MockitoBean;
//import org.springframework.test.web.servlet.MockMvc;
//import udpm.hn.server.core.common.base.ResponseObject;
//import udpm.hn.server.core.member.report.model.request.MBReportRequest;
//import udpm.hn.server.core.member.report.model.request.MBTodoProjectRequest;
//import udpm.hn.server.core.member.report.model.request.UpdateReportSettingRequest;
//import udpm.hn.server.core.member.report.service.MBReportService;
//import udpm.hn.server.core.member.report.service.MBTodoProjectService;
//import com.fasterxml.jackson.databind.ObjectMapper;
//
//import static org.mockito.Mockito.when;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.eq;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@SpringBootTest
//@AutoConfigureMockMvc(addFilters = false)
//@TestPropertySource("classpath:application.properties")
//
//public class MBReportControllerTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    @MockitoBean
//    private MBReportService reportService;
//
//    @MockitoBean
//    private MBTodoProjectService todoProjectService;
//
//    private ResponseObject<Object> mockResponse;
//
//    @BeforeEach
//    void setUp() {
//        mockResponse = new ResponseObject<>();
//        mockResponse.setStatus(HttpStatus.OK);
//        mockResponse.setMessage("Success");
//        mockResponse.setData(null);
//    }
//
//    @Test
//    void testGetAllReport() throws Exception {
//        when(reportService.getAll(any(MBReportRequest.class), eq("project1"))).thenReturn((ResponseObject) mockResponse);
//
//        mockMvc.perform(get("/api/v1/member/my-project/report/project1"))
//                .andExpect(status().isOk());
//    }
//
//    @Test
//    void testGetAllTodo() throws Exception {
//        when(todoProjectService.getAllTodoByProjectAndStaffProject(any(MBTodoProjectRequest.class), eq("project1")))
//                .thenReturn((ResponseObject) mockResponse);
//
//        mockMvc.perform(get("/api/v1/member/my-project/todo/project1"))
//                .andExpect(status().isOk());
//    }
//
//    @Test
//    void testAddReport() throws Exception {
//        MBReportRequest request = new MBReportRequest(); // fill fields if needed
//        when(reportService.add(any(MBReportRequest.class), eq("project1"))).thenReturn((ResponseObject) mockResponse);
//
//        mockMvc.perform(post("/api/v1/member/my-project/add-report/project1")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(request)))
//                .andExpect(status().isOk());
//    }
//
//    @Test
//    void testUpdateReport() throws Exception {
//        MBReportRequest request = new MBReportRequest(); // fill fields if needed
//        when(reportService.update(eq("report1"), any(MBReportRequest.class))).thenReturn((ResponseObject) mockResponse);
//
//        mockMvc.perform(put("/api/v1/member/my-project/update-report/report1")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(request)))
//                .andExpect(status().isOk());
//    }
//
//    @Test
//    void testDetailReport() throws Exception {
//        when(reportService.detail("report1", "project1")).thenReturn((ResponseObject) mockResponse);
//
//        mockMvc.perform(get("/api/v1/member/my-project/detail-report/project1/report1"))
//                .andExpect(status().isOk());
//    }
//
//    @Test
//    void testGetReportIdByDate() throws Exception {
//        when(reportService.detailByDate(1720425600000L, "project1")).thenReturn((ResponseObject) mockResponse);
//
//        mockMvc.perform(get("/api/v1/member/my-project/report-by-date/1720425600000/project1"))
//                .andExpect(status().isOk());
//    }
//
//    @Test
//    void testGetAllReport_NotFound() throws Exception {
//        ResponseObject<Object> notFound = new ResponseObject<>();
//        notFound.setStatus(HttpStatus.NOT_FOUND);
//        notFound.setMessage("Not found");
//        notFound.setData(null);
//        when(reportService.getAll(any(MBReportRequest.class), eq("project-notfound"))).thenReturn((ResponseObject) notFound);
//
//        mockMvc.perform(get("/api/v1/member/my-project/report/project-notfound"))
//                .andExpect(status().isNotFound());
//    }
//
//    @Test
//    void testAddReport_BadRequest() throws Exception {
//        MBReportRequest request = new MBReportRequest();
//        ResponseObject<Object> badReq = new ResponseObject<>();
//        badReq.setStatus(HttpStatus.BAD_REQUEST);
//        badReq.setMessage("Invalid");
//        badReq.setData(null);
//        when(reportService.add(any(MBReportRequest.class), eq("project1"))).thenReturn((ResponseObject) badReq);
//
//        mockMvc.perform(post("/api/v1/member/my-project/add-report/project1")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(request)))
//                .andExpect(status().isBadRequest());
//    }
//
//    @Test
//    void testDetailReport_InternalServerError() throws Exception {
//        when(reportService.detail(any(), any())).thenThrow(new RuntimeException("Unexpected error"));
//
//        mockMvc.perform(get("/api/v1/member/my-project/detail-report/project1/report1"))
//                .andExpect(status().is5xxServerError());
//    }
//    @Test
//    void testGetAllTodo_NullResponse() throws Exception {
//        when(todoProjectService.getAllTodoByProjectAndStaffProject(any(MBTodoProjectRequest.class), eq("project1")))
//                .thenReturn(null);
//
//        mockMvc.perform(get("/api/v1/member/my-project/todo/project1"))
//                .andExpect(status().isOk()); // hoặc NoContent nếu Helper xử lý vậy
//    }
//    @Test
//    void testGetReportSetting_Success() throws Exception {
//        when(reportService.getReportSetting("project1")).thenReturn((ResponseObject) mockResponse);
//
//        mockMvc.perform(get("/api/v1/member/my-project/report-setting/project1"))
//                .andExpect(status().isOk());
//    }
//
//    @Test
//    void testUpdateReportSetting_Success() throws Exception {
//        UpdateReportSettingRequest req = new UpdateReportSettingRequest();
//        when(reportService.updateReportSetting(eq("setting1"), any(UpdateReportSettingRequest.class)))
//                .thenReturn((ResponseObject) mockResponse);
//
//        mockMvc.perform(put("/api/v1/member/my-project/update-report-setting/setting1")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(req)))
//                .andExpect(status().isOk());
//    }
//    @Test
//    void testAddReport_EmptyBody() throws Exception {
//        // Không set content, sẽ nhận về 400 BadRequest do thiếu body
//        mockMvc.perform(post("/api/v1/member/my-project/add-report/project1")
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isBadRequest());
//    }
//
//    @Test
//    void testGetReportSetting_NotFound() throws Exception {
//        ResponseObject<Object> notFound = new ResponseObject<>();
//        notFound.setStatus(HttpStatus.NOT_FOUND);
//        notFound.setMessage("Not found");
//        notFound.setData(null);
//
//        when(reportService.getReportSetting("project-notfound")).thenReturn((ResponseObject) notFound);
//
//        mockMvc.perform(get("/api/v1/member/my-project/report-setting/project-notfound"))
//                .andExpect(status().isNotFound());
//    }
//    @Test
//    void testGetReportIdByDate_InvalidDate() throws Exception {
//        when(reportService.detailByDate(0L, "project1")).thenReturn((ResponseObject) mockResponse);
//
//        mockMvc.perform(get("/api/v1/member/my-project/report-by-date/0/project1"))
//                .andExpect(status().isOk());
//    }
//
//
//}