package udpm.hn.server.core.member.projectdetails.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import udpm.hn.server.core.common.base.ResponseObject;
import udpm.hn.server.core.member.projectdetails.model.request.MBMeJoinOrOutLabelTodoRequest;
import udpm.hn.server.core.member.projectdetails.repository.MBMeLabelRepository;
import udpm.hn.server.core.member.projectdetails.repository.MBMeLabelTodoRepository;
import udpm.hn.server.core.member.projectdetails.repository.MBMeProjectRepository;
import udpm.hn.server.core.member.projectdetails.repository.MBMeTodoRepository;
import udpm.hn.server.core.member.projectdetails.service.MBMeLabelTodoService;
import udpm.hn.server.entity.LabelProject;
import udpm.hn.server.entity.LabelProjectTodo;
import udpm.hn.server.entity.Todo;

import java.util.Optional;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class MBMeLabelTodoServiceImpl implements MBMeLabelTodoService {

    private final MBMeLabelRepository mbMeLabelRepository;

    private final MBMeTodoRepository mbMeTodoRepository;

    private final MBMeLabelTodoRepository mbMeLabelTodoRepository;

    private final MBMeProjectRepository mbMeProjectRepository;

    @Override
    public ResponseObject<?> joinLabelTodo(MBMeJoinOrOutLabelTodoRequest request) {
        LabelProjectTodo labelTodoFind = mbMeLabelTodoRepository.findByLabelProjectIdAndTodoId(request.getIdLabel(), request.getIdTodo());
        if (labelTodoFind != null) {
            return new ResponseObject<>(null, HttpStatus.CONFLICT,"label Project todo đã tồn tại");
        }

        Optional<Todo> todo = mbMeTodoRepository.findById(request.getIdTodo());
        if (todo.isEmpty()) {
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND,"Todo ko tồn tại");
        }

        Optional<LabelProject> labelProject = mbMeLabelRepository.findById(request.getIdLabel());

        if (labelProject.isEmpty()) {
            return  new ResponseObject<>(null,HttpStatus.NOT_FOUND,"label project đã tồn tại");
        }

        LabelProjectTodo labelTodo = new LabelProjectTodo();
        labelTodo.setTodo(todo.get());
        labelTodo.setLabelProject(labelProject.get());
        return  new ResponseObject<>(  mbMeLabelTodoRepository.save(labelTodo),HttpStatus.OK,"Join label todo thành công");
    }

    @Override
    public ResponseObject<?> outLabelTodo(MBMeJoinOrOutLabelTodoRequest request) {
        LabelProjectTodo labelTodoFind = mbMeLabelTodoRepository.findByLabelProjectIdAndTodoId(request.getIdLabel(), request.getIdTodo());
        if (labelTodoFind != null) {
            mbMeLabelTodoRepository.delete(labelTodoFind);
            return new ResponseObject<>(labelTodoFind, HttpStatus.OK, "Đã xóa label project todo thành công");
        }
        return new ResponseObject<>(null, HttpStatus.NOT_FOUND,"label Project todo ko tồn tại");
    }
}
