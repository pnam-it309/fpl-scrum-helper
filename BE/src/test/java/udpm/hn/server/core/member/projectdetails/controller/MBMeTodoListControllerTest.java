//package udpm.hn.server.core.member.projectdetails.controller;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.http.HttpStatus;
//import org.springframework.test.context.bean.override.mockito.MockitoBean;
//import org.springframework.test.web.servlet.MockMvc;
//import udpm.hn.server.core.common.base.ResponseObject;
//import udpm.hn.server.core.member.projectdetails.service.MBMeTodoListService;
//
//import static org.mockito.ArgumentMatchers.anyString;
//import static org.mockito.Mockito.when;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//@SpringBootTest
//@AutoConfigureMockMvc(addFilters = false)
//public class MBMeTodoListControllerTest {
//    @Autowired
//    private MockMvc mockMvc;
//
//    @MockitoBean
//    private MBMeTodoListService mbMeTodoListService;
//
//    private ResponseObject<Object> mockResponse;
//
//    @BeforeEach
//    void setUp() {
//        mockResponse = new ResponseObject<>();
//        mockResponse.setSuccess(true);
//        mockResponse.setStatus(HttpStatus.OK);
//        mockResponse.setMessage("Success");
//        mockResponse.setData(null);
//    }
//
//    @Test
//    @DisplayName("GET /all-todolist/by-id-phase-project - should return 200 OK")
//    void testGetAllTodoListByIdPhaseProject() throws Exception {
//        String idPhaseProject = "phase-001";
//
//        when(mbMeTodoListService.getAllTodoListByIdPhaseProject(anyString()))
//                .thenReturn((ResponseObject) mockResponse);
//
//        mockMvc.perform(get("/api/v1/project-details/todo-list/all-todolist/by-id-phase-project")
//                        .param("idPhaseProject", idPhaseProject))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.success").value(true))
//                .andExpect(jsonPath("$.message").value("Success"));
//    }
//
//    @Test
//    @DisplayName("GET /details-project/by-id-phase-project - should return 200 OK")
//    void testGetProjectByIdPhaseProject() throws Exception {
//        String idPhaseProject = "phase-002";
//
//        when(mbMeTodoListService.getProjectByIdPhaseProject(anyString()))
//                .thenReturn((ResponseObject) mockResponse);
//
//        mockMvc.perform(get("/api/v1/project-details/todo-list/details-project/by-id-phase-project")
//                        .param("idPhaseProject", idPhaseProject))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.success").value(true))
//                .andExpect(jsonPath("$.message").value("Success"));
//    }
//
//}
