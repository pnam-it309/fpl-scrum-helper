package udpm.hn.server.core.manage.project.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import udpm.hn.server.core.common.base.ResponseObject;
import udpm.hn.server.core.manage.project.model.request.MaCreateOrUpdateTodoListProject;
import udpm.hn.server.core.manage.project.model.request.MaTodoListProjectRequest;
import udpm.hn.server.core.manage.project.model.request.MaUpdateTodoProjectRequest;
import udpm.hn.server.core.manage.project.service.MaTodoListProjectService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class MaTodoListProjectRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MaTodoListProjectService maTodoListProjectService;

    private ResponseObject<Object> response;

    @BeforeEach
    void setUp() {
        response = new ResponseObject<>();
        response.setStatus(HttpStatus.OK);
        response.setMessage("Success");
        response.setData(null);
    }

    @Test
    void testGetAllTodoList() throws Exception {
        when(maTodoListProjectService.getAllTodoList(any(), anyString())).thenReturn((ResponseObject) response);

        mockMvc.perform(get("/api/v1/manage/todo-list/get-all-todolist/1"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetAllPhase() throws Exception {
        when(maTodoListProjectService.getAllPhase("1")).thenReturn((ResponseObject) response);

        mockMvc.perform(get("/api/v1/manage/todo-list/get-all-phase/1"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetAllTodoProject() throws Exception {
        when(maTodoListProjectService.getAllTodoByProject(any(), anyString())).thenReturn((ResponseObject) response);

        mockMvc.perform(get("/api/v1/manage/todo-list/get-all-todo/1"))
                .andExpect(status().isOk());
    }

    @Test
    void testAddTodoList() throws Exception {
        MaCreateOrUpdateTodoListProject request = new MaCreateOrUpdateTodoListProject();

        when(maTodoListProjectService.addTodoList(any())).thenReturn((ResponseObject) response);

        mockMvc.perform(post("/api/v1/manage/todo-list/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void testUpdateTodoList() throws Exception {
        MaCreateOrUpdateTodoListProject request = new MaCreateOrUpdateTodoListProject();

        when(maTodoListProjectService.updateTodpList(any(), anyString())).thenReturn((ResponseObject) response);

        mockMvc.perform(put("/api/v1/manage/todo-list/update/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void testDetailTodoList() throws Exception {
        when(maTodoListProjectService.detailTodoList("1")).thenReturn((ResponseObject) response);

        mockMvc.perform(get("/api/v1/manage/todo-list/detail/1"))
                .andExpect(status().isOk());
    }

    @Test
    void testReadCSV() throws Exception {
        when(maTodoListProjectService.readCSV()).thenReturn((ResponseObject) response);

        mockMvc.perform(get("/api/v1/manage/todo-list/read/fileLog"))
                .andExpect(status().isOk());
    }

    @Test
    void testUpdateTodo() throws Exception {
        MaUpdateTodoProjectRequest request = new MaUpdateTodoProjectRequest();

        when(maTodoListProjectService.updateTodoProject(any(), anyString())).thenReturn((ResponseObject) response);

        mockMvc.perform(put("/api/v1/manage/todo-list/update-todo/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void testUpdatePhaseTodo() throws Exception {
        when(maTodoListProjectService.updateTodoByPhaseProject("todo1", "phase2", "project3")).thenReturn((ResponseObject) response);

        mockMvc.perform(put("/api/v1/manage/todo-list/update-phase-todo/todo1/phase2/project3"))
                .andExpect(status().isOk());
    }




}