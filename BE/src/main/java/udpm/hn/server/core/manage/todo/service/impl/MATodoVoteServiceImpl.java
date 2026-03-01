package udpm.hn.server.core.manage.todo.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import udpm.hn.server.core.common.base.ResponseObject;
import udpm.hn.server.core.manage.todo.model.request.MAVoteTodoRequest;
import udpm.hn.server.core.manage.todo.repository.MATodoStageVoteRepository;
import udpm.hn.server.core.manage.todo.repository.MATodoVoteRepository;
import udpm.hn.server.core.manage.todo.service.MATodoVoteService;
import udpm.hn.server.entity.Project;
import udpm.hn.server.entity.Staff;
import udpm.hn.server.entity.StaffProject;
import udpm.hn.server.entity.Student;
import udpm.hn.server.entity.Todo;
import udpm.hn.server.entity.TodoVote;
import udpm.hn.server.repository.ProjectRepository;
import udpm.hn.server.repository.StaffProjectRepository;
import udpm.hn.server.repository.StaffRepository;
import udpm.hn.server.repository.StudentRepository;
import udpm.hn.server.repository.ToDoRepository;
import udpm.hn.server.repository.TodoVoteRepository;

import java.util.Calendar;
import java.util.Optional;


@Service
@Slf4j
@RequiredArgsConstructor
public class MATodoVoteServiceImpl implements MATodoVoteService {

    private final StudentRepository studentRepository;

    private final StaffRepository staffRepository;

    private final ToDoRepository toDoRepository;

    private final ProjectRepository projectRepository;

    private final TodoVoteRepository todoVoteRepository;

    private final StaffProjectRepository staffProjectRepository;

    private final MATodoVoteRepository maTodoVoteRepository;

    private final MATodoStageVoteRepository maStageVoteRepository;


    @Override
    public ResponseObject<?> createVote(MAVoteTodoRequest request) {

        log.info("request nhận vào khi bình chọn:{}",request);


        Long currentTime = System.currentTimeMillis();

        Optional<String> stageVoteIdOpt = maStageVoteRepository.findActiveStageVoteIdByProject(currentTime, request.getIdProject());

        if (!stageVoteIdOpt.isPresent()) {
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "cuộc bình chọn chưa diễn ra");
        }

        Optional<Student> optionalStudent = studentRepository.findById(request.getIdUser());

        Optional<Staff> optionalStaff = staffRepository.findById(request.getIdUser());

        Optional<Project> optionalProject = projectRepository.findById(request.getIdProject());
        if (optionalProject.isEmpty()) {
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "không tìm thấy project");
        }
        Optional<Todo> optionalTodo = toDoRepository.findById(request.getIdTodo());
        if(optionalTodo.isEmpty()){
            return new ResponseObject<>(null,HttpStatus.NOT_FOUND,"Không tìm thấy nhiêm vụ");
        }

        if (optionalStaff.isPresent()) {
            Staff staff = optionalStaff.get();

            Optional<StaffProject> optionalStaffProject = staffProjectRepository.findByStaffAndProject(staff, optionalProject.get());

            Optional<TodoVote> optionalTodoVote = maTodoVoteRepository.findByTodoAndStaffProjectAndStageVoteId(optionalTodo.get(),optionalStaffProject.get(),
                    stageVoteIdOpt.get());
            if(optionalTodoVote.isEmpty()){
                TodoVote todoVote = new TodoVote();
                todoVote.setTodo(optionalTodo.get());
                todoVote.setStaffProject(optionalStaffProject.get());
                todoVote.setStatusVote(0);
                todoVote.setStageVote(maStageVoteRepository.findById(stageVoteIdOpt.get()).get());
                todoVoteRepository.save(todoVote);
            }else {
                return new ResponseObject<>(null,HttpStatus.BAD_REQUEST,"Đã bình chọn");
            }

        }

        if (optionalStudent.isPresent()) {
            Student student = optionalStudent.get();

            Optional<StaffProject> optionalStaffProject = staffProjectRepository.findByStudentAndProject(student, optionalProject.get());

            Optional<TodoVote> optionalTodoVote = maTodoVoteRepository.findByTodoAndStaffProjectAndStageVoteId(optionalTodo.get(),optionalStaffProject.get(), stageVoteIdOpt.get());

            if(optionalTodoVote.isEmpty()){
                TodoVote todoVote = new TodoVote();
                todoVote.setTodo(optionalTodo.get());
                todoVote.setStaffProject(optionalStaffProject.get());
                todoVote.setStatusVote(0);
                todoVote.setStageVote(maStageVoteRepository.findById(stageVoteIdOpt.get()).get());
                todoVoteRepository.save(todoVote);
            }else {
                return new ResponseObject<>(null,HttpStatus.BAD_REQUEST,"Đã bình chọn");
            }

        }

        return new ResponseObject<>(null, HttpStatus.CREATED, "Bình chọn thành công");
    }

    @Override
    public ResponseObject<?> getAllVote() {

        return new ResponseObject<>(maTodoVoteRepository.getAllVote(),HttpStatus.OK,"Lấy dữ liệu thành công");

    }

    @Override
    public ResponseObject<?> countTodoVotedByPriorityLevel(String idProject) {

        Long currentTime = System.currentTimeMillis();
        return new ResponseObject<>(maTodoVoteRepository.countTodoVotedByPriorityLevel(idProject,currentTime), HttpStatus.OK,"Lấy dữ liệu thành công");
    }

    @Override
    public ResponseObject<?> deleteTodoVote(MAVoteTodoRequest request) {
        Long currentTime = System.currentTimeMillis();
        Optional<String> stageVoteIdOpt = maStageVoteRepository.findActiveStageVoteIdByProject(currentTime, request.getIdProject());

        Optional<Student> optionalStudent = studentRepository.findById(request.getIdUser());

        Optional<Staff> optionalStaff = staffRepository.findById(request.getIdUser());

        Optional<Project> optionalProject = projectRepository.findById(request.getIdProject());

        if (optionalProject.isEmpty()) {
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "không tìm thấy project");
        }

        Optional<Todo> optionalTodo = toDoRepository.findById(request.getIdTodo());
        if(optionalTodo.isEmpty()){
            return new ResponseObject<>(null,HttpStatus.NOT_FOUND,"Không tìm thấy nhiêm vụ");
        }


        if (!optionalStaff.isEmpty()) {
            Staff staff = optionalStaff.get();

            Optional<StaffProject> optionalStaffProject = staffProjectRepository.findByStaffAndProject(staff, optionalProject.get());

            Optional<TodoVote> optionalTodoVote = maTodoVoteRepository.findByTodoAndStaffProjectAndStageVoteId(optionalTodo.get(),optionalStaffProject.get(),stageVoteIdOpt.get() );
            if(optionalTodoVote.isEmpty()){
                return new ResponseObject<>(null,HttpStatus.NOT_FOUND,"Không tìm thấy vote này");
            }

            maTodoVoteRepository.delete(optionalTodoVote.get());
        }

        if (!optionalStudent.isEmpty()) {
            Student student = optionalStudent.get();
            Optional<StaffProject> optionalStaffProject = staffProjectRepository.findByStudentAndProject(student, optionalProject.get());

            Optional<TodoVote> optionalTodoVote = maTodoVoteRepository.findByTodoAndStaffProjectAndStageVoteId(optionalTodo.get(),optionalStaffProject.get(),stageVoteIdOpt.get());

            if(optionalTodoVote.isEmpty()){
                return new ResponseObject<>(null,HttpStatus.NOT_FOUND,"Không tìm thấy vote này");
            }

            maTodoVoteRepository.delete(optionalTodoVote.get());

        }

        return new ResponseObject<>(null, HttpStatus.CREATED, "Xóa bình chọn thành công");
    }

    @Override
    public ResponseObject<?> findAllVotedTodos(String idProject) {

        long currentTime = Calendar.getInstance().getTimeInMillis();
        return new ResponseObject<>(maTodoVoteRepository.findAllVotedTodos(idProject), HttpStatus.OK, "Lấy danh sách công việc thành công");
    }
}
