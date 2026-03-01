package udpm.hn.server.core.member.projectdetails.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import udpm.hn.server.core.common.base.ResponseObject;
import udpm.hn.server.core.common.base.TodoObject;
import udpm.hn.server.core.member.projectdetails.model.request.MBMeCreateResourceRequest;
import udpm.hn.server.core.member.projectdetails.model.request.MBMeDeleteResourceRequest;
import udpm.hn.server.core.member.projectdetails.model.request.MBMeUpdateResourceRequest;
import udpm.hn.server.core.member.projectdetails.repository.*;
import udpm.hn.server.core.member.projectdetails.service.MBMeResourceService;
import udpm.hn.server.entity.Activity;
import udpm.hn.server.entity.Resource;
import udpm.hn.server.entity.Todo;
import udpm.hn.server.utils.TodoListHelper;

import java.util.Optional;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class MBMeResourceServiceImpl implements MBMeResourceService {

    private final SimpMessagingTemplate messagingTemplate;

    private final MBMeTodoRepository mbMeTodoRepository;

    private final MBMeTodoListRepository mbMeTodoListRepository;

    private final MBMeLabelRepository meLabelRepository;

    private final MBMeProjectRepository mbMeProjectRepository;

    private final MBMePhaseProjectRepository mbMePhaseProjectRepository;

    private final TodoListHelper todoListHelper;

    private final MBMeResourceRepository mbMeResourceRepository;

    private final MBMeActivityRepository mbMeActivityRepository;

    @Override
    public ResponseObject<?> getAll(String idTodo) {
        return new ResponseObject<>(mbMeResourceRepository.getAll(idTodo),HttpStatus.OK,"Lay tất cả đính kèm thành công");
    }

    @Override
    public ResponseObject<?> create(MBMeCreateResourceRequest request, StompHeaderAccessor headerAccessor) {
        Resource resource = new Resource();
        resource.setName(request.getName());

        Optional<Todo> todoFind = mbMeTodoRepository.findById(request.getIdTodo());
        if(todoFind.isEmpty()){
            return new ResponseObject<>(null,HttpStatus.NOT_FOUND,"không tìm thấy todo ");
        }

        if (request.getName().isEmpty() || request.getName() == null) {
            resource.setName(null);
        }
        resource.setName(request.getName());
        if (!request.getUrl().contains("http")) {
            resource.setUrl("http://" + request.getUrl());
        } else {
            resource.setUrl(request.getUrl());
        }
        resource.setTodo(todoFind.get());

        Activity activity = new Activity();
        activity.setMemberCreatedId(request.getIdUser());
        activity.setTodoId(request.getIdTodo());
        activity.setTodoListId(request.getIdTodoList());
        activity.setProjectId(request.getProjectId());
        activity.setUrl(request.getUrlPath());
        if (request.getName() != null && !request.getName().isEmpty()) {
            activity.setContentAction("đã đính kèm " + request.getName() + " vào thẻ này");
            activity.setContentActionProject("đã đính kèm "+request.getName()+" vào thẻ "+todoFind.get().getName());
        } else {
            activity.setContentAction("đã đính kèm " + "http://" + request.getUrl() + " vào thẻ này");
            activity.setContentActionProject("đã đính kèm "+request.getUrl()+" vào thẻ "+todoFind.get().getName());
        }

        TodoObject todoObject = TodoObject.builder().data(mbMeResourceRepository.save(resource))
                .dataActivity(mbMeActivityRepository.save(activity))
                .idTodo(request.getIdTodo()).idTodoList(request.getIdTodoList()).build();
        messagingTemplate.convertAndSend("/topic/subscribe-activity","Thêm thành công dk");
        return new ResponseObject<>(todoObject, HttpStatus.OK,"thêm thành công đính kèm");
    }

    @Override
    public ResponseObject<?> update(MBMeUpdateResourceRequest request, StompHeaderAccessor headerAccessor) {
        Optional<Resource> resourceFind = mbMeResourceRepository.findById(request.getId());
        if (!resourceFind.isPresent()) {
            return new ResponseObject<>(null,HttpStatus.NOT_FOUND,"đính kèm không tồn tại");
        }
        resourceFind.get().setName(request.getName());
        resourceFind.get().setUrl(request.getUrl());

        mbMeResourceRepository.save(resourceFind.get());
        return new ResponseObject<>(null,HttpStatus.OK,"Cập nhật đính kèm thành công");
    }

    @Override
    public ResponseObject<?> delete(MBMeDeleteResourceRequest request, StompHeaderAccessor headerAccessor) {
        Optional<Todo> todoFind = mbMeTodoRepository.findById(request.getIdTodo());
        if(todoFind.isEmpty()){
            return new ResponseObject<>(null,HttpStatus.NOT_FOUND,"không tìm thấy todo ");
        }
        Activity activity = new Activity();
        activity.setProjectId(request.getProjectId());
        activity.setTodoId(request.getIdTodo());
        activity.setTodoListId(request.getIdTodoList());
        activity.setMemberCreatedId(request.getIdUser());
        activity.setUrl(request.getUrlPath());
        if (!request.getName().isEmpty() && request.getName() != null) {
            activity.setContentAction("đã xóa link đính kèm " + request.getName() + " khỏi thẻ này");
            activity.setContentActionProject("đã xóa link đính kèm "+request.getName()+" khỏi thẻ "+todoFind.get().getName());
        } else {
            activity.setContentAction("đã xóa link đính kèm " + request.getUrl() + " khỏi thẻ này");
            activity.setContentActionProject("đã xóa link đính kèm "+request.getName()+" khỏi thẻ "+todoFind.get().getName());
        }
        mbMeResourceRepository.deleteById(request.getId());
        TodoObject todoObject = TodoObject.builder().data(request.getId())
                .dataActivity(mbMeActivityRepository.save(activity))
                .idTodo(request.getIdTodo())
                .idTodoList(request.getIdTodoList()).build();
        messagingTemplate.convertAndSend("/topic/subscribe-activity","Xóa thành công dk");
        return new ResponseObject<>(todoObject,HttpStatus.OK,"Xóa thành công đính kèm ");
    }
}
