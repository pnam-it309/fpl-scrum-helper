package udpm.hn.server.core.member.projectdetails.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import udpm.hn.server.core.common.base.ResponseObject;
import udpm.hn.server.core.member.projectdetails.model.request.MBMeIDTodoByStaffProjectRequest;
import udpm.hn.server.core.member.projectdetails.service.MBMeAssignService;
import udpm.hn.server.infrastructure.constant.MappingConstants;

@RestController
@RequestMapping(MappingConstants.API_PROJECT_DETAILS_TODO_VOTE)
@RequiredArgsConstructor
public class MBAssignController {

    private final MBMeAssignService mbMeAssignService;

    @GetMapping("/member-by-idtodo")
    public ResponseObject<?> getMemberByIdTodo(@RequestParam("idTodo") String idTodo) {
        return mbMeAssignService.getAllMemberByIdTodo(idTodo);
    }

    @GetMapping("/todo-by-staffproject")
    public ResponseObject<?> getTodoByStaffProject(MBMeIDTodoByStaffProjectRequest request) {

        return mbMeAssignService.getTodoByIdStaffProject(request.getIdStaff(), request.getIdProject());
    }
    
}
