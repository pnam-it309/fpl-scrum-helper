package udpm.hn.server.core.member.projectdetails.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.server.ResponseStatusException;
import udpm.hn.server.core.common.base.ResponseObject;
import udpm.hn.server.core.common.base.TodoObject;
import udpm.hn.server.core.member.projectdetails.model.request.*;
import udpm.hn.server.core.member.projectdetails.model.response.*;
import udpm.hn.server.core.member.projectdetails.repository.*;
import udpm.hn.server.core.member.projectdetails.service.MBMeTodoService;
import udpm.hn.server.entity.Activity;
import udpm.hn.server.entity.Todo;
import udpm.hn.server.entity.TodoChild;
import udpm.hn.server.entity.TodoList;
import udpm.hn.server.infrastructure.constant.*;
import udpm.hn.server.utils.DateConverter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class MBMeTodoServiceImpl implements MBMeTodoService {

    private final SimpMessagingTemplate messagingTemplate;

    private final MBMeTodoRepository mbMeTodoRepository;

    private final MBMeTodoListRepository mbMeTodoListRepository;

    private final MBMeTodoChildRepository mbMeTodoChildRepository;

    private final MBMeAssignRepository mbMeAssignRepository;

    private final MBMeLabelRepository meLabelRepository;

    private final MBMeImageRepository mbMeImageRepository;

    private final MBMeActivityRepository mbMeActivityRepository;

    private final MBMePhaseProjectRepository mbMePhaseProjectRepository;

    @Override
    public ResponseObject<?> getAllBoard(MBMeFilterTodoRequest request) {
        List<TodoList> listTodoList = mbMeTodoRepository.findAllByProjectIdAndPhaseIdOrderByIndexTodoList(request.getProjectId(),request.getIdPhase());
        List<MBMeBoardResponse> listBoard = new ArrayList<>();
        for (TodoList td : listTodoList) {
            MBMeBoardResponse meBoardResponse = new MBMeBoardResponse();
            meBoardResponse.setId(td.getId());
            meBoardResponse.setCode(td.getCode());
            meBoardResponse.setIndexTodoList(td.getIndexTodoList());
            meBoardResponse.setName(td.getName());
            if (request.getIdPhase() != null && request.getIdPhase().equals("undefined")) {
                meBoardResponse.setTasks(new ArrayList<>());
            } else {
                request.setIdTodoList(td.getId());
                List<MBMeTodoResponse> listMeTodoResponse = mbMeTodoRepository.getToDoByPeriodAndIdTodoList(request);
                meBoardResponse.setTasks(convertTodoResponses(listMeTodoResponse));
            }
            listBoard.add(meBoardResponse);
        }

        return new ResponseObject<>(listBoard, HttpStatus.OK,"lấy bảng thành công");
    }

    public List<MBMeConvertTodoResponse> convertTodoResponses(List<MBMeTodoResponse> listMeTodoResponse) {
        List<MBMeConvertTodoResponse> listMeConvertTodoResponse = new ArrayList<>();
        for (MBMeTodoResponse td : listMeTodoResponse) {
            MBMeConvertTodoResponse meConvertTodoResponse = new MBMeConvertTodoResponse();
            meConvertTodoResponse.setId(td.getId());
            meConvertTodoResponse.setCode(td.getCode());
            meConvertTodoResponse.setName(td.getName());
            meConvertTodoResponse.setPriorityLevel(td.getPriorityLevel());
            meConvertTodoResponse.setDeadline(td.getDeadline());
            meConvertTodoResponse.setCompletionTime(td.getCompletionTime());
            meConvertTodoResponse.setIndexTodo(td.getIndexTodo());
            meConvertTodoResponse.setProgress(td.getProgress());
            meConvertTodoResponse.setImageId(td.getImageId());
            meConvertTodoResponse.setUrlImage(td.getUrlImage());
            meConvertTodoResponse.setTypeTodo(td.getTypeTodo());
            // công việc con todoChild ( đã hoàn thành và chưa )
            meConvertTodoResponse.setNumberTodoComplete(mbMeTodoRepository.countCompletedByTodoChildId(td.getId()));
            meConvertTodoResponse.setNumberTodo(mbMeTodoRepository.countByTodoChildId(td.getId()));
            meConvertTodoResponse.setTodoListId(td.getTodoListId());
//            if (td.getNumberTodo() != 0) {
//                //tiến độ (%) = (số công việc con hoàn thành / tổng số công việc con) * 100
//                meConvertTodoResponse.setProgressOfTodo(
//                        (int) Math.ceil(((double) td.getNumberTodoComplete() / (double) td.getNumberTodo()) * 100)
//                );
//            } else {
//                //
//                meConvertTodoResponse.setProgressOfTodo((int) td.getProgress());
//            }

            if (td.getDeadline() != null) {
                meConvertTodoResponse.setDeadlineString(DateConverter.convertDateToStringTodo(td.getDeadline()));
            }

            List<MBMeLabelResponse> listLabel = meLabelRepository.getAllLabelByIdTodo(td.getId());
            List<MBMeConvertLabelResponse> listMeConvertLabelResponse = new ArrayList<>();
            for (MBMeLabelResponse lb : listLabel) {
                MBMeConvertLabelResponse meConvertLabelResponse = new MBMeConvertLabelResponse();
                meConvertLabelResponse.setId(lb.getId());
                meConvertLabelResponse.setCode(lb.getCode());
                meConvertLabelResponse.setName(lb.getName());
                meConvertLabelResponse.setColorLabel(lb.getColorLabel());
                listMeConvertLabelResponse.add(meConvertLabelResponse);
            }
            meConvertTodoResponse.setLabels(listMeConvertLabelResponse);
            meConvertTodoResponse.setMemberTodo( mbMeAssignRepository.getAllMemberByIdTodo(td.getId()));
            meConvertTodoResponse.setNumberCommnets(mbMeTodoRepository.countCommentByIdTodo(td.getId()));
            meConvertTodoResponse.setNumberAttachments(mbMeTodoRepository.countResourceByIdTodo(td.getId()));
            listMeConvertTodoResponse.add(meConvertTodoResponse);
        }
        return listMeConvertTodoResponse;
    }

    @Override
    @Transactional
    public ResponseObject<?> updateIndexTodoViewTable(MBMeUpdateIndexTodoRequest request) {

        //Nếu thẻ không di chuyển (cùng cột và cùng vị trí), thì không cần làm gì → ném lỗi luôn.
        if (request.getIdTodoListOld().equals(request.getIdTodoListNew()) && request.getIndexBefore() == request.getIndexAfter()) {
            return new ResponseObject<>(null, HttpStatus.CONFLICT, "Không xác định");
        }
        // Kiểm tra Todo có tồn tại trong db  không
        Optional<Todo> todoFind = mbMeTodoRepository.findById(request.getIdTodo());
        if (!todoFind.isPresent()) {
            return new ResponseObject<>(null, HttpStatus.CONFLICT, "Todo không tồn tại");
        }
        //Nếu người dùng kéo thẻ trong cùng 1 cột
        if (request.getIdTodoListOld().equals(request.getIdTodoListNew())) {
            //Nếu kéo xuống dưới
            if (request.getIndexBefore() <= request.getIndexAfter()) {
                mbMeTodoRepository.updateIndexTodoDecs(request.getIndexBefore(), request.getIndexAfter(), request.getPhaseId(), request.getIdTodoListOld());
            } else {
                // Nếu kéo lên trên
                mbMeTodoRepository.updateIndexTodoAsc(request.getIndexBefore(), request.getIndexAfter(), request.getPhaseId(), request.getIdTodoListOld());
            }
            //Cuối cùng trả về TodoAndTodoListObject chứa todo đã update và info index mới.
            todoFind.get().setIndexTodo(request.getIndexAfter());
            MBTodoAndTodoListObject todoAndTodoListObject =
                    MBTodoAndTodoListObject.builder()
                            .data(mbMeTodoRepository.save(todoFind.get())).
                            idTodoListOld(request.getIdTodoListOld()).
                            indexBefore((int) request.getIndexBefore()).
                            indexAfter(Integer.valueOf(request.getIndexAfter()))
                            .sessionId(request.getSessionId())
                            .build();

            return new ResponseObject<>(todoAndTodoListObject, HttpStatus.OK, "Update index todo success");
        } else // Nếu kéo sang cột khác
        {
            //Cập nhật lại todoListId và indexTodo mới cho thẻ.
            //Ghi lại Activity lịch sử kéo thẻ để log.
            Activity activity = new Activity();

            activity.setTodoId(request.getIdTodo());
            activity.setTodoListId(request.getIdTodoListNew());
            activity.setProjectId(request.getProjectId());
            activity.setMemberCreatedId(request.getIdUser());
            activity.setUrl(request.getUrlPath());
            activity.setContentAction("đã kéo thẻ này từ " + request.getNameTodoListOld() + " tới " + request.getNameTodoListNew());
            activity.setContentActionProject("đã di chuyển thẻ "+todoFind.get().getName()+" từ danh sách "+request.getNameTodoListOld()+" tới danh sách "+request.getNameTodoListNew());
            //Đếm số thẻ trong cột đích (countTodo) để xử lý index:
            Short countTodo = mbMeTodoRepository.countTodoInTodoList(request.getIdTodoListNew(), request.getPhaseId());
            todoFind.get().setIndexTodo(request.getIndexAfter());
            todoFind.get().setTodoList(mbMeTodoListRepository.findById(request.getIdTodoListNew()).get());
            if (countTodo == 0) {
                log.info("vào đay nè1 getIndexAfter:"+request.getIndexAfter());
                //Dù countTodo == 0 hay không thì bạn đều gọi:(=0 l chưa có thẻ nào cả trong cột)
                // dịch lại index của các thẻ ở cột cũ sau khi thẻ bị kéo đi.
                mbMeTodoRepository.updateIndexTodoInTodoListOld(request.getIdTodoListOld(), request.getPhaseId(), request.getIndexBefore());
            } else {
                // dịch lại index của các thẻ ở cột cũ sau khi thẻ bị kéo đi.
                mbMeTodoRepository.updateIndexTodoInTodoListOld(request.getIdTodoListOld(), request.getPhaseId(), request.getIndexBefore());
                // Và nếu cột mới có thẻ sẵn, cũng cần dịch index ở cột mới:

                mbMeTodoRepository.updateIndexTodoInTodoListNew(
                        request.getIdTodoListNew(),
                        request.getPhaseId(),
                        request.getIndexAfter(),
                        request.getIdTodo()
                );


            }

            Todo newTodo = mbMeTodoRepository.save(todoFind.get());
            MBTodoAndTodoListObject mbTodoAndTodoListObject =MBTodoAndTodoListObject.builder().data(newTodo)
                    .dataActivity(mbMeActivityRepository.save(activity))
                    .idTodoListOld(request.getIdTodoListOld())
                    .indexBefore((int) request.getIndexBefore())
                    .indexAfter((int) request.getIndexAfter())
                    .sessionId(request.getSessionId())
                    .build();
            messagingTemplate.convertAndSend("/topic/subscribe-activity","Cập nhat thanh cong index todo");
            return new ResponseObject<>(mbTodoAndTodoListObject, HttpStatus.OK, "Update index todo success");
        }
    }

    @Override
    @Transactional
    public ResponseObject<?> createTodo(MBMeCreateTodoRequest request) {
        TodoList todoList = mbMeTodoListRepository.findById(request.getIdTodoList())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.CONFLICT, "Danh sách công việc không tồn tại"));

        // Tạo Todo entity
        Todo newTodo = Todo.builder()
                .name(request.getName())
                .todoList(todoList)
                .indexTodo(request.getIndexTodo())
                .build();

        // Lưu vào DB
        Todo savedTodo = mbMeTodoRepository.save(newTodo);

        // Chuyển đổi sang DTO để trả về
        MBMeCreateTodoResponse response = MBMeCreateTodoResponse.builder()
                .idTodo(savedTodo.getId())
                .name(savedTodo.getName())
                .indexTodo(savedTodo.getIndexTodo())
                .idTodoList(savedTodo.getTodoList().getId())
                .build();

        return new ResponseObject<>(response, HttpStatus.OK, "Tạo mới Todo thành công");
    }

    public ResponseObject<?> findTodoById(MBFilterTodoModalRequest request) {
        MBMeTodoResponse meTodoDetails =mbMeTodoRepository.findTodoDetailById(request.getIdTodo());
        return new ResponseObject<>(meTodoDetails, HttpStatus.OK, "lấy details Todo thành công");
    }

    @Override
    public ResponseObject<?> getAllDetailTodo(MBFilterTodoModalRequest request) {
        MBMeAllDetailTodo meAllDetailTodo = new MBMeAllDetailTodo();
        log.info("IDTODO:",request.getIdTodo());
        Optional<Todo> existTodo = mbMeTodoRepository.findById(request.getIdTodo());
        if(existTodo.isPresent()) {
            Todo todoFind = existTodo.get();
            meAllDetailTodo.setId(todoFind.getId());
            meAllDetailTodo.setCode(todoFind.getCode());
            meAllDetailTodo.setName(todoFind.getName());
            meAllDetailTodo.setDescriptions(todoFind.getDescriptions());
            meAllDetailTodo.setDeadline(todoFind.getDeadline());
            meAllDetailTodo.setReminderTime(todoFind.getDeliveryDate());
            meAllDetailTodo.setStatusReminder(todoFind.getStatusReminder());
            meAllDetailTodo.setCompletionTime(todoFind.getCompletionTime());

            meAllDetailTodo.setPriorityLevel(todoFind.getPriorityLevel() != null ? todoFind.getPriorityLevel().ordinal() : null);
            meAllDetailTodo.setProgress(
                    todoFind.getProgress() != null ? (float) Math.round(todoFind.getProgress()) : null
            );
            if (todoFind.getTypeTodo() != null && todoFind.getTypeTodo().getType() != null) {
                meAllDetailTodo.setType(todoFind.getTypeTodo().getType());
            } else {
                meAllDetailTodo.setType(null);
            }

            meAllDetailTodo.setImageId(todoFind.getImageId());
            meAllDetailTodo.setUrlImage(todoFind.getUrlImage());
            meAllDetailTodo.setIndexTodo(todoFind.getIndexTodo());
            if (todoFind.getTypeTodo() != null) {
                meAllDetailTodo.setIdType(todoFind.getTypeTodo().getId());
            } else {
                meAllDetailTodo.setIdType(null);
            }

            meAllDetailTodo.setTodoListId(todoFind.getTodoList().getId());
            meAllDetailTodo.setTodoListName(mbMeTodoListRepository.findById(todoFind.getTodoList().getId()).get().getName());
            meAllDetailTodo.setStatusTodo(todoFind.getStatusTodo() != null ? todoFind.getStatusTodo().ordinal() : null);
            meAllDetailTodo.setMembers(mbMeAssignRepository.getAllMemberByIdTodo(todoFind.getId()));
            meAllDetailTodo.setLabels(meLabelRepository.getAllLabelByIdTodo(todoFind.getId()));
            meAllDetailTodo.setImages(mbMeImageRepository.getAllByIdTodo(todoFind.getId()));
            meAllDetailTodo.setListTodoChild(mbMeTodoRepository.getAllCheckTodoChild(todoFind.getId()));
        } else {
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Không tìm thấy todo");
        }

        return new ResponseObject<>(meAllDetailTodo, HttpStatus.OK, "lấy tất cả details Todo child thành công");
    }

    @Override
    public ResponseObject<?> getAllCheckTodoChild(MBCheckTodoChildRequest request) {
        List<MBMeTodoChildResponse> meTodoChildDetails =mbMeTodoRepository.getAllCheckTodoChild(request.getIdTodo());
        return new ResponseObject<>(meTodoChildDetails, HttpStatus.OK, "lấy details Todo child thành công");
    }

    @Override
    public ResponseObject<?> createTodoChildChecklist(MBMeCreateDetailTodoChildRequest request) {
        Optional<Todo> todo = mbMeTodoRepository.findById(request.getIdTodo());
        if(todo.isEmpty()){
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Không tìm thấy todo");
        }
        TodoChild todoChild = new TodoChild();
        todoChild.setName(request.getName());
        todoChild.setStatusTodoChild(StatusTodoChild.UNCOMPLETE);
        todoChild.setTodo(todo.get());
        mbMeTodoChildRepository.save(todoChild);

        Short numberTodoChildComplete = mbMeTodoRepository.countCompletedByTodoChildId(request.getIdTodo());
        Short countTodoChild = mbMeTodoRepository.countByTodoChildId(request.getIdTodo());
        Optional<Todo> todoFind  = mbMeTodoRepository.findById(request.getIdTodo());
        if(todoFind.isEmpty()){
            return new ResponseObject<>(null,HttpStatus.NOT_FOUND,"Todo không tồn tại");
        }

        if (countTodoChild != 0) {
            //tiến độ (%) = (số công việc con hoàn thành / tổng số công việc con) * 100
            todoFind.get().setProgress(((float) numberTodoChildComplete / countTodoChild) * 100);
        } else {
            //
            todoFind.get().setProgress((float) 0);
        }
        mbMeTodoRepository.save(todoFind.get());
        return new ResponseObject<>( null, HttpStatus.OK, "Thêm todo child thành công");
    }

    @Override
    public ResponseObject<?> editTodoChildChecklist(MBMeEditDetailTodoChildRequest request) {
        Optional<TodoChild> todoChild = mbMeTodoChildRepository.findById(request.getIdTodoChild());
        if(todoChild.isEmpty()){
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Không tìm thấy todochild");
        }
        todoChild.get().setName(request.getName());
//        messagingTemplate.convertAndSend("/topic/update-status-todochild-checklist","Xóa Thành công cv");
        return new ResponseObject<>( mbMeTodoChildRepository.save(todoChild.get()), HttpStatus.OK, "Edit todo child thành công");
    }

    @Override
    public ResponseObject<?> deleteDetailTodoChild(MBMeDeleteDetailTodoChildRequest request) {
        Optional<TodoChild> todoChild = mbMeTodoChildRepository.findById(request.getIdTodoChild());
        if(todoChild.isEmpty()){
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Không tìm thấy todochild");
        }
        todoChild.get().setStatus(EntityStatus.INACTIVE);
        mbMeTodoChildRepository.save(todoChild.get());
        Short numberTodoChildComplete = mbMeTodoRepository.countCompletedByTodoChildId(request.getIdTodo());
        Short countTodoChild = mbMeTodoRepository.countByTodoChildId(request.getIdTodo());
        Optional<Todo> todoFind  = mbMeTodoRepository.findById(request.getIdTodo());
        if(todoFind.isEmpty()){
            return new ResponseObject<>(null,HttpStatus.NOT_FOUND,"Todo không tồn tại");
        }

        if (countTodoChild != 0) {
            //tiến độ (%) = (số công việc con hoàn thành / tổng số công việc con) * 100
            todoFind.get().setProgress(((float) numberTodoChildComplete / countTodoChild) * 100);
        } else {
            //
            todoFind.get().setProgress(null);
        }
        mbMeTodoRepository.save(todoFind.get());

        return new ResponseObject<>(null, HttpStatus.OK, "Xóa todo child thành công");
    }

    @Override
    public ResponseObject<?> handleStatusTodoChildChange(MBHandleStatusTodoChildRequest request) {

        Optional<TodoChild> todoChild = mbMeTodoChildRepository.findById(request.getIdTodoChild());

        if(todoChild.isEmpty()){
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Không tìm thấy todochild");
        }
        todoChild.get().setStatusTodoChild(request.getStatusTodoChild());

        mbMeTodoChildRepository.save(todoChild.get());

        Short numberTodoChildComplete = mbMeTodoRepository.countCompletedByTodoChildId(request.getIdTodo());
        Short countTodoChild = mbMeTodoRepository.countByTodoChildId(request.getIdTodo());
        Optional<Todo> todoFind  = mbMeTodoRepository.findById(request.getIdTodo());
        if(todoFind.isEmpty()){
            return new ResponseObject<>(null,HttpStatus.NOT_FOUND,"Todo không tồn tại");
        }

        if (countTodoChild != 0) {
            //tiến độ (%) = (số công việc con hoàn thành / tổng số công việc con) * 100
            todoFind.get().setProgress(((float) numberTodoChildComplete / countTodoChild) * 100);
        } else {
            //
            todoFind.get().setProgress((float) 0);
        }
        mbMeTodoRepository.save(todoFind.get());
        return new ResponseObject<>(null, HttpStatus.OK, "update status todo child thành công");
    }

    @Override
    public ResponseObject<?> updateDescriptionsTodo(MBMeUpdateDescriptionsTodoRequest request, StompHeaderAccessor headerAccessor) {
        Optional<Todo> todoFind = mbMeTodoRepository.findById(request.getIdTodo());
        if (!todoFind.isPresent()) {
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "khong tìm thấy todo ");
        }

        todoFind.get().setDescriptions(request.getDescriptions());

        if (request.getDescriptions().equals("<p><br></p>")) {
            todoFind.get().setDescriptions(null);
        }

        return new ResponseObject<>(mbMeTodoRepository.save(todoFind.get()), HttpStatus.OK, "update descriptions todo thành công");

    }

    @Override
    public ResponseObject<?> updateNameTodo(MBMeUpdateNameTodoRequest request, StompHeaderAccessor headerAccessor) {
        Optional<Todo> todoFind = mbMeTodoRepository.findById(request.getIdTodo());
        if (!todoFind.isPresent()) {
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "khong tìm thấy todo ");
        }

        todoFind.get().setName(request.getNameTodo());

        return new ResponseObject<>(mbMeTodoRepository.save(todoFind.get()), HttpStatus.OK, "update ten todo thành công");

    }

    @Override
    @Transactional
    public ResponseObject<?> updateDeadlineTodo(MBMeUpdateDeadlineTodoRequest request, StompHeaderAccessor headerAccessor) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

        Date deadline = null;
        try {
            deadline = sdf.parse(request.getDeadline());
        } catch (ParseException e) {
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Ngày không hợp lệ");
        }
        Optional<Todo> todoFind = mbMeTodoRepository.findById(request.getIdTodo());
        if (!todoFind.isPresent()) {
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Todo không tồn tại");
        }
        if( todoFind.get().getCompletionTime() == null){
            todoFind.get().setStatusTodo(StatusTodo.CHUA_HOAN_THANH);
            todoFind.get().setDeadline(deadline.getTime());

        }else {
            todoFind.get().setDeadline(deadline.getTime());
            if (new Date().getTime() > todoFind.get().getDeadline()) {
                todoFind.get().setStatusTodo(StatusTodo.QUA_HAN);

            } else {
                todoFind.get().setStatusTodo(StatusTodo.HOAN_THANH_SOM);

            }
        }

        if (request.getReminder() == null) {
            todoFind.get().setDeliveryDate(null);
            todoFind.get().setStatusReminder(null);
        } else {
            todoFind.get().setDeliveryDate(request.getReminder());
            todoFind.get().setStatusReminder(StatusReminder.CHUA_GUI);
        }

        Activity activity = new Activity();
        activity.setMemberCreatedId(request.getIdUser());
        activity.setProjectId(request.getProjectId());
        activity.setTodoListId(request.getIdTodoList());
        activity.setTodoId(request.getIdTodo());
        activity.setUrl(request.getUrlPath());
        activity.setContentAction("đã cập nhật ngày hạn của thẻ này thành " + DateConverter.convertDateToString(deadline.getTime()));
        activity.setContentActionProject("đã cập nhật ngày hạn của thẻ "+todoFind.get().getName()+" thành " + DateConverter.convertDateToString(deadline.getTime()));
        TodoObject todoObject = TodoObject.builder().data(mbMeTodoRepository.save(todoFind.get()))
                .dataActivity(mbMeActivityRepository.save(activity))
                .idTodoList(request.getIdTodoList()).idTodo(request.getIdTodo()).build();
        messagingTemplate.convertAndSend("/topic/subscribe-activity","Cập nhat thanh cong ngay het han");

        return new ResponseObject<>( todoObject, HttpStatus.OK, "Cập nhật thành công Ngày hết hạn");
    }

    @Override
    @Transactional
    public ResponseObject<?> deleteDeadlineTodo(MBMeDeleteDeadlineTodoRequest request, StompHeaderAccessor headerAccessor) {
        Optional<Todo> todoFind = mbMeTodoRepository.findById(request.getIdTodo());
        if (!todoFind.isPresent()) {
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Todo không ton tai");
        }
        todoFind.get().setDeadline(null);
        todoFind.get().setCompletionTime(null);
        todoFind.get().setStatusTodo(null);
        todoFind.get().setStatusReminder(null);
        todoFind.get().setDeliveryDate(null);
        Activity activity = new Activity();
        activity.setMemberCreatedId(request.getIdUser());
        activity.setProjectId(request.getProjectId());
        activity.setTodoListId(request.getIdTodoList());
        activity.setTodoId(request.getIdTodo());
        activity.setUrl(request.getUrlPath());
        activity.setContentAction("đã xóa ngày hạn của thẻ này");
        activity.setContentActionProject("đã xóa ngày hạn của thẻ "+todoFind.get().getName());
        TodoObject todoObject = TodoObject.builder().data(mbMeTodoRepository.save(todoFind.get())).
                dataActivity(mbMeActivityRepository.save(activity)).
                idTodoList(request.getIdTodoList()).idTodo(request.getIdTodo()).build();
        messagingTemplate.convertAndSend("/topic/subscribe-activity","Xoa thanh cong ngay het han");
        return new ResponseObject<>(  todoObject, HttpStatus.OK, "Xóa thành công Ngày hết hạn");
    }

    @Override
    @Transactional
    public ResponseObject<?> updateCompleteTodo(MBMeUpdateCompleteTodoRequest request) {
        Optional<Todo> todoFind = mbMeTodoRepository.findById(request.getId());

        if (!todoFind.isPresent()) {
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Todo không ton tai");
        }

        Activity activity = new Activity();
        activity.setTodoListId(request.getIdTodoList());
        activity.setTodoId(request.getIdTodo());
        activity.setProjectId(request.getProjectId());
        activity.setMemberCreatedId(request.getIdUser());
        activity.setUrl(request.getUrlPath());
        Short countTodo = mbMeTodoRepository.countTodoInCheckList(request.getIdTodo());
        if (request.getStatus() == 0) {
            todoFind.get().setCompletionTime(new Date().getTime());
            activity.setContentAction("đã đánh dấu hoàn thành công việc này");
            activity.setContentActionProject("đã đánh dấu thẻ "+todoFind.get().getName()+" là hoàn thành");
            if (new Date().getTime() > todoFind.get().getDeadline()) {
                todoFind.get().setStatusTodo(StatusTodo.QUA_HAN);
            } else {
                todoFind.get().setStatusTodo(StatusTodo.HOAN_THANH_SOM);
            }
//            if (countTodo == 0) {
//                todoFind.get().setProgress((short) 100);
//                updateProgressPeriod(request.getPeriodId());
//            }
        } else {
            todoFind.get().setCompletionTime(null);
            activity.setContentAction("đã đánh dấu chưa hoàn thành công việc");
            activity.setContentActionProject("đã đánh dấu thẻ "+todoFind.get().getName()+" là chưa hoàn thành");
            todoFind.get().setStatusTodo(StatusTodo.CHUA_HOAN_THANH);
//            if (countTodo == 0) {
//                todoFind.get().setProgress((short) 0);
//                updateProgressPeriod(request.getPeriodId());
//            }
        }
        TodoObject todoObject = TodoObject.builder().data(mbMeTodoRepository.save(todoFind.get()))
                .dataActivity(mbMeActivityRepository.save(activity))
                .idTodoList(request.getIdTodoList()).idTodo(request.getIdTodo()).build();
        messagingTemplate.convertAndSend("/topic/subscribe-activity","Cap nhat con gviec da hoan thanh");
        return new ResponseObject<>(todoObject,
                HttpStatus.OK,
                "Update công việc đã hoàn thành");
    }

    @Override
    public ResponseObject<?> updatePriorityLevel(MBMeUpdateTodoRequest request, StompHeaderAccessor headerAccessor) {
        Optional<Todo> todoFindById = mbMeTodoRepository.findById(request.getIdTodo());
        if (!todoFindById.isPresent()) {
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Todo không ton tai");
        }

        PriorityLevel[] priorityLevels = PriorityLevel.values();
        todoFindById.get().setPriorityLevel(priorityLevels[request.getPriorityLevel()]);

        return new ResponseObject<>(mbMeTodoRepository.save(todoFindById.get()),
                HttpStatus.OK,
                "Update mức độ ưu tiên cv");
    }

    @Override
    public ResponseObject<?> updateProgress(MBMeUpdateProgressTodoRequest request, StompHeaderAccessor headerAccessor) {
        Optional<Todo> todoFindById = mbMeTodoRepository.findById(request.getIdTodo());
        if (!todoFindById.isPresent()) {
           return  new ResponseObject<>(null,HttpStatus.NOT_FOUND,"không tìm thấy todo");
        }
        todoFindById.get().setProgress(request.getProgress());

        if (request.getProgress() == 100 && todoFindById.get().getDeadline() == null) {
            todoFindById.get().setStatusTodo(StatusTodo.HOAN_THANH_SOM);
        }

        mbMeTodoRepository.save(todoFindById.get());

        Activity activity = new Activity();
        activity.setTodoId(request.getIdTodo());
        activity.setTodoListId(request.getIdTodoList());
        activity.setProjectId(request.getProjectId());
        activity.setUrl(request.getUrlPath());
        activity.setContentAction("đã cập nhật phần trăm tiến độ của đầu việc thành " + request.getProgress() + "%");
        activity.setContentActionProject("đã cập nhật phần trăm tiến độ của đầu việc thành "+ request.getProgress() + "%"+" "+"cho thẻ "+todoFindById.get().getName());
        activity.setMemberCreatedId(request.getIdUser());
        mbMeActivityRepository.save(activity);


        return new ResponseObject<>(null,HttpStatus.OK,"Update progress success");
    }

    @Override
    public ResponseObject<?> sortTodoPriority(MBMeSortTodoRequest request) {
        if (request.getType() == 0) {
            List<TodoList> allTodoLists = mbMeTodoListRepository.findAll();
            for (TodoList todoList : allTodoLists) {
                List<Todo> todos = mbMeTodoRepository.findTodoSortTodoPriorityASC(todoList,request.getIdPhase());
                short index = 0;
                for (Todo todo : todos) {
                    todo.setIndexTodo(index++);
                }
                mbMeTodoRepository.saveAll(todos);
            }
        }
        if (request.getType() == 1) {
            List<TodoList> allTodoLists = mbMeTodoListRepository.findAll();
            for (TodoList todoList : allTodoLists) {
                List<Todo> todos = mbMeTodoRepository.findTodoSortTodoPriorityDESC(todoList,request.getIdPhase());
                short index = 0;
                for (Todo todo : todos) {
                    todo.setIndexTodo(index++);
                }
                mbMeTodoRepository.saveAll(todos);
            }
        }
        return new ResponseObject<>(request.getIdPhase(), HttpStatus.OK, "Sort todo priority success");
    }

    @Override
    public ResponseObject<?> sortTodoDeadline(MBMeSortTodoRequest request) {
        if (request.getType() == 0) {
            List<TodoList> allTodoLists = mbMeTodoListRepository.findAll();
            for (TodoList todoList : allTodoLists) {
                List<Todo> todos = mbMeTodoRepository.findTodoSortTodoDeadlineASC(todoList,request.getIdPhase());
                short index = 0;
                for (Todo todo : todos) {
                    todo.setIndexTodo(index++);
                }
                mbMeTodoRepository.saveAll(todos);
            }
        }
        if (request.getType() == 1) {
            List<TodoList> allTodoLists = mbMeTodoListRepository.findAll();
            for (TodoList todoList : allTodoLists) {
                List<Todo> todos = mbMeTodoRepository.findTodoSortTodoDeadlineDESC(todoList,request.getIdPhase());
                short index = 0;
                for (Todo todo : todos) {
                    todo.setIndexTodo(index++);
                }
                mbMeTodoRepository.saveAll(todos);
            }
        }
        return new ResponseObject<>(request.getIdPhase(), HttpStatus.OK, "Sort todo deadline success");
    }

    @Override
    public ResponseObject<?> sortTodoCreatedDate(MBMeSortTodoRequest request) {

        if (request.getType() == 0) {
            List<TodoList> allTodoLists = mbMeTodoListRepository.findAll();
            for (TodoList todoList : allTodoLists) {
                List<Todo> todos = mbMeTodoRepository.findTodoSortTodoCreateDateASC(todoList,request.getIdPhase());
                short index = 0;
                for (Todo todo : todos) {
                    todo.setIndexTodo(index++);
                }
                mbMeTodoRepository.saveAll(todos);
            }
        }

        if (request.getType() == 1) {
            List<TodoList> allTodoLists = mbMeTodoListRepository.findAll();
            for (TodoList todoList : allTodoLists) {
                List<Todo> todos = mbMeTodoRepository.findTodoSortTodoCreateDateDESC(todoList,request.getIdPhase());
                short index = 0;
                for (Todo todo : todos) {
                    todo.setIndexTodo(index++);
                }
                mbMeTodoRepository.saveAll(todos);
            }
        }

        return new ResponseObject<>(request.getIdPhase(), HttpStatus.OK, "Sort todo createdate success");
    }

    @Override
    public ResponseObject<?> sortTodoName(MBMeSortTodoRequest request) {

        if (request.getType() == 0) {
            List<TodoList> allTodoLists = mbMeTodoListRepository.findAll();
            for (TodoList todoList : allTodoLists) {
                List<Todo> todos = mbMeTodoRepository.findTodoSortTodoNameASC(todoList,request.getIdPhase());
                short index = 0;
                for (Todo todo : todos) {
                    todo.setIndexTodo(index++);
                }
                mbMeTodoRepository.saveAll(todos);
            }
        }

        if (request.getType() == 1) {
            List<TodoList> allTodoLists = mbMeTodoListRepository.findAll();
            for (TodoList todoList : allTodoLists) {
                List<Todo> todos = mbMeTodoRepository.findTodoSortTodoNameDESC(todoList,request.getIdPhase());
                short index = 0;
                for (Todo todo : todos) {
                    todo.setIndexTodo(index++);
                }
                mbMeTodoRepository.saveAll(todos);
            }
        }

        return new ResponseObject<>(request.getIdPhase(), HttpStatus.OK, "Sort todo name success");
    }

    @Override
    public ResponseObject<?> sortTodoProgress(MBMeSortTodoRequest request) {

        if (request.getType() == 0) {
            List<TodoList> allTodoLists = mbMeTodoListRepository.findAll();
            for (TodoList todoList : allTodoLists) {
                List<Todo> todos = mbMeTodoRepository.findTodoSortTodoProgressASC(todoList,request.getIdPhase());
                short index = 0;
                for (Todo todo : todos) {
                    todo.setIndexTodo(index++);
                }
                mbMeTodoRepository.saveAll(todos);
            }
        }

        if (request.getType() == 1) {
            List<TodoList> allTodoLists = mbMeTodoListRepository.findAll();
            for (TodoList todoList : allTodoLists) {
                List<Todo> todos = mbMeTodoRepository.findTodoSortTodoProgressDESC(todoList,request.getIdPhase());
                short index = 0;
                for (Todo todo : todos) {
                    todo.setIndexTodo(index++);
                }
                mbMeTodoRepository.saveAll(todos);
            }
        }

        return new ResponseObject<>(request.getIdPhase(), HttpStatus.OK, "Sort todo name success");
    }

    @Override
    public ResponseObject<?> getStatusByIdPhase(String idPhase) {
        return new ResponseObject<>(mbMePhaseProjectRepository.findStatusPhaseById(idPhase).get()
        , HttpStatus.OK,
                "Sort todo name success"
        );
    }

}
