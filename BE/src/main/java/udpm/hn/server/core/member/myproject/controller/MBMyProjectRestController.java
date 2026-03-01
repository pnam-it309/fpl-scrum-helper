package udpm.hn.server.core.member.myproject.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import udpm.hn.server.core.manage.project.model.request.MaProjectSearchRequest;
import udpm.hn.server.core.member.myproject.model.request.MBMyProjectSearchRequest;
import udpm.hn.server.core.member.myproject.service.MBMyProjectService;
import udpm.hn.server.infrastructure.constant.MappingConstants;
import udpm.hn.server.utils.Helper;

@RestController
@RequestMapping(MappingConstants.API_MEMBER_MYPROJECT)
@RequiredArgsConstructor
public class MBMyProjectRestController {

    private final MBMyProjectService myProjectService;

    @GetMapping
    public ResponseEntity<?> getAllMyProject(MaProjectSearchRequest request) {
        return Helper.createResponseEntity(myProjectService.getAllMyProject(request));
    }

}
