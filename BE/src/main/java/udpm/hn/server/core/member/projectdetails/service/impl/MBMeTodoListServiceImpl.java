package udpm.hn.server.core.member.projectdetails.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import udpm.hn.server.core.common.base.ResponseObject;
import udpm.hn.server.core.member.projectdetails.model.request.MBMeCreateTodoListRequest;
import udpm.hn.server.core.member.projectdetails.model.request.MBMeDeleteTodoListRequest;
import udpm.hn.server.core.member.projectdetails.model.request.MBMeUpdateNameTodoListRequest;
import udpm.hn.server.core.member.projectdetails.model.request.MBMeUpdateTodoListRequest;
import udpm.hn.server.core.member.projectdetails.model.response.MBTodoAndTodoListObject;
import udpm.hn.server.core.member.projectdetails.repository.*;
import udpm.hn.server.core.member.projectdetails.service.MBMeTodoListService;
import udpm.hn.server.entity.Project;
import udpm.hn.server.entity.TodoList;
import udpm.hn.server.infrastructure.constant.EntityStatus;
import udpm.hn.server.utils.TodoListHelper;

import java.util.Optional;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class MBMeTodoListServiceImpl implements MBMeTodoListService {

    private final MBMeTodoRepository mbMeTodoRepository;

    private final MBMeTodoListRepository mbMeTodoListRepository;

    private final MBMeLabelRepository meLabelRepository;

    private final MBMeProjectRepository mbMeProjectRepository;

    private final MBMePhaseProjectRepository mbMePhaseProjectRepository;

    private final TodoListHelper todoListHelper;

    @Override
    @Transactional
    public ResponseObject<?> updateIndexTodoList(MBMeUpdateTodoListRequest request) {
        log.info("Before: "+request.getIndexBefore()+"After: "+request.getIndexAfter());
        if (Integer.parseInt(request.getIndexBefore()) == Integer.parseInt(request.getIndexAfter())) {
            return new ResponseObject<>(null, HttpStatus.CONFLICT,"Lỗi không xác định");
        }

        if (Integer.parseInt(request.getIndexBefore()) < Integer.parseInt(request.getIndexAfter())) {
            log.info("vao 1");
            mbMeTodoListRepository.updateIndexTodoListDecs(Integer.parseInt(request.getIndexBefore()),
                    Integer.parseInt(request.getIndexAfter()),
                    request.getIdProject(),
                    request.getIdPhase());
        } else {  log.info("vao 2");
            mbMeTodoListRepository.updateIndexTodoListAsc(Integer.parseInt(request.getIndexBefore()),
                    Integer.parseInt(request.getIndexAfter()),
                    request.getIdProject(),
                    request.getIdPhase()
            );
        }

        mbMeTodoListRepository.updateIndexTodoList(request.getIdTodoList(), Integer.parseInt(request.getIndexAfter()),request.getIdPhase());
        MBTodoAndTodoListObject todoAndTodoList = MBTodoAndTodoListObject.builder().data(request.getIdTodoList())
                .indexBefore(Integer.parseInt(request.getIndexBefore()))
                .indexAfter(Integer.parseInt(request.getIndexAfter()))
                .sessionId(request.getSessionId())
                .build();
        return new ResponseObject<>(todoAndTodoList,HttpStatus.OK,"update index todo list success");
    }

    @Override
    public ResponseObject<?> createTodoList(MBMeCreateTodoListRequest request, StompHeaderAccessor headerAccessor) {
        Optional<Project> projectExp = mbMeProjectRepository.findById(request.getIdProject());
        if(projectExp.isEmpty()) {
            return new ResponseObject<>(null,HttpStatus.NOT_FOUND,"Project not found");
        }
        TodoList todoList = new TodoList();
        todoList.setCode(todoListHelper.genCodeTodoList(request.getIdProject()));
        todoList.setName(request.getName());
        todoList.setIndexTodoList(todoListHelper.genIndexTodoListWHEREIDPHASE(request.getIdProject(),request.getIdPhase()));
        todoList.setProject(projectExp.get());
        todoList.setPhaseProject(mbMePhaseProjectRepository.findById(request.getIdPhase()).get());
        TodoList newTodoList = mbMeTodoListRepository.save(todoList);
//        successNotificationSender.senderNotification(ConstantMessageSuccess.THEM_THANH_CONG, headerAccessor);
        return new ResponseObject<>(newTodoList,HttpStatus.NOT_FOUND,"create todo list success");
    }

    @Override
    @Transactional
    public ResponseObject<?> deleteTodoList(MBMeDeleteTodoListRequest request) {

        Optional<TodoList> todoListFind = mbMeTodoListRepository.findById(request.getId());

        if (!todoListFind.isPresent()) {
            return new ResponseObject<>(request.getId(),HttpStatus.NOT_FOUND,"todo list không tồn tại");
        }

//        boolean isUsed = mbMeTodoRepository.existsByTodoList_IdAndStatus(request.getId(), EntityStatus.ACTIVE);
//        if (isUsed) {
//            return new ResponseObject<>(request.getId(), HttpStatus.BAD_REQUEST, "Không thể xóa các cột có cv");
//        }

        Optional<TodoList> todoList = mbMeTodoListRepository.findById(request.getId());
        if (todoList.isPresent()) {
            TodoList item = todoList.get();
            item.setStatus(EntityStatus.INACTIVE);
            mbMeTodoListRepository.save(item);
        }

        mbMeTodoListRepository.updateIndexTodoList((int) todoListFind.get().getIndexTodoList(), request.getProjectId(),request.getIdPhase());

//        mbMeTodoListRepository.deleteById(request.getId());

        return new ResponseObject<>(request.getId(),HttpStatus.NOT_FOUND,"xóa cột thành công");

    }

    @Override
    @Transactional
    public ResponseObject<?> updateNameTodoList(MBMeUpdateNameTodoListRequest request) {

        Optional<TodoList> todoListFind = mbMeTodoListRepository.findById(request.getIdTodoList());

        if (!todoListFind.isPresent()) {
            return new ResponseObject<>(null,HttpStatus.NOT_FOUND,"todo list không tồn tại");
        }

        todoListFind.get().setName(request.getName());

        return new ResponseObject<>( mbMeTodoListRepository.save(todoListFind.get()),HttpStatus.NOT_FOUND,"cập nhật Tên cột thành công");

    }

    @Override
    public ResponseObject<?> getAllTodoListByIdPhaseProject(String idPhaseProject) {
        return new ResponseObject<>(mbMeTodoListRepository.getAllTodoListByIdPhaseProject(idPhaseProject)
        ,HttpStatus.OK,"lấy thành công"
        );
    }

    @Override
    public ResponseObject<?> getProjectByIdPhaseProject(String idPhaseProject) {
        return new ResponseObject<>(mbMeProjectRepository.findProjectViewByPhaseProjectId(idPhaseProject) , HttpStatus.OK,"lấy thông tin thành công");
    }





}
