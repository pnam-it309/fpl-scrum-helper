//package udpm.hn.server.core.member.projectdetails.controller;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mockito;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.MediaType;
//import org.springframework.test.context.bean.override.mockito.MockitoBean;
//import org.springframework.test.web.servlet.MockMvc;
//import udpm.hn.server.core.common.base.ResponseObject;
//import udpm.hn.server.core.member.projectdetails.model.request.*;
//import udpm.hn.server.core.member.projectdetails.service.MBMeTodoService;
//
//import static org.mockito.ArgumentMatchers.any;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//@SpringBootTest
//@AutoConfigureMockMvc(addFilters = false)
//public class MBMeTodoControllerTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @MockitoBean
//    private MBMeTodoService mbMeTodoService;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    private MBMeFilterTodoRequest filterRequest;
//    private MBFilterTodoModalRequest modalRequest;
//    private MBCheckTodoChildRequest checkChildRequest;
//
//    @BeforeEach
//    void setUp() {
//        filterRequest = new MBMeFilterTodoRequest();
//        modalRequest = new MBFilterTodoModalRequest();
//        checkChildRequest = new MBCheckTodoChildRequest();
//    }
//
//    @Test
//    void testGetTodoBoard() throws Exception {
//        ResponseObject<Object> mockResponse = new ResponseObject<>();
//        mockResponse.setSuccess(true);
//        mockResponse.setStatus(HttpStatus.OK);
//        mockResponse.setMessage("Success");
//        mockResponse.setData("Board Data");
//
//        Mockito.when(mbMeTodoService.getAllBoard(any(MBMeFilterTodoRequest.class)))
//                .thenReturn((ResponseObject) mockResponse);
//
//        mockMvc.perform(get("/api/v1/project-details/todo/board")
//                        .param("projectId", "p1")
//                        .param("phaseId", "ph1")
//                        .param("memberId", "m1"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.success").value(true))
//                .andExpect(jsonPath("$.message").value("Success"))
//                .andExpect(jsonPath("$.data").value("Board Data"));
//    }
//
//    @Test
//    void testFindTodoById() throws Exception {
//        ResponseObject<Object> mockResponse = new ResponseObject<>();
//        mockResponse.setSuccess(true);
//        mockResponse.setStatus(HttpStatus.OK); // Đây là cái bị thiếu
//        mockResponse.setMessage("Success");
//        mockResponse.setData("Todo Detail");
//
//        Mockito.when(mbMeTodoService.findTodoById(any(MBFilterTodoModalRequest.class)))
//                .thenReturn((ResponseObject) mockResponse);
//
//        mockMvc.perform(get("/api/v1/project-details/todo/find-todo")
//                        .param("todoId", "todo1"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.success").value(true))
//                .andExpect(jsonPath("$.message").value("Success"))
//                .andExpect(jsonPath("$.data").value("Todo Detail"));
//    }
//
//
//    @Test
//    void testGetAllDetailTodo() throws Exception {
//        ResponseObject<Object> mockResponse = new ResponseObject<>();
//        mockResponse.setSuccess(true);
//        mockResponse.setStatus(HttpStatus.OK);
//        mockResponse.setMessage("Details");
//        mockResponse.setData("Some Detail Data");
//
//        Mockito.when(mbMeTodoService.getAllDetailTodo(any(MBFilterTodoModalRequest.class)))
//                .thenReturn((ResponseObject) mockResponse);
//
//        mockMvc.perform(get("/api/v1/project-details/todo/get-all-detail")
//                        .param("todoId", "todo1"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.success").value(true))
//                .andExpect(jsonPath("$.message").value("Details"))
//                .andExpect(jsonPath("$.data").value("Some Detail Data"));
//    }
//
//
//    @Test
//    void testGetAllCheckTodoChild() throws Exception {
//        ResponseObject<Object> mockResponse = new ResponseObject<>();
//        mockResponse.setSuccess(true);
//        mockResponse.setStatus(HttpStatus.OK);
//        mockResponse.setMessage("Checklist");
//        mockResponse.setData("Some Checklist Data");
//
//        Mockito.when(mbMeTodoService.getAllCheckTodoChild(any(MBCheckTodoChildRequest.class)))
//                .thenReturn((ResponseObject) mockResponse);
//
//        mockMvc.perform(get("/api/v1/project-details/todo/check-todochild")
//                        .param("todoId", "todo1"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.success").value(true))
//                .andExpect(jsonPath("$.message").value("Checklist"))
//                .andExpect(jsonPath("$.data").value("Some Checklist Data"));
//    }
//
//
//    @Test
//    void testGetStatusByIdPhase() throws Exception {
//        ResponseObject<String> mockResponse = new ResponseObject<>();
//        mockResponse.setSuccess(true);
//        mockResponse.setStatus(HttpStatus.OK);
//        mockResponse.setMessage("Status info");
//        mockResponse.setData("In Progress");
//
//        Mockito.when(mbMeTodoService.getStatusByIdPhase("ph1"))
//                .thenReturn((ResponseObject) mockResponse);
//
//        mockMvc.perform(get("/api/v1/project-details/todo/status-phase/ph1"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.success").value(true))
//                .andExpect(jsonPath("$.message").value("Status info"))
//                .andExpect(jsonPath("$.data").value("In Progress"));
//    }
//
//}