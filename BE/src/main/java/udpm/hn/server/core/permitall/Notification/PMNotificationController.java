package udpm.hn.server.core.permitall.Notification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import udpm.hn.server.core.common.base.ResponseObject;
import udpm.hn.server.entity.Project;
import udpm.hn.server.infrastructure.constant.MappingConstants;
import udpm.hn.server.repository.ProjectRepository;
import udpm.hn.server.utils.Helper;

import java.util.Optional;

@Controller
@RequiredArgsConstructor
@RequestMapping(MappingConstants.API_PERMITALL_PREFIX)
@Slf4j
public class PMNotificationController {

    private final PMProjectRepo pmProjectRepo;

    @GetMapping("/notification/project")
    public ResponseEntity<?> getProject(@RequestParam("idProject") String idProject) {
        Optional<Project> projectOpt = pmProjectRepo.findById(idProject);
        if (projectOpt.isPresent()) {
            return Helper.createResponseEntity(new ResponseObject<>(projectOpt.get(), HttpStatus.OK,"thành công"));
        } else {
            return ResponseEntity.status(404).body("Không tìm thấy dự án");
        }
    }

}

