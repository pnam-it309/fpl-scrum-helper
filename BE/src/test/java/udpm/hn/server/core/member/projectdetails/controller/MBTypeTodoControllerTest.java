//package udpm.hn.server.core.member.projectdetails.controller;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.MediaType;
//import org.springframework.test.context.bean.override.mockito.MockitoBean;
//import org.springframework.test.web.servlet.MockMvc;
//import udpm.hn.server.core.common.base.ResponseObject;
//import udpm.hn.server.core.member.projectdetails.model.request.ADCreateUpdateTypeTodoRequest;
//import udpm.hn.server.core.member.projectdetails.service.MBMeTypeTodoService;
//
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.anyString;
//import static org.mockito.Mockito.when;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//@SpringBootTest
//@AutoConfigureMockMvc(addFilters = false)
//class MBTypeTodoControllerTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @MockitoBean
//    private MBMeTypeTodoService mbMeTypeTodoService;
//
//    @Autowired
//    private ObjectMapper objectMapper;
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
//    @DisplayName("GET /type-todo - should return all type todos")
//    void testGetAllTypeTodo() throws Exception {
//        when(mbMeTypeTodoService.getAllTypeTodo()).thenReturn((ResponseObject) mockResponse);
//
//        mockMvc.perform(get("/api/v1/project-details/type-todo"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.success").value(true))
//                .andExpect(jsonPath("$.message").value("Success"));
//    }
//
//    @Test
//    @DisplayName("PUT /update/{typeId} - should update type todo successfully")
//    void testUpdateTypeTodo() throws Exception {
//        String typeId = "type-001";
//        ADCreateUpdateTypeTodoRequest request = new ADCreateUpdateTypeTodoRequest();
//        request.setType("Updated Type");
//
//        when(mbMeTypeTodoService.updateTypeTodo(anyString(), any(ADCreateUpdateTypeTodoRequest.class)))
//                .thenReturn((ResponseObject) mockResponse);
//
//        mockMvc.perform(put("/api/v1/project-details/type-todo/update/{typeId}", typeId)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(request)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.success").value(true))
//                .andExpect(jsonPath("$.message").value("Success"));
//    }
//
//    @Test
//    @DisplayName("GET /detail/{typeId} - should return type todo by ID")
//    void testGetTypeTodoById() throws Exception {
//        String typeId = "type-002";
//
//        when(mbMeTypeTodoService.getTypeTodoById(anyString())).thenReturn((ResponseObject) mockResponse);
//
//        mockMvc.perform(get("/api/v1/project-details/type-todo/detail/{typeId}", typeId))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.success").value(true))
//                .andExpect(jsonPath("$.message").value("Success"));
//    }
//}
