package udpm.hn.server.core.manage.todotable.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import udpm.hn.server.core.common.base.ResponseObject;
import udpm.hn.server.core.manage.todotable.service.TodoByPhaseProjectService;

import java.io.FileNotFoundException;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@TestPropertySource("classpath:application.properties")
public class TodoByPhaseProjectControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TodoByPhaseProjectService todoByPhaseProjectService;

    private ResponseObject<?> response;

    String idProject = "123";


    @BeforeEach
    void setUp() {
        response = new ResponseObject<>();
        response.setStatus(HttpStatus.OK);
        response.setMessage("Success");
        response.setData(null);
    }

    @Test
    void testGetAllTodoProject() throws Exception {
        when(todoByPhaseProjectService.getAllTodoByPhaseProject(anyString(), anyString(), anyInt(), anyInt()))
                .thenReturn((ResponseObject)response);

        mockMvc.perform(get("/api/v1/manage/todo-list/get-todo-phase/123/456")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetAllTodoListByPhaseProject() throws Exception {
        when(todoByPhaseProjectService.getAllTodoListByPhaseProject(anyString(), anyString()))
                .thenReturn((ResponseObject)response);

        mockMvc.perform(get("/api/v1/manage/todo-list/get-todolist-phase/123/456"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetIndex() throws Exception {
        when(todoByPhaseProjectService.getIndex(anyString()))
                .thenReturn((ResponseObject)response);

        mockMvc.perform(get("/api/v1/manage/todo-list/index-todo/789"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetChangeHistory() throws Exception {

        when(todoByPhaseProjectService.getLogsImportTodo(anyInt(), anyInt(), idProject))
                .thenReturn((ResponseObject)response);

        mockMvc.perform(get("/api/v1/manage/todo-list/history")
                        .param("page", "0")
                        .param("size", "50"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetChangeHistory_FileNotFoundException() throws Exception {
        doThrow(new FileNotFoundException("file not found"))
                .when(todoByPhaseProjectService).getLogsImportTodo(anyInt(), anyInt(), idProject);

        mockMvc.perform(get("/api/v1/manage/todo-list/history")
                        .param("page", "0")
                        .param("size", "50"))
                .andExpect(status().isInternalServerError());
    }
}
