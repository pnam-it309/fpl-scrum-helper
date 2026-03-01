package udpm.hn.server.core.member.vote.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import udpm.hn.server.core.admin.category.modal.request.ADCategoryRequest;
import udpm.hn.server.core.member.vote.service.StaffStudentVoteService;
import udpm.hn.server.infrastructure.constant.MappingConstants;
import udpm.hn.server.utils.Helper;

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping(MappingConstants.API_MEMBER_TODO_VOTE)
public class StaffStudentController {

    private final StaffStudentVoteService staffStudentVoteService;

    @GetMapping("/{idProject}")
    public ResponseEntity<?> getAllCategory(@PathVariable String idProject) {
        return Helper.createResponseEntity(staffStudentVoteService.getStuentStaffVote(idProject));
    }

}
