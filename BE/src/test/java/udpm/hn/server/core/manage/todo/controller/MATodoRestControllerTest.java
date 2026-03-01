package udpm.hn.server.core.manage.todo.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import udpm.hn.server.core.common.base.ResponseObject;
import udpm.hn.server.core.manage.todo.model.request.MATodoCURequest;
import udpm.hn.server.core.manage.todo.model.request.MATodoSearchRequest;
import udpm.hn.server.core.manage.todo.model.request.MATodoStatisticsRequest;
import udpm.hn.server.core.manage.todo.service.MATodoService;
import udpm.hn.server.core.manage.todo.service.MATypeTodoService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@TestPropertySource("classpath:application.properties")

public class MATodoRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MATodoService maTodoService;

    @MockitoBean
    private MATypeTodoService maTypeTodoService;

    private ResponseObject<Object> response;

    @BeforeEach
    void setUp() {
        response = new ResponseObject<>();
        response.setStatus(HttpStatus.OK);
        response.setMessage("Success");
        response.setData(null);
    }

    @Test
    void testGetAllTodo() throws Exception {
        when(maTodoService.getAllTodo(any(MATodoSearchRequest.class))).thenReturn((ResponseObject) response);

        mockMvc.perform(get("/api/v1/manage/todo"))
                .andExpect(status().isOk());
    }

    @Test
    void testListTypeTodo() throws Exception {
        when(maTypeTodoService.listTyTodo()).thenReturn((ResponseObject) response);

        mockMvc.perform(get("/api/v1/manage/todo/type"))
                .andExpect(status().isOk());
    }

    @Test
    void testFetchDataTodoByProject() throws Exception {
        when(maTodoService.fetchDataTodoByProject("1")).thenReturn((ResponseObject) response);

        mockMvc.perform(get("/api/v1/manage/todo/1"))
                .andExpect(status().isOk());
    }

    @Test
    void testFetchAllDataTodoStatistics() throws Exception {
        when(maTodoService.getAllTodoStatistics(any(MATodoStatisticsRequest.class))).thenReturn((ResponseObject) response);

        mockMvc.perform(get("/api/v1/manage/todo/statistics"))
                .andExpect(status().isOk());
    }

    @Test
    void testCountTodoByStaffProject() throws Exception {
        when(maTodoService.countTodoByStaffProject(any(MATodoStatisticsRequest.class))).thenReturn((ResponseObject) response);

        mockMvc.perform(get("/api/v1/manage/todo/statistics/count-todo-staffproject"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetAllStaffByTodo() throws Exception {
        when(maTodoService.getAllStaffByTodo(any(MATodoSearchRequest.class), eq("123"))).thenReturn((ResponseObject) response);

        mockMvc.perform(get("/api/v1/manage/todo/user-todo/123"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetAllTodoByPhase() throws Exception {
        when(maTodoService.getAllTodoByPhase(any(MATodoSearchRequest.class), eq("456"))).thenReturn((ResponseObject) response);

        mockMvc.perform(get("/api/v1/manage/todo/todo-by-phase/456"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetAllTodoStaffProject() throws Exception {
        when(maTodoService.dataStaffProject("789")).thenReturn((ResponseObject) response);

        mockMvc.perform(get("/api/v1/manage/todo/staff-project/789"))
                .andExpect(status().isOk());
    }




}

