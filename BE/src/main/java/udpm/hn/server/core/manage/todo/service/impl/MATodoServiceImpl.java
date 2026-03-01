package udpm.hn.server.core.manage.todo.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.Nationalized;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import udpm.hn.server.core.common.base.ResponseObject;
import udpm.hn.server.core.manage.todo.model.request.MATodoCURequest;
import udpm.hn.server.core.manage.todo.model.request.MATodoSearchRequest;
import udpm.hn.server.core.manage.todo.model.request.MATodoStatisticsRequest;
import udpm.hn.server.core.manage.todo.model.response.MATodoByPhaseDTO;
import udpm.hn.server.core.manage.todo.model.response.MATodoProjection;
import udpm.hn.server.core.manage.todo.model.response.MATodoResponse;
import udpm.hn.server.core.manage.todo.repository.MAListTodoProjectRepository;
import udpm.hn.server.core.manage.todo.repository.MAStaffProjectRepository;
import udpm.hn.server.core.manage.todo.repository.MATodoRepository;
import udpm.hn.server.core.manage.todo.service.MATodoService;
import udpm.hn.server.core.member.projectdetails.repository.MBMeActivityRepository;
import udpm.hn.server.entity.*;
import udpm.hn.server.infrastructure.constant.EntityStatus;
import udpm.hn.server.infrastructure.constant.PriorityLevel;
import udpm.hn.server.infrastructure.constant.StatusTodo;
import udpm.hn.server.repository.ProjectRepository;
import udpm.hn.server.repository.TypeTodoRepository;
import udpm.hn.server.utils.Helper;
import udpm.hn.server.utils.TodoListHelper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class MATodoServiceImpl implements MATodoService {

    private final MATodoRepository maTodoRepository;

    private final MAStaffProjectRepository staffProjectRepository;

    private final MAListTodoProjectRepository maListTodoProjectRepository;

    private final ProjectRepository projectRepository;

    private final TodoListHelper todoListHelper;

    private final TypeTodoRepository typeTodoRepository;

    private final MBMeActivityRepository mbMeActivityRepository;

    private final SimpMessagingTemplate messagingTemplate;

//    @Override
//    public ResponseObject<?> getAllTodo(MATodoSearchRequest request) {
//
//        Pageable page = Helper.createPageable(request,"createDate");
//        return new ResponseObject<>(maTodoRepository.getAllTodo(page), HttpStatus.OK,"Lấy dữ liệu công việc thành công");
//    }

    @Override
    @Nationalized
    public ResponseObject<?> getAllTodo(MATodoSearchRequest request) {

        log.info("Request nhân vào khi vào get all todo :{}",request.toString());
        Pageable page = Helper.createPageable(request, "createDate");

        PriorityLevel level = null;
        if (request.getLevel() != null) {
            level = PriorityLevel.values()[Integer.valueOf(request.getLevel())];
        }

        Page<MATodoResponse> todos = maTodoRepository.getAllTodo(page, request.getIdProject(), level, request.getSearch());

        Map<String, MATodoProjection> todoMap = new LinkedHashMap<>();

        todos.forEach(todo -> {
            todoMap.computeIfAbsent(todo.getId(), id -> new MATodoProjection(todo))
                    .addParticipant(todo.getNameStaff(), todo.getImageStaff());
            todoMap.get(todo.getId()).addParticipant(todo.getNameStudent(), todo.getImageStudent());
        });


        log.info("Danh sách todo : {}",todoMap.toString());
        return new ResponseObject<>(new ArrayList<>(todoMap.values()), HttpStatus.OK, "Lấy dữ liệu thành công");
    }

    @Override
    public ResponseObject<?> fetchDataTodoByProject(String idProject) {
        return new ResponseObject<>(maTodoRepository.findAllTodoByProject(idProject),HttpStatus.OK,"Lâý dữ liệu thành công");
    }

    @Override
    @Nationalized
    public ResponseObject<?> getAllTodoStatistics(MATodoStatisticsRequest request) {
        return new ResponseObject<>(maTodoRepository.AllTodoStatistics(request.getIdProject(),request.getIdPhase()),HttpStatus.OK,"Lấy danh sách thành công");
    }

    @Override
    @Nationalized
    public ResponseObject<?> countTodoByStaffProject(MATodoStatisticsRequest request) {
        log.info(request.toString());
        return new ResponseObject<>(staffProjectRepository.countTodoByStaffProject(request.getIdProject(),request.getIdPhase()),HttpStatus.OK,"Lấy danh sách thành công");
    }

    @Override
    public ResponseObject<?> getAllStaffByTodo(MATodoSearchRequest request, String idTodo) {
        Pageable page = Helper.createPageable(request, "createdDate");
        long currentTime = Calendar.getInstance().getTimeInMillis();
        return new ResponseObject<>(maTodoRepository.getAllStaffByTodo(page, idTodo, currentTime), HttpStatus.OK, "Lấy dữ liệu thành công");
    }

    @Override
    public ResponseObject<?> getAllTodoByPhase(MATodoSearchRequest request, String idPhase) {
        Pageable page = Helper.createPageable(request, "createDate");

        Map<String, MATodoByPhaseDTO> todoByPhaseDTOMap = new LinkedHashMap<>();
        Page<MATodoResponse> todoList = maTodoRepository.getAllTodoByPhaseProject(page, idPhase, request.getSearch());
        todoList.forEach(item -> {
            todoByPhaseDTOMap
                    .computeIfAbsent(item.getId(), id -> new MATodoByPhaseDTO(item))
                    .addParticipant(
                            item.getNameStaff() != null ? item.getNameStaff() : item.getNameStudent(),
                            item.getImageStaff() != null ? item.getImageStaff() : item.getImageStudent()
                    );
        });

        return new ResponseObject<>(new ArrayList<>(todoByPhaseDTOMap.values()), HttpStatus.OK, "Lấy dữ liệu thành công");
    }



    @Override
    public ResponseObject<Todo> createTodo(MATodoCURequest request) {

        log.info("request todo: {}", request.toString());

        Optional<TypeTodo> optionalTypeTodo = typeTodoRepository.findById(request.getIdType());

        if (optionalTypeTodo.isEmpty()) {
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Không tìm thấy loại công việc");
        }

        Optional<Project> optionalProject = projectRepository.findById(request.getIdProject());

        if (optionalProject.isEmpty()) {
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Không tìm thấy project");
        }

        TodoList todoList = maListTodoProjectRepository.findByProject(request.getIdProject())
                .stream().findFirst().orElse(null);

        if (todoList == null) {
            todoList = new TodoList();
            todoList.setCode(request.getCode());
            todoList.setName(request.getName());
            todoList.setIndexTodoList(todoListHelper.genIndexTodoList(request.getIdProject()));

            todoList.setProject(optionalProject.get());
            maListTodoProjectRepository.save(todoList);
        }

        // Xử lý PriorityLevel (nếu request có giá trị hợp lệ)
        PriorityLevel priorityLevel = (request.getPriorityLevel() != null)
                ? PriorityLevel.values()[request.getPriorityLevel()]
                : null;


        Long count = maTodoRepository.findAll().stream().filter(a-> a.getTodoList().getProject().getId().equals(optionalProject.get().getId())).count();

        // Tạo mới Todo
        Todo todo = new Todo();
        todo.setCode("Scrum " + count );
        todo.setName(request.getName());
        todo.setStatusTodo(StatusTodo.CHUA_HOAN_THANH);
        todo.setPriorityLevel(priorityLevel);
        todo.setIndexTodo((short) 0);
        todo.setTypeTodo(optionalTypeTodo.get());
        todo.setPriorityLevel(PriorityLevel.THAP);
        todo.setTodoList(todoList);

        maTodoRepository.save(todo);

        Activity activity = new Activity();
        activity.setMemberCreatedId(request.getIdUser());
        activity.setProjectId(request.getIdProject());
        activity.setUrl(request.getUrlPath());

        String memberName = request.getNameUser(); // hoặc query từ idUser
        activity.setContentActionProject(
                 " đã tạo công việc mới '"
                        + todo.getName() + "' trong khối lượng công việc"
        );

        mbMeActivityRepository.save(activity);

        messagingTemplate.convertAndSend("/topic/subscribe-activity","Thêm cong việc thành công");

        return new ResponseObject<>(todo, HttpStatus.OK, "Thêm công việc thành công");
    }


    @Override
    public ResponseObject<?> deleteTodo(String id) {

        Optional<Todo> optionalTodo = maTodoRepository.findById(id);
        if (optionalTodo.isEmpty()) {
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Không tìm thấy công việc này");
        }

        Todo todo = optionalTodo.get();
        todo.setStatus(EntityStatus.INACTIVE);

        maTodoRepository.save(todo);
        return new ResponseObject<>(null, HttpStatus.OK, "Xóa thành công");

    }

    @Override
    public ResponseObject<?> dataStaffProject(String id) {
        return new ResponseObject<>(staffProjectRepository.getAllUserByProject(id), HttpStatus.OK, "Lấy dữ liệu thành viên dự án thành công");
    }

    @Override
    public ResponseObject<?> updateTodo(String id, MATodoCURequest request) {
        log.info("request vào trong update todo ===>{}", request.toString());
        Optional<Todo> optionalTodo = maTodoRepository.findById(id);

        if (optionalTodo.isEmpty()) {
            return new ResponseObject<>(null, HttpStatus.OK, "Không tìm thấy công việc này");
        }

        Todo todo = optionalTodo.get();

        if (request.getCode() != null) {
            todo.setCode(request.getCode());
        }

        if (request.getName() != null) {
            todo.setName(request.getName());
        }


        if (request.getPoint() != null) {
            todo.setStoryPoint(request.getPoint());
        }

//        if(request.getName().length() >= 250){
//            return new ResponseObject<>().error(HttpStatus.NOT_FOUND,"Tên ko được quá 250 kí tự ");
//        }



        if (request.getPriorityLevel() != null) {
            PriorityLevel priorityLevel = PriorityLevel.values()[request.getPriorityLevel()];
            todo.setPriorityLevel(priorityLevel);
        }

        maTodoRepository.save(todo);

        return new ResponseObject<>(null, HttpStatus.OK, "Cập nhật thành công");
    }


}
