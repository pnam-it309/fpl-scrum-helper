package udpm.hn.server.core.manage.project.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import udpm.hn.server.core.common.base.ResponseObject;
import udpm.hn.server.core.manage.project.model.response.MeDataDashboardLabelResponse;
import udpm.hn.server.core.manage.project.model.response.MeDataDashboardTodoListResponse;
import udpm.hn.server.core.manage.project.service.MaTodoService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class MaTodoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MaTodoService maTodoService;

    private ResponseObject<Object> response;

    @BeforeEach
    void setUp() {
        response = new ResponseObject<>();
        response.setStatus(HttpStatus.OK);
        response.setMessage("Success");
        response.setData(null);
    }

    @Test
    void testCountTodoByTodoListAllProject() throws Exception {
        // ✅ Tạo mock list dữ liệu
        List<MeDataDashboardTodoListResponse> mockList = List.of(); // hoặc List.of(new MeDataDashboardTodoListResponse(...));
        response.setData(mockList); // GÁN DATA TRƯỚC KHI RETURN

        // ✅ Khi gọi service, trả lại list giả lập
        when(maTodoService.countTodoByTodoListAllProject(anyString())).thenReturn(mockList);

        // ✅ Gọi API test
        mockMvc.perform(get("/api/v1/manage/todo/count-todo-by-todo-list-all-project")
                        .param("projectId", "1"))
                .andExpect(status().isOk());
    }


    @Test
    void testCountTodoByDueDateAllProject() throws Exception {
        when(maTodoService.countTodoByDueDateAllProject(anyString(), anyInt())).thenReturn((Integer) response.getData());
        mockMvc.perform(get("/api/v1/manage/todo/count-todo-by-due-date-all-project")
                        .param("projectId", "1")
                        .param("statusTodo", "0"))
                .andExpect(status().isOk());
    }

    @Test
    void testCountTodoByNoDueDateAllProject() throws Exception {
        when(maTodoService.countTodoByNoDueDateAllProject(anyString())).thenReturn((Integer) response.getData());
        mockMvc.perform(get("/api/v1/manage/todo/count-todo-by-no-due-date-all-project")
                        .param("projectId", "1"))
                .andExpect(status().isOk());
    }

    @Test
    void testCountTodoByLabelAllProject() throws Exception {
        List<MeDataDashboardLabelResponse> mockList = List.of(); // hoặc List.of(new MeDataDashboardLabelResponse(...))
        response.setData(mockList); // quan trọng!

        when(maTodoService.countTodoByLabelAllProject(anyString())).thenReturn((List<MeDataDashboardLabelResponse>) response.getData());

        mockMvc.perform(get("/api/v1/manage/todo/count-todo-by-label-all-project")
                        .param("projectId", "1"))
                .andExpect(status().isOk());
    }


    @Test
    void testCountTodoByNoLabelAllProject() throws Exception {
        when(maTodoService.countTodoByNoLabelAllProject(anyString())).thenReturn((Integer) response.getData());
        mockMvc.perform(get("/api/v1/manage/todo/count-todo-by-no-label-all-project")
                        .param("projectId", "1"))
                .andExpect(status().isOk());
    }

    @Test
    void testCountTodoByTodoListPeriod() throws Exception {
        // ✅ Khởi tạo mock list
        List<MeDataDashboardTodoListResponse> mockList = List.of(
                new MeDataDashboardTodoListResponse() {
                    @Override
                    public String getName() {
                        return null;
                    }

                    @Override
                    public Integer getList() {
                        return null;
                    }
                }
        );
        response.setData(mockList); // Quan trọng: tránh getData() là null

        // ✅ Gán trả về list giả lập khi gọi service
        when(maTodoService.countTodoByTodoListPhase(anyString(), anyString())).thenReturn(mockList);

        // ✅ Gọi MockMvc test API
        mockMvc.perform(get("/api/v1/manage/todo/todo-by-phase/count-todo-by-todo-list-phase")
                        .param("projectId", "1")
                        .param("phaseId", "2"))
                .andExpect(status().isOk());
    }


    @Test
    void testCountTodoByDueDatePeriod() throws Exception {
        when(maTodoService.countTodoByDueDatePhase(anyString(), anyString(), anyInt())).thenReturn((Integer) response.getData());
        mockMvc.perform(get("/api/v1/manage/todo/count-todo-by-due-date-period")
                        .param("projectId", "1")
                        .param("periodId", "2")
                        .param("statusTodo", "1"))
                .andExpect(status().isOk());
    }

    @Test
    void testCountTodoByNoDueDatePeriod() throws Exception {
        when(maTodoService.countTodoByNoDueDatePhase(anyString(), anyString())).thenReturn((Integer) response.getData());
        mockMvc.perform(get("/api/v1/manage/todo/count-todo-by-no-due-date-period")
                        .param("projectId", "1")
                        .param("periodId", "2"))
                .andExpect(status().isOk());
    }

    @Test
    void testCountTodoByPriorityLevel() throws Exception {
        when(maTodoService.countTodoByPriorityLevel(anyString(), anyInt())).thenReturn((Integer) response.getData());
        mockMvc.perform(get("/api/v1/manage/todo/count-todo-by-priority-level")
                        .param("projectId", "1")
                        .param("priorityLevel", "2"))
                .andExpect(status().isOk());
    }

    @Test
    void testCountTodoByTodoStatus() throws Exception {
        when(maTodoService.countTodoByTodoStatus(anyString())).thenReturn((ResponseObject) response);
        mockMvc.perform(get("/api/v1/manage/todo/count-todo-by-todo-status/1"))
                .andExpect(status().isOk());
    }
}
