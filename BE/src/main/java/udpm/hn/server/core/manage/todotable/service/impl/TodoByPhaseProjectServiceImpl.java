package udpm.hn.server.core.manage.todotable.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import udpm.hn.server.core.common.base.PageableObject;
import udpm.hn.server.core.common.base.ResponseObject;
import udpm.hn.server.core.manage.todotable.model.response.TodoListIndexTodoResponse;
import udpm.hn.server.core.manage.todotable.model.response.TodoListPhaseResponse;
import udpm.hn.server.core.manage.todotable.reposiroty.TodoTableByPhaseProjectRepository;
import udpm.hn.server.core.manage.todotable.model.response.TodoByPhaseProjectResponse;
import udpm.hn.server.core.manage.todotable.model.response.TodoPhaseProjectDTO;
import udpm.hn.server.core.manage.todotable.service.TodoByPhaseProjectService;
import udpm.hn.server.entity.*;
import udpm.hn.server.infrastructure.job.todolist.model.response.HistoryImportTodoResponse;
import udpm.hn.server.repository.*;
import udpm.hn.server.utils.UserContextHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.OptionalInt;

@Service
@Slf4j
@RequiredArgsConstructor
public class TodoByPhaseProjectServiceImpl implements TodoByPhaseProjectService {

    private final ObjectMapper objectMapper;

    private final TodoTableByPhaseProjectRepository todoTableByPhaseProjectRepository;

    private final TodoListRepository todoListRepository;

    private final UserContextHelper userContextHelper;

    private final HistoryImportTodoRepository historyImportTodoRepository;

    private final RoleRepository roleRepository;

    private final StaffRepository staffRepository;

    @Override
    public ResponseObject<?> getAllTodoByPhaseProject(String idProject, String idPhase, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<TodoByPhaseProjectResponse> results =
                todoTableByPhaseProjectRepository.getAllTodoByPhaseProject(pageable, idProject, idPhase);

        return new ResponseObject<>(
                PageableObject.of(results.map(this::convertToDto)),
                HttpStatus.OK,
                "Lấy thành công danh sách task theo dự án"
        );
    }


    @Override
    public ResponseObject<?> getAllTodoListByPhaseProject(String idProject, String idPhase) {
        List<TodoListPhaseResponse> allTodoListByPhaseProject = todoListRepository.getAllTodoListByPhaseProject(idProject, idPhase);

        return new ResponseObject<>(
                allTodoListByPhaseProject,
                HttpStatus.OK,
                "Lấy thành công danh sách theo dự án"
        );
    }

    @Override
    public ResponseObject<?> getIndex(String id) {
        List<TodoListIndexTodoResponse> indexTodoList = todoTableByPhaseProjectRepository.getIndexTodo(id);

        // Tìm giá trị indexTodo lớn nhất
        OptionalInt maxIndex = indexTodoList.stream()
                .mapToInt(item -> Integer.parseInt(item.getIndexTodo()))
                .max();

        return new ResponseObject<>(
                maxIndex.isPresent() ? String.valueOf(maxIndex.getAsInt()) : null,
                HttpStatus.OK,
                "Lấy thành công giá trị indexTodo lớn nhất theo dự án"
        );
    }

    private TodoPhaseProjectDTO convertToDto(TodoByPhaseProjectResponse response) {
        List<Student> students = new ArrayList<>();
        List<Staff> staffList = new ArrayList<>();
        List<LabelProject> labelProjects = new ArrayList<>();

        try {
            if (response.getStudents() != null) {
                students = objectMapper.readValue(response.getStudents(), new com.fasterxml.jackson.core.type.TypeReference<>() {});
            }
            if (response.getStaff() != null) {
                staffList = objectMapper.readValue(response.getStaff(), new TypeReference<>() {});
            }

            if (response.getLabel() != null) {
                labelProjects = objectMapper.readValue(response.getLabel(), new TypeReference<>() {});
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return new TodoPhaseProjectDTO(
                response.getOrderNumber(),
                response.getIdTodo(),
                response.getIndexTodo(),
                response.getTodoName(),
                response.getDeadlineTodo(),
                response.getCompletionTime(),
                response.getLabelName(),
                response.getCreatedDate(),
                response.getTodoListId(),
                response.getTodoListName(),
                response.getStatusTodo(),
                response.getPriorityLevel(),
                students,
                staffList,
                labelProjects,
                response.getTypeTodo()
        );
    }

    @Override
    public ResponseObject<?> getLogsImportTodo(int page, int size, String idProject) {
        if (page < 1) {
            page = 1;
        }

        String staffId = userContextHelper.getCurrentUserId();
        List<Role> roles = roleRepository.findRoleByStaff(staffId);
        String email = staffRepository.getEmailFpt(staffId);

        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("createdDate").descending());

        Page<HistoryImportTodo> historyPage = historyImportTodoRepository.findByProjectIdAndEmail(idProject,email,pageable);

        Page<HistoryImportTodoResponse> responsePage = historyPage.map(history ->
                new HistoryImportTodoResponse(
                        history.getId(),
                        email,
                        history.getMessage(),
                        roles,
                        history.getCreatedDate(),
                        idProject
                )
        );

        return ResponseObject.successForward(
                PageableObject.of(responsePage),
                "Lấy lịch sử import sinh viên thành công"
        );
    }

}
