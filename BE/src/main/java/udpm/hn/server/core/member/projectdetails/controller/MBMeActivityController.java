package udpm.hn.server.core.member.projectdetails.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import udpm.hn.server.core.common.base.ResponseObject;
import udpm.hn.server.core.member.projectdetails.model.request.MBMeFindActivityRequest;
import udpm.hn.server.core.member.projectdetails.service.MBMeActivityService;
import udpm.hn.server.infrastructure.constant.MappingConstants;

@Slf4j
@RestController
@RequestMapping(MappingConstants.API_PROJECT_DETAILS_ACTIVITY)
@RequiredArgsConstructor
public class MBMeActivityController {

    private final MBMeActivityService mbMeActivityService;

    @GetMapping
    public ResponseObject<?> getAllActivityWhereIdTodo(final MBMeFindActivityRequest request){
        return mbMeActivityService.getAllActivityWhereIdTodo(request);
    }

    @GetMapping("/all-project")
    public ResponseObject<?> getAllActivityWhereIdProject(final MBMeFindActivityRequest request){
        return mbMeActivityService.getAllActivityWhereIdProject(request);
    }

    @GetMapping("/count-activity-project")
    public ResponseObject<?> getCountTotalActivitiesWhereIdProject(final MBMeFindActivityRequest request){
        return mbMeActivityService.countTotalActivitiesWhereIdProject(request);
    }

}
