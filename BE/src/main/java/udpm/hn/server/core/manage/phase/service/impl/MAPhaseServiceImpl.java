package udpm.hn.server.core.manage.phase.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import udpm.hn.server.core.common.base.ResponseObject;
import udpm.hn.server.core.manage.phase.model.request.*;
import udpm.hn.server.core.manage.phase.repository.MAPhaseRepository;
import udpm.hn.server.core.manage.phase.repository.MAPhaseStaffProjectRepository;
import udpm.hn.server.core.manage.phase.service.MAPhaseService;
import udpm.hn.server.core.manage.todo.model.response.MATodoResponse;
import udpm.hn.server.core.manage.todo.repository.MAListTodoProjectRepository;
import udpm.hn.server.core.manage.todo.repository.MATodoRepository;
import udpm.hn.server.core.member.projectdetails.repository.MBMeActivityRepository;
import udpm.hn.server.entity.*;
import udpm.hn.server.infrastructure.configemail.AsyncEmailService;
import udpm.hn.server.infrastructure.configemail.EmailSender;
import udpm.hn.server.infrastructure.constant.EntityStatus;
import udpm.hn.server.infrastructure.constant.StatusListTodo;
import udpm.hn.server.infrastructure.constant.StatusTodo;
import udpm.hn.server.repository.CapacityEstimateRepository;
import udpm.hn.server.repository.PhaseProjectRepository;
import udpm.hn.server.repository.PhaseTodoProjectRepository;
import udpm.hn.server.repository.ProjectRepository;
import udpm.hn.server.repository.ToDoRepository;
import udpm.hn.server.repository.VelocityRecordRepository;
import udpm.hn.server.utils.Helper;
import udpm.hn.server.utils.StatusPhase;
import udpm.hn.server.utils.TodoListHelper;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;


@Service
@RequiredArgsConstructor
@Slf4j
public class MAPhaseServiceImpl implements MAPhaseService {

    private final MAPhaseRepository maPhaseRepository;

    private final ToDoRepository toDoRepository;

    private final PhaseTodoProjectRepository phaseTodoProjectRepository;

    private final MAListTodoProjectRepository maListTodoProjectRepository;

    private final TodoListHelper todoListHelper;

    private final ProjectRepository projectRepository;

    private final MATodoRepository maTodoRepository;

    private final EmailSender emailService;

    private final MAPhaseStaffProjectRepository maPhaseStaffProjectRepository;

    private final VelocityRecordRepository velocityRecordRepository;

    private final CapacityEstimateRepository capacityEstimateRepository;


    private final PhaseProjectRepository phaseProjectRepository;

    private final AsyncEmailService asyncEmailService;

    private final MBMeActivityRepository mbMeActivityRepository;

    private final SimpMessagingTemplate messagingTemplate;


    @Override
    public ResponseObject<?> getAllPhase(MAPhaseRequest request) {
        Pageable pageable = Helper.createPageable(request, "createdDate");
        return new ResponseObject<>(maPhaseRepository.getAllPhaseProject(pageable, request.getIdProject()), HttpStatus.OK, "Lấy dự liệu giai đoạn thành công");
    }

    @Override
    public ResponseObject<?> findByPhaseStatus(String idProject) {

        Optional<PhaseProject> optionalPhaseProject = maPhaseRepository.findAll().stream().filter(ps -> ps.getStatusPhase() == StatusPhase.DANG_LAM).filter(ps -> ps.getProject().getId().equals(idProject)) // lọc thêm theo idProject
                .findFirst();

        if (optionalPhaseProject.isEmpty()) {
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Không tìm thấy");
        }

        log.info(optionalPhaseProject.get() + " DỮ LIỆU");
        return new ResponseObject<>(optionalPhaseProject.get(), HttpStatus.OK, "Tìm thấy");
    }

    @Override
    public ResponseObject<?> getAllPhase(String idProject) {
        return new ResponseObject<>(maPhaseRepository.getAllPhaseProjectStatistics(idProject), HttpStatus.OK, "Lấy dữ liệu thành công");
    }

    @Override
    public ResponseObject<?> getAllSprint(MAPhaseRequest request) {
        StatusPhase statusPhase = null;
        if (request.getStatus() != null) {
            statusPhase = StatusPhase.values()[Integer.valueOf(request.getStatus())];
        }

        Long startOfDay = null;
        Long endOfDay = null;

        // Log: Kiểm tra timestamp time nhận vào
        System.out.println("Received timestamp (time): " + request.getTime());

        // Chuyển đổi timestamp (time) thành ngày bắt đầu (00:00:00) và kết thúc (23:59:59) nếu time không null
        if (request.getTime() != null) {
            LocalDate localDate = LocalDate.ofEpochDay(request.getTime() / 86400000);  // Chuyển timestamp thành LocalDate (ngày)

            startOfDay = localDate.atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli();  // 00:00:00
            endOfDay = localDate.atTime(23, 59, 59).toInstant(ZoneOffset.UTC).toEpochMilli();  // 23:59:59

            // Log: Kiểm tra ngày bắt đầu và kết thúc tính toán từ timestamp

        }

        Pageable pageable = Helper.createPageable(request, "statusPhase");

        // Log trước khi gọi phương thức repository
        System.out.println("Calling repository with startOfDay: " + startOfDay + " and endOfDay: " + endOfDay);

        return new ResponseObject<>(maPhaseRepository.getAllSprintProject(pageable, request.getIdProject(), statusPhase, startOfDay, endOfDay, request.getSearch()), HttpStatus.OK, "Lấy dữ liệu giai đoạn thành công");
    }


    @Override
    public ResponseObject<?> getAllPhaseByProjectId(String id) {
        return new ResponseObject<>(maPhaseRepository.getAllPhaseByProjectId(id));
    }

    @Override
    public ResponseObject<?> createPhase(MACreatePhaseRequest request) {

        Optional<Project> optionalProject = projectRepository.findById(request.getIdProject());
        if (optionalProject.isEmpty()) {
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Không tìm thấy project");
        }


        long countPhase = maPhaseRepository.findAll().stream().filter(a -> a.getProject().getId().equals(optionalProject.get().getId())).count() + 1;


        long endTimeMax = maPhaseRepository.findAll().stream()
                .filter(p -> p.getProject() != null && p.getProject().getId().equals(optionalProject.get().getId()))
                .map(PhaseProject::getEndTime)
                .filter(Objects::nonNull)
                .max(Long::compareTo)
                .orElse(System.currentTimeMillis());

        String namePhase = "Giai đoạn " + (countPhase == 0 ? 1 : countPhase);

        long currentTime = endTimeMax;

        long oneWeekAgo = currentTime + (7L * 24 * 60 * 60 * 1000);
        PhaseProject phaseProject = new PhaseProject();
        phaseProject.setCode(namePhase);
        phaseProject.setName(namePhase);
        phaseProject.setStartTime(currentTime);
        phaseProject.setEndTime(oneWeekAgo);
        phaseProject.setProject(optionalProject.get());
        phaseProject.setStatusPhase(StatusPhase.CHUA_HOAN_THANH);
        maPhaseRepository.save(phaseProject);

        for (int i = 0; i < 3; i++) {
            TodoList newTodoList = new TodoList();
            newTodoList.setCode(todoListHelper.genCodeTodoList(request.getIdProject()) + "_" + phaseProject.getId());
            newTodoList.setName(i == 0 ? "Chưa làm" : i == 1 ? "Đang làm" : "Đã xong");
            Byte index = todoListHelper.genIndexTodoList(request.getIdProject());
            newTodoList.setIndexTodoList(index != null ? index : 0);
            newTodoList.setProject(optionalProject.get());
            newTodoList.setPhaseProject(phaseProject);
            newTodoList.setStatusListTodo(i == 0 ? StatusListTodo.CHUA_BAT_DAU : i == 1 ? StatusListTodo.DANG_DIEN_RA : StatusListTodo.DA_XONG);
            maListTodoProjectRepository.save(newTodoList);
        }


        int countPhaseCompleted = maPhaseRepository.countPhaseProjectByIdProject(phaseProject.getProject().getId(), StatusPhase.DA_HOAN_THANH);
        int countAllPhase = maPhaseRepository.getAllPhaseByProjectId(optionalProject.get().getId()).size();

        float progress = ((float) countPhaseCompleted / countAllPhase) * 100;

        optionalProject.get().setProgress(progress);

        projectRepository.save(optionalProject.get());

        return new ResponseObject<>(null, HttpStatus.CREATED, "Thêm giai đoạn thành công");

    }

    @Override
    public ResponseObject<?> detailPhase(String id) {
        return new ResponseObject<>(maPhaseRepository.findById(id), HttpStatus.OK, "Lấy dữ liệu thành công");
    }

    @Override
    public ResponseObject<?> createPhaseTOdoList(MACreatePhaseRequest request) {
        return null;
    }


    @Override
    public ResponseObject<?> createTodoByPhase(MATodoByPhase request) {

        log.info("Dữ liệu request khi kéo todo và phase: {}", request.toString());

        if (request.getIdTodo() == null || request.getIdTodo().isEmpty()) {
            return new ResponseObject<>(null, HttpStatus.BAD_REQUEST, "Danh sách công việc không được để trống");
        }

        Pageable page = Helper.createPageable(request, "createDate");

        Page<MATodoResponse> todoByPhase = maTodoRepository.getAllTodoByPhaseProject(page, request.getIdPhase(), "");

        Project project = projectRepository.findById(request.getIdProject()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy project"));

        PhaseProject phaseProject = maPhaseRepository.findById(request.getIdPhase()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy giai đoạn"));


        TodoList todoList = maListTodoProjectRepository.findTodoListNotStratedByProjectAndPhase(request.getIdProject(), request.getIdPhase(), StatusListTodo.CHUA_BAT_DAU).stream().findFirst().orElseGet(() -> {
            TodoList newTodoList = new TodoList();
            newTodoList.setCode(todoListHelper.genCodeTodoList(request.getIdProject()) + request.getIdPhase());
            newTodoList.setName("Chưa làm");
            Byte index = todoListHelper.genIndexTodoList(request.getIdProject());
            newTodoList.setIndexTodoList(index != null ? index : 0);
            newTodoList.setProject(project);
            newTodoList.setPhaseProject(phaseProject);
            newTodoList.setStatusListTodo(StatusListTodo.CHUA_BAT_DAU);
            return maListTodoProjectRepository.save(newTodoList);
        });

        // nếu chưa có thì bắt đầu từ 1

        List<Todo> todos = toDoRepository.findAllById(request.getIdTodo());
        if (todos.size() != request.getIdTodo().size()) {
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Một hoặc nhiều công việc không tồn tại");
        }


        todos.forEach(todo -> {
            if (!todoList.equals(todo.getTodoList())) {
                todo.setTodoList(todoList);
                todo.setIndexTodo(Short.valueOf(request.getIndex()));
                toDoRepository.save(todo);
            }

            if (!phaseTodoProjectRepository.existsDistinctByTodoAndPhaseProject(todo, phaseProject)) {
                PhaseTodoProject phaseTodoProject1 = new PhaseTodoProject();
                phaseTodoProject1.setPhaseProject(phaseProject);
                phaseTodoProject1.setTodo(todo);
                phaseTodoProjectRepository.save(phaseTodoProject1);
            }
        });

        Activity activity = new Activity();
        activity.setMemberCreatedId(request.getIdUser());
        activity.setProjectId(request.getIdProject());
        activity.setUrl(request.getUrlPath());

        String memberName = request.getNameUser(); // hoặc query từ idUser
        activity.setContentActionProject(
                  " đã kéo "
                        + request.getIdTodo().size() + " công việc vào giai đoạn '"
                        + phaseProject.getName() + "'"
        );

        mbMeActivityRepository.save(activity);

        messagingTemplate.convertAndSend("/topic/subscribe-activity","kéo công viec vào giai đoạn thành công");
        return new ResponseObject<>(null, HttpStatus.OK, "Thêm công việc vào giai đoạn thành công");
    }


    @Override
    public ResponseObject<?> updatePhase(String id, MACreatePhaseRequest request) {

        log.info("Request nhận vào bắt đầu sprint:{}", request.toString());

        Optional<PhaseProject> optionalPhaseProject = maPhaseRepository.findById(id);
        if (optionalPhaseProject.isEmpty()) {
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Không tìm thấy giai đoạn công việc này");
        }

        Optional<Project> optionalProject = projectRepository.findById(request.getIdProject());
        if (optionalProject.isEmpty()) {
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Không tìm thấy project");
        }

        Long startTime = Long.valueOf(request.getStartTime());
        Long endTime = Long.valueOf(request.getEndTime());

        // Kiểm tra endTime phải sau startTime
        if (endTime <= startTime) {
            return new ResponseObject<>(null, HttpStatus.BAD_REQUEST, "Thời gian kết thúc phải lớn hơn thời gian bắt đầu");
        }

        // Tiến hành cập nhật
        PhaseProject phaseProject = optionalPhaseProject.get();
        phaseProject.setName(request.getName());
        phaseProject.setCode(request.getCode());
        phaseProject.setStartTime(startTime);
        phaseProject.setEndTime(endTime);

        if (request.getDescriptions() != null) {
            phaseProject.setDescriptions(request.getDescriptions());
        }

        maPhaseRepository.save(phaseProject);

        Optional<PhaseProject> existsPhaseProject = maPhaseRepository.findById(id);
        log.info("{} {} {}", existsPhaseProject.get().getId(), existsPhaseProject.get().getName(), existsPhaseProject.get().getDescriptions());

        return new ResponseObject<>(null, HttpStatus.OK, "Cập nhật thành công giai đoạn");
    }


    @Override
    @Transactional
    public ResponseObject<?> updateStatusPhase(String id, Integer indexStatus) {
        log.info("Request đầu vào: {} {}", id, indexStatus);
        StatusPhase newStatus = null; // Đổi tên biến để rõ ràng hơn
        if (indexStatus != null) {
            newStatus = StatusPhase.values()[indexStatus];
        }

        Optional<PhaseProject> optionalPhaseProject = maPhaseRepository.findById(id);

        if (optionalPhaseProject.isEmpty()) {
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Không tìm thấy giai đoạn công việc này");
        }
        PhaseProject phaseProject = optionalPhaseProject.get();
        StatusPhase oldStatus = phaseProject.getStatusPhase();

        VelocityRecord velocityRecord;
        Optional<VelocityRecord> optionalVelocityRecord = velocityRecordRepository.findAll().stream()
                // Thêm null check để tránh NullPointerException nếu getPhaseProject() null
                .filter(vr -> vr.getPhaseProject() != null && vr.getPhaseProject().getId().equals(phaseProject.getId()))
                .findFirst();

        if (optionalVelocityRecord.isEmpty()) {
            velocityRecord = new VelocityRecord();
            // Đặt PhaseProject cho VelocityRecord mới
            velocityRecord.setPhaseProject(phaseProject);
        } else {
            velocityRecord = optionalVelocityRecord.get();
        }

        // Cập nhật actualPoint nếu trạng thái mới là DA_HOAN_THANH hoặc QUA_HAN
        if (newStatus == StatusPhase.DA_HOAN_THANH || newStatus == StatusPhase.QUA_HAN) {
            Double actualStoryPoint = StoryPoint(phaseProject);
            velocityRecord.setActualPoint(actualStoryPoint);
        }

        // Cập nhật estimatedPoint và xử lý email khi trạng thái chuyển sang DANG_LAM (bắt đầu)
        // Đây là nơi hợp lý để tính estimatedPoint và gửi email, thường là khi giai đoạn BẮT ĐẦU
        if (newStatus == StatusPhase.DANG_LAM && oldStatus != StatusPhase.DANG_LAM) { // Chỉ thực hiện khi chuyển trạng thái sang DANG_LAM
            Double estimatedStoryPoint = commit(phaseProject);
            velocityRecord.setEstimatedPoint(estimatedStoryPoint);

            // Gửi email
            List<String> emailStaffs = maPhaseStaffProjectRepository.getStaffByPhaseProject(id);
            List<String> emailStudents = maPhaseStaffProjectRepository.getStudentByPhaseProject(id);

            Project project = phaseProject.getProject(); // Đảm bảo Project được fetch hoặc lazily loaded

            String subject = "Dự án " + project.getName() + " có giai đoạn mới đã bắt đầu";
            String title = ""; // Có thể điền nội dung hoặc bỏ qua nếu không dùng

            String timeStart = Instant.ofEpochMilli(phaseProject.getStartTime()).atZone(ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
            String timeEnd = Instant.ofEpochMilli(phaseProject.getEndTime()).atZone(ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));

            String content = String.format("""
                    <p>Thời gian của giai đoạn %s: </p>
                    <ul>
                        <li>Bắt đầu: <strong>%s</strong></li>
                        <li>Kết thúc: <strong>%s</strong></li>
                    </ul>
                    <p>Vui lòng đăng nhập để biết thêm chi tiết.</p>
                    """, phaseProject.getName(), timeStart, timeEnd);

            asyncEmailService.sendEmailToStaffs(emailStaffs.toArray(new String[0]), subject, title, content);
            asyncEmailService.sendEmailToStudents(emailStudents.toArray(new String[0]), subject, title, content);

            log.info("Bắt đầu giai đoạn {}, đã gửi email nhân viên và sinh viên trong dự án", phaseProject.getId());
        }

        // Lưu VelocityRecord sau khi tất cả các điểm đã được cập nhật
        velocityRecordRepository.save(velocityRecord);

        // Cập nhật trạng thái của PhaseProject
        phaseProject.setStatusPhase(newStatus);
        maPhaseRepository.save(phaseProject); // Lưu PhaseProject cuối cùng, sau khi tất cả logic đã hoàn tất

        // Cập nhật progress của Project
        Optional<Project> existProjectOptional = projectRepository.findProjectById(phaseProject.getProject().getId());

        if (existProjectOptional.isPresent()) {
            Project projectToUpdate = existProjectOptional.get();
            int countPhaseCompleted = maPhaseRepository.countPhaseProjectByIdProject(projectToUpdate.getId(), StatusPhase.DA_HOAN_THANH);
            int countAllPhase = maPhaseRepository.getAllPhaseByProjectId(projectToUpdate.getId()).size();

            if (countAllPhase > 0) { // Tránh chia cho 0
                float progress = ((float) countPhaseCompleted / countAllPhase) * 100;
                projectToUpdate.setProgress(progress);
            } else {
                projectToUpdate.setProgress(0.0f); // Nếu không có giai đoạn nào, tiến độ là 0
            }
            projectRepository.save(projectToUpdate); // Lưu Project sau khi cập nhật progress
        }
//        Activity activity = new Activity();
//        activity.setMemberCreatedId(request.getIdUser());
//        activity.setProjectId(request.getIdProject());
//        activity.setUrl(request.getUrlPath());
//
//        String memberName = request.getNameUser(); // hoặc query từ idUser
//        activity.setContentActionProject(
//                memberName +   " đã bắt đầu  giai đoạn '"
//                        + phaseProject.getName()
//        );
//
//        mbMeActivityRepository.save(activity);

//        messagingTemplate.convertAndSend("/topic/subscribe-activity","Thêm cong việc thành công");


        return new ResponseObject<>(null, HttpStatus.OK, "Cập nhật thành công giai đoạn");
    }

    @Override
    public ResponseObject<?> deletePhase(String id) {

        Optional<PhaseProject> optionalPhaseProject = maPhaseRepository.findById(id);
        if (optionalPhaseProject.isEmpty()) {
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Không tìm thấy giai đoạn");
        }

        PhaseProject phaseProject = optionalPhaseProject.get();
        phaseProject.setStatus(EntityStatus.INACTIVE);

        maPhaseRepository.save(phaseProject);
        return new ResponseObject<>(null, HttpStatus.OK, "Xóa thành công giai đoạn");

    }

    @Override
    public ResponseObject<?> deleteTodoByPhase(String id) {
        Optional<Todo> optionalTodo = toDoRepository.findById(id);
        if (optionalTodo.isEmpty()) {
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Không tìm thấy công việc");
        }

//        Optional<Project> optionalProject = projectRepository.findById(request.getIdProject());
//        if(optionalProject.isEmpty()){
//            return new ResponseObject<>(null,HttpStatus.NOT_FOUND,"không tìm thấy project");
//        }
//
//        TodoList todoList = maListTodoProjectRepository.findByProject(request.getIdProject())
//                .stream().findFirst().orElse(null);
//
//        // Nếu chưa có TodoList, tạo mới
//        if (todoList == null) {
//            todoList = new TodoList();
//            todoList.setCode(optionalTodo.get().getCode());
//            todoList.setName(optionalTodo.get().getName());
//            todoList.setIndexTodoList(todoListHelper.genIndexTodoList(request.getIdProject()));
//
//            todoList.setProject(optionalProject.get());
//            maListTodoProjectRepository.save(todoList);
//        }
//

        Optional<PhaseTodoProject> optionalPhaseTodoProject = phaseTodoProjectRepository.findByTodo(optionalTodo.get());
        if (optionalPhaseTodoProject.isEmpty()) {
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Không tìm thấy giai đoạn ");
        }
        phaseTodoProjectRepository.delete(optionalPhaseTodoProject.get());

        return new ResponseObject<>(null, HttpStatus.OK, "Xoá thành công");

    }


    private Double commit(PhaseProject phaseProject) {

        Double velocity = tb(phaseProject.getProject().getId());

        Double totalWorkdayForPhase = capacityEstimateRepository.findAll().stream()
                .filter(v -> v.getPhaseProject() != null && v.getPhaseProject().getId().equals(phaseProject.getId())) // Compare by ID for correctness
                .filter(v -> v.getStatus() == EntityStatus.ACTIVE)
                .mapToDouble(v -> v.getWorkday() != null ? v.getWorkday() : 0.0)
                .sum();

        log.info("Tổng số giai ngày làm việc ước luognjw trong giai đoạn : ===> {}", totalWorkdayForPhase);

        Double estimatedPoints = velocity * totalWorkdayForPhase;

        return estimatedPoints;
    }


    private Double tb(String id) {
        Float actualPoint = velocityRecordRepository.avgActualPoint(id);
        Float actualWorkingDay = velocityRecordRepository.avgActualWorkingDay(id);

        Double velocity = 0.0;
        if (actualWorkingDay != null && actualWorkingDay > 0) {
            velocity = (double) actualPoint / actualWorkingDay == 0 ? 1.0 : (double) actualPoint / actualWorkingDay;
        } else {
            velocity = 1.0;
            log.warn("Actual working day is null or zero for project ID: " + ". Velocity will be 0.");
        }

        return velocity;
    }

    private Double StoryPoint(PhaseProject pp) {
        Set<StatusTodo> validPhases = Set.of(StatusTodo.DA_HOAN_THANH, StatusTodo.HOAN_THANH_SOM, StatusTodo.QUA_HAN);

        List<Todo> todos = toDoRepository.findAll().stream()
                .filter(t -> t.getTodoList() != null && t.getTodoList().getPhaseProject() != null)
                .filter(t -> t.getTodoList().getPhaseProject().getId().equals(pp.getId()))
                .filter(t -> t.getStatusTodo() != null && validPhases.contains(t.getStatusTodo()))
                .toList();
        log.info("Danh sách todos : ==> {}", todos);

        Double storyPointUL = todos.stream()
                .map(Todo::getStoryPoint)
                .filter(Objects::nonNull)
                .map(Short::doubleValue)
                .reduce(0.0, Double::sum);

        log.info("Tổng số story point thực tế đạt đc ===>{}", storyPointUL);

        return storyPointUL;
    }

}
