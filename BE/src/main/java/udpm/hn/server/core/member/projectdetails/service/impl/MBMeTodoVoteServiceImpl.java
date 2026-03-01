package udpm.hn.server.core.member.projectdetails.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import udpm.hn.server.core.common.base.ResponseObject;
import udpm.hn.server.core.member.projectdetails.model.response.MBMemberInfoTodoResponse;
import udpm.hn.server.core.member.projectdetails.repository.MBMeAssignRepository;
import udpm.hn.server.core.member.projectdetails.service.MBMeAssignService;
import udpm.hn.server.entity.Project;
import udpm.hn.server.entity.Staff;
import udpm.hn.server.entity.StaffProject;
import udpm.hn.server.entity.Student;
import udpm.hn.server.repository.ProjectRepository;
import udpm.hn.server.repository.StaffProjectRepository;
import udpm.hn.server.repository.StaffRepository;
import udpm.hn.server.repository.StudentRepository;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class MBMeTodoVoteServiceImpl implements MBMeAssignService {

    private final MBMeAssignRepository mbMeTodoVoteRepository;

    private final ProjectRepository projectRepository;

    private final StaffProjectRepository staffProjectRepository;

    private final StaffRepository staffRepository;

    private final StudentRepository studentRepository;


    @Override
    public ResponseObject<?> getAllMemberByIdTodo(String idTodo) {
         List<MBMemberInfoTodoResponse> mb = mbMeTodoVoteRepository.getAllMemberByIdTodo(idTodo);
        return new ResponseObject<>(mb, HttpStatus.OK,"lấy id thành viên dự án thành công");
    }

    @Override
    public ResponseObject<?> getTodoByIdStaffProject(String idStaff, String idProject) {
        log.info("Request nhận vào trong detail checkbox vote: ==> idStaff: {}, idProject: {}", idStaff, idProject);

        long time = System.currentTimeMillis();
log.info("Thời gian hiện tại detail stage vote : ===>{}",time);

        // Kiểm tra người dùng là Staff hay Student
        Optional<Staff> optionalStaff = staffRepository.findById(idStaff);
        Optional<Student> optionalStudent = studentRepository.findById(idStaff);

        Optional<Project> optionalProject = projectRepository.findById(idProject);
        if(optionalProject.isEmpty()){
            return new ResponseObject<>().error(HttpStatus.NO_CONTENT,"Không tìm thấy project");
        }

        Optional<StaffProject> staffProjectOptional = Optional.empty();

        if (optionalStaff.isPresent()) {
            staffProjectOptional = staffProjectRepository.findByStaffAndProject(optionalStaff.get(),optionalProject.get());
        } else if (optionalStudent.isPresent()) {
            staffProjectOptional = staffProjectRepository.findByStudentAndProject(optionalStudent.get(),optionalProject.get());
        }

        if (staffProjectOptional.isEmpty()) {
            log.info("Không tìm thấy staffProject ứng với người dùng");
            return new ResponseObject<>().error(HttpStatus.NO_CONTENT, "Không tìm thấy staffProject của người dùng");
        }

        StaffProject staffProject = staffProjectOptional.get();

        // Truy vấn danh sách ID todo
        List<String> idTodos = mbMeTodoVoteRepository.getIDTodoByIdStaffProject(staffProject.getId(), idProject,time);
        log.info("Danh sách todoId user đã vote: {}", idTodos);

        return new ResponseObject<>(idTodos, HttpStatus.OK, "Lấy id todo nhân viên dự án thành công");
    }

}
