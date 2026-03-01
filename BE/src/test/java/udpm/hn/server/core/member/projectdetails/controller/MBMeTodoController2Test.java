package udpm.hn.server.core.member.projectdetails.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import udpm.hn.server.core.common.base.ResponseObject;
import udpm.hn.server.core.member.projectdetails.model.request.*;
import udpm.hn.server.core.member.projectdetails.service.MBMeTodoService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@TestPropertySource("classpath:application.properties")
public class MBMeTodoController2Test {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MBMeTodoService mbMeTodoService;

    private ResponseObject<Object> mockResponse;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockResponse = new ResponseObject<>();
        mockResponse.setStatus(HttpStatus.OK);
        mockResponse.setMessage("Success");
        mockResponse.setData(null);
    }

    // REST ENDPOINTS (GET)
    @Test
    void testGetTodoBoard() throws Exception {
        when(mbMeTodoService.getAllBoard(any(MBMeFilterTodoRequest.class)))
                .thenReturn((ResponseObject) mockResponse);

        mockMvc.perform(get("/api/v1/project-details/todo/board"))
                .andExpect(status().isOk());
    }

    @Test
    void testFindTodoById() throws Exception {
        when(mbMeTodoService.findTodoById(any(MBFilterTodoModalRequest.class)))
                .thenReturn((ResponseObject) mockResponse);

        mockMvc.perform(get("/api/v1/project-details/todo/find-todo"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetAllDetailTodo() throws Exception {
        when(mbMeTodoService.getAllDetailTodo(any(MBFilterTodoModalRequest.class)))
                .thenReturn((ResponseObject) mockResponse);

        mockMvc.perform(get("/api/v1/project-details/todo/get-all-detail"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetAllCheckTodoChild() throws Exception {
        when(mbMeTodoService.getAllCheckTodoChild(any(MBCheckTodoChildRequest.class)))
                .thenReturn((ResponseObject) mockResponse);

        mockMvc.perform(get("/api/v1/project-details/todo/check-todochild"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetStatusByIdPhase() throws Exception {
        when(mbMeTodoService.getStatusByIdPhase(anyString()))
                .thenReturn((ResponseObject) mockResponse);

        mockMvc.perform(get("/api/v1/project-details/todo/status-phase/abc123"))
                .andExpect(status().isOk());
    }

    // MESSAGE MAPPING (gọi trực tiếp hàm controller)
    @Test
    void testUpdateIndexTodo() {
        MBMeTodoController controller = new MBMeTodoController(mbMeTodoService);
        MBMeUpdateIndexTodoRequest req = new MBMeUpdateIndexTodoRequest();
        MBDesVarProjectIdAndPeriodIdRequest des = new MBDesVarProjectIdAndPeriodIdRequest();
        when(mbMeTodoService.updateIndexTodoViewTable(any(MBMeUpdateIndexTodoRequest.class)))
                .thenReturn((ResponseObject) mockResponse);
        ResponseObject<?> result = controller.updateIndexTodo(req, des);
        assert result.getStatus() == HttpStatus.OK;
    }

    @Test
    void testCreateTodo() {
        MBMeTodoController controller = new MBMeTodoController(mbMeTodoService);
        MBMeCreateTodoRequest req = new MBMeCreateTodoRequest();
        MBDesVarProjectIdAndPeriodIdRequest des = new MBDesVarProjectIdAndPeriodIdRequest();
        when(mbMeTodoService.createTodo(any(MBMeCreateTodoRequest.class)))
                .thenReturn((ResponseObject) mockResponse);
        ResponseObject<?> result = controller.createTodo(req, des);
        assert result.getStatus() == HttpStatus.OK;
    }

    @Test
    void testCreateTodoChildChecklist() {
        MBMeTodoController controller = new MBMeTodoController(mbMeTodoService);
        MBMeCreateDetailTodoChildRequest req = new MBMeCreateDetailTodoChildRequest();
        when(mbMeTodoService.createTodoChildChecklist(any(MBMeCreateDetailTodoChildRequest.class)))
                .thenReturn((ResponseObject) mockResponse);
        ResponseObject<?> result = controller.createTodoChildChecklist(req);
        assert result.getStatus() == HttpStatus.OK;
    }

    @Test
    void testEditTodoChildChecklist() {
        MBMeTodoController controller = new MBMeTodoController(mbMeTodoService);
        MBMeEditDetailTodoChildRequest req = new MBMeEditDetailTodoChildRequest();
        when(mbMeTodoService.editTodoChildChecklist(any(MBMeEditDetailTodoChildRequest.class)))
                .thenReturn((ResponseObject) mockResponse);
        ResponseObject<?> result = controller.editTodoChildChecklist(req);
        assert result.getStatus() == HttpStatus.OK;
    }

    @Test
    void testDeleteTodoChecklist() {
        MBMeTodoController controller = new MBMeTodoController(mbMeTodoService);
        MBMeDeleteDetailTodoChildRequest req = new MBMeDeleteDetailTodoChildRequest();
        when(mbMeTodoService.deleteDetailTodoChild(any(MBMeDeleteDetailTodoChildRequest.class)))
                .thenReturn((ResponseObject) mockResponse);
        ResponseObject<?> result = controller.deleteTodoChecklist(req);
        assert result.getStatus() == HttpStatus.OK;
    }

    @Test
    void testUpdateStatusTodoChild() {
        MBMeTodoController controller = new MBMeTodoController(mbMeTodoService);
        MBHandleStatusTodoChildRequest req = new MBHandleStatusTodoChildRequest();
        when(mbMeTodoService.handleStatusTodoChildChange(any(MBHandleStatusTodoChildRequest.class)))
                .thenReturn((ResponseObject) mockResponse);
        ResponseObject<?> result = controller.updateStatusTodoChild(req);
        assert result.getStatus() == HttpStatus.OK;
    }




    @Test
    void testUpdateCompleteTodo() {
        MBMeTodoController controller = new MBMeTodoController(mbMeTodoService);
        MBMeUpdateCompleteTodoRequest req = new MBMeUpdateCompleteTodoRequest();
        when(mbMeTodoService.updateCompleteTodo(any(MBMeUpdateCompleteTodoRequest.class)))
                .thenReturn((ResponseObject) mockResponse);
        ResponseObject<?> result = controller.updateCompleteTodo(req);
        assert result.getStatus() == HttpStatus.OK;
    }



    @Test
    void testSortTodoPriority() {
        MBMeTodoController controller = new MBMeTodoController(mbMeTodoService);
        MBMeSortTodoRequest req = new MBMeSortTodoRequest();
        when(mbMeTodoService.sortTodoPriority(any(MBMeSortTodoRequest.class)))
                .thenReturn((ResponseObject) mockResponse);
        ResponseObject<?> result = controller.sortTodoPriority(req);
        assert result.getStatus() == HttpStatus.OK;
    }

    @Test
    void testSortTodoDeadline() {
        MBMeTodoController controller = new MBMeTodoController(mbMeTodoService);
        MBMeSortTodoRequest req = new MBMeSortTodoRequest();
        when(mbMeTodoService.sortTodoDeadline(any(MBMeSortTodoRequest.class)))
                .thenReturn((ResponseObject) mockResponse);
        ResponseObject<?> result = controller.sortTodoDeadline(req);
        assert result.getStatus() == HttpStatus.OK;
    }

    @Test
    void testSortTodoCreatedDate() {
        MBMeTodoController controller = new MBMeTodoController(mbMeTodoService);
        MBMeSortTodoRequest req = new MBMeSortTodoRequest();
        when(mbMeTodoService.sortTodoCreatedDate(any(MBMeSortTodoRequest.class)))
                .thenReturn((ResponseObject) mockResponse);
        ResponseObject<?> result = controller.sortTodoCreatedDate(req);
        assert result.getStatus() == HttpStatus.OK;
    }

    @Test
    void testSortTodoProgress() {
        MBMeTodoController controller = new MBMeTodoController(mbMeTodoService);
        MBMeSortTodoRequest req = new MBMeSortTodoRequest();
        when(mbMeTodoService.sortTodoProgress(any(MBMeSortTodoRequest.class)))
                .thenReturn((ResponseObject) mockResponse);
        ResponseObject<?> result = controller.sortTodoProgress(req);
        assert result.getStatus() == HttpStatus.OK;
    }

    @Test
    void testSortTodoName() {
        MBMeTodoController controller = new MBMeTodoController(mbMeTodoService);
        MBMeSortTodoRequest req = new MBMeSortTodoRequest();
        when(mbMeTodoService.sortTodoName(any(MBMeSortTodoRequest.class)))
                .thenReturn((ResponseObject) mockResponse);
        ResponseObject<?> result = controller.sortTodoName(req);
        assert result.getStatus() == HttpStatus.OK;
    }

    @Test
    void testUpdateDescriptionsTodo() {
        MBMeTodoController controller = new MBMeTodoController(mbMeTodoService);
        MBMeUpdateDescriptionsTodoRequest req = new MBMeUpdateDescriptionsTodoRequest();
        when(mbMeTodoService.updateDescriptionsTodo(any(MBMeUpdateDescriptionsTodoRequest.class), any()))
                .thenReturn((ResponseObject) mockResponse);
        ResponseObject<?> result = controller.updateDescriptionsTodo(req, null);
        assert result.getStatus() == HttpStatus.OK;
    }

    @Test
    void testUpdateNameTodo() {
        MBMeTodoController controller = new MBMeTodoController(mbMeTodoService);
        MBMeUpdateNameTodoRequest req = new MBMeUpdateNameTodoRequest();
        when(mbMeTodoService.updateNameTodo(any(MBMeUpdateNameTodoRequest.class), any()))
                .thenReturn((ResponseObject) mockResponse);
        ResponseObject<?> result = controller.updateNameTodo(req, null);
        assert result.getStatus() == HttpStatus.OK;
    }

    @Test
    void testUpdateDeadlineTodo() {
        MBMeTodoController controller = new MBMeTodoController(mbMeTodoService);
        MBMeUpdateDeadlineTodoRequest req = new MBMeUpdateDeadlineTodoRequest();
        when(mbMeTodoService.updateDeadlineTodo(any(MBMeUpdateDeadlineTodoRequest.class), any()))
                .thenReturn((ResponseObject) mockResponse);
        ResponseObject<?> result = controller.updateDeadlineTodo(req, null);
        assert result.getStatus() == HttpStatus.OK;
    }

    @Test
    void testDeleteDeadlineTodo() {
        MBMeTodoController controller = new MBMeTodoController(mbMeTodoService);
        MBMeDeleteDeadlineTodoRequest req = new MBMeDeleteDeadlineTodoRequest();
        when(mbMeTodoService.deleteDeadlineTodo(any(MBMeDeleteDeadlineTodoRequest.class), any()))
                .thenReturn((ResponseObject) mockResponse);
        ResponseObject<?> result = controller.deleteDeadlineTodo(req, null);
        assert result.getStatus() == HttpStatus.OK;
    }

    @Test
    void testUpdatePriorityLevel() {
        MBMeTodoController controller = new MBMeTodoController(mbMeTodoService);
        MBMeUpdateTodoRequest req = new MBMeUpdateTodoRequest();
        when(mbMeTodoService.updatePriorityLevel(any(MBMeUpdateTodoRequest.class), any()))
                .thenReturn((ResponseObject) mockResponse);
        ResponseObject<?> result = controller.updatePriorityLevel(req, null);
        assert result.getStatus() == HttpStatus.OK;
    }

    @Test
    void testUpdateProgress() {
        MBMeTodoController controller = new MBMeTodoController(mbMeTodoService);
        MBMeUpdateProgressTodoRequest req = new MBMeUpdateProgressTodoRequest();
        when(mbMeTodoService.updateProgress(any(MBMeUpdateProgressTodoRequest.class), any()))
                .thenReturn((ResponseObject) mockResponse);
        ResponseObject<?> result = controller.updateProgress(req, null);
        assert result.getStatus() == HttpStatus.OK;
    }

}
