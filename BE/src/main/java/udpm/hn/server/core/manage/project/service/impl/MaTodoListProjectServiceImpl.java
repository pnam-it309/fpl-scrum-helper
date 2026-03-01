package udpm.hn.server.core.manage.project.service.impl;

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
import udpm.hn.server.core.manage.project.model.request.MaCreateOrUpdateTodoListProject;
import udpm.hn.server.core.manage.project.model.request.MaTodoListProjectRequest;
import udpm.hn.server.core.manage.project.model.request.MaUpdateTodoProjectRequest;
import udpm.hn.server.core.manage.project.model.response.MATodoProjectResponse;
import udpm.hn.server.core.manage.project.model.response.MaPhaseProjectResponse;
import udpm.hn.server.core.manage.project.model.response.TodoProjectResponseDTO;
import udpm.hn.server.core.manage.project.repository.MaPhaseProjectRepository;
import udpm.hn.server.core.manage.project.repository.MaProjectRepository;
import udpm.hn.server.core.manage.project.repository.MaTodoListProjectRepository;
import udpm.hn.server.core.manage.project.repository.MaTodoProjectRepository;
import udpm.hn.server.core.manage.project.service.MaTodoListProjectService;
import udpm.hn.server.entity.*;
import udpm.hn.server.infrastructure.constant.EntityStatus;
import udpm.hn.server.infrastructure.constant.StatusListTodo;
import udpm.hn.server.infrastructure.log.LogEntryTodoList;
import udpm.hn.server.infrastructure.log.LogService;
import udpm.hn.server.repository.*;
import udpm.hn.server.utils.Helper;
import udpm.hn.server.utils.StatusPhase;
import udpm.hn.server.utils.TodoListHelper;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class MaTodoListProjectServiceImpl implements MaTodoListProjectService {
    private final TodoListRepository todoListRepository;

    private final MaTodoListProjectRepository maTodoListProjectRepository;

    private final MaProjectRepository maProjectRepository;

    private final MaTodoProjectRepository todoProjectRepository;

    private final LogService logService;

    private final ToDoRepository toDoRepository;

    private final ObjectMapper objectMapper;

    private final MaPhaseProjectRepository phaseRepository;

    private final PhaseTodoProjectRepository phaseTodoProjectRepository;

    private final TodoListHelper todoListHelper;

    private final ProjectRepository projectRepository;

    @Override
    public ResponseObject<?> getAllTodoList(MaTodoListProjectRequest request, String id) {
        Pageable pageable = Helper.createPageable(request, "createdDate");
        return new ResponseObject<>(
                PageableObject.of(maTodoListProjectRepository.getAllTodo(pageable, id, request)),
                HttpStatus.OK,
                "Lấy thành công danh sách task theo dự án"
        );
    }

    @Override
    public ResponseObject<?> addTodoList(MaCreateOrUpdateTodoListProject request) {
        Optional<Project> optionalProject = maProjectRepository.findById(request.getProjectId());

        if(optionalProject.isPresent()){
            TodoList todoList = new TodoList();
            todoList.setCode(request.getCodeTodoList());
            todoList.setName(request.getNameTodoList());
            todoList.setStatus(EntityStatus.ACTIVE);
            todoList.setDescribe(request.getDescribeTodoList());
            todoList.setProject(optionalProject.get());
            todoList.setIndexTodoList((byte) 0);
            todoListRepository.save(todoList);
            return new ResponseObject<>(null, HttpStatus.CREATED, "Thêm thành công");
        } else {
            return new ResponseObject<>(null, HttpStatus.NOT_ACCEPTABLE, "Không tìm thấy dự án trên");
        }
    }

    @Override
    public ResponseObject<?> updateTodpList(MaCreateOrUpdateTodoListProject request, String id) {
        Optional<TodoList> optionalTodoList = maTodoListProjectRepository.findById(id);
        if (optionalTodoList.isPresent()){
            Optional<Project> optionalProject = maProjectRepository.findById(request.getProjectId());
            if (optionalProject.isPresent()){
                TodoList updateTodoList = optionalTodoList.get();
                updateTodoList.setCode(request.getCodeTodoList());
                updateTodoList.setName(request.getNameTodoList());
                updateTodoList.setStatus(EntityStatus.ACTIVE);
                updateTodoList.setDescribe(request.getDescribeTodoList());
                updateTodoList.setIndexTodoList((byte) 0);
                maTodoListProjectRepository.save(updateTodoList);
                return new ResponseObject<>(null, HttpStatus.OK, "Sửa thành công");
            } else {
                return new ResponseObject<>(null, HttpStatus.NOT_ACCEPTABLE, "Không tìm thấy dự án");
            }
        } else {
            return new ResponseObject<>(null, HttpStatus.OK, "Task không tồn tại");
        }
    }

    @Override
    public ResponseObject<?> detailTodoList(String id) {
        return new ResponseObject<>(
                maTodoListProjectRepository.getDetailTodoListProject(id),
                HttpStatus.OK,
                "Lấy thành công chi tiết task"
        );
    }

    public ResponseObject<?> readCSV() {
        String filePath = "src/main/resources/log-accountability-index/todolist.csv";

        try {
            List<LogEntryTodoList> logEntries = logService.readCSVTodoList(filePath);
            return new ResponseObject<>(logEntries, HttpStatus.OK, "Đọc file thành công!");
        } catch (FileNotFoundException e) {
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Không tìm thấy file!");
        }
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

    @Override
    public ResponseObject<?> getAllTodoByProject(MaTodoListProjectRequest request, String id) {
        Pageable pageable = Helper.createPageable(request, "createdDate");
        Page<MATodoProjectResponse> results = maTodoListProjectRepository.getAllTodoProject(pageable, id, request);
        return new ResponseObject<>(
                PageableObject.of(results.map(this::convertToDto)),
                HttpStatus.OK,
                "Lấy thành công danh sách task theo dự án"
        );
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
                response.getDeadline(),
                response.getDescriptionsTodo(),
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





    @Override
    public ResponseObject<?> updateTodoProject(MaUpdateTodoProjectRequest request, String id) {
        Optional<Todo> optionalTodo = toDoRepository.findById(id);
        if (optionalTodo.isPresent()){
            Optional<Project> optionalProject = maProjectRepository.findById(request.getProjectId());
            if (optionalProject.isPresent()){
                Todo updateTodo = optionalTodo.get();
                updateTodo.setName(request.getNameTodo());
                updateTodo.setStatusTodo(request.getStatusTodo());
                updateTodo.setPriorityLevel(request.getPriorityLevel());
                toDoRepository.save(updateTodo);
                return new ResponseObject<>(null, HttpStatus.OK, "Sửa thành công");
            } else {
                return new ResponseObject<>(null, HttpStatus.NOT_ACCEPTABLE, "Không tìm thấy dự án");
            }
        } else {
            return new ResponseObject<>(null, HttpStatus.NOT_ACCEPTABLE, "Task không tồn tại");
        }
    }

}
