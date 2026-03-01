package udpm.hn.server.core.member.phase.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import udpm.hn.server.core.common.base.PageableObject;
import udpm.hn.server.core.common.base.ResponseObject;
import udpm.hn.server.core.manage.project.model.response.MATodoProjectResponse;
import udpm.hn.server.core.manage.project.model.response.MaPhaseProjectResponse;
import udpm.hn.server.core.manage.project.model.response.TodoProjectResponseDTO;
import udpm.hn.server.core.manage.project.repository.MaPhaseProjectRepository;
import udpm.hn.server.core.manage.project.repository.MaTodoListProjectRepository;
import udpm.hn.server.core.manage.project.repository.MaTodoProjectRepository;
import udpm.hn.server.core.member.phase.model.request.MBPhaseRequest;
import udpm.hn.server.core.member.phase.repository.MBPhaseRepository;
import udpm.hn.server.core.member.phase.service.MBPhaseService;
import udpm.hn.server.entity.*;
import udpm.hn.server.infrastructure.constant.EntityStatus;
import udpm.hn.server.infrastructure.constant.StatusListTodo;
import udpm.hn.server.repository.*;
import udpm.hn.server.utils.Helper;
import udpm.hn.server.utils.StatusPhase;
import udpm.hn.server.utils.TodoListHelper;
import udpm.hn.server.utils.UserContextHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Service
@RequiredArgsConstructor
@Slf4j
public class MBPhaseServiceImpl implements MBPhaseService {

    private final MBPhaseRepository maPhaseRepository;

    private final UserContextHelper userContextHelper;

    private final ObjectMapper objectMapper;

    private final MaPhaseProjectRepository phaseRepository;

    private final MaTodoProjectRepository todoProjectRepository;

    private final MaTodoListProjectRepository maTodoListProjectRepository;

    private final TodoListRepository todoListRepository;

    private final PhaseTodoProjectRepository phaseTodoProjectRepository;

    private final ToDoRepository toDoRepository;

    private final TodoListHelper todoListHelper;

    private final ProjectRepository projectRepository;

    @Override
    public ResponseObject<?> getAllPhase(MBPhaseRequest request, String idProject) {
        Pageable pageable = Helper.createPageable(request,"createDate");
        return new ResponseObject<>(maPhaseRepository.getAllPhaseProject(pageable,request.getIdProject(), request),
                HttpStatus.OK,
                "Lấy dự liệu giai đoạn thành công");
    }

    @Override
    public ResponseObject<?> detail(String idProject) {
        return new ResponseObject<>(
                maPhaseRepository.getPhaseId(idProject),
                HttpStatus.OK,
                "Lấy dữ liệu thành công"
        );
    }

    @Override
    public ResponseObject<?> getAllTodoByProject(MBPhaseRequest request, String id) {
        Pageable pageable = Helper.createPageable(request, "createdDate");
        Page<MATodoProjectResponse> results = maPhaseRepository.getAllTodoProjectMemBer(pageable, id, request, userContextHelper);
        return new ResponseObject<>(
                PageableObject.of(results.map(this::convertToDto)),
                HttpStatus.OK,
                "Lấy thành công danh sách task theo dự án"
        );
    }

    @Override
    public ResponseObject<?> getAllPhase(String id) {
        List<StatusPhase> excludedStatuses = List.of(
                StatusPhase.DA_HOAN_THANH,
                StatusPhase.QUA_HAN
        );

        List<MaPhaseProjectResponse> phaseList = phaseRepository.getALl(id, excludedStatuses);

        return new ResponseObject<>(
                phaseList,
                HttpStatus.OK,
                "Lấy thành công danh sách task theo dự án"
        );
    }


    @Override
    public ResponseObject<?> updateTodoByPhaseProject(String todoId, String newPhaseId, String projectId) {
        Optional<PhaseProject> optionalPhaseProject = phaseRepository.findById(newPhaseId);
        if (!optionalPhaseProject.isPresent()) {
            return new ResponseObject<>(null, HttpStatus.BAD_REQUEST, "PhaseProject không tồn tại");
        }

        Optional<Project> optionalProject = projectRepository.findById(projectId);
        if (!optionalProject.isPresent()) {
            return new ResponseObject<>(null, HttpStatus.BAD_REQUEST, "Project không tồn tại");
        }
        Optional<Todo> optionalTodo = toDoRepository.findById(todoId);

        Optional<PhaseTodoProject> optionalPhaseTodoProject = phaseTodoProjectRepository.findByTodoId(todoId);
        if (optionalPhaseTodoProject.isPresent()) {
            PhaseTodoProject updateTodo = optionalPhaseTodoProject.get();
            updateTodo.setPhaseProject(optionalPhaseProject.get());
            phaseTodoProjectRepository.save(updateTodo);
        } else {
            PhaseTodoProject phaseTodoProject = new PhaseTodoProject();
            phaseTodoProject.setPhaseProject(optionalPhaseProject.get());
            phaseTodoProject.setTodo(optionalTodo.get());
            phaseTodoProjectRepository.save(phaseTodoProject);
        }

        if (optionalTodo.isPresent()) {
            Todo todo = optionalTodo.get();
            Optional<TodoList> optionalTodoList = todoListRepository.findByPhaseProjectId(newPhaseId);
            TodoList todoList;

            if (optionalTodoList.isPresent()) {
                todoList = optionalTodoList.get();
            } else {
                todoList = new TodoList();
                todoList.setPhaseProject(optionalPhaseProject.get());
                todoList.setName("Chưa làm");
                todoList.setCode(UUID.randomUUID().toString().substring(0, 6).toUpperCase());
                todoList.setStatusListTodo(StatusListTodo.CHUA_BAT_DAU);
                todoList.setStatus(EntityStatus.ACTIVE);
                todoList.setIndexTodoList(todoListHelper.genIndexTodoListWHEREIDPHASE(projectId,newPhaseId));
                todoList.setProject(optionalProject.get());
                todoList = todoListRepository.save(todoList);
            }

            todo.setTodoList(todoList);
            toDoRepository.save(todo);
        }

        return new ResponseObject<>(null, HttpStatus.OK, "Cập nhật thành công");
    }


    private TodoProjectResponseDTO convertToDto(MATodoProjectResponse response) {
        List<Student> students = new ArrayList<>();
        List<Staff> staffList = new ArrayList<>();

        try {
            if (response.getStudents() != null) {
                students = objectMapper.readValue(response.getStudents(), new com.fasterxml.jackson.core.type.TypeReference<>() {});
            }
            if (response.getStaff() != null) {
                staffList = objectMapper.readValue(response.getStaff(), new TypeReference<>() {});
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return new TodoProjectResponseDTO(
                response.getOrderNumber(),
                response.getIdTodo(),
                response.getNameTodo(),
                response.getDescriptionsTodo(),
                response.getDeadline(),
                response.getStatusTodo(),
                response.getNamePhaseProject(),
                response.getPicture(),
                response.getEmail(),
                response.getPriorityLevel(),
                response.getCreatedDate(),
                response.getLastModifiedDate(),
                response.getPhaseId(),
                response.getProjectId(),
                students,
                staffList
        );
    }

}
