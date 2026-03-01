package udpm.hn.server.core.member.projectdetails.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import udpm.hn.server.core.common.base.ResponseObject;
import udpm.hn.server.core.member.projectdetails.model.request.*;
import udpm.hn.server.core.member.projectdetails.service.MBMeTodoListService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@TestPropertySource("classpath:application.properties")
class MBMeTodoListController2Test {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MBMeTodoListService mbMeTodoListService;

    private ResponseObject<Object> mockResponse;

    @BeforeEach
    void setUp() {
        mockResponse = new ResponseObject<>();
        mockResponse.setStatus(HttpStatus.OK);
        mockResponse.setMessage("Success");
        mockResponse.setData(null);
    }

    // Test MessageMapping - updateIndexTodoList
    @Test
    void testUpdateIndexTodoList_MessageMapping() {
        MBMeUpdateTodoListRequest req = new MBMeUpdateTodoListRequest();
        when(mbMeTodoListService.updateIndexTodoList(any(MBMeUpdateTodoListRequest.class)))
                .thenReturn((ResponseObject)mockResponse);

        MBMeTodoListController controller = new MBMeTodoListController(mbMeTodoListService);
        ResponseObject<?> res = controller.updateIndexTodoList(req, "projectId123");
        assertEquals(HttpStatus.OK, res.getStatus());
    }

    // Test MessageMapping - deleteTodoList
    @Test
    void testDeleteTodoList_MessageMapping() {
        MBMeDeleteTodoListRequest req = new MBMeDeleteTodoListRequest();
        when(mbMeTodoListService.deleteTodoList(any(MBMeDeleteTodoListRequest.class)))
                .thenReturn((ResponseObject)mockResponse);

        MBMeTodoListController controller = new MBMeTodoListController(mbMeTodoListService);
        ResponseObject<?> res = controller.deleteTodoList(req, "projectId123");
        assertEquals(HttpStatus.OK, res.getStatus());
    }

    // Test MessageMapping - updateNameTodoList
    @Test
    void testUpdateNameTodoList_MessageMapping() {
        MBMeUpdateNameTodoListRequest req = new MBMeUpdateNameTodoListRequest();
        when(mbMeTodoListService.updateNameTodoList(any(MBMeUpdateNameTodoListRequest.class)))
                .thenReturn((ResponseObject)mockResponse);

        MBMeTodoListController controller = new MBMeTodoListController(mbMeTodoListService);
        ResponseObject<?> res = controller.updateNameTodoList(req, "projectId123");
        assertEquals(HttpStatus.OK, res.getStatus());
    }

    // Test GET: /all-todolist/by-id-phase-project
    @Test
    void testGetAllTodoListByIdPhaseProject() throws Exception {
        when(mbMeTodoListService.getAllTodoListByIdPhaseProject(anyString()))
                .thenReturn((ResponseObject)mockResponse);

        mockMvc.perform(get("/api/v1/project-details/todo-list/all-todolist/by-id-phase-project")
                        .param("idPhaseProject", "phaseId123"))
                .andExpect(status().isOk());
    }

    // Test GET: /details-project/by-id-phase-project
    @Test
    void testGetProjectByIdPhaseProject() throws Exception {
        when(mbMeTodoListService.getProjectByIdPhaseProject(anyString()))
                .thenReturn((ResponseObject)mockResponse);

        mockMvc.perform(get("/api/v1/project-details/todo-list/details-project/by-id-phase-project")
                        .param("idPhaseProject", "phaseId123"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetAllTodoListByIdPhaseProject_ServiceReturnNull() throws Exception {
        when(mbMeTodoListService.getAllTodoListByIdPhaseProject(anyString())).thenReturn(null);

        mockMvc.perform(get("/api/v1/project-details/todo-list/all-todolist/by-id-phase-project")
                        .param("idPhaseProject", "phaseId123"))
                .andExpect(status().isOk()); // hoặc .isNoContent() tuỳ Helper xử lý
    }

    @Test
    void testGetProjectByIdPhaseProject_ServiceReturnError() throws Exception {
        ResponseObject<Object> errorRes = new ResponseObject<>();
        errorRes.setStatus(HttpStatus.NOT_FOUND);
        errorRes.setMessage("Not Found");
        errorRes.setData(null);

        when(mbMeTodoListService.getProjectByIdPhaseProject(anyString()))
                .thenReturn((ResponseObject)errorRes);

        mockMvc.perform(get("/api/v1/project-details/todo-list/details-project/by-id-phase-project")
                        .param("idPhaseProject", "phaseId123"))
                .andExpect(status().isNotFound());
    }
    @Test
    void testGetAllTodoListByIdPhaseProject_ServiceThrowException() throws Exception {
        when(mbMeTodoListService.getAllTodoListByIdPhaseProject(anyString()))
                .thenThrow(new RuntimeException("Service error"));

        mockMvc.perform(get("/api/v1/project-details/todo-list/all-todolist/by-id-phase-project")
                        .param("idPhaseProject", "phaseId123"))
                .andExpect(status().is5xxServerError());
    }
    @Test
    void testUpdateIndexTodoList_ServiceReturnNull() {
        MBMeUpdateTodoListRequest req = new MBMeUpdateTodoListRequest();
        when(mbMeTodoListService.updateIndexTodoList(any(MBMeUpdateTodoListRequest.class)))
                .thenReturn(null);

        MBMeTodoListController controller = new MBMeTodoListController(mbMeTodoListService);
        ResponseObject<?> res = controller.updateIndexTodoList(req, "projectId123");
        assertEquals(null, res); // hoặc assertNull(res)
    }

    @Test
    void testUpdateIndexTodoList_ServiceReturnError() {
        MBMeUpdateTodoListRequest req = new MBMeUpdateTodoListRequest();
        ResponseObject<Object> errorRes = new ResponseObject<>();
        errorRes.setStatus(HttpStatus.BAD_REQUEST);
        errorRes.setMessage("Error");
        errorRes.setData(null);

        when(mbMeTodoListService.updateIndexTodoList(any(MBMeUpdateTodoListRequest.class)))
                .thenReturn((ResponseObject)errorRes);

        MBMeTodoListController controller = new MBMeTodoListController(mbMeTodoListService);
        ResponseObject<?> res = controller.updateIndexTodoList(req, "projectId123");
        assertEquals(HttpStatus.BAD_REQUEST, res.getStatus());
    }

}
