package udpm.hn.server.core.manage.todo.controller;

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
import udpm.hn.server.core.manage.todo.service.MATodoVoteService;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@TestPropertySource("classpath:application.properties")

public class MATodoVoteRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MATodoVoteService maTodoVoteService;

    private ResponseObject<Object> response;

    @BeforeEach
    void setUp() {
        response = new ResponseObject<>();
        response.setStatus(HttpStatus.OK);
        response.setMessage("Success");
        response.setData(null);
    }

    @Test
    void testGetAllTodoVote() throws Exception {
        when(maTodoVoteService.getAllVote()).thenReturn((ResponseObject) response);

        mockMvc.perform(get("/api/v1/manage/todo/vote"))
                .andExpect(status().isOk());
    }

    @Test
    void testCountTodoVotedByPriorityLevel() throws Exception {
        when(maTodoVoteService.countTodoVotedByPriorityLevel(anyString())).thenReturn((ResponseObject) response);

        mockMvc.perform(get("/api/v1/manage/todo/vote/count-todo-voted-by-priority-level/1"))
                .andExpect(status().isOk());
    }

    @Test
    void testFindAllVotedTodos() throws Exception {
        when(maTodoVoteService.findAllVotedTodos(anyString())).thenReturn((ResponseObject) response);

        mockMvc.perform(get("/api/v1/manage/todo/vote/find-all-voted-todos/1"))
                .andExpect(status().isOk());
    }
    
}

